package com.symmetrywand;

import com.symmetrywand.network.ModNetworking;
import com.symmetrywand.registry.ModDataComponents;
import com.symmetrywand.registry.ModItemGroups;
import com.symmetrywand.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymmetryWandMod implements ModInitializer {
	public static final String MOD_ID = "symmetrywand";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModDataComponents.register();
		ModItems.register();
		ModItemGroups.register();
		ModNetworking.register();
		SymmetryGameEvents.register();
	}
}
