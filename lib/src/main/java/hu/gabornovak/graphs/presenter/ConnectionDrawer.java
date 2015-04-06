package hu.gabornovak.graphs.presenter;

import android.graphics.Canvas;

import hu.gabornovak.graphs.data.Connection;

/**
 * This is the interface to draw a connection between two {@link hu.gabornovak.graphs.data.Node}
 *
 * @author Gabor Novak
 */
public interface ConnectionDrawer {
     void drawConnection(Canvas c, Connection connection);
}
