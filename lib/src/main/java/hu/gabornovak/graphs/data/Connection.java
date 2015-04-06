package hu.gabornovak.graphs.data;

/**
 * This class represent one Connection between two {@link Node} instance.
 *
 * The connection can be labeled, it has a color, and it can be directed. These information will
 * be shown with a special {@link hu.gabornovak.graphs.presenter.ConnectionDrawer} class.
 *
 * @author Gabor Novak
 */
public class Connection {
    private Node fromNode;
    private Node toNode;

    private String label;
    private int color;
    private boolean directed;

    Connection(Node from, Node to){
        this.fromNode = from;
        this.toNode = to;
    }

    public Node getFromNode() {
        return fromNode;
    }

    public Node getToNode() {
        return toNode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

}
