package blink.cli;

import blink.Gem;
import blink.GemExhaustiveSimplifier;
import blink.GemPackedLabelling;
import blink.GemSimplificationPathFinder;
import blink.Path;

import java.util.ArrayList;

class FunctionSimplify extends Function {
    public FunctionSimplify() {
        super("simplify", "Find the best attractor for a gem using a time limit.");
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

    private Object hardwork(ArrayList<Object> params, DataMap localData) throws EvaluationException, Exception {
        if(params.get(0) instanceof Gem) {
            Gem gem = (Gem) params.get(0);
            int max_time = 3000;
            if(params.size() == 2 && params.get(1) instanceof Number) {
            	max_time = ((Integer) params.get(1));
            }
            GemExhaustiveSimplifier exhaustive_simplifier = 
				new GemExhaustiveSimplifier(gem, max_time);

		    Gem gem_att = exhaustive_simplifier.getBestAttractorFound();
            return gem_att;
        } else {
            throw new EvaluationException("Wrong parameters");
        }
    }
}
