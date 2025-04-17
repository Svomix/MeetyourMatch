package com.javanostra.meetyourmatch.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;

public class AnimatedMarker extends Marker {

    private ValueAnimator scaleAnimator;
    private float currentScale = 0.0f;
    private boolean isAnimating = false;
    private MapView mapViewRef;

    private static final long ANIMATION_DURATION_MS = 400;
    private Point mPositionPixelsCached = new Point();
    private boolean wasVisible = false;

    public AnimatedMarker(MapView mapView) {
        super(mapView);
        this.mapViewRef = mapView;
        setAlpha(0);
        setupAnimator();
    }

    private void setupAnimator() {
        scaleAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        scaleAnimator.setDuration(ANIMATION_DURATION_MS);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleAnimator.addUpdateListener(animation -> {
            currentScale = (float) animation.getAnimatedValue();
            setAlpha((int)(currentScale * 255));
            if (mapViewRef != null) {
                mapViewRef.invalidate();
            }
        });

        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
                currentScale = 0.0f;
                setAlpha(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                currentScale = 1.0f;
                setAlpha(255);
                if (mapViewRef != null) {
                    mapViewRef.invalidate();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimating = false;
                resetAnimationState();
            }
        });
    }

    @Override
    public boolean hitTest(final MotionEvent event, final MapView mapView) {
        if (mIcon == null || currentScale < 0.1f) {
            return false;
        }

        final Projection pj = mapView.getProjection();
        final int iconWidth = mIcon.getIntrinsicWidth();
        final int iconHeight = mIcon.getIntrinsicHeight();
        final int scaledWidth = (int) (iconWidth * currentScale);
        final int scaledHeight = (int) (iconHeight * currentScale);

        final int x = mPositionPixelsCached.x - (int) (mAnchorU * scaledWidth);
        final int y = mPositionPixelsCached.y - (int) (mAnchorV * scaledHeight);

        final Rect hitRect = new Rect(x, y, x + scaledWidth, y + scaledHeight);

        return hitRect.contains((int) event.getX(), (int) event.getY());
    }

    @Override
    public void draw(Canvas canvas, Projection pj) {
        if (mIcon == null || mapViewRef == null) {
            return;
        }

        pj.toPixels(mPosition, mPositionPixelsCached);

        boolean currentlyVisible = pj.getIntrinsicScreenRect().contains(mPositionPixelsCached.x, mPositionPixelsCached.y);

        if (currentlyVisible && !wasVisible && !isAnimating) {
            if (scaleAnimator.isRunning()) {
                scaleAnimator.cancel();
            }
            resetAnimationState();
            scaleAnimator.start();
        } else if (!currentlyVisible && wasVisible && !isAnimating) {
            resetAnimationState();
        }

        wasVisible = currentlyVisible;

        if (currentlyVisible && currentScale > 0.01f) {
            canvas.save();

            canvas.translate(mPositionPixelsCached.x, mPositionPixelsCached.y);

            canvas.scale(currentScale, currentScale);

            if (mBearing != 0.0f) {
                canvas.rotate(mBearing);
            }

            float anchorX = mAnchorU * mIcon.getIntrinsicWidth();
            float anchorY = mAnchorV * mIcon.getIntrinsicHeight();
            canvas.translate(-anchorX, -anchorY);

            mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());

            mIcon.draw(canvas);

            canvas.restore();

        } else if (!currentlyVisible) {
            if (isInfoWindowShown()) {
                closeInfoWindow();
            }
        }
    }

    public void resetAnimationState() {
        if (scaleAnimator != null && scaleAnimator.isRunning()) {
            scaleAnimator.cancel();
        }
        isAnimating = false;
        currentScale = 0.0f;
        setAlpha(0);
        wasVisible = false;
    }

    @Override
    public void setIcon(Drawable icon) {
        super.setIcon(icon);
        resetAnimationState();
    }

    @Override
    public void onDetach(MapView mapView) {
        if (scaleAnimator != null) {
            scaleAnimator.cancel();
            scaleAnimator.removeAllUpdateListeners();
            scaleAnimator.removeAllListeners();
            scaleAnimator = null;
        }
        mapViewRef = null;
        super.onDetach(mapView);
    }
}