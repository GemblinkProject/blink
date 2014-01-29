package blink;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class GemInfo {
	public static int[][] colorsPermNumbers = {
        {0,1,2,3}, {1,0,3,2}, {2,3,0,1}, {3,2,1,0}
    };
    
    public static String bigons(Gem gem, int colorsPermIdx) {
    	StringBuffer sb = new StringBuffer();
    	gem = gem.copy();
    	
    	GemColor[][] colorsPerm = new GemColor[4][4];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                colorsPerm[i][j] = GemColor.getByNumber(
                    colorsPermNumbers[i][j]
                );
            }
        }

        char[][] colorPairs = {
        		{'-','a','b','c'},
        		{'A','-','d','e'},
        		{'B','D','-','f'},
        		{'C','E','F','-'}
        };
        GemColor[] colors = colorsPerm[colorsPermIdx];
        {
            sb.append("Bigons using fourth color (0) as " + colors[0] + " ("+ colors[0].getNumber() +"):\n");
            
            int maxSize = 0;
            GemVertex[][][] bigons = gem.getBigons(colors[1], colors[2], colors[3]);
            
            HashMap<GemVertex, HashMap<GemColor, Integer>> faces = new HashMap<GemVertex, HashMap<GemColor, Integer>>();
            for (GemVertex v: gem.getVertices()) {
                faces.put(v, new HashMap<GemColor, Integer>());
            }
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < bigons[i].length; ++j) {
                    for (int k = 0; k < bigons[i][j].length; ++k) {
                        faces.get(bigons[i][j][k]).put(colors[(i+(k%2))%3+1], new Integer(j));
                    }
                }
            }
            
            for (int i = 0; i < 3; ++i) {
            	sb.append("Bigons " + colors[i+1].getNumber() + " - " + colors[(i+1)%3+1].getNumber() + " in anticlockwise order:\n");
		HashMap<Integer, Integer> amount = new HashMap<Integer, Integer>();
		
            	for (int j = 0; j < bigons[i].length; ++j) {
			Integer lastInteger = amount.get(new Integer(bigons[i][j].length));
			int last = 0;
			if (lastInteger != null) last = lastInteger.intValue();
			amount.put(new Integer(bigons[i][j].length), new Integer(last+1));
            		sb.append(colorPairs[colors[i+1].getNumber()][colors[(i+1)%3+1].getNumber()] + "" + (j+1) + ": ");
            		char[] bigonPair = new char[2];
            		bigonPair[0] = colorPairs[colors[(i+2)%3+1].getNumber()][colors[i+1].getNumber()];
            		bigonPair[1] = colorPairs[colors[(i+1)%3+1].getNumber()][colors[(i+2)%3+1].getNumber()];
            		for (int k = 0; k < bigons[i][j].length; ++k) {
            			sb.append(bigons[i][j][k].getLabel() + " ");
            			int counterFace = faces.get(bigons[i][j][(k+1)%bigons[i][j].length]).get(colors[(i+(k%2))%3+1]).intValue() + 1;
            			sb.append("[" + bigonPair[k%2] + counterFace + "] ");
            		}
            		sb.append("size: " + bigons[i][j].length + "\n");
    	            maxSize = Math.max(maxSize, bigons[i][j].length);
            	}
            	for(Entry<Integer, Integer> entry: amount.entrySet()) {
            		sb.append(entry.getValue() + " bigon(s) of size " + entry.getKey() + "\n");
            	}
            }
            
            sb.append("Biggest bigon has size: " + maxSize + "\n\n");
        }
        return new String(sb);
    }
    
	public static String bainhas(Gem gem, int colorsPermIdx) {
		StringBuffer sb = new StringBuffer();
        gem = gem.copy();
         
        GemColor[][] colorsPerm = new GemColor[4][4];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                colorsPerm[i][j] = GemColor.getByNumber(
                    colorsPermNumbers[i][j]
                );
            }
        }
        
        GemColor[] colors = colorsPerm[colorsPermIdx];
        {
            String permutacao = "(" +
                colors[0].getNumber() + "," + colors[1].getNumber() + "," +
                colors[2].getNumber() + "," + colors[3].getNumber() + ")";
            sb.append("Bainhas usando permutacao "+permutacao+":\n");
            GemColor trigonColors[] = {colors[1], colors[2], colors[3]};
            
            GemVertex[] vertexes = gem.getVertices().toArray(new GemVertex[0]);
            Arrays.sort(vertexes);
            for (GemVertex v: vertexes) {
                if (v.getLabel()%2 == 0) {
                    continue;
                }
                GemColor[] path = v.bfsPathFromC1ByLeft(v.getNeighbour(colors[0]), trigonColors);
                if (path == null) {
                    sb.append(
                        v.getLabel() + " - " +
                        v.getNeighbour(colors[0]).getLabel() + 
                        "(bainha " + v.getLabel() + "-" +
                        v.getNeighbour(colors[1]).getLabel() + "): " +
                        "No path!\n"
                    );
                    continue;
                }
                if (path.length > 63) {
                    sb.append(
                        v.getLabel() + " - " +
                        v.getNeighbour(colors[0]).getLabel() + 
                        "(bainha " + v.getLabel() + "-" +
                        v.getNeighbour(colors[1]).getLabel() + "): " +
                        "Path too large!\n"
                    );
                    continue;
                }
                char[][] directions = new char[4][4];
                for (int i = 0; i < 3; ++i) {
                    directions[trigonColors[i].getNumber()][trigonColors[(i+1)%3].getNumber()] = '\\'; // Left
                    directions[trigonColors[(i+1)%3].getNumber()][trigonColors[i].getNumber()] = '/'; // Right
                }
                long pathNumber = 0;
                //StringBuffer upDownPath = new StringBuffer();
                GemVertex u = v.getNeighbour(path[0]);
                System.out.print("\n" + path.length + " " + v.getLabel() + " " + u.getLabel());
                for (int i = 1; i < path.length; ++i) {
                    u = u.getNeighbour(path[i]);
                    System.out.print(" " + u.getLabel());
                    if (i % 2 == 0) {
                        if (directions[path[i-1].getNumber()][path[i].getNumber()] == '\\') {
                            pathNumber = (pathNumber << 1) | 1;
                        } else {
                            pathNumber = (pathNumber << 1);
                        }
                        //upDownPath.append(
                        //    directions[path[i-1].getNumber()][path[i].getNumber()]
                        //);
                    } else {
                        if (directions[path[i].getNumber()][path[i-1].getNumber()] == '\\') {
                            pathNumber = (pathNumber << 1) | 1;
                        } else {
                            pathNumber = (pathNumber << 1);
                        }
                        //upDownPath.append(
                        //    directions[path[i].getNumber()][path[i-1].getNumber()]
                        //);
                    }
                }
                sb.append(
                    v.getLabel() +
                    " (bainha " + v.getLabel() + "-" +
                    v.getNeighbour(colors[1]).getLabel() + ") " +
                    v.getNeighbour(colors[0]).getLabel() + ": " +
                    pathNumber + "\n"
                );
            }
            sb.append("\n");
        }
        return new String(sb);
    }
}
