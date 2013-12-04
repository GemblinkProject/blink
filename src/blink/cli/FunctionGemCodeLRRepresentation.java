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
    		
			GemColor missingColor = GemColor.getByNumber(((Number)params.get(1)).intValue());
    		GemColor smallestColor = GemColor.getByNumber(((Number)params.get(2)).intValue());
			
			ArrayList<GemVertex> gemOddVertexList = G.getOddVertices(); //DEBUG getVertex
			
			for(GemVertex startVertex : gemOddVertexList)
			{
				
				this.breadFirstSearch(G, startVertex, startVertex.getNeighbour(missingColor), missingColor, smallestColor);
			}
			
			
    		
    		
		}
		return null; //BUGBUG
	}
	
	//BUGBUG checar vertices marcados
	private HashMap<GemVertex, GemColor> breadFirstSearch(Gem gem, GemVertex startVertex, GemVertex endVertex, GemColor missingColor, GemColor smallestColor)
	{
		HashMap<GemVertex, Boolean>  vertexMarked = new HashMap<GemVertex, Boolean>();
		HashMap<GemVertex, GemColor> vertexColor = new HashMap<GemVertex, GemColor>();
		HashMap<GemVertex, String> vertexDirection = new HashMap<GemVertex, String>();
				
		
		GemVertex[] queue = new GemVertex[gem.getNumVertices()];
		int queueIndex = 0;
		
		GemVertex current = startVertex;
		ArrayList<GemColor> listColors = new ArrayList<GemColor>();
		
		for(GemColor cor : GemColor.getComplementColors(missingColor))
		{
			if(cor.getNumber() != smallestColor.getNumber())
			{
				listColors.add(cor);
			}
		}
		
		queue[queueIndex++] = current.getNeighbour(smallestColor);
		vertexColor.put(current, smallestColor);
		GemColor lastColor = smallestColor;
		
		while(queueIndex > 0 || current.equals(endVertex))
		{
			GemVertex atual = queue[queueIndex--];
			
			
			for(GemColor cor : listColors)
			{
				if(cor.getNumber() != smallestColor.getNumber())
				{
					if(current.hasEvenLabel()) //clockwise
					{
						if(lastColor.getNumber() > cor.getNumber())
						{
							queue[queueIndex++] = current.getNeighbour(cor);
							lastColor = cor;
							vertexDirection.put(current, "-");
							current = current.getNeighbour(cor);
							break;
						}
						else
						{
							if(isVertexColorTheBiggestOne(listColors, lastColor))
							{
								GemColor modColor = getSmallestColorFromList(listColors);
								queue[queueIndex++] = current.getNeighbour(modColor);
								lastColor = modColor;
								vertexDirection.put(current, "-");
								current = current.getNeighbour(modColor);
								break;
							}
							
						}
					}
					else if(current.hasOddLabel()) //anticlockwise - maior para o menor
					{
						if(lastColor.getNumber() > cor.getNumber())
						{
							queue[queueIndex++] = current.getNeighbour(cor);
							lastColor = cor;
							vertexDirection.put(current, "|");
							current = current.getNeighbour(cor);
							break;
						}
						else
						{
							if(isVertexColorTheSmallestOne(listColors, lastColor))
							{
								GemColor modColor = getBiggestColorFromList(listColors);
								queue[queueIndex++] = current.getNeighbour(modColor);
								lastColor = modColor;
								vertexDirection.put(current, "|");
								current = current.getNeighbour(modColor);
								break;
							}
							
						}
					}
				}
			}
		}
		return vertexColor;
	}
	
	private boolean isVertexColorTheBiggestOne(ArrayList<GemColor> listColors, GemColor vertexColor)
	{
		boolean isMaior = true;
		for(GemColor cor : listColors)
		{
			if(cor.getNumber() > vertexColor.getNumber())
			{
				isMaior = false;
				break;
			}
		}
		return isMaior;
	}
	
	private boolean isVertexColorTheSmallestOne(ArrayList<GemColor> listColors, GemColor vertexColor)
	{
		boolean isMenor = true;
		for(GemColor cor : listColors)
		{
			if(cor.getNumber() < vertexColor.getNumber())
			{
				isMenor = false;
				break;
			}
		}
		return isMenor;
	}
	
	private GemColor getBiggestColorFromList(ArrayList<GemColor> listColors)
	{
		GemColor current = null;
		for(GemColor cor : listColors)
		{
			if(current == null)
			{
				current = cor;
			}
			else
			{
				if(cor.getNumber() > current.getNumber())
				{
					current = cor;
					break;
				}
			}
		}
		return current;
	}
	
	private GemColor getSmallestColorFromList(ArrayList<GemColor> listColors)
	{
		GemColor current = null;
		for(GemColor cor : listColors)
		{
			if(current == null)
			{
				current = cor;
			}
			else
			{
				if(cor.getNumber() < current.getNumber())
				{
					current = cor;
					break;
				}
			}
		}
		return current;
	}
}

