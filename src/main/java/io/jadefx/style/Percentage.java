package io.jadefx.style;
public class Percentage {
	public static final Percentage ONE_HUNDRED = new Percentage(100);
	public static final Percentage SEVENTY_FIVE = new Percentage(75);
	public static final Percentage FIFTY = new Percentage(50);
	public static final Percentage TWENTY_FIVE = new Percentage(25);
	public static final Percentage ZERO = new Percentage(0);
	
	private final double percent;
	private final double value;

	public Percentage(double percent) {
		this.percent = percent;
		this.value = percent / 100d;
	}

	public double getPercent() {
		return this.percent;
	}

	public double getValue() {
		return this.value;
	}
	
	public String toString() {
		return percent + "%";
	}

	public double getValueClamped() {
		return Math.min(1, Math.max(0, getValue()));
	}

	public static Percentage fromRatio(double ratio) {
		return new Percentage(ratio*100);
	}

	public Percentage add(Percentage percent) {
		return Percentage.fromRatio(this.value+percent.getValue());
	}

	public Percentage subtract(Percentage percent) {
		return Percentage.fromRatio(this.value-percent.getValue());
	}

	public Percentage multiply(Percentage percent) {
		return Percentage.fromRatio(this.value*percent.getValue());
	}

	public Percentage divide(Percentage percent) {
		return Percentage.fromRatio(this.value*percent.getValue());
	}
}