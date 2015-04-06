package hu.gabornovak.graphs.data;

import hu.gabornovak.graphs.Constants;
import hu.gabornovak.graphs.presenter.NodePresenter;

/**
 *
 * This class represent one node in a graph.
 *
 * @author Gabor Novak
 */
public class Node {
    private NodePresenter nodePresenter;

    private float speed;
    private float weight;
    private Vector2D direction;

    private float positionX;
    private float positionY;

    private boolean fixed;

    private float circleCenterX;
    private float circleCenterY;
    private boolean dragged;
    private boolean clockwise;

    Node(NodePresenter nodePresenter) {
        this.nodePresenter = nodePresenter;
        nodePresenter.setNode(this);
    }

    public boolean contains(float x, float y) {
        return x >= positionX && x <= (positionX + nodePresenter.getNodeWidth()) && y >= positionY && y <= (positionY + nodePresenter.getNodeHeight());
    }

    public NodePresenter getNodePresenter() {
        return nodePresenter;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getPositionX() {
        return positionX;
    }

    //Do not remove! this is used by the objectAnimator
    public void setPositionX(float x) {
        positionX = x;
        setPosition(x, positionY);
    }

    //Do not remove! this is used by the objectAnimator
    public void setPositionY(float y) {
        positionY = y;
        setPosition(positionX, y);
    }

    public float getPositionY() {
        return positionY;
    }

    public void setDirection(Vector2D direction) {
        this.direction = direction.normalize();
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        if (weight > 1f) {
            this.weight = 1f;
        } else if (weight < 0f) {
            this.weight = 0f;
        } else {
            this.weight = weight;
        }
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
        speed = 0;
        direction = null;
    }

    public void setPosition(float x, float y) {
        positionX = x;
        positionY = y;
        if (getNodePresenter() != null) {
            getNodePresenter().setPosition(x, y);
        }
    }

    public void recalculatePosition() {
        if (!fixed) {
            if (speed > Constants.MAX_SPEED) {
                speed = Constants.MAX_SPEED;
            }
            if (speed < 0.5f) {
                speed = 0;
            }
            if (speed > 0 && direction != null) {
                Vector2D v = direction;
                v = v.scale(speed);
                setPosition(positionX + v.getX(), positionY + v.getY());
                speed = speed * weight;
            } else if (!dragged) {
                moveInIdleState();
            }
        }
    }

    private void moveInIdleState() {
        float distance = (float) Math.sqrt((circleCenterX - positionX) * (circleCenterX - positionX) + (circleCenterY - positionY) * (circleCenterY - positionY));
        Vector2D center = new Vector2D(circleCenterX, circleCenterY);
        if (distance > 30) {
            center = getCircleCenterByCurrentPosition(positionX, positionY);
            clockwise = center.getX() % 2 == 0;
            circleCenterX = center.getX();
            circleCenterY = center.getY();
        }

        float radius = (float) Math.sqrt((center.getX() - positionX) * (center.getX() - positionX) + (center.getY() - positionY) * (center.getY() - positionY));
        float degrees = (float) Math.atan2(positionX - center.getX(), -positionY + center.getY());

        if (clockwise) {
            degrees += 4.735;
        } else {
            degrees -= 4.735;
        }
        if (degrees > 2 * Math.PI) {
            degrees -= 2 * Math.PI;
        }
        float x = (float) (radius * Math.cos(degrees));
        float y = (float) (radius * Math.sin(degrees));
        setPosition(circleCenterX + x, circleCenterY + y);
    }

    private Vector2D getCircleCenterByCurrentPosition(float x, float y) {
        return new Vector2D((int) Math.floor(x / 20) * 20, (int) Math.floor(y / 20) * 20);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return !(nodePresenter != null ? !nodePresenter.equals(node.nodePresenter) : node.nodePresenter != null);
    }

    @Override
    public int hashCode() {
        return nodePresenter != null ? nodePresenter.hashCode() : 0;
    }

    public boolean isHighlighted() {
        return nodePresenter.isHighlighted();
    }

    public void setHighlighted(boolean selected) {
        nodePresenter.setHighlighted(selected);
    }

    public boolean isDragged() {
        return dragged;
    }

    public void setDragged(boolean dragged) {
        this.dragged = dragged;
    }
}