package io.jadefx.scene;

import io.jadefx.collections.ObservableList;

public abstract class Parent extends Node {
    /**
     * Gets the list of children of this {@code Parent}.
     * @return the list of children of this {@code Parent}.
     */
    protected ObservableList<Node> getChildren() {
        return children;
    }

    /**
     * Gets the list of children of this {@code Parent} as a read-only list.
     * @return read-only access to this parent's children ObservableList
     */
    public ObservableList<Node> getChildrenUnmodifyable() {
        return new ObservableList<Node>(super.getChildren());
    }
}
