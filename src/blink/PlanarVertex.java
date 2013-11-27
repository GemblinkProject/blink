package blink;

import java.util.ArrayList;

public class PlanarVertex {
    public PlanarGraph graph;
    public ArrayList<PlanarEdge> edges;
    
    public PlanarVertex(PlanarGraph graph) {
        this.graph = graph;
        edges = new ArrayList<PlanarEdge>();
    }
    
    public static PlanarVertex newLoop(PlanarGraph graph) {
        PlanarVertex v = new PlanarVertex(graph);
        v.addEdge(new PlanarEdge(v));
        return v;
    }
    
    public void addEdge(PlanarEdge e) {
        edges.add(e);
    }
    
    public PlanarEdge addEdge(PlanarVertex v) {
        PlanarEdge e = new PlanarEdge(v);
        edges.add(new PlanarEdge(v));
        return e;
    }
    
    public void updateNextEdges() {
        PlanarEdge prevEdge = edges.get(edges.size()-1);
        for (PlanarEdge currEdge: edges) {
            prevEdge.setNextEdge(currEdge);
            prevEdge = currEdge;
        }
    }
    
}
