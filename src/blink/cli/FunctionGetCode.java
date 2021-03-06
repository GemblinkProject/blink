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
public class FunctionGetCode extends Function {
    public FunctionGetCode() {
        super("getcode","gerar codigo canonico");
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
        if(params.get(0) instanceof Gem ){
        	Gem G2 = (Gem) params.get(0);
        	Gem G1 = G2.copy();
        	G1.goToCodeLabel().getCode();
        	return G1;
        	
        }
        return null;
    }
}

class FunctionCanNumCode extends Function {
    public FunctionCanNumCode() {
        super("cannumcode", "canonical numeric code of a gem");
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
        if(params.get(0) instanceof Gem ){
        	Gem G2 = (Gem) params.get(0);
        	Gem G1 = G2.copy();
        	G1.goToCodeLabel();
        	return G1.getNumCode();
        }
        return null;
    }
}

class FunctionCurrentNumCode extends Function {
    public FunctionCurrentNumCode() {
        super("currentnumcode", "current numeric code of a gem");
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
        if(params.get(0) instanceof Gem ){
        	Gem G2 = (Gem) params.get(0);
        	Gem G1 = G2.copy();
        	return G1.getNumCode();
        }
        return null;
    }
}

class FunctionGemFromNumCode extends Function {
    public FunctionGemFromNumCode() {
        super("gemfromnumcode", "create gem from canonical numeric code");
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
        if(params.get(0) instanceof String ){
        	return Gem.fromNumCode((String)params.get(0));
        } else {
            ArrayList<Integer> nums = new ArrayList<Integer>();
            for (Object param: params) {
                if (param instanceof Integer) {
                    nums.add((Integer)param);
                } else {
                    return null;
                }
            }
            int[] code = new int[nums.size()];
            for (int i = 0; i < code.length; ++i) {
                code[i] = nums.get(i).intValue();
            }
            return Gem.fromNumCode(code);
        }
    }
}
