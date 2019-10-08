package com.flash.message.tabooword.core;

import it.unimi.dsi.fastutil.chars.Char2ObjectAVLTreeMap;

/**
 * 字典树节点
 *
 * @author chengli
 */
public class Node {

    private char c;
    private int count;
    private boolean isVisited;
    private boolean isLeaf;
    private boolean isRoot;
    private Node parent;
    Char2ObjectAVLTreeMap<Node> children = new Char2ObjectAVLTreeMap<>();

    Node() {
        setCount(0);
        setVisited(false);
    }

    Node(char c) {
        this();
        setC(c);
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
