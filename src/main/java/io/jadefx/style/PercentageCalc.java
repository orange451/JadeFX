package io.jadefx.style;

public class PercentageCalc extends Percentage {
	
	private double offset;
	
	private CalcOperation operation;
	
	private double referenceSize;
	
	public PercentageCalc(Percentage percent, double offset, CalcOperation operation) {
		super(percent.getPercent());
		
		this.offset = offset;
		this.operation = operation;
	}
	
	public void setReferenceSize(double referenceSize) {
		this.referenceSize = referenceSize;
	}
	
	@Override
	public String toString() {
		return "calc(" + super.toString() + " " + this.operation.getOperator() + " " + this.offset + ")";
	}
	
	@Override
	public boolean equals(Object object) {
		boolean equals = super.equals(object);
		if ( !equals )
			return false;
		
		if (!(object instanceof PercentageCalc) ) {
			if ( offset > 0 || offset < 0 )
				return false;
		}
		
		PercentageCalc per = (PercentageCalc)object;
		if ( per.operation != operation )
			return false;
		
		if ( per.offset != offset )
			return false;
		
		return true;
	}
	
	@Override
	public double getValue() {
		Percentage percent = calc();
		return percent == this ? super.getValue() : percent.getValue();
	}

	private Percentage calc() {
		if ( this.referenceSize == 0 )
			return Percentage.ZERO;
		
		switch(operation) {
			case ADD: {
				Percentage offsetPercentage = Percentage.fromRatio(offset/referenceSize);
				return this.add(offsetPercentage);
			}
			case SUBTRACT: {
				Percentage offsetPercentage = Percentage.fromRatio(offset/referenceSize);
				return this.subtract(offsetPercentage);
			}
			case MULTIPLY: {
				Percentage offsetPercentage = Percentage.fromRatio(offset);
				return this.multiply(offsetPercentage);
			}
			case DIVIDE: {
				Percentage offsetPercentage = Percentage.fromRatio(offset);
				return this.divide(offsetPercentage);
			}
		}
		
		return this;
	}
}
