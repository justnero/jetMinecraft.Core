package ru.justnero.minecraft.forge.jet.core.patch;

import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.launchwrapper.IClassTransformer;

public abstract class IClassPatcher implements IClassTransformer  {

	protected String targetClass;

	public abstract byte[] patchClass(byte[] data, boolean isObfuscated);

	public byte[] transform(String name, String transformedName, byte[] data) {
		if(data == null) {
			return null;
		}
		if(transformedName.equals(targetClass)) {
			byte[] result = patchClass(data,!name.equals(transformedName));
			return result;
		}
		return data;
	}

}
