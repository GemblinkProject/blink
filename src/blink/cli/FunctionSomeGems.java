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
 
public class FunctionSomeGems {

}

class FunctionRandomGemFromWheel extends Function {
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

class FunctionFibonacciGem extends Function {
	public FunctionFibonacciGem() {
		super("fibgem","Generate a Fibonacci Gem with 4(N+1) vertexes");
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
		int N;
		if( params.size() == 1 && params.get(0) instanceof Number ) {
			N = ((Number)params.get(0)).intValue();
			if (N < 1) throw new EvaluationException("N is expected to be >= 1");
			int M = 4*(N+1);
			GemVertex[] gemVertexes = new GemVertex[M];
			
			for (int i = 0; i < M; ++i) {
				gemVertexes[i] = new GemVertex(i+1);
			}
			
			for (int i = 0; i < 8; i += 2) {
				int j = 7-i;
				gemVertexes[i].setNeighbour(gemVertexes[j], GemColor.blue);
				gemVertexes[j].setNeighbour(gemVertexes[i], GemColor.blue);
				
				j = i/4*4 + (i+1)%4;
				gemVertexes[i].setNeighbour(gemVertexes[j], GemColor.red);
				gemVertexes[j].setNeighbour(gemVertexes[i], GemColor.red);
				
				j = i/4*4 + (i+3)%4;
				gemVertexes[i].setNeighbour(gemVertexes[j], GemColor.green);
				gemVertexes[j].setNeighbour(gemVertexes[i], GemColor.green);
			}
			int nextColorNumber = 1;
			
			for (int count = 1; count < N; ++count) {
				GemColor color1 = GemColor.getByNumber(nextColorNumber);
				GemColor color2 = GemColor.getByNumber(nextColorNumber%3+1);
				GemColor color3 = GemColor.getByNumber((nextColorNumber+1)%3+1);
				nextColorNumber = (nextColorNumber+1)%3+1;
				
				GemVertex oldA = gemVertexes[count*4+0], oldB = gemVertexes[count*4+1];
				GemVertex oldC = gemVertexes[count*4+2], oldD = gemVertexes[count*4+3];
				GemVertex newA = gemVertexes[count*4+4], newB = gemVertexes[count*4+5];
				GemVertex newC = gemVertexes[count*4+6], newD = gemVertexes[count*4+7];
				
				newA.setNeighbour(newB, color1);
				newB.setNeighbour(newA, color1);
				newC.setNeighbour(newD, color1);
				newD.setNeighbour(newC, color1);
				
				newA.setNeighbour(newD, color2);
				newD.setNeighbour(newA, color2);
				newC.setNeighbour(newB, color2);
				newB.setNeighbour(newC, color2);
				
				newA.setNeighbour(oldD, color3);
				oldD.setNeighbour(newA, color3);
				newB.setNeighbour(oldA, color3);
				oldA.setNeighbour(newB, color3);
				newC.setNeighbour(oldB, color3);
				oldB.setNeighbour(newC, color3);
				newD.setNeighbour(oldC, color3);
				oldC.setNeighbour(newD, color3);
			}
			
			Gem gem = new Gem();
			for (int i = 0; i < M; i += 2) {
				GemVertex a = gemVertexes[i], b = a.getNeighbour(GemColor.blue).getNeighbour(GemColor.green).getNeighbour(GemColor.red);
				a.setNeighbour(b, GemColor.yellow);
				b.setNeighbour(a, GemColor.yellow);
				
				b.setLabel(i+2);
				gem.addVertex(a);
				gem.addVertex(b);
			}
			
			return gem;
		}
		
		throw new EvaluationException("You have to pass N as parameter");
	}
}
