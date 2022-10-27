package io.jadefx.scene.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HtmlElement {
	private List<HtmlElement> children = new ArrayList<>();
	private Map<String, HtmlElement> descendants = new HashMap<>();
	private HtmlElement parent;
	private String id;
	
	public void addChild(HtmlElement child) {
		children.add(child);
		child.parent = this;
		// TODO call child add event
	}
	
	public void removeChild(HtmlElement child) {
		children.remove(child);
		child.parent  = null;
		// TODO call child remove event
	}
	
	public void setId(String id) {
		this.id = id;
		// TODO propagate to parents descendants
	}
	
	public String getId() {
		return this.id;
	}
	
	public HtmlElement getParent() {
		return this.parent;
	}
	
	/**
	 * Returns the element which is having an ID attribute with the specific/specified value.
	 */
	public HtmlElement getElementById(String id) {
		return descendants.get(id);
	}
	
	public abstract String getTag();
}
