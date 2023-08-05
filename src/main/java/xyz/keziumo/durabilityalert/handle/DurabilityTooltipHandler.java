package xyz.keziumo.durabilityalert.handle;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Unique;

public class DurabilityTooltipHandler {
    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.isDamageable() && stack.getItem() != null) {
                int maxDurability = stack.getMaxDamage();
                int currentDurability = maxDurability - stack.getDamage();
                double durabilityPercentage = calculateDurabilityPercentage(maxDurability, currentDurability);

                lines.add(Text.literal("Remaining durability: " + durabilityPercentage + "%"));
            }
        });
    }

    /**
     * Calculates the percentage of durability remaining for an item or object.
     *
     * @param maxDurability The maximum durability value the item/object can have.
     * @param currentDurability The current durability value of the item/object.
     * @return The percentage of durability remaining, rounded to two decimal places.
     */
    private static double calculateDurabilityPercentage(int maxDurability, int currentDurability) {
        // Calculate the percentage of remaining durability by dividing the current durability by the maximum durability
        // and multiplying by 100 to convert it to percentage.
        double percentage = ((double) currentDurability / maxDurability) * 100;

        // Round the calculated percentage to two decimal places using Math.round().
        // This ensures that the percentage is presented in a human-readable format.
        return Math.round(percentage * 100.0) / 100.0;
    }

}
