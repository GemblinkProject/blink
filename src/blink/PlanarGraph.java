package blink;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Random;
import java.lang.Math;
import java.lang.Integer;

public class PlanarGraph {
    public ArrayList<PlanarVertex> vertexes;
    public ArrayList<PlanarFace> faces;
    
    
    public PlanarGraph() {
        vertexes = new ArrayList<PlanarVertex>();
        faces = new ArrayList<PlanarFace>();
    }
    
    public static PlanarGraph newWheel(int N) {
        PlanarGraph g = new PlanarGraph();
        PlanarVertex central = new PlanarVertex(g);
        for (int i = 0; i < N; ++i) {
            PlanarVertex v = new PlanarVertex(g);
            PlanarEdge e1 = v.addEdge(central);
            PlanarEdge e2 = central.addEdge(v);
            e1.setCounterEdge(e2);
        }
        PlanarEdge[] ongoingEdges = new PlanarEdge[N];
        PlanarVertex prevVertex = central.edges.get(N-1).toVertex;
        for (int i = 0; i < N; ++i) {
            PlanarVertex currVertex = central.edges.get(i).toVertex;
            ongoingEdges[i] = currVertex.addEdge(prevVertex);
            prevVertex = currVertex;
        }
        for (int i = 0; i < N; ++i) {
            PlanarVertex currVertex = central.edges.get(i).toVertex;
            PlanarEdge e2 = prevVertex.addEdge(currVertex);
            e2.setCounterEdge(ongoingEdges[i]);
            prevVertex = currVertex;
        }
        g.updateNextEdges();
        g.updateFaces();
        return g;
    }
    
    public void updateNextEdges() {
        for (PlanarVertex v: this.vertexes) {
            v.updateNextEdges();
        }
    }
    
    public void updateFaces() {
        HashSet mark = new HashSet<PlanarEdge>();
        for (PlanarVertex v: this.vertexes) {
            for (PlanarEdge e: v.edges) {
                if (!mark.contains(e)) {
                    PlanarFace face = new PlanarFace(this);
                    while (!mark.contains(e)) {
                        e.counterEdge.toFace = face;
                        face.addEdge(e);
                        mark.add(e);
                        e = e.nextEdgeOverFace();
                    }
                }
            }
        }
    }
    
