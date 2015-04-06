package hu.gabornovak.graphs.example;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import hu.gabornovak.graphs.data.Node;
import hu.gabornovak.graphs.presenter.NodePresenter;

/**
 *
 * @author Gabor Novak
 */
public class ExampleNodeView extends RelativeLayout implements NodePresenter {
    private boolean highlighted = true;
    private boolean expanded = false;

    private ImageView pinImageView;
    private ImageView expandImageView;
    private RelativeLayout expandedLayout;
    private Node node;

    public ExampleNodeView(Context context) {
        super(context);
        init();
    }

    public ExampleNodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExampleNodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setNode(Node node) {
        this.node = node;
        setFixed(node.isFixed(), false);
    }

    private void init() {
        inflate(getContext(), R.layout.node_layout, this);

        pinImageView = (ImageView) findViewById(R.id.pin_image);
        expandImageView = (ImageView) findViewById(R.id.expand_image);
        expandedLayout = (RelativeLayout) findViewById(R.id.expand_layout);

        pinImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                node.setFixed(!node.isFixed());
                setFixed(node.isFixed(), true);
            }
        });

        expandImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int translation = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                AnimatorSet set = new AnimatorSet();
                if (!expanded) {
                    set.play(ObjectAnimator.ofFloat(expandedLayout, "rotationX", -90, 0)).with(
                            ObjectAnimator.ofFloat(expandImageView, "rotation", 180));
                } else {
                    set.play(ObjectAnimator.ofFloat(expandedLayout, "rotationX", 0, -90)).with(
                            ObjectAnimator.ofFloat(expandImageView, "rotation", 0));
                }
                set.setDuration(600);
                set.setInterpolator(new AnticipateOvershootInterpolator());
                set.start();
                expandedLayout.setTranslationY(translation);
                expanded = !expanded;
            }
        });
    }


    @Override
    public float getNodeWidth() {
        return getWidth();
    }

    @Override
    public float getNodeHeight() {
        return getHeight() / 2;
    }

    @Override
    public void setPosition(float x, float y) {
        setTranslationY(y);
        setTranslationX(x);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        if (!highlighted) {
            ObjectAnimator.ofFloat(this, "alpha", 0.5f).setDuration(200).start();
        } else {
            ObjectAnimator.ofFloat(this, "alpha", 1f).setDuration(200).start();
        }
    }

    @Override
    public boolean isHighlighted() {
        return highlighted;
    }

    @Override
    public void onDragStarted() {
        bringToFront();
    }

    @Override
    public void onDragEnded() {
    }

    public void setFixed(boolean fixed) {
        setFixed(fixed, false);
    }

    public void setFixed(boolean fixed, boolean withAnim) {
        if (withAnim) {
            if (fixed) {
                ObjectAnimator.ofFloat(pinImageView, "alpha", 1f).setDuration(200).start();
                ObjectAnimator.ofFloat(pinImageView, "rotation", 0f).setDuration(200).start();
            } else {
                ObjectAnimator.ofFloat(pinImageView, "alpha", 0.3f).setDuration(200).start();
                ObjectAnimator.ofFloat(pinImageView, "rotation", -90f).setDuration(200).start();
            }
        } else {
            if (fixed) {
                pinImageView.setAlpha(1f);
                pinImageView.setRotation(0f);
            } else {
                pinImageView.setAlpha(0.3f);
                pinImageView.setRotation(-90f);
            }
        }
    }
}