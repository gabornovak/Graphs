package hu.gabornovak.graphs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import hu.gabornovak.graphs.Constants;
import hu.gabornovak.graphs.data.Graph;
import hu.gabornovak.graphs.data.Node;
import hu.gabornovak.graphs.presenter.ConnectionDrawer;

/**
 * @author Gabor Novak
 */
public class GraphView extends RelativeLayout {
    private Graph graph;
    private int refreshTime;

    private ConnectionView connectionView;
    private Handler viewHandler;

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Runnable updateView = new Runnable() {
        @Override
        public void run() {
            invalidate();
            connectionView.invalidate();
            viewHandler.postDelayed(updateView, refreshTime);
        }
    };

    private OnTouchListener touchListener = new OnTouchListener() {
        private VelocityTracker mVelocityTracker = null;
        private float velocityX;
        private float velocityY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean result = false;
            int position = event.getActionIndex();
            int maskedAction = event.getActionMasked();
            switch (maskedAction) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    result = graph.pointerDownOnPosition(position, event.getX(position), event.getY(position));

                    //Handle velocity tracking
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    mVelocityTracker.addMovement(event);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        mVelocityTracker.addMovement(event);
                        mVelocityTracker.computeCurrentVelocity(100);

                        velocityX = VelocityTrackerCompat.getXVelocity(mVelocityTracker, 0);
                        velocityY = VelocityTrackerCompat.getYVelocity(mVelocityTracker, 0);

                        graph.pointerMoveOnPosition(i, event.getX(i), event.getY(i));
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL: {
                    graph.pointerUpOnPosition(position, event.getX(position), event.getY(position), velocityX, velocityY);
                    break;
                }
            }
            return result;
        }
    };

    public void setGraph(Graph graph, ConnectionDrawer connectionDrawer) {
        if (this.graph != null) {
            Log.e(getClass().getSimpleName(), "You already set the graph!");
            return;
        }
        init();
        this.graph = graph;

        connectionView = new ConnectionView(getContext());
        connectionView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        connectionView.setConnections(graph.getConnections());
        connectionView.setConnectionDrawer(connectionDrawer);

        addView(connectionView);

        for (Node node : graph.getNodes()) {
            if (node.getNodePresenter() instanceof View) {
                addView((View) node.getNodePresenter());
            } else {
                Log.w(getClass().getSimpleName(), "The NodePresenter class is not an instance of a View! Please consider to use View.");
            }
        }

        //Because it won't redraw the layout without this
        setWillNotDraw(false);
        setFocusable(false);
        setClickable(true);

        setOnTouchListener(touchListener);

        viewHandler = new Handler();
        viewHandler.post(updateView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewHandler.removeCallbacks(updateView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        graph.recalculatePositions();
        super.onDraw(canvas);
    }

    private void init() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        refreshTime = (int) (1000f / display.getRefreshRate());

        Constants.MAX_SPEED = size.x * Constants.MAX_SPEED_SCREEN_WIDTH_FACTOR;
    }
}