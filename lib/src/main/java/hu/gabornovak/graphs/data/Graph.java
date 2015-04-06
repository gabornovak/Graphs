package hu.gabornovak.graphs.data;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.gabornovak.graphs.Constants;
import hu.gabornovak.graphs.presenter.NodePresenter;

/**
 * The main graph represented by an instance of this class.
 * You need to set a Graph instance to the {@link hu.gabornovak.graphs.view.GraphView}, and before that
 * you have to set up a Graph instance.
 * <br/>
 * You can create {@link Node} and {@link Connection} from your class instance. Those objects
 * will be added to the graph automatically.
 *
 * @author Gabor Novak
 */
public class Graph {
    class MovedNodeData {
        Node movedNode;
        List<Node> neighbors;
        List<Vector2D> neighborsStartPositions;
        float downX, downY;
        float diffX, diffY;
        float nodeStartX, nodeStartY;
        long downTime;
    }

    private List<Node> nodes;
    private List<Connection> connections;

    private SparseArray<MovedNodeData> movedNodesData;

    private boolean nodeSelected;

    public Graph() {
        nodes = new ArrayList<>();
        connections = new ArrayList<>();
        movedNodesData = new SparseArray<>();
    }

    public Node createNewNode(NodePresenter nodePresenter) {
        Node node = new Node(nodePresenter);
        nodes.add(node);
        return node;
    }

