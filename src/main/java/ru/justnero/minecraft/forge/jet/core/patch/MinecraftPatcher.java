package ru.justnero.minecraft.forge.jet.core.patch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import codechicken.lib.asm.CC_ClassWriter;
import ru.justnero.minecraft.forge.jet.core.patch.IClassPatcher;

public class MinecraftPatcher extends IClassPatcher {

	public MinecraftPatcher() {
		super();
		targetClass = "net.minecraft.client.Minecraft";
	}

	@Override
	public byte[] patchClass(byte[] data, boolean isObfuscated) {
		System.out.println("Patching "+targetClass);
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(classNode, 0);

		for(int i=0;i<classNode.methods.size();i++) {
			MethodNode methodNode = classNode.methods.get(i);
			String injectClass = "ru/justnero/minecraft/forge/jet/core/JetAuthService";
			String invokeOwner = "ru/justnero/minecraft/forge/mprotect/ProtectManager";
			String invokeName = "checkPermission";
			String invokeDesc = "(Z)Z";

			if(methodNode.name.equals("<init>")) {
				for(int j=0;j<methodNode.instructions.size();j++) {
					if(methodNode.instructions.get(j).getOpcode() == Opcodes.NEW) {
						TypeInsnNode tmp = (TypeInsnNode) methodNode.instructions.get(j);
						if(tmp.desc.equals("com/mojang/authlib/yggdrasil/YggdrasilAuthenticationService")) {
							tmp.desc = injectClass;
						}
					} else if(methodNode.instructions.get(j).getOpcode() == Opcodes.INVOKESPECIAL ||methodNode.instructions.get(j).getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode tmp = (MethodInsnNode) methodNode.instructions.get(j);
						if(tmp.owner.equals("com/mojang/authlib/yggdrasil/YggdrasilAuthenticationService")) {
							tmp.owner = injectClass;
						}
					}
				}
			} else if((methodNode.name.equals("func_147115_a") || methodNode.name.equals("a")) && methodNode.desc.equals("(Z)V")) {
				AbstractInsnNode targetNode = methodNode.instructions.getFirst();
				if(targetNode != null) {
					InsnList toInject = new InsnList();
					toInject.add(new VarInsnNode(Opcodes.ILOAD,1));
					toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,invokeOwner,invokeName,invokeDesc,false));
					LabelNode ifne = new LabelNode();
					toInject.add(new JumpInsnNode(Opcodes.IFNE,ifne));
					toInject.add(new InsnNode(Opcodes.RETURN));
					toInject.add(ifne);
					toInject.add(new FrameNode(Opcodes.F_SAME,0,null,0,null));

					methodNode.instructions.insertBefore(targetNode,toInject);
				} else {
					System.out.println("Target for"+invokeName+" not found");
				}
			} else if((methodNode.name.equals("func_147116_af") || methodNode.name.equals("al"))  && methodNode.desc.equals("()V")) {
				AbstractInsnNode targetNode = methodNode.instructions.getFirst();

				if(targetNode != null) {
					InsnList toInject = new InsnList();
					toInject.add(new InsnNode(Opcodes.ICONST_1));
					toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,invokeOwner,invokeName,invokeDesc,false));
					LabelNode ifne = new LabelNode();
					toInject.add(new JumpInsnNode(Opcodes.IFNE,ifne));
					toInject.add(new InsnNode(Opcodes.RETURN));
					toInject.add(ifne);

					methodNode.instructions.insertBefore(targetNode,toInject);
				} else {
					System.out.println("Target for"+invokeName+" not found");
				}
			} else if((methodNode.name.equals("func_147121_ag") || methodNode.name.equals("am"))  && methodNode.desc.equals("()V")) {
				AbstractInsnNode targetNode = methodNode.instructions.getFirst();

				if(targetNode != null) {
					InsnList toInject = new InsnList();
					toInject.add(new InsnNode(Opcodes.ICONST_0));
					toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,invokeOwner,invokeName,invokeDesc,false));
					LabelNode ifne = new LabelNode();
					toInject.add(new JumpInsnNode(Opcodes.IFNE,ifne));
					toInject.add(new InsnNode(Opcodes.RETURN));
					toInject.add(ifne);

					methodNode.instructions.insertBefore(targetNode,toInject);
				} else {
					System.out.println("Target for"+invokeName+" not found");
				}
			}

		}

		ClassWriter writer = new CC_ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES,true);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
