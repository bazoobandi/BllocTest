package com.example.blloctest.customwidget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.blloctest.R;

public class SwitchView extends View {

    protected int width;
    protected int height;

    /**
     * Field to determine whether switch is on/off.
     *
     * @see #isOn()
     * @see #setOn(boolean)
     */
    protected boolean isOn;

    private int padding;

    private int colorOn;
    private int colorOff;
    private int colorBorder;


    private int outerRadii;
    private int thumbRadii;
    private int animationRate;

    private Paint paint;

    private long startTime;

    private RectF thumbBounds;

    private RectF leftBgArc;
    private RectF rightBgArc;

    private RectF leftFgArc;
    private RectF rightFgArc;


    private float thumbOnCenterX;
    private float thumbOffCenterX;



    /**
     * Listener used to dispatch switch events.
     *
     * @see #setOnSwitchViewdListener(OnSwitchViewdListener)
     */
    protected OnSwitchViewdListener onSwitchViewdListener;

    /**
     * Simple constructor to use when creating a switch from code.
     * @param context The Context the switch is running in, through which it can
     *        access the current theme, resources, etc.
     */
    public SwitchView(Context context) {
        super(context);
        initView();
        init(null);
    }

    /**
     * Constructor that is called when inflating a switch from XML.
     *
     * @param context The Context the switch is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag .
     */

