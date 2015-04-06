package hu.gabornovak.graphs.presenter;

import hu.gabornovak.graphs.data.Node;

/**
 * The interface which let the node present itself. This interface should be implemented in a
 * CustomView class.
 *
 * @author Gabor Novak
 */
public interface NodePresenter {
    void setNode(Node node);

    float getNodeWidth();
    float getNodeHeight();

    void setPosition(float x, float y);

    void setHighlighted(boolean selected);
    boolean isHighlighted();

    void onDragStarted();
    void onDragEnded();
}
