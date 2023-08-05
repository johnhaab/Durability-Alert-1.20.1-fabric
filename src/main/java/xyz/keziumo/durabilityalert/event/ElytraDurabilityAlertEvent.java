package xyz.keziumo.durabilityalert.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface ElytraDurabilityAlertEvent {
    Event<ElytraDurabilityAlertEvent> INSTANCE = EventFactory.createArrayBacked(ElytraDurabilityAlertEvent.class,
            (listeners) -> (player, durabilityPercentage) -> {
                for (ElytraDurabilityAlertEvent listener : listeners) {
                    listener.onElytraDurabilityAlert(player, durabilityPercentage);
                }
            });

    void onElytraDurabilityAlert(PlayerEntity player, double durabilityPercentage);
}
