package io.jadefx.style;

import java.util.HashMap;

import io.jadefx.paint.Color;
import io.jadefx.scene.Node;

public class StyleFunctionDefinitions {
	protected static HashMap<String, StyleFunction> operations = new HashMap<>();
	
	public static StyleFunction NOFUNC = new StyleFunction("nofunc") {
		@Override
		public Object process(Node node, StyleVarArgs value, String attribute) {
			return null;
		}
	};
	
	public static StyleFunction RGB = new StyleFunction("rgb") {
		@Override
		public Object process(Node node, StyleVarArgs value, String attribute) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			StyleParams params = value.get(0);
			if ( params.size() < 3 )
				return Color.WHITE;
			
			return new Color(
					ParseUtil.toNumber(params.get(0))/255d,
					ParseUtil.toNumber(params.get(1))/255d,
					ParseUtil.toNumber(params.get(2))/255d
			);
		}
	};
	
	public static StyleFunction RGBA = new StyleFunction("rgba") {
		@Override
		public Object process(Node node, StyleVarArgs value, String attribute) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			StyleParams params = value.get(0);
			if ( params.size() < 4 )
				return Color.WHITE;
			
			return new Color(
					ParseUtil.toNumber(params.get(0))/255d,
					ParseUtil.toNumber(params.get(1))/255d,
					ParseUtil.toNumber(params.get(2))/255d,
					ParseUtil.toNumber(params.get(3))
			);
		}
	};
	
	public static StyleFunction CALC = new StyleFunction("calc") {
		@Override
		public Object process(Node node, StyleVarArgs value, String attribute) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			StyleParams params = value.get(0);
			if ( params.size() < 1 )
				return 0;
			
			String query = params.get(0).toString();
			String[] vals = query.split(" ");
			if ( vals.length < 3 )
				return 0;

			Object v1 = ParseUtil.parseVal(vals[0]);
			Object v2 = ParseUtil.parseVal(vals[2]);
			CalcOperation operation = CalcOperation.match(vals[1].trim());
			if ( operation == null )
				return 0;
			
			// Number on Number
			if ( v1 instanceof Number && v2 instanceof Number ) {
				switch(operation) {
					case ADD: {
						return ((Number)v1).doubleValue() + ((Number)v2).doubleValue();
					}
					case SUBTRACT: {
						return ((Number)v1).doubleValue() - ((Number)v2).doubleValue();
					}
					case MULTIPLY: {
						return ((Number)v1).doubleValue() * ((Number)v2).doubleValue();
					}
					case DIVIDE: {
						return ((Number)v1).doubleValue() / ((Number)v2).doubleValue();
					}
				}
			}
			
			// Percentage on Percentage
			if ( v1 instanceof Percentage && v2 instanceof Percentage ) {
				switch(operation) {
					case ADD: {
						return ((Percentage)v1).add((Percentage)v2);
					}
					case SUBTRACT: {
						return ((Percentage)v1).subtract((Percentage)v2);
					}
					case MULTIPLY: {
						return ((Percentage)v1).multiply((Percentage)v2);
					}
					case DIVIDE: {
						return ((Percentage)v1).divide((Percentage)v2);
					}
				}
			}
			
			// Percentage On Number
			if ( v1 instanceof Percentage && v2 instanceof Number ) {
				return new PercentageCalc((Percentage)v1, ((Number)v2).doubleValue(), operation);
			}
			
			// Number On Percentage
			if ( v1 instanceof Number && v2 instanceof Percentage ) {
				if ( operation == CalcOperation.ADD ) {
					return new PercentageCalc((Percentage)v2, ((Number)v1).doubleValue(), operation);
				} else if ( operation == CalcOperation.SUBTRACT ) {
					return new PercentageCalc(((Percentage)v2).multiply(Percentage.fromRatio(-1)), ((Number)v1).doubleValue(), CalcOperation.ADD);
				} else {
					System.err.println("WARNING: INVOKE CALC(). PERCENTAGE MODIFYING CONSTANT NOT SUPPORTED FOR OPERATION " + operation);
					return 0;
				}
			}
			
			return 0;
		}
	};
}
