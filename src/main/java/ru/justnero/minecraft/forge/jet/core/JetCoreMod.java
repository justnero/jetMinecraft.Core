package ru.justnero.minecraft.forge.jet.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class JetCoreMod implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
				"ru.justnero.minecraft.forge.jet.core.patch.NetHandlerLoginClientPatcher",
				"ru.justnero.minecraft.forge.jet.core.patch.MinecraftServerPatcher",
				"ru.justnero.minecraft.forge.jet.core.patch.MinecraftPatcher",
		};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	public static boolean check(boolean a) {
		return true;
	}

}
