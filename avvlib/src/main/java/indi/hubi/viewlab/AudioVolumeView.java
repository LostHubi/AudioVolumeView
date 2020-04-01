package indi.hubi.viewlab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: AudioVolumeView
 * @Description: 音量显示控件
 * @Author: hb
 * @Date: 2020-04-01
 */
public class AudioVolumeView extends View {
    /**
     * 从右向左移动
     */
    private final int RECT_RIGHT_TO_LEFT = 0;
    /**
     * 从左向右移动
     */
    private final int RECT_LEFT_TO_RIGHT = 1;

    /**
     * 矩形块颜色
     */
    private int rectColor = Color.BLACK;
    /**
     * 移动方向
     * 0 从右向左移动（默认）
     * 1 从左向右移动
     */
    private int rectOrientation = RECT_RIGHT_TO_LEFT;
    /**
     * 矩形宽度
     * 初始值单位是dp，读取属性后保存的为px
     */
    private float rectWidth = 2;
    /**
     * 矩形之间的间隔
     * 初始值单位是dp，读取属性后保存的为px
     */
    private float rectSpace = 8;
    /**
     * 矩形最小高度，小于等于最小音量时，矩形显示此高度
     * 初始值单位是dp，读取属性后保存的为px
     */
    private float rectInitHeight = 4;
    /**
     * 矩形圆角半径
     * 初始值单位是dp，读取属性后保存的为px
     */
    private float rectCorner = 0;
    /**
     * rectDuration
     * 移动整个控件宽度所需的时间，默认5000ms
     */
    private int rectDuration = 5000;
    /**
     * setVolume时传入的音量最大值，超过此值使用maxVolume
     */
    private float maxVolume = 85;
    /**
     * setVolume时传入的音量最小值，大于此值才会显示波动，如果使用场景有噪声，可以适当调高minVolume
     */
    private float minVolume = 45;
    /**
     * 当前展示的音量高度
     */
    private float volumeHeight = 0;
    /**
     * 需要展示到的音量高度
     */
    private float targetVolumeHeight = 0;
    /**
     * 横轴偏移值
     */
    private float rectTranslateX = 0;
    /**
     * 移动速度 px/s
     */
    private float rectSpeedX = 0;
    /**
     * 待绘制的矩形列表
     */
    private List<RectF> rectList;

    public AudioVolumeView(Context context) {
        super(context);
    }

