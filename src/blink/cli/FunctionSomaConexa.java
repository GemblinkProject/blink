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
        if(params.get(0) instanceof Gem && params.get(1) instanceof Gem && params.get(2) instanceof Number && params.get(3) instanceof Number){
        	
        	Gem firstGemIn;
        	Gem secondGemIn;
        	
        	Gem gemResult;
        	Gem G2;
        	
        	
        	int firstVertexIn;
        	int secondVertexIn;
        	
        	

        	
        	if(((Gem) params.get(0)).getVertices().size() < ((Gem) params.get(1)).getVertices().size())
        	{
	        	firstGemIn = (Gem) params.get(0);
	        	secondGemIn = (Gem) params.get(1);
	        	firstVertexIn = (Integer) params.get(2);
	        	secondVertexIn = (Integer) params.get(3);
        	}else
        	{
        		firstGemIn = (Gem) params.get(1);
	        	secondGemIn = (Gem) params.get(0);
	        	firstVertexIn = (Integer) params.get(3);
	        	secondVertexIn = (Integer) params.get(2);
        		
        	}
        	
        	
        	int FIRSTGEMLENGTH = firstGemIn.getNumVertices()-1;
        	
        	int L2 = (Integer) secondVertexIn+FIRSTGEMLENGTH-1;
        	
        	int dif1 = firstGemIn.getNumVertices() - firstVertexIn;
        	int dif2 = secondVertexIn - 1;
        	
        	gemResult = firstGemIn.copy();
        	G2 = secondGemIn.copy();
        	
        	
        	for(int i = 1; i <= firstGemIn.getNumVertices(); i++)
        	{
        		int newlabel = firstGemIn.getVertex(i).getLabel();
        		newlabel += dif1;
        		
        		if(newlabel > firstGemIn.getNumVertices()) 
    			{
        			newlabel -= firstGemIn.getNumVertices();
    			}
        		gemResult.getVertex(i).setLabel(newlabel);
        	}
        	
        	
        	while(gemResult.getVertex(gemResult.getNumVertices()).getLabel() != gemResult.getNumVertices())
        	{
        		GemVertex vtemp = gemResult.removerVertice(1);
        		gemResult.inserirVertice(vtemp);
        	}
        	
        	for(int i = 1; i <= G2.getNumVertices(); i++)
        	{
        		int newlabel = G2.getVertex(i).getLabel();
        		newlabel -= dif2;
        		if(newlabel < 1 ) newlabel += G2.getNumVertices();
        		G2.getVertex(i).setLabel(newlabel);
        	}
        	
        	while(G2.getVertex(1).getLabel() != 1)
        	{
        		GemVertex vtemp = G2.removerVertice(1);
        		G2.inserirVertice(vtemp);
        	}

        	for(int i = 1; i <= G2.getNumVertices(); i++)
        	{        	
        		G2.getVertex(i).setLabel(FIRSTGEMLENGTH++);
        	}
        	
        	GemVertex u = gemResult.getVertex(gemResult.getNumVertices());
        	GemVertex v = G2.getVertex(1);
        	
        	GemVertex t1;
        	GemVertex t2;
        	
        	
        	t1 = u.getYellow(); 
        	t2 = v.getYellow();
        	
        	t1.setYellow(t2);
        	t2.setYellow(t1);
        	
        	t1 = u.getBlue(); 
        	t2 = v.getBlue();
        	
        	t1.setBlue(t2);
        	t2.setBlue(t1);
        	
        	t1 = u.getRed(); 
        	t2 = v.getRed();
        	
        	t1.setRed(t2);
        	t2.setRed(t1);
        	
        	t1 = u.getGreen(); 
        	t2 = v.getGreen();
        	
        	t1.setGreen(t2);
        	t2.setGreen(t1);
        	
        	gemResult.removerVertice(gemResult.getNumVertices());
        	
        	for(int i = 2; i <= G2.getNumVertices(); i++)
        	{
        		gemResult.inserirVertice(G2.getVertex(i));
        	}
        	
        	gemResult.goToCodeLabel().getCode();
        	return gemResult; 
        	//return G3; //BUGBUG
        	
        }
        return null;
    }
}
