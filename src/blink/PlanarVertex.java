package blink;

import java.util.ArrayList;
import java.util.Random;

public class PlanarVertex {
    public int id;
    public PlanarGraph graph;
    public ArrayList<PlanarEdge> edges;
    
    public PlanarVertex(PlanarGraph graph) {
        this.graph = graph;
        this.edges = new ArrayList<PlanarEdge>();
        graph.addVertex(this);
    }
    public String toString() {
        return "" + id;
    }
    
    public void addEdge(PlanarEdge e) {
        edges.add(e);
    }
    
    public PlanarEdge addEdge(PlanarVertex v) {
        PlanarEdge e = new PlanarEdge(v);
        edges.add(e);
        return e;
    }
    
    public void updateNextEdges() {
        PlanarEdge prevEdge = edges.get(edges.size()-1);
        for (PlanarEdge currEdge: edges) {
            prevEdge.setNextEdge(currEdge);
            prevEdge = currEdge;
        }
    }
    
    public void expand2(int first, int last) {
        PlanarVertex newVertex = new PlanarVertex(graph);
        int nEdges = (last - first + edges.size()) % edges.size();
        for (int i = 0; i < nEdges; ++i) {
            PlanarEdge e = edges.get((i+first) % edges.size());
            newVertex.addEdge(e);
            e.counterEdge.toVertex = newVertex;
        }
        ArrayList<PlanarEdge> remainingEdges = new ArrayList<PlanarEdge>();
        for (int i = nEdges; i < edges.size(); ++i) {
            remainingEdges.add(edges.get((i+first) % edges.size()));
        }
        this.edges = remainingEdges;
        PlanarEdge e1 = this.addEdge(newVertex);
        PlanarEdge e2 = newVertex.addEdge(this);
        e1.setCounterEdge(e2);
        e1.toFace = this.edges.get(0).fromFace();
        e2.toFace = newVertex.edges.get(0).fromFace();
        e1.toFace.addEdgeBefore(e2, this.edges.get(0));
        e2.toFace.addEdgeBefore(e1, newVertex.edges.get(0));
        newVertex.updateNextEdges();
        this.updateNextEdges();
    }
    
    public PlanarFace randExplode(int maxSequent, Random rand) {
        // Divide edges in N blocks with size <= maxSequent
        ArrayList<ArrayList<PlanarEdge>> partEdges = new ArrayList<ArrayList<PlanarEdge>>();
        ArrayList<PlanarEdge> currEdges = new ArrayList<PlanarEdge>();
        int used = 0, choice = 0;
        for (PlanarEdge e: edges) {
            if (used == choice) {
                currEdges = new ArrayList<PlanarEdge>();
                partEdges.add(currEdges);
                choice = rand.nextInt(maxSequent) + 1;
            }
            currEdges.add(e);
        }
        int N = partEdges.size();
        // If it's just one block, don't do anything
        if (N == 1) {
            return null;
        }
        // Create a new face and N-1 new vertexes
        PlanarFace newFace = new PlanarFace(graph);
        PlanarVertex[] vertexes = new PlanarVertex[N];
        vertexes[0] = this;
        for (int i = 1; i < N; ++i) {
            vertexes[i] = new PlanarVertex(graph);
        }
        // Put the block of edges and create edges connecting the vertexes
        PlanarEdge[] ongoingEdges = new PlanarEdge[2*N];
        for (int i = 0; i < N; ++i) {
            vertexes[i].edges = partEdges.get(i);
            for (PlanarEdge e: vertexes[i].edges) {
                e.counterEdge.toVertex = vertexes[i];
            }
            PlanarEdge firstEdge = vertexes[i].edges.get(0);
            PlanarEdge lastEdge = vertexes[i].edges.get(vertexes[i].edges.size()-1);
            
            PlanarEdge e1 = vertexes[i].addEdge(vertexes[i+1]);
            e1.toFace = lastEdge.fromFace();
            PlanarEdge e2 = vertexes[i].addEdge(vertexes[(i-1+N)%N]);
            e2.toFace = newFace;
            firstEdge.fromFace().addEdgeAfter(e2, firstEdge);
            ongoingEdges[2*i+1] = e1;
            ongoingEdges[2*i] = e2;
            vertexes[i].updateNextEdges();
        }
        for (int i = 0; i < N; ++i) {
            ongoingEdges[2*i+1].setCounterEdge(ongoingEdges[(2*i+2)%(2*N)]);
        }
        for (int i = N-1; i >= 0; --i) {
            newFace.addEdge(ongoingEdges[2*i+1]);
        }
        return newFace;
    }
    
    public void addEdgeBefore(PlanarEdge newEdge, PlanarEdge beforeEdge) {
        for (int i = 0; i < edges.size(); ++i) {
            if (edges.get(i) == beforeEdge) {
                edges.add(i, newEdge);
                return;
            }
        }
    }
}
