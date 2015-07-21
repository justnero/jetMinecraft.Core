package ru.justnero.minecraft.forge.jet.core.patch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

import codechicken.lib.asm.CC_ClassWriter;

public class MinecraftServerPatcher extends IClassPatcher {

	public MinecraftServerPatcher() {
		super();
		targetClass = "net.minecraft.server.MinecraftServer";
	}

	@Override
	public byte[] patchClass(byte[] data, boolean isObfuscated) {
		System.out.println("Patching "+targetClass);
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(classNode, 0);

		String injectClass = "ru/justnero/minecraft/forge/jet/core/JetAuthService";
		String injectDesc  = "L"+injectClass+";";

		for(int i=0;i<classNode.fields.size();i++) {
			FieldNode fieldNode = classNode.fields.get(i);
			if(fieldNode.desc.equals("Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;")) {
				fieldNode.desc = injectDesc;
			}
		}

		for(int i=0;i<classNode.methods.size();i++) {
			MethodNode methodNode = classNode.methods.get(i);
			if(methodNode.name.equals("<init>")) {
				for(int j=0;j<methodNode.instructions.size();j++) {
					if(methodNode.instructions.get(j).getOpcode() == Opcodes.NEW) {
						TypeInsnNode tmp = (TypeInsnNode) methodNode.instructions.get(j);
						if(tmp.desc.equals("com/mojang/authlib/yggdrasil/YggdrasilAuthenticationService")) {
							tmp.desc = injectClass;
						}
					} else if(methodNode.instructions.get(j).getOpcode() == Opcodes.INVOKESPECIAL || methodNode.instructions.get(j).getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode tmp = (MethodInsnNode) methodNode.instructions.get(j);
						if(tmp.owner.equals("com/mojang/authlib/yggdrasil/YggdrasilAuthenticationService")) {
							tmp.owner = injectClass;
						}
					} else if(methodNode.instructions.get(j).getOpcode() == Opcodes.PUTFIELD || methodNode.instructions.get(j).getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode tmp = (FieldInsnNode) methodNode.instructions.get(j);
						if(tmp.desc.equals("Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;")) {
							tmp.desc = injectDesc;
						}
					}
				}
			}
		}

		ClassWriter writer = new CC_ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES,true);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