    public SwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        init(attrs);
    }


    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute.
     *
     * @param context The Context the switch is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the switch.
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the switch. Can be 0 to not look for defaults.
     */
    public SwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        init(attrs);
    }



    /**
     * Function for setting default values
     *
     *
     */

    private void initView() {
        this.isOn = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorBorder = colorOn = getResources().getColor(R.color.colorAccent, getContext().getTheme());
        } else {
            colorBorder = colorOn = getResources().getColor(R.color.colorAccent);
        }

        paint = new Paint();
        paint.setAntiAlias(true);

        leftBgArc = new RectF();
        rightBgArc = new RectF();

        leftFgArc = new RectF();
        rightFgArc = new RectF();
        thumbBounds = new RectF();

        this.colorOff = Color.parseColor("#FFFFFF");
    }


    /**
     * Function for setting default attributes
     *
     *
     */
    private void init(AttributeSet attrs) {
        TypedArray tarr = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SwitchView, 0, 0);
        final int N = tarr.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = tarr.getIndex(i);
            if (attr == R.styleable.SwitchView_on) {
                isOn = tarr.getBoolean(R.styleable.SwitchView_on, false);
            } else if (attr == R.styleable.SwitchView_colorOff) {
                colorOff = tarr.getColor(R.styleable.SwitchView_colorOff, Color.parseColor("#FFFFFF"));
            } else if (attr == R.styleable.SwitchView_colorBorder) {
                int accentColor;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    accentColor = getResources().getColor(R.color.colorAccent, getContext().getTheme());
                } else {
                    accentColor = getResources().getColor(R.color.colorAccent);
                }
                colorBorder = tarr.getColor(R.styleable.SwitchView_colorBorder, accentColor);
            } else if (attr == R.styleable.SwitchView_colorOn) {
                int accentColor;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    accentColor = getResources().getColor(R.color.colorAccent, getContext().getTheme());
                } else {
                    accentColor = getResources().getColor(R.color.colorAccent);
                }
                colorOn = tarr.getColor(R.styleable.SwitchView_colorOn, accentColor);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Drawing switch track
        {
            paint.setColor(colorBorder);
            canvas.drawArc(leftBgArc, 90, 180, false, paint);
            canvas.drawArc(rightBgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, 0, (width - outerRadii), height, paint);

            paint.setColor(colorOff);

            canvas.drawArc(leftFgArc, 90, 180, false, paint);
            canvas.drawArc(rightFgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, padding / 10, (width - outerRadii), height - (padding / 10), paint);


            int alpha = (int) (((thumbBounds.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int onColor;

            onColor = Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn));

            paint.setColor(onColor);

            canvas.drawArc(leftBgArc, 90, 180, false, paint);
            canvas.drawArc(rightBgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, 0, (width - outerRadii), height, paint);

            alpha = (int) (((thumbOnCenterX - thumbBounds.centerX()) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int offColor = Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff));
            paint.setColor(offColor);

            canvas.drawArc(leftFgArc, 90, 180, false, paint);
            canvas.drawArc(rightFgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, padding / 10, (width - outerRadii), height - (padding / 10), paint);
        }

        //      Drawing Switch Thumb here
        {
            int alpha = (int) (((thumbBounds.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX)) * 255);

            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));

            int offColor = Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff));
            paint.setColor(offColor);

            canvas.drawCircle(thumbBounds.centerX(), thumbBounds.centerY(), alpha/animationRate, paint);


            alpha = (int) (((thumbOnCenterX - thumbBounds.centerX()) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int onColor;

            onColor = Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn));

            paint.setColor(onColor);
            canvas.drawCircle(thumbBounds.centerX(), thumbBounds.centerY(), alpha/animationRate, paint);

        }
    }

    /**
     *  We need to implement onMeasure method to tell Android
     *  how big we want our custom view to be dependent the layout constraints provided by the parent.
     *
     *
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int desiredWidth = getResources().getDimensionPixelSize(R.dimen.labeled_default_width);
        int desiredHeight = getResources().getDimensionPixelSize(R.dimen.labeled_default_height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);

        outerRadii = Math.min(width, height) >>> 1;
        thumbRadii = (int) (Math.min(width, height) / (2.88f));

        //calculate for thumb animation
        if(thumbRadii!=0)
            animationRate = 255/thumbRadii;
        padding = (height - thumbRadii) >>> 1;

        thumbBounds.set(width - padding - thumbRadii, padding, width - padding, height - padding);
        thumbOnCenterX = thumbBounds.centerX();

        thumbBounds.set(padding, padding, padding + thumbRadii, height - padding);
        thumbOffCenterX = thumbBounds.centerX();

        if (isOn) {
            thumbBounds.set(width - padding - thumbRadii, padding, width - padding, height - padding);
        } else {
            thumbBounds.set(padding, padding, padding + thumbRadii, height - padding);
        }

        leftBgArc.set(0, 0, outerRadii << 1, height);
        rightBgArc.set(width - (outerRadii << 1), 0, width, height);

        leftFgArc.set(padding / 10, padding / 10, (outerRadii << 1) - (padding / 10), height - (padding / 10));
        rightFgArc.set(width - (outerRadii << 1) + (padding / 10), padding / 10, width - (padding / 10), height - (padding / 10));
    }


    /**
     * Call this view's OnClickListener, if it is defined.  Performs all normal
     * actions associated with clicking: reporting accessibility event, playing
     * a sound, etc.
     *
     * @return True there was an assigned OnClickListener that was called, false
     *         otherwise is returned.
     */
    @Override
    public final boolean performClick() {
        super.performClick();
        if (isOn) {
            ValueAnimator switchColor = ValueAnimator.ofFloat(width - padding - thumbRadii, padding);
            switchColor.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                thumbBounds.set(value, thumbBounds.top, value + thumbRadii, thumbBounds.bottom);
                invalidate();
            });
            switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
            switchColor.setDuration(600);
            switchColor.start();
        } else {
            ValueAnimator switchColor = ValueAnimator.ofFloat(padding, width - padding - thumbRadii);
            switchColor.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                thumbBounds.set(value, thumbBounds.top, value + thumbRadii, thumbBounds.bottom);
                invalidate();
            });
            switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
            switchColor.setDuration(600);
            switchColor.start();
        }
        isOn = !isOn;
        if (onSwitchViewdListener != null) {
            onSwitchViewdListener.onSwitched(this, isOn);
        }
        return true;
    }


    /**
     * Method to handle touch screen motion events.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public final boolean onTouchEvent(MotionEvent event) {
                float x = event.getX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startTime = System.currentTimeMillis();
                        return true;
                    }

            case MotionEvent.ACTION_MOVE: {
                if (x - (thumbRadii >>> 1) > padding && x + (thumbRadii >>> 1) < width - padding) {
                    thumbBounds.set(x - (thumbRadii >>> 1), thumbBounds.top, x + (thumbRadii >>> 1), thumbBounds.bottom);
                    invalidate();
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                long endTime = System.currentTimeMillis();
                long span = endTime - startTime;
                if (span < 200) {
                    performClick();
                } else {
                    if (x >= width >>> 1) {
                        ValueAnimator switchColor = ValueAnimator.ofFloat((x > (width - padding - thumbRadii) ? (width - padding - thumbRadii) : x), width - padding - thumbRadii);
                        switchColor.addUpdateListener(animation -> {
                            float value = (float) animation.getAnimatedValue();
                            thumbBounds.set(value, thumbBounds.top, value + thumbRadii, thumbBounds.bottom);
                            invalidate();
                        });
                        switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
                        switchColor.setDuration(600);
                        switchColor.start();
                        isOn = true;
                    } else {
                        ValueAnimator switchColor = ValueAnimator.ofFloat((x < padding ? padding : x), padding);
                        switchColor.addUpdateListener(animation -> {
                            float value = (float) animation.getAnimatedValue();
                            thumbBounds.set(value, thumbBounds.top, value + thumbRadii, thumbBounds.bottom);
                            invalidate();
                        });
                        switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
                        switchColor.setDuration(600);
                        switchColor.start();
                        isOn = false;
                    }
                    if (onSwitchViewdListener != null) {
                        onSwitchViewdListener.onSwitched(this, isOn);
                    }
                }
                invalidate();
                return true;
            }
            default: {
                return super.onTouchEvent(event);
            }
        }
    }


    /**
     * <p>Returns the color value for colorOn.</p>
     *
     * @return color value for label and thumb in off state and background in on state.
     */
    public int getColorOn() {
        return colorOn;
    }

    /**
     * <p>Changes the on color value of this Switch.</p>
     *
     * @param colorOn color value for label and thumb in off state and background in on state.
     */
    public void setColorOn(int colorOn) {
        this.colorOn = colorOn;
        invalidate();
    }

    /**
     * <p>Returns the color value for colorOff.</p>
     *
     * @return color value for label and thumb in on state and background in off state.
     */
    public int getColorOff() {
        return colorOff;
    }

    /**
     * <p>Changes the off color value of this Switch.</p>
     *
     * @param colorOff color value for label and thumb in on state and background in off state.
     */
    public void setColorOff(int colorOff) {
        this.colorOff = colorOff;
        invalidate();
    }


    /**
     * <p>Returns the boolean state of this Switch.</p>
     *
     * @return true if the switch is on, false if it is off.
     */
    public boolean isOn() {
        return isOn;
    }


    /**
     * <p>Changes the boolean state of this Switch.</p>
     *
     * @param on true to turn switch on, false to turn it off.
     */

    public void setOn(boolean on) {
        if (isOn) {
            thumbBounds.set(width - padding - thumbRadii, padding, width - padding, height - padding);
        } else {
            thumbBounds.set(padding, padding, padding + thumbRadii, height - padding);
        }
        invalidate();
    }


    /**
     * <p>Returns the color value for Switch border.</p>
     *
     * @return color value used by Switch border.
     */
    public int getColorBorder() {
        return colorBorder;
    }

    /**
     * <p>Changes the color value for Switch disabled state.</p>
     *
     * @param colorBorder color value used by Switch border.
     */
    public void setColorBorder(int colorBorder) {
        this.colorBorder = colorBorder;
        invalidate();
    }


    /**
     * Register a callback to be invoked when the boolean state of switch is changed. If this switch is not
     * enabled, there won't be any event.
     *
     * @param onSwitchViewdListener The callback that will run
     *
     * @see #setEnabled(boolean)
     */
    public void setOnSwitchViewdListener(OnSwitchViewdListener onSwitchViewdListener) {
        this.onSwitchViewdListener = onSwitchViewdListener;
    }


    /**
     *
     * Interface definition for a callback to be invoked when a digital switch is either on/off.
     *
     */
    public interface OnSwitchViewdListener {


        /**
         * Called when a view changes it's state.
         *
         * @param switchView The view which either is on/off.
         * @param isOn The on/off state of switch, true when switch turns on.
         */
        void onSwitched(SwitchView switchView, boolean isOn);
    }

}
