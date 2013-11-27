package blink;

import java.util.ArrayList;

public class PlanarGraph {
    public ArrayList<PlanarVertex> vertexes;
    
    public PlanarGraph() {
        vertexes = new ArrayList<PlanarVertex>();
    }
    
    public static PlanarGraph newLoop() {
        PlanarGraph g = new PlanarGraph();
        g.vertexes.add(PlanarVertex.newLoop(g));
        return g;
    }
    
    public static PlanarGraph newWheel(int N) {
        PlanarGraph g = new PlanarGraph();
        PlanarVertex central = new PlanarVertex(g);
        g.addVertex(central);
        for (int i = 0; i < N; ++i) {
            PlanarVertex v = new PlanarVertex(g);
            g.addVertex(v);
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
        return g;
    }
    
    public void updateNextEdges() {
        for(PlanarVertex v: this.vertexes) {
            v.updateNextEdges();
        }
    }
    
    public void addVertex(PlanarVertex v) {
        vertexes.add(v);
    }
}
