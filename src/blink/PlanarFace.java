package blink;

import java.util.ArrayList;
import java.util.Random;

public class PlanarFace {
    public int id;
    public PlanarGraph graph;
    public ArrayList<PlanarEdge> edges;
    
    public PlanarFace(PlanarGraph graph) {
        this.graph = graph;
        this.edges = new ArrayList<PlanarEdge>();
        graph.addFace(this);
    }
    public String toString() {
        return "" + id;
    }
    
    public PlanarEdge addEdge(PlanarFace f) {
        PlanarEdge e = new PlanarEdge(f);
        edges.add(e);
        return e;
    }
    public void addEdge(PlanarEdge e) {
        edges.add(e);
    }
    
    public void expand2(int first, int last) {
        PlanarFace newFace = new PlanarFace(graph);
        int nEdges = (last - first + edges.size()) % edges.size();
        for (int i = 0; i < nEdges; ++i) {
            PlanarEdge e = edges.get((i+first) % edges.size());
            newFace.addEdge(e);
            e.counterEdge.toFace = newFace;
        }
        ArrayList<PlanarEdge> remainingEdges = new ArrayList<PlanarEdge>();
        for (int i = nEdges; i < edges.size(); ++i) {
            remainingEdges.add(edges.get((i+first) % edges.size()));
        }
        this.edges = remainingEdges;
        PlanarEdge e1 = this.addEdge(newFace);
        PlanarEdge e2 = newFace.addEdge(this);
        e1.setCounterEdge(e2);
        e1.toVertex = this.edges.get(0).fromVertex();
        e2.toVertex = newFace.edges.get(0).fromVertex();
        e1.toVertex.addEdgeBefore(e2, this.edges.get(0));
        e2.toVertex.addEdgeBefore(e1, newFace.edges.get(0));
        e1.toVertex.updateNextEdges();
        e2.toVertex.updateNextEdges();
    }
    
    public void addEdgeBefore(PlanarEdge newEdge, PlanarEdge beforeEdge) {
        for (int i = 0; i < edges.size(); ++i) {
            if (edges.get(i) == beforeEdge) {
                edges.add(i, newEdge);
                return;
            }
        }
    }
    public void addEdgeAfter(PlanarEdge newEdge, PlanarEdge afterEdge) {
        for (int i = 0; i < edges.size(); ++i) {
            if (edges.get(i) == afterEdge) {
                edges.add(i+1, newEdge);
                return;
            }
        }
    }
}
