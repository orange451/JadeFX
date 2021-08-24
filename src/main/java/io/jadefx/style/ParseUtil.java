package io.jadefx.style;

public class ParseUtil {
	public static long toDuration(Object object) {
		String str = object.toString();
		double multiplier = 1.0;
		
		if ( !str.endsWith("ms")  && str.endsWith("s") )
			multiplier = 1000;
		
		int letters = 0;
		for (int i = str.length()-1; i >= 0; i--) {
			char c = str.charAt(i);
			if ( Character.isLetter(c) )
				letters++;
			else
				break;
		}
		
		str = str.substring(0, str.length()-letters).trim();
		return (long) (toNumber(str) * multiplier);
	}

	public static float toNumber(Object value) {
		if ( value == null )
			return 0;
		
		if ( value instanceof Number )
			return ((Number)value).floatValue();
		
		try {
			return (float) Double.parseDouble(value.toString());
		} catch(Exception e) {
			return 0;
		}
	}
	
	public static boolean isNumber(Object value) {
		if ( value == null )
			return false;
		
		try {
			Double.parseDouble(value.toString());
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	/**
	 * Try to parse string to a value (number, percent, string).
	 * 
	 * @param value
	 * @return
	 */
	public static Object parseVal(String value) {
		Object t = parseNumber(value);
		if (t != null)
			return t;

		t = parsePercent(value);
		if (t != null)
			return t;

		return value;
	}

	/**
	 * Try to parse string to number.
	 * 
	 * @param value
	 * @return
	 */
	public static Number parseNumber(String value) {
		if (!value.endsWith("px") && !value.endsWith("pt") && !value.endsWith("deg"))
			return null;
		
		
		value = value.substring(0,value.length()-2);
		if ( value.endsWith("deg") )
			value = value.substring(0,value.length()-3);
		
		try {
			Number t = Double.parseDouble(value);
			return t;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Try to parse string to percentage.
	 * 
	 * @param value
	 * @return
	 */
	public static Percentage parsePercent(String value) {
		if (!value.endsWith("%"))
			return null;

		value = value.substring(0, value.length() - 1);
		Number num = parseNumber(value + "px");
		if (num == null)
			return null;

		return new Percentage(num.doubleValue());
	}
}