    public AudioVolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAtts(context, attrs);
    }

    public AudioVolumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtts(context, attrs);
    }

    private void initAtts(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.avv);
        rectColor = typedArray.getColor(R.styleable.avv_rectColor, rectColor);
        rectWidth = typedArray.getDimension(R.styleable.avv_rectWidth, Utils.dip2px(context, rectWidth));
        rectSpace = typedArray.getDimension(R.styleable.avv_rectSpace, Utils.dip2px(context, rectSpace));
        rectInitHeight = typedArray.getDimension(R.styleable.avv_rectInitHeight, Utils.dip2px(context, rectInitHeight));
        rectCorner = typedArray.getDimension(R.styleable.avv_rectCorner, Utils.dip2px(context, rectCorner));
        rectOrientation = typedArray.getInt(R.styleable.avv_rectOrientation, RECT_RIGHT_TO_LEFT);
        rectDuration = typedArray.getInt(R.styleable.avv_rectDuration, rectDuration);
        maxVolume = typedArray.getFloat(R.styleable.avv_maxVolume, maxVolume);
        minVolume = typedArray.getFloat(R.styleable.avv_minVolume, minVolume);
        typedArray.recycle();
    }

    /**
     * 上一次绘制时间
     */
    private long latestDrawTime = 0;
    private Paint rectPaint;

    private void initAnimParams() {
        if (rectSpeedX <= 0) {
            rectSpeedX = getWidth() / (float) rectDuration;
            int width = getWidth();
            int totalWidth = (int) (rectSpace + rectWidth);
            if (rectOrientation == RECT_LEFT_TO_RIGHT) {
                if (latestRectCenterX >= 0) {
                    latestRectCenterX = -rectWidth / 2 - totalWidth;
                }
            } else {
                if (latestRectCenterX <= 0) {
                    latestRectCenterX = width + rectWidth / 2 - totalWidth;
                }
            }
            latestDrawTime = System.currentTimeMillis();
        }
        if (rectPaint == null) {
            rectPaint = new Paint();
            rectPaint.setColor(rectColor);
            rectPaint.setAntiAlias(true);
            rectPaint.setStyle(Paint.Style.FILL);
        }
        if (rectList == null) {
            rectList = new LinkedList<>();
        }
    }

    /**
     * 动画的目的：
     * rectSpeedX控制移动速度，rectTranslateX只与当前时间和rectSpeedX有关，不再通过重绘修改该值
     * 每次绘制之后修改rectTranslateX，因为每次绘制时间不一，会有移动时快时慢的感觉
     */
    private void beginAnim() {
        initAnimParams();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        beginAnim();
        drawVoiceRect(canvas);
        run();
    }

    private float latestRectCenterX = 0;

    /**
     * 计算本次重绘应该移动的距离
     */
    private void moveTranslateX() {
        long timeTmp = System.currentTimeMillis();
        long duration = timeTmp - latestDrawTime;
        latestDrawTime = timeTmp;
        float moveX = duration * rectSpeedX;
        if (rectOrientation == RECT_LEFT_TO_RIGHT) {
            rectTranslateX += moveX;
        } else {
            rectTranslateX += -1 * moveX;
        }
    }

    private void drawVoiceRect(Canvas canvas) {
        moveTranslateX();
        int width = getWidth();
        int totalWidth = (int) (rectSpace + rectWidth);
        RectF rect = null;
        //计算此次绘制是否需要新增rect
        if (rectOrientation == RECT_LEFT_TO_RIGHT) {
            if (Math.abs(rectTranslateX) - Math.abs(latestRectCenterX) > rectSpace) {
                rect = new RectF((int) (latestRectCenterX - totalWidth - (rectWidth / 2)),
                        (int) (getHeight() / 2 - rectInitHeight / 2 - volumeHeight),
                        (int) (latestRectCenterX - totalWidth + rectWidth / 2),
                        (int) (getHeight() / 2 + rectInitHeight / 2 + volumeHeight));
                latestRectCenterX = latestRectCenterX - totalWidth;
            }
        } else {
            if (width + Math.abs(rectTranslateX) - Math.abs(latestRectCenterX) > rectSpace) {
                rect = new RectF((int) (latestRectCenterX + totalWidth - rectWidth / 2),
                        (int) (getHeight() / 2 - rectInitHeight / 2 - volumeHeight),
                        (int) (latestRectCenterX + totalWidth + rectWidth / 2),
                        (int) (getHeight() / 2 + rectInitHeight / 2 + volumeHeight));
                latestRectCenterX = latestRectCenterX + totalWidth;
            }
        }
        if (rectList.size() > getWidth() / totalWidth + 2) {
            rectList.remove(0);
        }

        if (null != rect) {
            Log.d("VoiceLineView", "rectList.size():" + rectList.size() + ",rectTranslateX: " + rectTranslateX + ", add rect:" + rect.toString());
            rectList.add(rect);
        }

        canvas.translate(rectTranslateX, 0);
        for (int i = rectList.size() - 1; i >= 0; i--) {
            canvas.drawRoundRect(rectList.get(i), rectCorner, rectCorner, rectPaint);
        }
        rectChange();
    }

    /**
     * 是否设置了音量
     */
    private boolean isSet = false;

    /**
     * 设置当前输入音量
     *
     * @param volume
     */
    public void setVolume(float volume) {
        if (volume > minVolume) {
            if (volume > maxVolume) {
                volume = maxVolume;
            }
            isSet = true;
            //相对音量
            float volumeTmp = volume - minVolume;
            this.targetVolumeHeight = ((getHeight() - rectInitHeight) / 2) * volumeTmp / (maxVolume - minVolume);
        }
    }

    private void rectChange() {
        int maxHeight = (int) (getHeight() - rectInitHeight) / 2;
        if (volumeHeight < targetVolumeHeight && isSet) {
            volumeHeight += maxHeight / 15;
            if (volumeHeight > maxHeight) {
                volumeHeight = maxHeight;
            }
        } else {
            isSet = false;
            if (volumeHeight <= 0) {
                volumeHeight = 0;
            } else {
                if (volumeHeight < maxHeight / 15) {
                    volumeHeight -= maxHeight / 30;
                } else {
                    volumeHeight -= maxHeight / 15;
                }
            }
        }
    }

    public void run() {
        if (isPaused) {
            return;
        }
        invalidate();
    }

    private boolean isPaused = false;

    public void pause() {
        isPaused = true;
    }
}
