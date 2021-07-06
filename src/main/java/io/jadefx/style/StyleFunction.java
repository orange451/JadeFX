package io.jadefx.style;

import io.jadefx.scene.Node;

/**
 * This class maps a property to a style operation. It's implemented when giving java the ability to interface with CSS.
 * @author Andrew
 *
 */
public abstract class StyleFunction {
	private String name;
	
	public StyleFunction(String key) {
		this.name = key;
		StyleFunctionDefinitions.operations.put(key, this);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return name + "(" + super.toString() + ")";
	}

	public abstract Object process(Node node, StyleVarArgs value, String attribute);
}

class StyleFunctionValue {
	int len;
	StyleFunction function;
	StyleVarArgs args = new StyleVarArgs();
}