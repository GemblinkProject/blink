package blink;

import java.util.ArrayList;

public class PlanarEdge {
    public PlanarVertex toVertex;
    public PlanarEdge counterEdge;
    public PlanarEdge nextEdge;
    
    public PlanarEdge(PlanarVertex toVertex) {
        this.toVertex = toVertex;
    }
    
    public void setCounterEdge(PlanarEdge e) {
        this.counterEdge = e;
        e.counterEdge = this;
    }
    public void setNextEdge(PlanarEdge e) {
        this.nextEdge = e;
    }
    public PlanarEdge nextEdgeOverFace() {
        return this.counterEdge.nextEdge;
    }
}
