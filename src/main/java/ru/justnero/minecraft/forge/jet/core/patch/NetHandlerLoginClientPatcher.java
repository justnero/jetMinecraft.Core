package ru.justnero.minecraft.forge.jet.core.patch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import codechicken.lib.asm.CC_ClassWriter;

public class NetHandlerLoginClientPatcher extends IClassPatcher {

	public NetHandlerLoginClientPatcher() {
		super();
		targetClass = "net.minecraft.client.network.NetHandlerLoginClient";
	}

	@Override
	public byte[] patchClass(byte[] data, boolean isObfuscated) {
		System.out.println("Patching "+targetClass);
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(classNode, 0);

		for(int i=0;i<classNode.methods.size();i++) {
			String injectClass = "ru/justnero/minecraft/forge/jet/core/JetAuthService";

			MethodNode methodNode = classNode.methods.get(i);
			if(methodNode.name.equals(isObfuscated ? "c" : "func_147391_c")) {

				TypeInsnNode targetNode1 = null;
				MethodInsnNode targetNode2 = null;
				MethodInsnNode targetNode3 = null;

				for(int j=0;j<methodNode.instructions.size();j++) {
					if(methodNode.instructions.get(j).getOpcode() == Opcodes.NEW) {
						targetNode1 = (TypeInsnNode) methodNode.instructions.get(j);
					} else if(targetNode2 == null && methodNode.instructions.get(j).getOpcode() == Opcodes.INVOKESPECIAL) {
						targetNode2 = (MethodInsnNode) methodNode.instructions.get(j);
					} else if(targetNode2 != null && targetNode3 == null && methodNode.instructions.get(j).getOpcode() == Opcodes.INVOKEVIRTUAL) {
						targetNode3 = (MethodInsnNode) methodNode.instructions.get(j);
					}
				}

				if(targetNode1 != null && targetNode2 != null && targetNode3 != null) {
					targetNode1.desc = injectClass;
					targetNode2.owner = injectClass;
					targetNode3.owner = injectClass;
				} else {
					System.out.println("Target for AuthPatcher injection not found");
				}
			}
		}

		ClassWriter writer = new CC_ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES,true);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
