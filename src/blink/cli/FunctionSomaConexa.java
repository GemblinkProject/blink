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
    
    class HardworkGraph {
        ArrayList<Gem> gems;
        int ngems;
        boolean[] inverse;
        boolean[] mark;
        ArrayList<ArrayList<Integer>> adj_i = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> adj_s = new ArrayList<ArrayList<Integer>>();
        
        HardworkGraph(ArrayList<Gem> gems, int[] L) throws EvaluationException {
            this.gems = gems;
            ngems = gems.size();
            inverse = new boolean[ngems];
            mark = new boolean[ngems];
            for (int i = 0; i < ngems; ++i) {
                adj_i.add(new ArrayList<Integer>());
                adj_s.add(new ArrayList<Integer>());
            }
            
            for (int i = 0; i < L.length; i += 4) {
                if ((L[i]^1) == (L[i+2]^1)) {
                    adj_i.get(L[i+1]).add(new Integer(L[i+3]));
                    adj_i.get(L[i+3]).add(new Integer(L[i+1]));
                    inverse[L[i+3]] = !inverse[L[i+1]];
                } else {
                    adj_s.get(L[i+1]).add(new Integer(L[i+3]));
                    adj_s.get(L[i+3]).add(new Integer(L[i+1]));
                    inverse[L[i+3]] = inverse[L[i+1]];
                }
                for (int j = 0; j < ngems; ++j) {
                    mark[j] = false;
                }
                correct(L[i+3]);
            }
            
            for (int i = 0; i < ngems; ++i) {
                if (inverse[i]) {
                    gems.get(i).swapColors(GemColor.getByNumber(1), GemColor.getByNumber(2));
                }
            }
        }
        
        void correct(int g) throws EvaluationException {
            if (mark[g]) {
                throw new EvaluationException("Impossible to keep bipartition");
            }
            mark[g] = true;
            for (int i = 0; i < adj_i.get(g).size(); ++i) {
                int h = adj_i.get(g).get(i);
                if (inverse[g] == inverse[h]) {
                    inverse[h] = !inverse[g];
                    correct(h);
                }
            }
            for (int i = 0; i < adj_s.get(g).size(); ++i) {
                int h = adj_s.get(g).get(i);
                if (inverse[g] != inverse[h]) {
                    inverse[h] = inverse[g];
                    correct(h);
                }
            }
        }
        
        
        
        
    }

    public Object hardwork(ArrayList params, DataMap localMap) throws EvaluationException, Exception {
        Gem G = new Gem();
        GemVertex[] V;
        ArrayList<Gem> gems = new ArrayList<Gem>();
        int nparams = params.size();
        for (int i = 0; i < nparams && params.get(i) instanceof Gem; i += 2) {
            Gem g = (Gem)params.get(i);
            g = g.copy();
            int c0 = (Integer) params.get(i+1);
            if (c0 != 0) {
                g.swapColors(GemColor.getByNumber(0), GemColor.getByNumber(c0));
                int c1 = 0, c2 = 0;
                for (int j = 1; j < 4 ; ++j) {
                    if (j != c0 && c1 == 0) c1 = j;
                    else if (j != c0) c2 = j;
                }
                g.swapColors(GemColor.getByNumber(c1), GemColor.getByNumber(c2));
            }
            gems.add(g);
        } 
        int ngems = gems.size();
        int[] L = new int[nparams - ngems*2];
        if (L.length % 4 != 0) {
            throw new EvaluationException("You should pass a even number of pars label, gem_index");
        }
        V = new GemVertex[L.length/2];
        for (int i = ngems*2; i < nparams; ++i) {
            L[i-ngems*2] = (Integer) params.get(i);
        }
        
        // Graph
        new HardworkGraph(gems, L);
        
        for (int i = 0 ; i < ngems; ++i ){
            for (GemVertex v: gems.get(i).getVertices()) {
                boolean oneOfThem = false;
                for (int j = 0; j < L.length; j+=2) {
        	        if (L[j+1] == i && v.getLabel() == L[j]) {
        	            V[j/2] = v;
        	            oneOfThem = true;
        	            break;
        	        }
        	    }
        	    if (!oneOfThem) {
        	        G.addVertex(v);
        	    }
            }
        }
        
        for (int i = 0; i < V.length; ++i) {
            if (V[i] == null) {
                throw new EvaluationException("Wrong or repeated labels");
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
