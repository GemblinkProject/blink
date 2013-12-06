package blink.cli;
import blink.GBlink;
import blink.GemColor;
import blink.Gem;
import blink.GemVertex;
import blink.PlanarGraph;

import java.util.ArrayList;
import java.util.Random;
//-> src/blink/cli/CommandLineInterface.java -> 128 -> , new Fun...

/**
 * <p>
 * A {@link CommandLineInterface} command that generates a random
 * {@link GBlink} with 8 times the number given as parameter.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class FunctionRandomGemFromWheel extends Function {
	public FunctionRandomGemFromWheel() {
		super("randwg","Generate random Gem from a wheel graph with N+1 vertexes exploding vertexes with degree greater than V and faces with degree greater than F");
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
		int N, V, F;
		Random rand;
		if( params.size() >= 1 && params.get(0) instanceof Number ) {
			N = ((Number)params.get(0)).intValue();
  			if( params.size() >= 2 && params.get(1) instanceof Number ) {
	    	    V = ((Number)params.get(1)).intValue();
	    	    if( params.size() >= 3 && params.get(2) instanceof Number ) {
	    	        F = ((Number)params.get(2)).intValue();
	    	    } else if (params.size() == 2) {
	    	        F = V;
	    	    } else throw new EvaluationException("You can pass N[, V[, F]] as parameters");
	    	} else {
	    	    V = F = 5;
	    	}
		} else throw new EvaluationException("You have to pass N as parameter");
		
		if( params.size() == 4 && params.get(3) instanceof Number) {
			rand = new Random(((Number)params.get(3)).intValue());
		} else if( params.size() < 4 ) {
			rand = new Random(System.currentTimeMillis());
		} else throw new EvaluationException("You can pass N[, V[, F[, seed]]] as parameters");
		
		return PlanarGraph.newRandFromWheel(N, V, F, rand).psi();
	}
}