    public Connection createNewConnection(Node from, Node to) {
        Connection connection = new Connection(from, to);
        connections.add(connection);
        return connection;
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    public Collection<Connection> getConnections() {
        return connections;
    }

    public void recalculatePositions() {
        for (Node node : nodes) {
            node.recalculatePosition();
        }
    }

    private List<Node> getNeighborNodes(Node node) {
        List<Node> nodes = new ArrayList<>();
        for (Connection c : getConnections()) {
            if (c.getFromNode().equals(node)) {
                nodes.add(c.getToNode());
            } else if (c.getToNode().equals(node)) {
                nodes.add(c.getFromNode());
            }
        }
        return nodes;
    }

    private Node getTouchedNode(float x, float y) {
        for (Node node : getNodes()) {
            if (node.contains(x, y)) {
                return node;
            }
        }
        return null;
    }

    public boolean pointerDownOnPosition(int position, float x, float y) {
        Node node = getTouchedNode(x, y);
        if (node != null) {
            //Create a new MovedNodeData
            node.setDragged(true);
            List<Node> connectedNodes = getNeighborNodes(node);
            for (Node n : connectedNodes) {
                n.setDragged(true);
            }
            List<Vector2D> neighborNodesStartPositions = new ArrayList<>();
            for (int i = 0; i < connectedNodes.size(); i++) {
                neighborNodesStartPositions.add(new Vector2D(connectedNodes.get(i).getPositionX(), connectedNodes.get(i).getPositionY()));
            }
            MovedNodeData movedNodeData = new MovedNodeData();
            movedNodeData.movedNode = node;
            movedNodeData.neighbors = connectedNodes;
            movedNodeData.neighborsStartPositions = neighborNodesStartPositions;
            movedNodeData.diffX = x - node.getPositionX();
            movedNodeData.diffY = y - node.getPositionY();
            movedNodeData.downX = x;
            movedNodeData.downY = y;
            movedNodeData.nodeStartX = node.getPositionX();
            movedNodeData.nodeStartY = node.getPositionY();
            movedNodeData.downTime = System.currentTimeMillis();

            node.getNodePresenter().onDragStarted();
            movedNodesData.put(position, movedNodeData);
            return true;
        }
        return false;
    }

    public void pointerMoveOnPosition(int position, float x, float y) {
        MovedNodeData movedNodeData = movedNodesData.get(position);
        if (movedNodeData != null) {
            if (movedNodeData.movedNode.isFixed()) {
                Vector2D v = new Vector2D(x - movedNodeData.diffX - movedNodeData.nodeStartX, y - movedNodeData.diffY - movedNodeData.nodeStartY);
                float maxMove = Constants.MAX_MOVE_DISTANCE_FOR_FIXED_NODE;
                float max = Constants.MAX_DISTANCE_FOR_FIXED_NODE;
                float dist = Math.min(maxMove, v.length());

                v = v.normalize();
                v = v.scale(max * Constants.FIXED_NODE_MOVE_INTERPOLATOR.getInterpolation(dist / maxMove));
                movedNodeData.movedNode.setPosition(movedNodeData.nodeStartX + v.getX(), movedNodeData.nodeStartY + v.getY());
            } else {
                movedNodeData.movedNode.setPosition(x - movedNodeData.diffX, y - movedNodeData.diffY);

                //Move the others a little bit
                for (int i = 0; i < movedNodeData.neighbors.size(); i++) {
                    Node neighbor = movedNodeData.neighbors.get(i);
                    if (neighbor.isFixed() || neighbor.isDragged()) {
                        continue;
                    }
                    Vector2D neighborStartPosition = movedNodeData.neighborsStartPositions.get(i);
                    neighbor.setPosition(neighborStartPosition.getX() + (x - movedNodeData.downX) / (Constants.NEAREST_MOVE_WEIGHT_FACTOR * neighbor.getWeight()),
                            neighborStartPosition.getY() + (y - movedNodeData.downY) / (Constants.NEAREST_MOVE_WEIGHT_FACTOR * neighbor.getWeight()));
                }
            }
        }
    }

    public void pointerUpOnPosition(int position, float x, float y, float velocityX, float velocityY) {
        MovedNodeData movedNodeData = movedNodesData.get(position);
        if (movedNodeData != null) {
            //Handle on click
            if (System.currentTimeMillis() - movedNodeData.downTime < 200 && Math.abs(x - movedNodeData.downX) < 100 && Math.abs(y - movedNodeData.downY) < 100) {
                if (!((View) movedNodeData.movedNode.getNodePresenter()).performClick()) {
                    if (!nodeSelected) {
                        nodeSelected = true;
                        setNodeDataAndNeighborsHighlighted(movedNodeData.movedNode);
                    } else {
                        nodeSelected = false;
                        setAllNodeHighlighted();
                    }
                }
            } else {
                if (movedNodeData.movedNode.isFixed()) {
                    AnimatorSet set = new AnimatorSet();
                    set.setDuration(200);
                    set.setInterpolator(Constants.FIXED_NODE_BACK_INTERPOLATOR);
                    set.play(ObjectAnimator.ofFloat(movedNodeData.movedNode, "positionX", movedNodeData.nodeStartX)).with(
                            ObjectAnimator.ofFloat(movedNodeData.movedNode, "positionY", movedNodeData.nodeStartY));
                    set.start();
                } else {
                    Vector2D velocityVector = new Vector2D(velocityX, velocityY);
                    float velocityFactor = velocityVector.length() / Constants.MAX_VELOCITY;
                    if (velocityFactor > 1f) {
                        velocityFactor = 1f;
                    }
                    movedNodeData.movedNode.setDirection(velocityVector);
                    movedNodeData.movedNode.setSpeed(velocityFactor * Constants.MAX_SPEED);

                    for (int i = 0; i < movedNodeData.neighbors.size(); i++) {
                        Node neighbors = movedNodeData.neighbors.get(i);
                        neighbors.setDirection(velocityVector);
                        neighbors.setSpeed(velocityFactor * Constants.MAX_SPEED / 3f);
                    }
                }
                movedNodeData.movedNode.getNodePresenter().onDragEnded();
            }

            movedNodeData.movedNode.setDragged(false);
            for (Node n : movedNodeData.neighbors) {
                n.setDragged(false);
            }
            movedNodesData.remove(position);

        } else {
            Log.w(getClass().getSimpleName(), "Pointer up occurred but there was no pointer down");
        }
    }

    private void setAllNodeHighlighted() {
        for (Node n : getNodes()) {
            n.setHighlighted(true);
        }
    }

    private void setNodeDataAndNeighborsHighlighted(Node node) {
        Set<Node> selectedNodes = new HashSet<>();
        selectedNodes.add(node);
        selectedNodes.addAll(getNeighborNodes(node));

        for (Node n : getNodes()) {
            if (selectedNodes.contains(n)) {
                n.setHighlighted(true);
            } else {
                n.setHighlighted(false);
            }
        }
    }
}
