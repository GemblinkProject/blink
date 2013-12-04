package blink;

public class PlanarEdge {
    public PlanarVertex toVertex;
    public PlanarFace toFace;
    public PlanarEdge counterEdge;
    public PlanarEdge nextEdge;
    
    public PlanarEdge(PlanarVertex toVertex) {
        this.toVertex = toVertex;
    }
    public PlanarEdge(PlanarFace toFace) {
        this.toFace = toFace;
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
    public PlanarVertex fromVertex() {
        return this.counterEdge.toVertex;
    }
    public PlanarFace fromFace() {
        return this.counterEdge.toFace;
    }
}
