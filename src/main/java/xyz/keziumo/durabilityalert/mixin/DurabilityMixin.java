package xyz.keziumo.durabilityalert.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class DurabilityMixin {

    @Unique
    private MinecraftClient client;

    @Unique
    private PlayerEntity player;

    /**
     * Injects into the 'damage' method to track durability changes when the item is used.
     * This method is called when an entity takes durability damage from using the item.
     *
     * @param amount       The amount of damage to be applied to the item.
     * @param entity       The living entity using the item.
     * @param breakCallback A callback that is triggered when the item breaks.
     * @param ci           The callback info provided by the mixin system.
     * @param <T>          The type of living entity using the item.
     */
    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    public <T extends LivingEntity> void trackDurability(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
        // Get the ItemStack associated with this method call
        ItemStack stack = (ItemStack)(Object)this;

        // Check if the item is damageable (has durability)
        if (!stack.isDamageable()) {
            return;
        }

        // Calculate the current durability percentage
        double durabilityPercentage = calculateDurabilityPercentage(stack.getMaxDamage(), stack.getDamage());

        client = MinecraftClient.getInstance();

        // Check if the player is in their inventory
        if (client.currentScreen instanceof InventoryScreen) {
            // Player is in their inventory, do not show overlay message
            return;
        }

        player = client.player;

        // Check if the durability percentage is below 20.0%
        if (durabilityPercentage < 20.0) {
            // Get the translation key of the item and convert it to a human-readable item name
            String translationKey = stack.getTranslationKey();
            String itemName = convertItemName(translationKey);

            // Compose the low durability message
            String message = "Your " + itemName + "'s durability is getting low, less than " + durabilityPercentage + "% remaining!";
            String lowDurabilityMessage = Formatting.RED + message;

            // Show the overlay message to the player
            client.inGameHud.setOverlayMessage(Text.of(lowDurabilityMessage), false);
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