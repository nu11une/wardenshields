package net.nu11une.wardenshields;

import net.fabricmc.api.ModInitializer;
import net.nu11une.wardenshields.register.WSItems;
import net.nu11une.wardenshields.util.WSLootTableModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WardenShields implements ModInitializer {
	public static final String MOD_ID = "wardenshields";
	public static final Logger LOGGER = LoggerFactory.getLogger("Warden Shields");

	@Override
	public void onInitialize() {
		WSItems.registerWSItems();
		WSLootTableModifier.registerWSLootPools();
		LOGGER.info("owo");
	}
}
