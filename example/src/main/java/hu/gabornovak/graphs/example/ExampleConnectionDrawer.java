package hu.gabornovak.graphs.example;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import hu.gabornovak.graphs.data.Connection;
import hu.gabornovak.graphs.data.Node;
import hu.gabornovak.graphs.presenter.ConnectionDrawer;

public class ExampleConnectionDrawer implements ConnectionDrawer {
    private int highlightedLineColor = Color.parseColor("#3a3a3a");
    private int lineColor = Color.parseColor("#553a3a3a");

    private Path path = new Path();
    private Point midPoint = new Point();
    private Point startPoint = new Point();
    private Point endPoint = new Point();

    private Paint linePaint = new Paint() {{
        setStyle(Style.STROKE);
        setDither(true);
    }};

    private Paint textPaint = new Paint() {{
        setStyle(Style.FILL);
        setColor(Color.WHITE);
        setTextSize(40f);
        setDither(true);
    }};

    private Paint circlePaint = new Paint() {{
        setStyle(Style.FILL);
        setDither(true);
    }};

    @Override
    public void drawConnection(Canvas canvas, Connection connection) {
        Node from = connection.getFromNode();
        Node to = connection.getToNode();

        boolean highlighted = from.isHighlighted() && to.isHighlighted();

        if (highlighted) {
            linePaint.setColor(highlightedLineColor);
            circlePaint.setColor(highlightedLineColor);
            linePaint.setStrokeWidth(8f);
        } else {
            linePaint.setColor(lineColor);
            circlePaint.setColor(lineColor);
            linePaint.setStrokeWidth(4f);
        }

        //Bezier source: http://roosmaa.net/drawing-beautiful-bezier-lines/

        startPoint.x =(int) (from.getPositionX() + from.getNodePresenter().getNodeWidth());
        startPoint.y = (int)(from.getPositionY() + from.getNodePresenter().getNodeHeight() / 2);
        endPoint.x = (int) (to.getPositionX());
        endPoint.y = (int)(to.getPositionY() + to.getNodePresenter().getNodeHeight() / 2);

        midPoint.set((startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2);

        path.reset();
        path.moveTo(startPoint.x, startPoint.y);
        path.quadTo((startPoint.x + midPoint.x) / 2, startPoint.y, midPoint.x, midPoint.y);
        path.quadTo((midPoint.x + endPoint.x) / 2, endPoint.y, endPoint.x, endPoint.y);

        canvas.drawCircle(startPoint.x, startPoint.y, 10f, circlePaint);
        canvas.drawCircle(endPoint.x, endPoint.y, 10f, circlePaint);
        canvas.drawPath(path, linePaint);

        if (connection.getLabel() != null) {
            canvas.drawText(connection.getLabel(), midPoint.x, midPoint.y, textPaint);
        }
    }
}