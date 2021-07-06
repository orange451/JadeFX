package io.jadefx.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.jadefx.collections.ObservableList;
import io.jadefx.geometry.Insets;
import io.jadefx.paint.Color;
import io.jadefx.scene.Node;
import io.jadefx.scene.control.Labeled;
import io.jadefx.scene.layout.Region;
import io.jadefx.scene.layout.Spacable;
import io.jadefx.scene.text.Font;
import io.jadefx.transition.FillTransition;
import io.jadefx.transition.Transition;

@SuppressWarnings("unused")
public class StyleOperationDefinitions {
	protected static HashMap<String, StyleOperation> operations = new HashMap<>();

	private final static String AUTO = "auto";
	private final static String ALL = "all";
	private final static String NONE = "none";
	private final static String INSET = "inset";
	
	public static StyleOperation WIDTH = new StyleOperation("width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			Object x = value.get(0).get(0);
			if ( x instanceof Percentage ) {
				node.setPrefWidthRatio(((Percentage)x));
			} else {
				node.setPrefWidth(ParseUtil.toNumber(x));
			}
		}
	};
	
	public static StyleOperation MIN_WIDTH = new StyleOperation("min-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			Object x = value.get(0).get(0);
			if ( x instanceof Percentage ) {
				node.setMinWidthRatio(((Percentage)x));
			} else {
				node.setMinWidth(ParseUtil.toNumber(x));
			}
		}
	};
	
	public static StyleOperation MAX_WIDTH = new StyleOperation("max-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(Integer.MAX_VALUE));
			
			Object x = value.get(0).get(0);
			if ( x instanceof Percentage ) {
				node.setMaxWidthRatio(((Percentage)x));
			} else {
				node.setMaxWidth(ParseUtil.toNumber(x));
			}
		}
	};
	
	public static StyleOperation HEIGHT = new StyleOperation("height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
						
			Object x = value.get(0).get(0);
			if ( x instanceof Percentage ) {
				node.setPrefHeightRatio(((Percentage)x));
			} else {
				node.setPrefHeight(ParseUtil.toNumber(x));
			}
		}
	};
	
	public static StyleOperation MIN_HEIGHT = new StyleOperation("min-height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			Object x = value.get(0).get(0);
			if ( x instanceof Percentage ) {
				node.setMinHeightRatio(((Percentage)x));
			} else {
				node.setMinHeight(ParseUtil.toNumber(x));
			}
		}
	};
	
	public static StyleOperation MAX_HEIGHT = new StyleOperation("max-height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(Integer.MAX_VALUE));
			
			Object x = value.get(0).get(0);
			if ( x instanceof Percentage ) {
				node.setMaxHeightRatio(((Percentage)x));
			} else {
				node.setMaxHeight(ParseUtil.toNumber(x));
			}
		}
	};
	
	public static StyleOperation BORDER_RADIUS = new StyleOperation("border-radius") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			StyleParams params = value.get(0);
			
			float[] border = new float[4];
			
			if ( params.size() == 1 ) {
				for (int i = 0; i < border.length; i++) {
					border[i] = (float) ParseUtil.toNumber(params.get(0));	
				}
			} else if ( params.size() == 2 ) {
				border[0] = (float) ParseUtil.toNumber(params.get(0));
				border[2] = (float) ParseUtil.toNumber(params.get(0));
				border[1] = (float) ParseUtil.toNumber(params.get(1));
				border[3] = (float) ParseUtil.toNumber(params.get(1));
			} else if ( params.size() == 4 ) {
				border[0] = (float) ParseUtil.toNumber(params.get(0));
				border[1] = (float) ParseUtil.toNumber(params.get(1));
				border[2] = (float) ParseUtil.toNumber(params.get(2));
				border[3] = (float) ParseUtil.toNumber(params.get(3));
			}

			BORDER_TOP_LEFT_RADIUS.process(node, new StyleVarArgs(new StyleParams(border[0])));
			BORDER_TOP_RIGHT_RADIUS.process(node, new StyleVarArgs(new StyleParams(border[1])));
			BORDER_BOTTOM_RIGHT_RADIUS.process(node, new StyleVarArgs(new StyleParams(border[2])));
			BORDER_BOTTOM_LEFT_RADIUS.process(node, new StyleVarArgs(new StyleParams(border[3])));
		}
	};
	
	public static StyleOperation BORDER_TOP_LEFT_RADIUS = new StyleOperation("border-top-left-radius") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			float destBorderRadius = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceBorderRadius = (float) t.getBorder().getLeft();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorderRadius == sourceBorderRadius || transition == null ) {
				float[] currentRadius = t.getBorderRadii();
				t.setBorderRadii(destBorderRadius,
								currentRadius[1],
								currentRadius[2],
								currentRadius[3]);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						float[] currentRadius = t.getBorderRadii();
						t.setBorderRadii(tween(sourceBorderRadius, destBorderRadius, progress),
										currentRadius[1],
										currentRadius[2],
										currentRadius[3]);
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_TOP_RIGHT_RADIUS = new StyleOperation("border-top-right-radius") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			float destBorderRadius = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceBorderRadius = (float) t.getBorder().getLeft();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorderRadius == sourceBorderRadius || transition == null ) {
				float[] currentRadius = t.getBorderRadii();
				t.setBorderRadii(currentRadius[0],
								destBorderRadius,
								currentRadius[2],
								currentRadius[3]);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						float[] currentRadius = t.getBorderRadii();
						t.setBorderRadii(currentRadius[0],
										tween(sourceBorderRadius, destBorderRadius, progress),
										currentRadius[2],
										currentRadius[3]);
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_BOTTOM_RIGHT_RADIUS = new StyleOperation("border-bottom-right-radius") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			float destBorderRadius = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceBorderRadius = (float) t.getBorder().getLeft();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorderRadius == sourceBorderRadius || transition == null ) {
				float[] currentRadius = t.getBorderRadii();
				t.setBorderRadii(currentRadius[0],
								currentRadius[1],
								destBorderRadius,
								currentRadius[3]);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						float[] currentRadius = t.getBorderRadii();
						t.setBorderRadii(currentRadius[0],
										currentRadius[1],
										tween(sourceBorderRadius, destBorderRadius, progress),
										currentRadius[3]);
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_BOTTOM_LEFT_RADIUS = new StyleOperation("border-bottom-left-radius") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			float destBorderRadius = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceBorderRadius = (float) t.getBorder().getLeft();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorderRadius == sourceBorderRadius || transition == null ) {
				float[] currentRadius = t.getBorderRadii();
				t.setBorderRadii(currentRadius[0],
								currentRadius[1],
								currentRadius[2],
								destBorderRadius);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						float[] currentRadius = t.getBorderRadii();
						t.setBorderRadii(currentRadius[0],
										currentRadius[1],
										currentRadius[2],
										tween(sourceBorderRadius, destBorderRadius, progress));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_STYLE = new StyleOperation("border-style") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			BorderStyle bs = BorderStyle.valueOf(value.get(0).get(0).toString().toUpperCase());
			if ( bs == null )
				bs = BorderStyle.NONE;
			
			t.setBorderStyle(bs);
		}
	};
	
	public static StyleOperation BORDER_LEFT = new StyleOperation("border-left") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			float destBorder = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceBorder = (float) t.getBorder().getLeft();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorder == sourceBorder || transition == null ) {
				Insets currentBorder = t.getBorder();
				t.setBorder(new Insets(currentBorder.getTop(),
										currentBorder.getRight(),
										currentBorder.getBottom(),
										destBorder));
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						Insets currentBorder = t.getBorder();
						t.setBorder(new Insets(currentBorder.getTop(),
												currentBorder.getRight(),
												currentBorder.getBottom(),
												tween(sourceBorder, destBorder, progress)));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_RIGHT = new StyleOperation("border-right") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			float destBorder = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceBorder = (float) t.getBorder().getRight();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorder == sourceBorder || transition == null ) {
				Insets currentBorder = t.getBorder();
				t.setBorder(new Insets(currentBorder.getTop(),
										destBorder,
										currentBorder.getBottom(),
										currentBorder.getLeft()));
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						Insets currentBorder = t.getBorder();
						t.setBorder(new Insets(currentBorder.getTop(),
												tween(sourceBorder, destBorder, progress),
												currentBorder.getBottom(),
												currentBorder.getLeft()));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_TOP = new StyleOperation("border-top") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			float destBorder = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceBorder = (float) t.getBorder().getTop();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorder == sourceBorder || transition == null ) {
				Insets currentBorder = t.getBorder();
				t.setBorder(new Insets(destBorder,
										currentBorder.getRight(),
										currentBorder.getBottom(),
										currentBorder.getLeft()));
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						Insets currentBorder = t.getBorder();
						t.setBorder(new Insets(tween(sourceBorder, destBorder, progress),
												currentBorder.getRight(),
												currentBorder.getBottom(),
												currentBorder.getLeft()));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_BOTTOM = new StyleOperation("border-bottom") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			float destBorder = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceBorder = (float) t.getBorder().getBottom();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorder == sourceBorder || transition == null ) {
				Insets currentBorder = t.getBorder();
				t.setBorder(new Insets(currentBorder.getRight(),
										currentBorder.getRight(),
										destBorder,
										currentBorder.getLeft()));
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						Insets currentBorder = t.getBorder();
						t.setBorder(new Insets(currentBorder.getTop(),
												currentBorder.getRight(),
												tween(sourceBorder, destBorder, progress),
												currentBorder.getLeft()));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_WIDTH = new StyleOperation("border-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			float border = (float)ParseUtil.toNumber(value.get(0).get(0));
			float borderLeft = border;
			float borderRight = border;
			float borderTop = border;
			float borderBottom = border;
			
			if ( value.get(0).size() == 2 ) {
				float borderV = (float)ParseUtil.toNumber(value.get(0).get(0));
				float borderH = (float)ParseUtil.toNumber(value.get(0).get(1));

				borderTop = borderV;
				borderBottom = borderV;
				borderLeft = borderH;
				borderRight = borderH;
			} else if ( value.get(0).size() == 3 ) {
				borderTop = (float)ParseUtil.toNumber(value.get(0).get(0));
				borderRight = (float)ParseUtil.toNumber(value.get(0).get(1));
				borderLeft = (float)ParseUtil.toNumber(value.get(0).get(1));
				borderBottom = (float)ParseUtil.toNumber(value.get(0).get(2));
			} else if ( value.get(0).size() == 4 ) {
				borderTop = (float)ParseUtil.toNumber(value.get(0).get(0));
				borderRight = (float)ParseUtil.toNumber(value.get(0).get(1));
				borderBottom = (float)ParseUtil.toNumber(value.get(0).get(2));
				borderLeft = (float)ParseUtil.toNumber(value.get(0).get(3));
			}
			
			BORDER_LEFT.process(node, new StyleVarArgs(new StyleParams(borderLeft)));
			BORDER_RIGHT.process(node, new StyleVarArgs(new StyleParams(borderRight)));
			BORDER_TOP.process(node, new StyleVarArgs(new StyleParams(borderTop)));
			BORDER_BOTTOM.process(node, new StyleVarArgs(new StyleParams(borderBottom)));
		}
	};
	
	public static StyleOperation BORDER_COLOR = new StyleOperation("border-color") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			t.setBorderColor(getColor(value.get(0).get(0)));
		}
	};
	
	public static StyleOperation BACKGROUND_COLOR = new StyleOperation("background-color") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBackground) )
				return;
			
			StyleBackground t = (StyleBackground)node;
			Color destColor = getColor(value.get(0).get(0));
			Background currentBackground = t.getBackground();
			
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( transition == null || currentBackground == null || !(currentBackground instanceof BackgroundSolid) ) {
				t.setBackground(new BackgroundSolid(destColor));
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Color sourceColor = new Color(((BackgroundSolid)currentBackground).getColor());
				if ( sourceColor.equals(destColor) )
					return;
				
				Color fillColor = new Color(sourceColor);
				
				// Color transition
				FillTransition tran = new FillTransition(transition.getDurationMillis(), sourceColor, destColor, fillColor);
				tran.play();
				current.add(tran);

				// Apply fill color
				t.setBackground(new BackgroundSolid(fillColor));
			}
		}
	};
	
	public static StyleOperation FILL_COLOR = new StyleOperation("fill-color") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleFillColor) )
				return;
			
			StyleFillColor t = (StyleFillColor)node;
			Color destColor = getColor(value.get(0).get(0));
			Color currentBackground = t.getFill();
			
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( transition == null || currentBackground == null || !(currentBackground instanceof Color) ) {
				t.setFill(new Color(destColor));
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Color sourceColor = new Color(currentBackground);
				if ( sourceColor.equals(destColor) )
					return;
				
				Color fillColor = new Color(sourceColor);
				
				// Color transition
				FillTransition tran = new FillTransition(transition.getDurationMillis(), sourceColor, destColor, fillColor);
				tran.play();
				current.add(tran);

				// Apply fill color
				t.setFill(fillColor);
			}
		}
	};
	
	public static StyleOperation BACKGROUND_IMAGE = new StyleOperation("background-image") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBackground) )
				return;
			
			StyleBackground t = (StyleBackground)node;
			ObservableList<Background> backgrounds = t.getBackgrounds();
			
			// Clear all NON solid backgrounds
			for (int i = 0; i < backgrounds.size(); i++) {
				Background b = backgrounds.get(i);
				if ( b instanceof BackgroundSolid )
					continue;
				
				backgrounds.remove(i--);
			}
			
			// Parse bacgkrounds
			for (int i = 0; i < value.size(); i++) {
				StyleParams params = value.get(i);
				if ( params.size() == 0 )
					continue;
				
				Background back = getBackground(params.get(0));
				if ( back == null )
					continue;
				
				backgrounds.add(back);
			}
		}
	};
	
	public static StyleOperation BOX_SHADOW = new StyleOperation("box-shadow") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBoxShadow) )
				return;
			
			StyleBoxShadow t = (StyleBoxShadow)node;
			
			// Generate the box shadow
			List<BoxShadow> newShadows = new ArrayList<BoxShadow>();
			if ( !value.get(0).get(0).equals(NONE) ) {
				
				for (int i = 0; i < value.size(); i++) {
					StyleParams params = value.get(i);
					
					boolean inset = false;
					for (int j = 0; j < params.size(); j++) {
						Object param = params.get(j);
						if ( param.toString().equalsIgnoreCase(INSET) ) {
							StyleParams newParams = (StyleParams) params.clone();
							newParams.remove(param);
							params = newParams;
							inset = true;
							break;
						}
					}
					
					BoxShadow newShadow = null;
					if ( params.size() == 2 ) {
						newShadow = new BoxShadow(ParseUtil.toNumber(params.get(0)), ParseUtil.toNumber(params.get(1)), 0);
					} else if ( params.size() == 3 ) {
						boolean isNumber = ParseUtil.isNumber(params.get(2));
						if ( isNumber ) {
							newShadow = new BoxShadow(
									ParseUtil.toNumber(params.get(0)),
									ParseUtil.toNumber(params.get(1)),
									ParseUtil.toNumber(params.get(2))
							);
						} else {
							newShadow = new BoxShadow(
									ParseUtil.toNumber(params.get(0)),
									ParseUtil.toNumber(params.get(1)),
									0,
									getColor(params.get(2))
							);
						}
					} else if ( params.size() == 4 ) {
						boolean isNumber = ParseUtil.isNumber(params.get(3));
						if ( isNumber ) {
							newShadow = new BoxShadow(
									ParseUtil.toNumber(params.get(0)),
									ParseUtil.toNumber(params.get(1)),
									ParseUtil.toNumber(params.get(2)),
									ParseUtil.toNumber(params.get(3))
							);
						} else {
							newShadow = new BoxShadow(
									ParseUtil.toNumber(params.get(0)),
									ParseUtil.toNumber(params.get(1)),
									ParseUtil.toNumber(params.get(2)),
									getColor(params.get(3))
							);
						}
					} else if ( params.size() == 5 ) {
						newShadow = new BoxShadow(
								ParseUtil.toNumber(params.get(0)),
								ParseUtil.toNumber(params.get(1)),
								ParseUtil.toNumber(params.get(2)),
								ParseUtil.toNumber(params.get(3)),
								getColor(params.get(4))
						);
					}
					
					if ( newShadow != null ) {
						newShadow.setInset(inset);
						newShadows.add(newShadow);
					}
				}
			}
			
			// Get the style transition for this node with this transition name
			StyleTransition transition = node.getStyleTransition(this.getName());
			
			// If no transition, directly copy in shadows.
			if ( transition == null ) {
				t.getBoxShadowList().clear();
				for (int i = 0; i < newShadows.size(); i++) {
					t.getBoxShadowList().add(newShadows.get(i));
				}
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				// Transition already existing shadows
				for (int i = 0; i < Math.max(newShadows.size(), t.getBoxShadowList().size()); i++) {
					
					// Source shadow (copying TO)
					BoxShadow st = null;
					if ( i < t.getBoxShadowList().size() ) {
						st = t.getBoxShadowList().get(i);
					} else {
						st = new BoxShadow(0, 0, 0, Color.TRANSPARENT);
						t.getBoxShadowList().add(st);
					}
					
					// Destination shadow (copying FROM)
					BoxShadow dt = null;
					if ( i < newShadows.size() ) {
						dt = newShadows.get(i);
					} else {
						dt = new BoxShadow(st.getXOffset(), st.getYOffset(), st.getBlurRadius(), Color.TRANSPARENT);
						dt.setInset(st.isInset());
					}
					
					// Finalize them (needed for below methods)
					final BoxShadow sourceShadow = st;
					final BoxShadow destShadow = dt;
					
					// No way to transition inset
					st.setInset(dt.isInset());
					
					// Position transition
					if ( sourceShadow.getXOffset() != destShadow.getXOffset() || sourceShadow.getYOffset() != destShadow.getYOffset() ) {
						float sx = sourceShadow.getXOffset();
						float sy = sourceShadow.getYOffset();
						
						Transition tran = new Transition(transition.getDurationMillis()) {
							@Override
							public void tick(double progress) {
								sourceShadow.setXOffset(tween(sx, destShadow.getXOffset(), progress));
								sourceShadow.setYOffset(tween(sy, destShadow.getYOffset(), progress));
							}
						};
						tran.play();
						current.add(tran);
					}
					
					// Blur transition
					if ( sourceShadow.getBlurRadius() != destShadow.getBlurRadius() ) {
						float a = sourceShadow.getBlurRadius();
						
						Transition tran = new Transition(transition.getDurationMillis()) {
							@Override
							public void tick(double progress) {
								sourceShadow.setBlurRadius(tween(a, destShadow.getBlurRadius(), progress));
							}
						};
						tran.play();
						current.add(tran);
					}
					
					// Spread transition
					if ( sourceShadow.getSpread() != destShadow.getSpread() ) {
						float a = sourceShadow.getSpread();
						
						Transition tran = new Transition(transition.getDurationMillis()) {
							@Override
							public void tick(double progress) {
								sourceShadow.setSpread(tween(a, destShadow.getSpread(), progress));
							}
						};
						tran.play();
						current.add(tran);
					}
					
					// Color transition
					if ( !sourceShadow.getFromColor().equals(destShadow.getFromColor()) ) {
						Color tt = new Color(sourceShadow.getFromColor()).immutable(false);
						Transition tran = new FillTransition(transition.getDurationMillis(), new Color(sourceShadow.getFromColor()), destShadow.getFromColor(), tt);
						sourceShadow.setFromColor(tt);
						tran.play();
						current.add(tran);
					}
				}
			}
			
			newShadows.clear();
		}
	};
	
	public static StyleOperation TEXT_SHADOW = new StyleOperation("text-shadow") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof Labeled) )
				return;
			
			Labeled t = (Labeled)node;
			
			// Generate the box shadow
			List<Shadow> newShadows = new ArrayList<Shadow>();
			if ( !value.get(0).get(0).equals(NONE) ) {
				
				for (int i = 0; i < value.size(); i++) {
					StyleParams params = value.get(i);
					
					if ( params.size() == 2 ) {
						newShadows.add(new Shadow(ParseUtil.toNumber(params.get(0)), ParseUtil.toNumber(params.get(1)), 0));
					} else if ( params.size() == 3 ) {
						boolean isNumber = ParseUtil.isNumber(params.get(2));
						if ( isNumber ) {
							newShadows.add(new Shadow(
									ParseUtil.toNumber(params.get(0)),
									ParseUtil.toNumber(params.get(1)),
									ParseUtil.toNumber(params.get(2))
							));
						} else {
							newShadows.add(new Shadow(
									ParseUtil.toNumber(params.get(0)),
									ParseUtil.toNumber(params.get(1)),
									0,
									getColor(params.get(2))
							));
						}
					} else if ( params.size() == 4 ) {
						newShadows.add(new Shadow(
								ParseUtil.toNumber(params.get(0)),
								ParseUtil.toNumber(params.get(1)),
								ParseUtil.toNumber(params.get(2)),
								getColor(params.get(3))
						));
					} else if ( params.size() == 5 ) {
						newShadows.add(new Shadow(
								ParseUtil.toNumber(params.get(0)),
								ParseUtil.toNumber(params.get(1)),
								ParseUtil.toNumber(params.get(2)),
								getColor(params.get(3)),
								params.get(4).toString().equalsIgnoreCase(INSET)
						));
					}
				}
			}
			// Get the style transition for this node with this transition name
			StyleTransition transition = node.getStyleTransition(this.getName());
			
			// If no transition, directly copy in shadows.
			if ( transition == null ) {
				t.getTextShadowList().clear();
				for (int i = 0; i < newShadows.size(); i++) {
					t.getTextShadowList().add(newShadows.get(i));
				}
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				// Transition already existing shadows
				for (int i = 0; i < Math.max(newShadows.size(), t.getTextShadowList().size()); i++) {
					
					// Source shadow (copying TO)
					Shadow st = null;
					if ( i < t.getTextShadowList().size() ) {
						st = t.getTextShadowList().get(i);
					} else {
						st = new Shadow(0, 0, 0, Color.TRANSPARENT);
						t.getTextShadowList().add(st);
					}
					
					// Destination shadow (copying FROM)
					Shadow dt = null;
					if ( i < newShadows.size() ) {
						dt = newShadows.get(i);
					} else {
						dt = new Shadow(st.getXOffset(), st.getYOffset(), st.getBlurRadius(), Color.TRANSPARENT);
						dt.setInset(st.isInset());
					}
					
					// Finalize them (needed for below methods)
					final Shadow sourceShadow = st;
					final Shadow destShadow = dt;
					
					// No way to transition inset
					st.setInset(dt.isInset());
					
					// Position transition
					if ( sourceShadow.getXOffset() != destShadow.getXOffset() || sourceShadow.getYOffset() != destShadow.getYOffset() ) {
						float sx = sourceShadow.getXOffset();
						float sy = sourceShadow.getYOffset();
						
						Transition tran = new Transition(transition.getDurationMillis()) {
							@Override
							public void tick(double progress) {
								sourceShadow.setXOffset(tween(sx, destShadow.getXOffset(), progress));
								sourceShadow.setYOffset(tween(sy, destShadow.getYOffset(), progress));
							}
						};
						tran.play();
						current.add(tran);
					}
					
					// Blur transition
					if ( sourceShadow.getBlurRadius() != destShadow.getBlurRadius() ) {
						float a = sourceShadow.getBlurRadius();
						
						Transition tran = new Transition(transition.getDurationMillis()) {
							@Override
							public void tick(double progress) {
								sourceShadow.setBlurRadius(tween(a, destShadow.getBlurRadius(), progress));
							}
						};
						tran.play();
						current.add(tran);
					}
					
					// Color transition
					if ( !sourceShadow.getFromColor().equals(destShadow.getFromColor()) ) {
						Color tt = new Color(sourceShadow.getFromColor()).immutable(false);
						Transition tran = new FillTransition(transition.getDurationMillis(), new Color(sourceShadow.getFromColor()), destShadow.getFromColor(), tt);
						sourceShadow.setFromColor(tt);
						tran.play();
						current.add(tran);
					}
				}
			}
			
			newShadows.clear();
		}
	};
	
	public static StyleOperation PADDING = new StyleOperation("padding") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof Region) )
				return;
			
			Region region = (Region)node;
			Insets dest = null;
			Insets source = region.getPadding();
			
			// Get dest padding
			if ( value.get(0).size() == 1 ) {
				dest = new Insets( ParseUtil.toNumber(value.get(0).get(0)) );
			} else if ( value.get(0).size() == 2 ) {
				dest = new Insets( ParseUtil.toNumber(value.get(0).get(0)), ParseUtil.toNumber(value.get(0).get(1)) );
			} else if ( value.get(0).size() == 4 ) {
				dest = new Insets( ParseUtil.toNumber(value.get(0).get(0)), ParseUtil.toNumber(value.get(0).get(1)), ParseUtil.toNumber(value.get(0).get(2)), ParseUtil.toNumber(value.get(0).get(3)) );
			}
			
			// NPE
			if ( dest == null )
				return;
			
			// Transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( dest.equals(source) || transition == null ) {
				region.setPadding(dest);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				final Insets destF = dest;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						Insets t = new Insets( tween(source.getTop(), destF.getTop(), progress),
								tween(source.getRight(), destF.getRight(), progress),
								tween(source.getBottom(), destF.getBottom(), progress),
								tween(source.getLeft(), destF.getLeft(), progress));
						
						region.setPadding(t);
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation TRANSITION = new StyleOperation("transition") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			for (int i = 0; i< value.size(); i++) {
				if ( value.get(i).size() < 2 )
					continue;
				
				String property = value.get(i).get(0).toString().trim();
				long duration = ParseUtil.toDuration(value.get(i).get(1));
				
				
				StyleTransition styleTransition = node.getStyleTransition(property);
				if ( styleTransition == null ) {
					styleTransition = new StyleTransition(property, duration, StyleTransitionType.LINEAR);
					node.setStyleTransition(property, styleTransition);
				}
				
				styleTransition.setDuration(duration);
			}
		}
	};
	
	/*public static StyleOperation GAP = new StyleOperation("gap") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof Gappable) )
				return;
			
			Gappable t = (Gappable)node;
			float dest1 = (float)ParseUtil.toNumber(value.get(0).get(0));
			float dest2 = dest1;
			if ( value.get(0).size() > 1 )
				dest2 = (float)ParseUtil.toNumber(value.get(0).get(1));
			float dest2F = dest2;
			float source1 = t.getHgap();
			float source2 = t.getVgap();
			
			// Transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( (dest1 == source1 && dest2 == source2) || transition == null ) {
				t.setHgap(dest1);
				t.setVgap(dest2);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						t.setHgap(tween(source1, dest1, progress));
						t.setVgap(tween(source2, dest2F, progress));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};*/
	
	public static StyleOperation SPACING = new StyleOperation("spacing") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof Spacable) )
				return;
			
			Spacable t = (Spacable)node;
			float dest1 = (float)ParseUtil.toNumber(value.get(0).get(0));
			float source1 = (float) t.getSpacing();
			
			// Transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( dest1 == source1 || transition == null ) {
				t.setSpacing(dest1);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						t.setSpacing(tween(source1, dest1, progress));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	/*public static StyleOperation SCROLLBAR_WIDTH = new StyleOperation("scrollbar-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof ScrollPane) )
				return;
			
			ScrollPane t = (ScrollPane)node;
			float dest1 = (float)ParseUtil.toNumber(value.get(0).get(0));
			float source1 = (float) t.getScrollBarThickness();
			
			// Transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( dest1 == source1 || transition == null ) {
				t.setScrollBarThickness(dest1);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						t.setScrollBarThickness(tween(source1, dest1, progress));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};*/
	
	/*public static StyleOperation DIVIDER_WIDTH = new StyleOperation("divider-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof SplitPane) )
				return;
			
			SplitPane t = (SplitPane)node;
			float dest1 = (float)ParseUtil.toNumber(value.get(0).get(0));
			float source1 = (float) t.getDividerThickness();
			
			// Transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( dest1 == source1 || transition == null ) {
				t.setDividerThickness(dest1);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						t.setDividerThickness(tween(source1, dest1, progress));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};*/
	
	/*public static StyleOperation POINTER_EVENTS = new StyleOperation("pointer-events") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			String evts = value.get(0).get(0).toString().toUpperCase();
			if ( evts.equals(ALL) )
				node.setMouseTransparent(false);
			else if ( evts.equals(NONE) )
				node.setMouseTransparent(true);
		}
	};*/
	
	public static StyleOperation FONT_SIZE = new StyleOperation("font-size") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof Labeled) )
				return;
			
			Labeled t = (Labeled)node;
			float destSize = (float)ParseUtil.toNumber(value.get(0).get(0));
			float sourceSize = (float) t.getFont().getSize();
			
			// Font size transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destSize == sourceSize || transition == null ) {
				t.setFont(new Font(t.getFont().getFamily(), destSize));
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						t.setFont(new Font(t.getFont().getFamily(), tween(sourceSize, destSize, progress)));
					}
				};
				
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation COLOR = new StyleOperation("color") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof Labeled) )
				return;
			
			Labeled t = (Labeled)node;
			Color destColor = getColor(value.get(0).get(0));
			Color sourceColor = t.getTextFill();
			
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( transition == null || sourceColor == null ) {
				t.setTextFill(destColor);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Color fillColor = new Color(sourceColor);
				
				// Color transition
				FillTransition tran = new FillTransition(transition.getDurationMillis(), sourceColor, destColor, fillColor);
				tran.play();
				current.add(tran);

				// Apply fill color
				t.setTextFill(fillColor);
			}
		}
	};

	public static StyleOperation match(String key) {
		return operations.get(key);
	}

	protected static float tween(double value1, double value2, double progress) {
		return (float) (value1 + (value2-value1)*progress);
	}
	
	protected static Background getBackground(Object arg) {
		
		if ( arg instanceof StyleFunctionValue ) {
			StyleFunctionValue func = (StyleFunctionValue)arg;
			StyleFunction func2 = func.function;

			if ( func2.getName().equals("linear-gradient") ) {
				// Must have at least 2 args!
				if ( func.args.size() < 2 )
					return null;
				
				// Get direction
				float direction = 90;
				int a = 0;
				boolean firstArgIsDirection = false;
				if ( ParseUtil.isNumber(func.args.get(0)) ) {
					firstArgIsDirection = true;
					direction = ParseUtil.toNumber(func.args.get(0)) - 90;
					a++;
				}
				
				// Must have 2 colors
				int amtColors = func.args.size() - (firstArgIsDirection?1:0);
				if ( amtColors < 2 )
					return null;

				ColorStop[] colors = new ColorStop[amtColors];
				int t = 0;

				// Parse exact color stops first
				for (int i = a; i < func.args.size(); i++) {
					Object arg1 = func.args.get(i);
					ColorStop stop = null;
					
					if ( arg1.toString().contains("%") )
						stop = parseColorStop(arg1);
					
					if ( stop == null )
						continue;
					
					colors[i-a] = stop;
					t++;
				}
				
				// Make sure first color stop is defined
				if ( colors[0] == null )
					colors[0] = parseColorStop(func.args.get(a) + " 0%");
				
				// Make sure last color stop is defined
				if ( colors[colors.length-1] == null )
					colors[colors.length-1] = parseColorStop(func.args.get(func.args.size()-1) + " 100%");
				
				// Parse non precomputed stops... (Does not contain %)
				if ( t != colors.length ) {
					ColorStop[] colorsFinal = new ColorStop[amtColors];
					int leftMost = 0;
					int rightMost = -1; // Most likely will end up being the last color

					for (int i = 0; i < colors.length; i++) {
						ColorStop tempStop = colors[i];
						
						// Define left most stop if it's not null
						if ( tempStop != null ) {
							leftMost = i;
							if ( leftMost == rightMost )
								rightMost = -1;
							
							colorsFinal[i] = colors[i];
						}
						
						if ( tempStop == null ) {
							
							Object arg1 = func.args.get(i+a);
							Color color = getColor(arg1);
							
							// Search for right most stop
							if ( rightMost == -1 ) {
								for (int j = i; j < colors.length; j++) {
									ColorStop aaaa = colors[j];
									if ( aaaa != null ) {
										rightMost = j;
									}
								}
							}
							
							// Compute color stop with our own percent
							float lowerRatio = colors[leftMost].getRatio();
							float higherRatio = colors[rightMost].getRatio();
							float percent = lowerRatio + ((i-leftMost) / (float)(rightMost-leftMost))*(higherRatio-lowerRatio);
							ColorStop stop = new ColorStop(color, percent);
							
							// Store to final
							colorsFinal[i] = stop;
						}
					}
					
					// Overwrite colors
					colors = colorsFinal;
				}
				
				// Return gradient
				return new BackgroundLinearGradient(direction, colors);
			}
		}
		
		return null;
	}

	private static ColorStop parseColorStop(Object arg) {
		String[] split = arg.toString().split(" ");
		if ( split.length != 2 )
			return null;
		
		double percent = ParseUtil.toNumber(split[1].replace("%", "")) / 100d;
		return new ColorStop(getColor(split[0]), (float)percent);
	}

	protected static Color getColor(Object arg) {
		if ( arg == null )
			return Color.WHITE;
		
		// Function can resolve directly to color
		if ( arg instanceof Color )
			return (Color)arg;
		
		// Colors can be defined with a string
		String string = arg.toString();
		
		// Color by hex string
		if ( string.startsWith("#") ) {
			try {
				return new Color(string);
			} catch(Exception e) {
				//
			}
		}

		// Color by name
		Color color = Color.match(string);
		if ( color != null )
			return new Color(color);
		
		// Fallback
		return Color.PINK;
	}
}
