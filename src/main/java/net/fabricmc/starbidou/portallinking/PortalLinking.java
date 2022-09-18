package net.fabricmc.starbidou.portallinking;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalLinking implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("starbidous_portal_linking");
	public static final TagKey<Block> LINKING_BLOCKS = TagKey.of(Registry.BLOCK_KEY, new Identifier("starbidous_portal_linking", "portal_linking"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	}
}