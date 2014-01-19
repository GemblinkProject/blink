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
        GemVertex[] V;
        if(params.get(0) instanceof Gem && params.get(1) instanceof Gem) {
            int[] L = new int[params.size()-2];
            if (L.length % 2 != 0) {
                throw new EvaluationException("You should pass a even number of labels");
            }
            V = new GemVertex[L.length];
            for (int i = 2; i < params.size(); ++i) {
                L[i-2] = (Integer) params.get(i);
            }
        	Gem G1 = ((Gem) params.get(0)).copy();
        	Gem G2 = ((Gem) params.get(1)).copy();
        	int novo = G1.getNumVertices()-L.length/2;
        	
        	for (GemVertex v: G1.getVertices()) {
        	    int labelDec = 0;
        	    for (int i = 0; i < L.length; i+=2) {
        	        if (v.getLabel() == L[i]) {
        	            V[i] = v;
        	            labelDec = -1;
        	            break;
        	        }
        	        if (v.getLabel() > L[i]) {
        	            ++labelDec;
        	        }
        	    }
        	    if (labelDec != -1) {
        	        v.setLabel(v.getLabel()-labelDec);
        	        G.addVertex(v);
        	    }
        	}
        	for (GemVertex v: G2.getVertices()) {
        	    int labelDec = 0;
        	    for (int i = 1; i < L.length; i+=2) {
        	        if (v.getLabel() == L[i]) {
        	            V[i] = v;
        	            labelDec = -1;
        	            break;
        	        }
        	        if (v.getLabel() > L[i]) {
        	            ++labelDec;
        	        }
        	    }
        	    if (labelDec != -1) {
        	        v.setLabel(v.getLabel()-labelDec+novo);
        	        G.addVertex(v);
        	    }
        	}
        } else if(params.get(0) instanceof Gem) {
            int[] L = new int[params.size()-1];
            if (L.length % 2 != 0) {
                throw new EvaluationException("You should pass a even number of labels");
            }
            for (int i = 1; i < params.size(); ++i) {
                L[i-1] = (Integer) params.get(i);
            }
            V = new GemVertex[L.length];
        	Gem G1 = ((Gem) params.get(0)).copy();
        	for (GemVertex v: G1.getVertices()) {
        	    int labelDec = 0;
        	    for (int i = 0; i < L.length; ++i) {
        	        if (v.getLabel() == L[i]) {
        	            V[i] = v;
        	            labelDec = -1;
        	            break;
        	        }
        	        if (v.getLabel() > L[i]) {
        	            ++labelDec;
        	        }
        	    }
        	    if (labelDec != -1) {
        	        v.setLabel(v.getLabel()-labelDec);
        	        G.addVertex(v);
        	    }
        	}
        } else {
            throw new EvaluationException("You should pass: gem G1[, gem G2], int I1, int J1, ...");
        }
        
        for (int i = 0; i < V.length; ++i) {
            if (V[i] == null) {
                throw new EvaluationException("Wrong labels");
            }
            for (int j = i+1; j < V.length; ++j) {
                if (V[i] == V[j]) {
                    throw new EvaluationException("All labels has to be different");
                }
            }
        }
        
        for (int i = 0; i < V.length; i+=2) {
            for (GemColor c: GemColor.values()) {
                GemVertex n1 = V[i].getNeighbour(c);
                GemVertex n2 = V[i+1].getNeighbour(c);
                n1.setNeighbour(n2, c);
                n2.setNeighbour(n1, c);
            }
        }
        
        G.goToCodeLabel();
        return G;
    }
}
