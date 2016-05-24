package com.legitdevs.legitnotes;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

public class CircledPulsatingButton extends ImageView {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
    private static final int PRESSED_RING_ALPHA = 75;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 16;
    private static final int ANIMATION_TIME_ID = R.integer.config_superShortAnimTime;

    public static final String TAG = "Pulsating Button";

    private int centerY;
    private int centerX;
    private int outerRadius;
    private int pressedRingRadius;

    private Paint circlePaint;
    private Paint focusPaint;

    private float animationProgress;

    private int pressedRingWidth;
    private int defaultColor = Color.BLACK;
    private int pressedColor;
    private ObjectAnimator pressedAnimator;

    private MediaRecorder mRecorder;
    private int maxAmplitude = 1;
    private int currentAmp;

    private Handler mProgressUpdateHandler;

    private Runnable mUpdateProgress = new Runnable() {

        public void run() {

            if (mRecorder == null) {
                return;
            }

            if (mProgressUpdateHandler != null) {
                pulsateAnimation();

                //per fare prove modificare il secondo valore, non scendere sotto ai 150-200
                mProgressUpdateHandler.postDelayed(this, 200);
            }
        }
    };

    public CircledPulsatingButton(Context context) {
        super(context);
        init(context, null);
    }

    public CircledPulsatingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircledPulsatingButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (circlePaint != null) {
            circlePaint.setColor(pressed ? pressedColor : defaultColor);
        }

        if (pressed) {
            showPressedRing();
        } else {
            hidePressedRing();
        }

        mProgressUpdateHandler = new Handler();
        mProgressUpdateHandler.postDelayed(mUpdateProgress, 200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
        canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = Math.min(w, h) / 2;
        pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
        this.invalidate();
    }

    public void setColor(int color) {
        this.defaultColor = color;
        this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

        circlePaint.setColor(defaultColor);
        focusPaint.setColor(defaultColor);
        focusPaint.setAlpha(PRESSED_RING_ALPHA);

        this.invalidate();
    }

    private void hidePressedRing() {
        pressedAnimator.setFloatValues(pressedRingWidth, 0f);
        pressedAnimator.start();
    }

    private void showPressedRing() {
        pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
        pressedAnimator.start();
    }

    public void setMediaRecorder(MediaRecorder recorder) {
        mRecorder = recorder;
    }

    //IMPORTANTE: NON METTERE LOG QUA DENTRO
    private void pulsateAnimation() {
        currentAmp = mRecorder.getMaxAmplitude();

        maxAmplitude = (currentAmp > maxAmplitude) ? currentAmp : maxAmplitude;

        if(currentAmp<maxAmplitude/2){
            pressedAnimator.setFloatValues(animationProgress, (pressedRingWidth * ((currentAmp*3)/2)) / maxAmplitude);
            pressedAnimator.start();
            return;
        }
        if (currentAmp<maxAmplitude/3) {
            pressedAnimator.setFloatValues(animationProgress, (pressedRingWidth * (currentAmp*2)) / maxAmplitude);
            pressedAnimator.start();
            return;
        }

        pressedAnimator.setFloatValues(animationProgress, (pressedRingWidth * currentAmp) / maxAmplitude);
        pressedAnimator.start();



    }

    private void init(Context context, AttributeSet attrs) {
        this.setFocusable(true);
        this.setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);

        focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusPaint.setStyle(Paint.Style.STROKE);

        pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());

        int color = Color.BLACK;
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircledPulsatingButton);
            color = a.getColor(R.styleable.CircledPulsatingButton_cpb_color, color);
            pressedRingWidth = (int) a.getDimension(R.styleable.CircledPulsatingButton_cpb_pressedRingWidth, pressedRingWidth);
            a.recycle();
        }

        setColor(color);

        focusPaint.setStrokeWidth(pressedRingWidth);
        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
        pressedAnimator.setDuration(pressedAnimationTime);
    }

    private int getHighlightColor(int color, int amount) {
        return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
                Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
    }

    public void releaseRecorder() {
        mRecorder = null;
    }
}