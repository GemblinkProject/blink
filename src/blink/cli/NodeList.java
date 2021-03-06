//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : Function.java
//  @ Date : 10/30/2006
//  @ Author :
//
//
package blink.cli;

import java.util.ArrayList;

public class NodeList extends Node {

    private ArrayList<Node> _childs;

    public NodeList() {
        _childs = new ArrayList<Node>();
    }

    public Object evaluate() throws EvaluationException {
        return CommandLineInterface.getInstance().evaluateNodeList(this);
    }

    public void addChild(Node node) {
        _childs.add(node);
    }

    public int getNumberOfChilds() {
        return _childs.size();
    }

    public Node getChild(int index) {
        return _childs.get(index);
    }

    public ArrayList<Node> getChilds() {
        return _childs;
    }
}
