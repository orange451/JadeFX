package io.jadefx.style;

import io.jadefx.scene.Node;

/**
 * This class maps a property to a style operation. It's implemented when giving java the ability to interface with CSS.
 * @author Andrew
 *
 */
public abstract class StyleOperation {
	private String name;
	
	public StyleOperation(String key) {
		this.name = key;
		StyleOperationDefinitions.operations.put(key, this);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public abstract void process(Node node, StyleVarArgs value);
}

/**
 * This class maps a style operation and user-supplied CSS arguments. 
 * @author Andrew
 *
 */
class StyleOperationValue {
	private StyleOperation operation;
	private StyleVarArgs value;
	protected String styleKey;
	
	public StyleOperationValue(StyleOperation operation, StyleVarArgs value) {
		this.value = value;
		this.operation = operation;
	}
	
	public String getName() {
		return this.operation.getName();
	}

	public void process(Node node) {
		if ( value.size() <= 0 )
			return;
		
		// Preprocess functions (TODO cache this!)
		StyleVarArgs preprocessedArgs = null;
		for (int i = 0; i < value.size(); i++) {
			StyleParams params = value.get(i);
			for (int j = 0; j < params.size(); j++) {
				Object arg = params.get(j);
				
				if ( arg instanceof StyleFunctionValue ) {
					preprocessedArgs = resolveFunctions(node, value);
					break;
				}
			}
		}
		
		if ( preprocessedArgs == null ) {
			operation.process(node, value);
		} else {
			operation.process(node, preprocessedArgs);
		}
	}
	
	private StyleVarArgs resolveFunctions(Node node, StyleVarArgs args) {
		StyleVarArgs preprocessedArgs = new StyleVarArgs();
		for (int i = 0; i < value.size(); i++) {
			StyleParams params = value.get(i);
			
			StyleParams newParams = new StyleParams();
			preprocessedArgs.add(newParams);
			
			for (int j = 0; j < params.size(); j++) {
				Object arg = params.get(j);
				
				if ( arg instanceof StyleFunctionValue ) {
					arg = ((StyleFunctionValue)arg).function.process(node, ((StyleFunctionValue)arg).args, styleKey);
				}
				
				newParams.add(arg);
			}
		}
		
		return preprocessedArgs;
	}

	@Override
	public String toString() {
		return operation + " " + value;
	}
}
