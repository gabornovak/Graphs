package hu.gabornovak.graphs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import java.util.Collection;

import hu.gabornovak.graphs.data.Connection;
import hu.gabornovak.graphs.presenter.ConnectionDrawer;

/**
 *
 * @author Gabor Novak
 */
class ConnectionView extends View {
    private ConnectionDrawer connectionDrawer;
    private Collection<Connection> connectionList;

    public ConnectionView(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (Connection connection : connectionList) {
            connectionDrawer.drawConnection(canvas, connection);
        }
    }

    public void setConnections(Collection<Connection> connectionList) {
        this.connectionList = connectionList;
    }

    public void setConnectionDrawer(ConnectionDrawer connectionDrawer) {
        this.connectionDrawer = connectionDrawer;
    }
}
