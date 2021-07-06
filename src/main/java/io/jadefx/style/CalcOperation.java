package io.jadefx.style;

public enum CalcOperation {
	ADD("+"),
	SUBTRACT("-"),
	MULTIPLY("*"),
	DIVIDE("/");
	
	private String operator;
	
	private CalcOperation(String operator) {
		this.operator = operator;
	}
	
	public String getOperator() {
		return this.operator;
	}
	
	public static CalcOperation match(String operator) {
		for (CalcOperation operation : CalcOperation.values())
			if ( operation.getOperator().equals(operator) )
				return operation;
		return null;
	}
}
