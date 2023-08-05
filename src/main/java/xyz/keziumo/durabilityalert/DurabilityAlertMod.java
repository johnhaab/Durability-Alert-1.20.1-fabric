package xyz.keziumo.durabilityalert;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.keziumo.durabilityalert.handle.DurabilityTooltipHandler;

public class DurabilityAlertMod implements ModInitializer {
	public static final String MOD_ID = "durability-alert-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// Initialize things.
		DurabilityTooltipHandler.init();
	}
}