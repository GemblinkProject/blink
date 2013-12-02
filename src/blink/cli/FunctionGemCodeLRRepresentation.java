package blink.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import blink.Gem;
import blink.GemColor;
import blink.GemVertex;

public class FunctionGemCodeLRRepresentation extends Function {
	public FunctionGemCodeLRRepresentation () {
		super("gemLR","description");
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
		if (params.size() == 3) {
			Gem G = (Gem) params.get(0);
    		
			GemColor missedColor = GemColor.getByNumber(((Number)params.get(1)).intValue());
    		GemColor smallestColor = GemColor.getByNumber(((Number)params.get(2)).intValue());
			
			ArrayList<GemVertex> gemOddVertexList = G.getOddVertices(); //DEBUG getVertex
			ArrayList<GemVertex> gemAllVertexList = G.getOddVertices(); //DEBUG getVertex
			
			HashMap<GemVertex, Integer> edgeMark = new HashMap<GemVertex, Integer>(); //DEBUG Vertices marcados
    		
			for (GemVertex v : gemAllVertexList) {
     	  		edgeMark.put(v, new Integer(0));
    		}
			
			int numEdges = 3 * G.getNumVertices();
			
			GemVertex[] vertexQuee = new GemVertex[numEdges];
			
			for (GemVertex v : gemOddVertexList) {
     	  		
    		}
    		
    		
		}
		return null; //BUGBUG
	}
	
	
	private HashMap<GemVertex, GemColor> breadFirstSearch(Gem gem, GemVertex startVertex, GemColor missingColor, GemColor smallestColor)
	{
		HashMap<HashMap<GemVertex, GemColor>, Boolean>  edgeMarked = new HashMap<HashMap<GemVertex, GemColor>, Boolean>();
		HashMap<GemVertex, GemColor> vertexColor = new HashMap<GemVertex, GemColor>();
		
		HashMap<GemVertex, Integer> vertexDistance = new HashMap<GemVertex, Integer>();
		
		
		GemVertex[] queue = new GemVertex[gem.getNumVertices()];
		int queueIndex = 0;
		
		GemVertex current = startVertex;
		GemColor[] listColors = GemColor.getComplementColors(missingColor);
		
		queue[queueIndex++] = current.getNeighbour(smallestColor);
		vertexColor.put(current, smallestColor);
		
		while(queueIndex > 0)
		{
			current = queue[queueIndex--];
			if(vertexDistance.containsKey(current)) // if why?
			{
				for(GemColor cor : listColors)
				{
					if(cor != smallestColor) //se já estiver markado
					{
						if(vertexDistance.get(current.getNeighbour(cor)).intValue() >
							vertexDistance.get(current).intValue() + 1)
						{
							vertexDistance.put(current.getNeighbour(cor)
									,vertexDistance.get(current).intValue() + 1);
							
							vertexColor.put(current.getNeighbour(cor), cor);
							
							HashMap<GemVertex, GemColor> toInsert = new HashMap<GemVertex, GemColor>();
							toInsert.put(current, cor);
							
							edgeMarked.put(toInsert, true);
						}
					}
				}
			}
			else
			{
			
			}
		}
		return vertexColor;
	}
}

