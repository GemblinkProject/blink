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
    		GemColor startColor = GemColor.getByNumber(((Number)params.get(2)).intValue());
			
			ArrayList<GemVertex> gemOddVertexList = G.getOddVertices();
			
			StringBuilder sb = new StringBuilder();
			
			for(GemVertex startVertex : gemOddVertexList)
			{
				sb.append("Vertice inicio " + startVertex.getLabel()+" \n");
				sb.append("Vertice final " + startVertex.getNeighbour(missingColor).getLabel() + "\n");
				sb.append(this.getGemCode(G, missingColor, startColor, startVertex, this.breadFirstSearch(G, startVertex, 
							startVertex.getNeighbour(missingColor), missingColor, startColor)));
				sb.append(("\n"));
			
				
			}
			System.out.println(sb.toString());
			
		}
		return null; //BUGBUG
	}
	
	//BUGBUG checar vertices marcados
	private ArrayList<GemColor> breadFirstSearch(Gem gem, GemVertex startVertex, GemVertex endVertex, GemColor missingColor, GemColor startColor)
	{
		HashMap<GemVertex, Boolean>  vertexFlag = new HashMap<GemVertex, Boolean>();
		HashMap< Integer ,ArrayList<GemVertex>> listListVertex = new HashMap<Integer,ArrayList<GemVertex>>();
		HashMap<Integer,ArrayList<GemColor>> listListGemColor = new HashMap<Integer,ArrayList<GemColor>>(gem.getVertices().size());
		
		ArrayList<GemVertex> fila = new ArrayList<GemVertex>();
//		GemVertex[] queue = new GemVertex[gem.getNumVertices()];

		GemVertex current = startVertex;
		GemColor[] listColors = GemColor.getComplementColors(missingColor);
		
		for(GemVertex vertex : gem.getVertices())
		{
			listListVertex.put( vertex.getLabel() , new ArrayList<GemVertex>());
			listListGemColor.put( vertex.getLabel() , new ArrayList<GemColor>());
		
		}
		
		fila.add(current.getNeighbour(startColor));
		
//		listListVertex.get(startVertex.getLabel()).add(null);
		
		while( fila.size() > 0 )
		{
			if(current.equals(endVertex)){
				break;
			}
			
			if(!vertexFlag.containsKey(current))
			{
				
				listListVertex.get(current.getLabel()).add(current);
				vertexFlag.put(current, true);


				
			
				
				for(GemColor cor : listColors)
				{	
					if(!vertexFlag.containsKey(current.getNeighbour(cor)))
					{

						fila.add(current.getNeighbour(cor));
						listListVertex.put(current.getNeighbour(cor).getLabel(), listListVertex.get(current.getLabel()) );
						ArrayList <GemColor> aux = (ArrayList<GemColor>)listListGemColor.get(current.getLabel()).clone();
						aux.add(cor);
						listListGemColor.put(current.getNeighbour(cor).getLabel(), aux);


					}
				}
				
				
			}
			current = fila.get(0);
			fila.remove(0);
		}
		return listListGemColor.get(endVertex.getLabel());
	}
	
	private String getGemCode(Gem gem,GemColor missingColor , GemColor startColor,GemVertex startVextex, ArrayList<GemColor> vertexColor)
	{
		int indice = 0;
		StringBuilder sb = new StringBuilder();
		startVextex = startVextex.getNeighbour(startColor);

		for(GemColor vertex : vertexColor)
		{
			if(startVextex.hasEvenLabel()){
			if(startColor.getNumber() == 0) 
			{
				if(vertex.getNumber() == 1){
					sb.append("|");	
				}else if(vertex.getNumber() == 2){
					if(missingColor.getNumber()==1){
						sb.append("|");
					}else{
						sb.append("-");
					}
				}else{
					sb.append("-");
				}
				
			}else if(startColor.getNumber() == 1){
				if(vertex.getNumber() == 0){
					sb.append("-");	
				}else if(vertex.getNumber() == 2){
					sb.append("|");	
				}else{
					if(missingColor.getNumber()==0){
						sb.append("-");
					}else{
						sb.append("|");
					}
					
				}
				
			}else if(startColor.getNumber() == 2){
				if(vertex.getNumber() == 0){
					if(missingColor.getNumber()==1){
						sb.append("-");	
					}else{
						sb.append("|");	
					}
				
				}else if(vertex.getNumber() == 1){
					sb.append("-");	
				}else{
					sb.append("|");	
				}
			}else{
				if(vertex.getNumber() == 0){
					sb.append("|");
				
				}else if(vertex.getNumber() == 1){
					if(missingColor.getNumber()==0){
						sb.append("|");	
					}else{
						sb.append("-");	
					}
					
					
				}else{
					sb.append("-");	
				}
			}
			
			}else{
				if(startColor.getNumber() == 0) 
				{
					if(vertex.getNumber() == 1){
						sb.append("-");	
					}else if(vertex.getNumber() == 2){
						if(missingColor.getNumber()==1){
							sb.append("-");
						}else{
							sb.append("|");
						}
					}else{
						sb.append("|");
					}
					
				}else if(startColor.getNumber() == 1){
					if(vertex.getNumber() == 0){
						sb.append("|");	
					}else if(vertex.getNumber() == 2){
						sb.append("-");	
					}else{
						if(missingColor.getNumber()==0){
							sb.append("|");
						}else{
							sb.append("-");
						}
						
					}
					
				}else if(startColor.getNumber() == 2){
					if(vertex.getNumber() == 0){
						if(missingColor.getNumber()==1){
							sb.append("|");	
						}else{
							sb.append("-");	
						}
					
					}else if(vertex.getNumber() == 1){
						sb.append("|");	
					}else{
						sb.append("-");	
					}
				}else{
					if(vertex.getNumber() == 0){
						sb.append("-");
					
					}else if(vertex.getNumber() == 1){
						if(missingColor.getNumber()==0){
							sb.append("-");	
						}else{
							sb.append("|");	
						}
						
						
					}else{
						sb.append("|");	
					}
				}
			}
			
			startVextex = startVextex.getNeighbour(vertexColor.get(indice));
			indice++;
			
			
			
		}
		return sb.toString();
	}
}



