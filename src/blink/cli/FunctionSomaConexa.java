package blink.cli;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import blink.App;
import blink.DrawPanel;
import blink.GBlink;
import blink.Gem;
import blink.GemColor;
import blink.GemVertex;
import blink.MapD;
import blink.PanelBlinkViewer;
import blink.PanelGemViewer;
import blink.PanelMapViewer;

/**
 * <p>
 * A {@link CommandLineInterface} command that draws a {@link Gem}
 * or {@link GBlink}.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * @author Lauro Didier Lins
 * @version 1.0
 */
public class FunctionSomaConexa extends Function {
    public FunctionSomaConexa() {
        super("consum","somaConexa");
    }

    public Object evaluate(ArrayList<Object> params, DataMap localData) throws EvaluationException {
        try {
            Object result = hardwork(params, localData);
            return result;
        } catch (EvaluationException ex) {
            ex.printStackTrace();
            throw ex;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new EvaluationException(e.getMessage());
        }
    }

    public Object hardwork(ArrayList params, DataMap localMap) throws EvaluationException, Exception {
        Gem G = new Gem();
        GemVertex v1 = null, v2 = null;
        if(params.get(0) instanceof Gem && params.get(1) instanceof Gem && params.get(2) instanceof Number && params.get(3) instanceof Number) {
        	Gem G3 = (Gem) params.get(0);
        	Gem G4 = (Gem) params.get(1);
        	Gem G1 = G3.copy();
        	Gem G2 = G4.copy();
        	int novo = G1.getNumVertices()-1;
        	int L1 = (Integer) params.get(2);
        	int L2 = (Integer) params.get(3);
        	for (GemVertex v: G1.getVertices()) {
        	    if (v.getLabel() != L1) {
        	        if (v.getLabel() > L1) {
        	            v.setLabel(v.getLabel()-1);
        	        }
        	        G.addVertex(v);
        	    } else {
        	        v1 = v;
        	    }
        	}
        	for (GemVertex v: G2.getVertices()) {
        	    if (v.getLabel() != L2) {
        	        if (v.getLabel() > L2) {
        	            v.setLabel(v.getLabel()-1 + novo);
        	        } else {
        	            v.setLabel(v.getLabel() + novo);
        	        }
        	        G.addVertex(v);
        	    } else {
        	        v2 = v;
        	    }
        	}
        } else if(params.get(0) instanceof Gem && params.get(1) instanceof Number && params.get(2) instanceof Number) {
        	Gem G2 = (Gem) params.get(0);
        	Gem G1 = G2.copy();
        	int L1 = (Integer) params.get(1);
        	int L2 = (Integer) params.get(2);
        	if (L1 > L2) {
        	    int tmp = L1;
        	    L1 = L2;
        	    L2 = tmp;
        	}
        	for (GemVertex v: G1.getVertices()) {
        	    if (v.getLabel() < L1) {
        	        G.addVertex(v);
        	    } else if (v.getLabel() == L1) {
        	        v1 = v;
        	    } else if (v.getLabel() < L2) {
        	        v.setLabel(v.getLabel()-1);
        	        G.addVertex(v);
        	    } else if (v.getLabel() == L2) {
        	        v2 = v;
        	    } else {
        	        v.setLabel(v.getLabel()-2);
        	        G.addVertex(v);
        	    }
        	}
        } else {
            throw new EvaluationException("You should pass: gem G1[, gem G2], int L1, int L2");
        }
        if (v1 == null || v2 == null) {
            throw new EvaluationException("Wrong labels");
        }
        
        for (GemColor c: GemColor.values()) {
            GemVertex n1 = v1.getNeighbour(c);
            GemVertex n2 = v2.getNeighbour(c);
            n1.setNeighbour(n2, c);
            n2.setNeighbour(n1, c);
        }
        
        G.goToCodeLabel();
        return G;
    }
}