    public void addVertex(PlanarVertex v) {
        v.id = vertexes.size();
        vertexes.add(v);
    }
    public void addFace(PlanarFace f) {
        f.id = faces.size();
        faces.add(f);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Vertexes:\n");
        for (PlanarVertex v: vertexes) {
            sb.append(v + ":");
            for (PlanarEdge e: v.edges) {
                sb.append(" " + e.toVertex);
                sb.append("(" + e.toFace + ")");
                if (e.fromVertex() != v) {
                    sb.append("*");
                }
            }
            sb.append("\n");
        }
        sb.append("Faces:\n");
        for (PlanarFace f: faces) {
            sb.append(f + ":");
            for (PlanarEdge e: f.edges) {
                sb.append(" " + e.toFace);
                sb.append("(" + e.toVertex + ")");
                if (e.fromFace() != f) {
                    sb.append("*");
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    public static int indexFromDouble(double x, int N) {
        double E = Math.exp(1);
        double A = 1.0 / (1.0 + E), B = E / (1 + E);
        x = x * (B-A) + A;
        return (int)((Math.log(x / (1-x)) + 1) * N);
    }
    
    public static PlanarGraph newRandFromWheel(int N, int vD, int fD) {
        return newRandFromWheel(N, vD, fD, new Random());
    }
    
    public static PlanarGraph newRandFromWheel(int N, int vD, int fD, Random rand) {
        PlanarGraph g = newWheel(N);
        boolean expanded = true;
        while (expanded) {
            expanded = false;
            for (PlanarVertex v : g.vertexes) {
                int degree = v.edges.size();
                if (degree > vD) {
                    //System.out.println("Degree v " + degree);
                    int a, b, it = 0;
                    while (true) {
                        a = rand.nextInt(degree);
                        b = (a + degree / 2) % degree;
                        if (v.edges.get(a).fromFace().edges.size() < fD
                            && v.edges.get(b).fromFace().edges.size() < fD) {
                            break;
                        }
                        if (it++ >= degree) {
                            a = 0;
                            int best = g.vertexes.size() + g.faces.size();
                            for (int i = 0; i < degree; ++i) {
                                int dist = Math.max(fD - v.edges.get(i).fromFace().edges.size(), 0);
                                b = (i + degree/2) % degree;
                                dist += Math.max(fD - v.edges.get(b).fromFace().edges.size(), 0);
                                if (dist < best) {
                                    a = i;
                                    best = dist;
                                }
                            }
                            b = (a + degree/2) % degree;
                            break;
                        }
                    }
                    v.expand2(a, b);
                    expanded = true;
                    break;
                }
            }
            for (PlanarFace f : g.faces) {
                int degree = f.edges.size();
                if (degree > fD) {
                    //System.out.println("Degree f " + degree);
                    int a, b, it = 0;
                    while (true) {
                        a = rand.nextInt(degree);
                        b = (a + degree / 2) % degree;
                        if (f.edges.get(a).fromVertex().edges.size() < vD
                            && f.edges.get(b).fromVertex().edges.size() < vD) {
                            break;
                        }
                        if (it++ >= degree) {
                            a = 0;
                            int best = g.vertexes.size() + g.faces.size();
                            for (int i = 0; i < degree; ++i) {
                                int dist = Math.max(vD - f.edges.get(i).fromVertex().edges.size(), 0);
                                b = (i + degree/2) % degree;
                                dist += Math.max(vD - f.edges.get(b).fromVertex().edges.size(), 0);
                                if (dist < best) {
                                    a = i;
                                    best = dist;
                                }
                            }
                            b = (a + degree/2) % degree;
                            break;
                        }
                    }
                    f.expand2(a, b);
                    expanded = true;
                    break;
                }
            }
            //System.out.println(g.vertexes.size() + " " + g.faces.size());
        }
        return g;
    }
    
    public Gem psi() {
        GemColor[] colors = GemColor.PERMUTATIONS[0];
        GemColor colorZero = colors[0];
        GemColor colorShort = colors[1];
        GemColor colorLong = colors[2];
        GemColor colorAng = colors[3];
        ArrayList<GemVertex> gemVertexes = new ArrayList<GemVertex>();
        int indexGem = 0;
        //int nEdges = vertices.size() + faces.size() - 2;
        HashMap<PlanarEdge, Integer> edgesIndexes = new HashMap<PlanarEdge, Integer>();
        for (PlanarVertex v: vertexes) {
            for (PlanarEdge e: v.edges) {
                edgesIndexes.put(e, new Integer(indexGem/2));
                gemVertexes.add(new GemVertex(++indexGem));
                gemVertexes.add(new GemVertex(++indexGem));
            }
        }
        
        for (PlanarEdge e: edgesIndexes.keySet()) {
            int index = edgesIndexes.get(e).intValue();
            GemVertex gv1 = gemVertexes.get(2*index);
            GemVertex gv2 = gemVertexes.get(2*index + 1);
            
            int counterIndex = edgesIndexes.get(e.counterEdge).intValue();
            GemVertex gv3 = gemVertexes.get(2*counterIndex);
            GemVertex gv4 = gemVertexes.get(2*counterIndex + 1);
            
            int nextIndex = edgesIndexes.get(e.nextEdge).intValue();
            GemVertex gv5 = gemVertexes.get(2*nextIndex);
            
            gv1.setNeighbour(gv2, colorShort);
            gv2.setNeighbour(gv1, colorShort);
            gv1.setNeighbour(gv4, colorLong);
            gv2.setNeighbour(gv3, colorLong);
            gv2.setNeighbour(gv5, colorAng);
            gv5.setNeighbour(gv2, colorAng);
        }
        Gem gem = new Gem();
        for (GemVertex v: gemVertexes) {
            if (v.getLabel()%2 == 1) {
                GemVertex u = v.getNeighbour(colorShort).getNeighbour(colorLong).getNeighbour(colorAng);
                v.setNeighbour(u, colorZero);
            } else {
                GemVertex u = v.getNeighbour(colorAng).getNeighbour(colorLong).getNeighbour(colorShort);
                v.setNeighbour(u, colorZero);
            }
            gem.addVertex(v);
        }
        gem.goToCodeLabel().getCode();
        return gem;
    }
    
    public static void main(String[] args) {
        PlanarGraph g = newRandFromWheel(200, 5, 5);
        //g.vertexes.get(0).expand2(7,2);
        //g.faces.get(8).expand2(7,11);
        Gem gem = g.psi();
        System.out.println(g);
    }
}
