package io.jadefx.util;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

public class GLUtil {
	/**
	 * Returns glGetString based on whoever implemented it.
	 * This is required because {@link GL11#glGetString(int)} is implemented with two different return types
	 * in MiniJVM/LWJGL. As such we need to hackily try to get it.
	 */
	public static String glGetString(int name) {
		String value = null;
		try {
			Method method = GL11.class.getMethod("glGetString", int.class);
			Object result = method.invoke(null, name);
			if ( result instanceof String ) {
				value = result.toString();
			} else {
				value = new String((byte[])result);
			}
		} catch (Exception e) {
			try {
				Method method = GL11.class.getSuperclass().getMethod("glGetString", int.class);
				Object result = method.invoke(null, name);
				if ( result instanceof String ) {
					value = result.toString();
				} else {
					value = new String((byte[])result);
				}
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		return value;
	}
}
