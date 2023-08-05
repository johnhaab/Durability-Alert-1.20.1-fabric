package xyz.keziumo.durabilityalert.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class DurabilityMixin {
    @Unique
    MinecraftClient player = MinecraftClient.getInstance();

    // Define additional variables to store the custom elytra durability and percentage.
    private int maxItemHealth;
    private int currentDurability;
    private double durabilityPercentage;

    private int lastKnownDurability;

    // Inject a method to update the durability
    @Inject(method = "getDamage", at = @At("TAIL"))
    public void getItemDurability(CallbackInfoReturnable<Integer> cir) {

        // The method is not associated with a player, do not proceed
        if (this.player == null || this.player.getCameraEntity() == null) {
            return;
        }

        // Player is in their inventory, do not show overlay message
        if (player.currentScreen instanceof InventoryScreen) {
            return;
        }

        // Setting various variables
        maxItemHealth = ((ItemStack)(Object)this).getMaxDamage();
        currentDurability = cir.getReturnValue();
        durabilityPercentage = calculateDurabilityPercentage(maxItemHealth, currentDurability);

        String translationKey = ((ItemStack)(Object)this).getTranslationKey();
        String itemName = convertItemName(translationKey);

        System.out.println("max" + maxItemHealth);
        System.out.println("current" + currentDurability);
        System.out.println("percent" + durabilityPercentage);

        // If the durability percentage is less than 25%, it's not 0%, and the durability has decreased since the last update
        // Display an overlay message on the player's screen
        if (durabilityPercentage < 20.0 && durabilityPercentage != 0.0 && currentDurability > lastKnownDurability) {
            String message = "Your " + itemName + "'s durability is getting low, less than " + durabilityPercentage + "% remaining!";
            String lowDurabilityMessage = Formatting.RED + message;
            player.inGameHud.setOverlayMessage(Text.of(lowDurabilityMessage), false);
            // Update the last known durability to the current durability
            lastKnownDurability = currentDurability;
        }
    }

    /**
     * Calculates the percentage of durability remaining for an item or object.
     *
     * @param maxDurability The maximum durability value the item/object can have.
     * @param currentDurability The current durability value of the item/object.
     * @return The percentage of durability remaining, rounded to two decimal places.
     */
    @Unique
    private double calculateDurabilityPercentage(int maxDurability, int currentDurability) {
        // Calculate the remaining durability by subtracting the current durability from the maximum durability.
        double remainingDurability = maxDurability - currentDurability;

        // Calculate the percentage of remaining durability by dividing the remaining durability by the maximum durability
        // and multiplying by 100 to convert it to percentage.
        double percentage = (remainingDurability / maxDurability) * 100;

        // Round the calculated percentage to two decimal places using Math.round().
        // This ensures that the percentage is presented in a human-readable format.
        return Math.round(percentage * 100.0) / 100.0;
    }

    /**
     * Converts an item's translation key into a more readable item name.
     *
     * This method takes a translation key of the format "item.minecraft.iron_shovel"
     * and converts it into "Iron Shovel". It extracts the item name from the translation
     * key by removing the "item.minecraft." prefix and replacing underscores with spaces.
     * Additionally, it capitalizes the first letter of each word to make it look more like
     * a proper name. If the translation key does not follow the expected format, the original
     * key is returned.
     *
     * @param translationKey The translation key of the item.
     * @return The converted item name with spaces and capitalized first letters of each word,
     *         or the original key if not in the expected format.
     */
    @Unique
    public String convertItemName(String translationKey) {
        String[] keyParts = translationKey.split("\\.");

        // Check if the translation key is in the expected "item.minecraft.XXX" format.
        if (keyParts.length > 2 && keyParts[0].equals("item") && keyParts[1].equals("minecraft")) {
            String itemName = keyParts[2];

            // Replace underscores with spaces and capitalize the first letter of each word.
            itemName = itemName.replaceAll("_", " ");
            StringBuilder formattedName = new StringBuilder();
            for (String word : itemName.split(" ")) {
                if (formattedName.length() > 0) {
                    formattedName.append(" ");
                }
                formattedName.append(word.substring(0, 1).toUpperCase());
                formattedName.append(word.substring(1).toLowerCase());
            }

            return formattedName.toString();
        } else {
            // Return the original key if it does not follow the expected format.
            return translationKey;
        }
    }
}