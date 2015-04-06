package hu.gabornovak.graphs;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

/**
 * @author Gabor Novak
 */
public class Constants {
    public static final float MAX_VELOCITY = 1000;
    public static final float MAX_SPEED_SCREEN_WIDTH_FACTOR = 0.02f;
    public static final float NEAREST_MOVE_WEIGHT_FACTOR = 30f;

    public static final float MAX_DISTANCE_FOR_FIXED_NODE = 100f;
    public static final float MAX_MOVE_DISTANCE_FOR_FIXED_NODE = 1000f;

    public static final Interpolator FIXED_NODE_MOVE_INTERPOLATOR = new DecelerateInterpolator();
    public static final Interpolator FIXED_NODE_BACK_INTERPOLATOR = new OvershootInterpolator();

    public static float MAX_SPEED = 30;
}
