package io.jadefx.scene.layout;

import io.jadefx.collections.ObservableList;
import io.jadefx.geometry.Pos;
import io.jadefx.scene.Node;
import io.jadefx.style.CalcOperation;
import io.jadefx.style.Percentage;
import io.jadefx.style.PercentageCalc;

public class BorderPane extends Pane {
	private VBox vbox;
	private HBox hbox;
	
	private Node center;
	private Node top;
	private Node bottom;
	private Node left;
	private Node right;
	
	private Node centerPane;
	
	private double spacing;
	
	public BorderPane() {
		this.vbox = new VBox();
		this.hbox = new HBox();
		
		this.hbox.setAlignment(Pos.CENTER);
		this.vbox.setAlignment(Pos.CENTER);

		this.vbox.setPrefWidthRatio(Percentage.ONE_HUNDRED);
		this.vbox.setPrefHeightRatio(Percentage.ONE_HUNDRED);
		this.children.add(vbox);

		this.hbox.setPrefWidthRatio(Percentage.ONE_HUNDRED);
	}
	
	public void setCenter(Node node) {
		this.center = node;
		this.update();
	}

	public Node getCenter() {
		return this.center;
	}

	public void setTop(Node node) {
		this.top = node;
		this.update();
	}
	
	public Node getTop() {
		return this.top;
	}

	public void setBottom(Node node) {
		this.bottom = node;
		this.update();
	}
	
	public Node getBottom() {
		return this.bottom;
	}

	public void setLeft(Node node) {
		this.left = node;
		this.update();
	}
	
	public Node getLeft() {
		return this.left;
	}

	public void setRight(Node node) {
		this.right = node;
		this.update();
	}
	
	public Node getRight() {
		return this.right;
	}
	
	private void update() {
		this.hbox.getChildren().clear();
		this.vbox.getChildren().clear();
		
		this.hbox.setSpacing(spacing);
		this.vbox.setSpacing(spacing);
		
		// Top
		if ( top != null )
			this.vbox.getChildren().add(top);
		
		// Center box
		this.vbox.getChildren().add(this.hbox);
		
		// Left side
		if ( left != null )
			this.hbox.getChildren().add(sp(left));
		
		// Center
		this.hbox.getChildren().add(centerPane = sp(center));
		
		// Right side
		if ( right != null )
			this.hbox.getChildren().add(sp(right));
		
		// Bottom
		if ( bottom != null )
			this.vbox.getChildren().add(bottom);
		
		size();
	}
	
	private Pane sp(Node node) {
		Pane ret = new Pane();
		//ret.setBackgroundLegacy(Color.fromHSB((float)new Random().nextFloat(), 1, 1));
		ret.setPrefHeightRatio(Percentage.ONE_HUNDRED);
		ret.setAlignment(Pos.ANCESTOR);
		if ( node != null ) {
			ret.getChildren().add(node);
			ret.setPrefWidth(node.getPrefWidth());
		}
		//ret.setBackgroundLegacy(null);
		ret.setStyle("background-color:transparent");
		
		return ret;
	}
	
	@Override
	protected void size() {
		super.size();
		
		float topBottomHeight = 0;
		if ( top != null )
			topBottomHeight += top.getHeight();
		if ( bottom != null )
			topBottomHeight += bottom.getHeight();
		
		this.hbox.setPrefHeightRatio(new PercentageCalc(Percentage.ONE_HUNDRED, topBottomHeight, CalcOperation.SUBTRACT));
		this.hbox.setMaxHeightRatio(this.hbox.getPrefHeightRatio());

		float leftRightWidth = 0;
		if ( left != null )
			leftRightWidth += left.getWidth();
		if ( right != null )
			leftRightWidth += right.getWidth();
		if ( this.centerPane != null ) {
			this.centerPane.setPrefWidthRatio(new PercentageCalc(Percentage.ONE_HUNDRED, leftRightWidth, CalcOperation.SUBTRACT));
			this.centerPane.setMaxWidthRatio(this.centerPane.getPrefWidthRatio());
		}
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
	}
	
	@Override
	public ObservableList<Node> getChildren() {
		return new ObservableList<>(this.children);
	}
}
