package io.wexchain.android.dcc.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.math.BigDecimal;

import io.wexchain.dcc.R;

/**
 * 自定义圆形倒计时
 */
public class CountDownProgress extends View {

    //定义一些常量(大小写字母切换快捷键 Ctrl + Shift + U)
    private static final int DEFAULT_CIRCLE_SOLIDE_COLOR = Color.parseColor("#FFFFFF");
    private static final int DEFAULT_CIRCLE_STROKE_COLOR = Color.parseColor("#D1D1D1");
    private static final int DEFAULT_CIRCLE_STROKE_WIDTH = 12;
    private static final int DEFAULT_CIRCLE_RADIUS = 100;

    private static final int PROGRESS_COLOR = Color.parseColor("#F76E6B");
    private static final int PROGRESS_WIDTH = 12;

    private static final int SMALL_CIRCLE_SOLIDE_COLOR = Color.parseColor("#27282F");
    private static final int SMALL_CIRCLE_STROKE_COLOR = Color.parseColor("#4027282F");
    private static final int SMALL_CIRCLE_STROKE_WIDTH = 6;
    private static final int SMALL_CIRCLE_RADIUS = 9;

    private static final int TEXT_COLOR = Color.parseColor("#777B40FF");
    private static final int TEXT_SIZE = 70;

    private Boolean isDrawText = true;

    //默认圆
    private int defaultCircleSolideColor = DEFAULT_CIRCLE_SOLIDE_COLOR;
    private int defaultCircleStrokeColor = DEFAULT_CIRCLE_STROKE_COLOR;
    private int defaultCircleStrokeWidth = dp2px(DEFAULT_CIRCLE_STROKE_WIDTH);
    private int defaultCircleRadius = dp2px(DEFAULT_CIRCLE_RADIUS);//半径
    //进度条
    private int progressColor = PROGRESS_COLOR;
    private int progressStartColor = Color.parseColor("#FFB274FF");
    private int progressEndColor = Color.parseColor("#FF7B40FF");
    private int progressWidth = dp2px(PROGRESS_WIDTH);

    private int mPercent;
    //默认圆边框上面的小圆
    private int smallCircleSolideColor = SMALL_CIRCLE_SOLIDE_COLOR;
    private int smallCircleStrokeColor = SMALL_CIRCLE_STROKE_COLOR;
    private int smallCircleStrokeWidth = dp2px(SMALL_CIRCLE_STROKE_WIDTH);
    private int smallCircleRadius = dp2px(SMALL_CIRCLE_RADIUS);
    //最里层的文字
    private int textColor = TEXT_COLOR;
    private int textSize = sp2px(TEXT_SIZE);


    //画笔
    private Paint fillCriclePaint;
    private Paint defaultCriclePaint;
    private Paint progressPaint;
    private Paint smallCirclePaint;//画小圆边框的画笔
    private Paint textPaint;
    private Paint smallCircleSolidePaint;//画小圆的实心的画笔

    //圆弧开始的角度
    private float mStartSweepValue = -90;
    //当前的角度
    private float currentAngle;
    //提供一个外界可以设置的倒计时数值，毫秒值
    private long countdownTime;
    //中间文字描述
    private String textDesc;
    //小圆运动路径Path
    private Path mPath;

    //额外距离
    private float extraDistance = 0.7F;



    public CountDownProgress(Context context) {
        this(context, null);
    }

    public CountDownProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownProgress);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CountDownProgress_is_draw_text:
                    isDrawText = typedArray.getBoolean(attr, true);
                    break;
                case R.styleable.CountDownProgress_default_circle_solide_color:
                    defaultCircleSolideColor = typedArray.getColor(attr, defaultCircleSolideColor);
                    break;
                case R.styleable.CountDownProgress_default_circle_stroke_color:
                    defaultCircleStrokeColor = typedArray.getColor(attr, defaultCircleStrokeColor);
                    break;
                case R.styleable.CountDownProgress_default_circle_stroke_width:
                    defaultCircleStrokeWidth = (int) typedArray.getDimension(attr, defaultCircleStrokeWidth);
                    break;
                case R.styleable.CountDownProgress_default_circle_radius:
                    defaultCircleRadius = (int) typedArray.getDimension(attr, defaultCircleRadius);
                    break;
                case R.styleable.CountDownProgress_progress_color:
                    progressColor = typedArray.getColor(attr, progressColor);
                    break;
                case R.styleable.CountDownProgress_progress_width:
                    progressWidth = (int) typedArray.getDimension(attr, progressWidth);
                    break;
                case R.styleable.CountDownProgress_small_circle_solide_color:
                    smallCircleSolideColor = typedArray.getColor(attr, smallCircleSolideColor);
                    break;
                case R.styleable.CountDownProgress_small_circle_stroke_color:
                    smallCircleStrokeColor = typedArray.getColor(attr, smallCircleStrokeColor);
                    break;
                case R.styleable.CountDownProgress_small_circle_stroke_width:
                    smallCircleStrokeWidth = (int) typedArray.getDimension(attr, smallCircleStrokeWidth);
                    break;
                case R.styleable.CountDownProgress_small_circle_radius:
                    smallCircleRadius = (int) typedArray.getDimension(attr, smallCircleRadius);
                    break;
                case R.styleable.CountDownProgress_text_color:
                    textColor = typedArray.getColor(attr, textColor);
                    break;
                case R.styleable.CountDownProgress_text_size:
                    textSize = (int) typedArray.getDimension(attr, textSize);
                    break;
                case R.styleable.CountDownProgress_progress_start_color:
                    progressStartColor = typedArray.getColor(attr, progressStartColor);
                    break;
                case R.styleable.CountDownProgress_progress_end_color:
                    progressEndColor = typedArray.getColor(attr, progressEndColor);
                    break;
            }
        }
        //回收typedArray对象
        typedArray.recycle();
        //设置画笔
        setPaint();
    }

    private float mWidth;
    private float mHeight;

    private void setPaint() {
        //默认圆
        defaultCriclePaint = new Paint();
        defaultCriclePaint.setAntiAlias(true);//抗锯齿
        defaultCriclePaint.setDither(true);//防抖动
        defaultCriclePaint.setStyle(Paint.Style.STROKE);
        defaultCriclePaint.setStrokeWidth(defaultCircleStrokeWidth);
        defaultCriclePaint.setColor(defaultCircleStrokeColor);//这里先画边框的颜色，后续再添加画笔画实心的颜色


        fillCriclePaint = new Paint();
        fillCriclePaint.setAntiAlias(true);//抗锯齿
        fillCriclePaint.setDither(true);//防抖动
        fillCriclePaint.setStyle(Paint.Style.FILL);
        fillCriclePaint.setColor(defaultCircleStrokeColor);//这里先画边框的颜色，后续再添加画笔画实心的颜色

        //默认圆上面的进度弧度
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(progressColor);

        progressPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔笔刷样式
        //进度上面的小圆
        smallCirclePaint = new Paint();
        smallCirclePaint.setAntiAlias(true);
        smallCirclePaint.setDither(true);
        smallCirclePaint.setStyle(Paint.Style.FILL);
        smallCirclePaint.setStrokeWidth(smallCircleStrokeWidth);
        smallCirclePaint.setColor(smallCircleStrokeColor);
        //画进度上面的小圆的实心画笔（主要是将小圆的实心颜色设置成白色）
        smallCircleSolidePaint = new Paint();
        smallCircleSolidePaint.setAntiAlias(true);
        smallCircleSolidePaint.setDither(true);
        smallCircleSolidePaint.setStyle(Paint.Style.FILL);
        smallCircleSolidePaint.setColor(smallCircleSolideColor);

        //文字画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    /**
     * 如果该View布局的宽高开发者没有精确的告诉，则需要进行测量，如果给出了精确的宽高则我们就不管了
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize;
        int heightSize;
        int strokeWidth = Math.max(defaultCircleStrokeWidth, progressWidth);
        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = getPaddingLeft() + defaultCircleRadius * 2 + strokeWidth + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = getPaddingTop() + defaultCircleRadius * 2 + strokeWidth + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //private int[] RATE_COLORS = {0xFFbb59ff,0xFF44dcfc};

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int[] RATE_COLORS = {progressStartColor, progressEndColor};

        mWidth = this.getMeasuredWidth();
        mHeight = this.getMeasuredHeight();
        LinearGradient gradient = new LinearGradient(mWidth / 2, 0, mWidth / 2, mHeight, RATE_COLORS, null, Shader.TileMode.MIRROR);
        progressPaint.setShader(gradient);
        progressPaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.SOLID));

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        //画默认圆
        canvas.drawCircle(defaultCircleRadius, defaultCircleRadius, defaultCircleRadius, defaultCriclePaint);

        //画进度圆弧
        //currentAngle = getProgress()*1.0f/getMax()*360;
        canvas.drawArc(new RectF(0, 0, defaultCircleRadius * 2, defaultCircleRadius * 2), mStartSweepValue, mPercent * currentAngle, false, progressPaint);
        //画中间文字
        // String text = getProgress()+"%";
        //获取文字的长度的方法
        if (isDrawText) {
            float textWidth = textPaint.measureText(textDesc);
            float textHeight = (textPaint.descent() + textPaint.ascent()) / 2;
            canvas.drawText(textDesc, defaultCircleRadius - textWidth / 2, defaultCircleRadius - textHeight, textPaint);

            fillCriclePaint.setShader(gradient);
            fillCriclePaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.SOLID));
        }else{
            LinearGradient gradient2 = new LinearGradient(0, 0, mWidth, mHeight, RATE_COLORS, null, Shader.TileMode.MIRROR);
            fillCriclePaint.setShader(gradient2);
            canvas.drawCircle(defaultCircleRadius, defaultCircleRadius, defaultCircleRadius-(progressWidth/2), fillCriclePaint);
        }

        //画小圆
        float currentDegreeFlag = mPercent * currentAngle + extraDistance;
        float smallCircleX = 0, smallCircleY = 0;
        float hudu = (float) Math.abs(Math.PI * currentDegreeFlag / 180);//Math.abs：绝对值 ，Math.PI：表示π ， 弧度 = 度*π / 180
        smallCircleX = (float) Math.abs(Math.sin(hudu) * defaultCircleRadius + defaultCircleRadius);
        smallCircleY = (float) Math.abs(defaultCircleRadius - Math.cos(hudu) * defaultCircleRadius);

        canvas.drawCircle(smallCircleX, smallCircleY, smallCircleRadius, smallCirclePaint);
        canvas.drawCircle(smallCircleX, smallCircleY, smallCircleStrokeWidth, smallCircleSolidePaint);//画小圆的实心


        canvas.restore();

    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }

    //提供一个外界可以设置的倒计时数值
    public void setCountdownTime(long countdownTime) {
        this.countdownTime = countdownTime;
        //  textDesc = countdownTime / 1000 + "″";
    }

    public void setPercent(int i) {

        mPercent = new BigDecimal(i).multiply(new BigDecimal("3.6")).toBigInteger().intValue();
        textDesc = "" + i + "%";
    }


    //属性动画
    public void startCountDownTime(final OnCountdownFinishListener countdownFinishListener) {
        setClickable(false);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        //动画时长，让进度条在CountDown时间内正好从0-360走完，这里由于用的是CountDownTimer定时器，倒计时要想减到0则总时长需要多加1000毫秒，所以这里时间也跟着+1000ms
        long durationtime = countdownTime + 1000;
        if (durationtime < 1000) {
            durationtime = 1000;
        }
        animator.setDuration(durationtime);
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(0);//表示不循环，-1表示无限循环
        //值从0-1.0F 的动画，动画时长为countdownTime，ValueAnimator没有跟任何的控件相关联，那也正好说明ValueAnimator只是对值做动画运算，而不是针对控件的，我们需要监听ValueAnimator的动画过程来自己对控件做操作
        //添加监听器,监听动画过程中值的实时变化(animation.getAnimatedValue()得到的值就是0-1.0)
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /**
                 * 这里我们已经知道ValueAnimator只是对值做动画运算，而不是针对控件的，因为我们设置的区间值为0-1.0f
                 * 所以animation.getAnimatedValue()得到的值也是在[0.0-1.0]区间，而我们在画进度条弧度时，设置的当前角度为360*currentAngle，
                 * 因此，当我们的区间值变为1.0的时候弧度刚好转了360度
                 */
                currentAngle = (float) animation.getAnimatedValue();
                //       Log.e("currentAngle",currentAngle+"");
                invalidate();//实时刷新view，这样我们的进度条弧度就动起来了
            }
        });
        //开启动画
        animator.start();
        //还需要另一个监听，监听动画状态的监听器
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //倒计时结束的时候，需要通过自定义接口通知UI去处理其他业务逻辑
                if (countdownFinishListener != null) {
                    countdownFinishListener.countdownFinished();
                }
                if (countdownTime > 0) {
                    setClickable(true);
                } else {
                    setClickable(false);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        //调用倒计时操作
        countdownMethod();
    }

    //倒计时的方法
    private void countdownMethod() {
        new CountDownTimer(countdownTime + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //         Log.e("time",countdownTime+"");
                countdownTime = countdownTime - 1000;
                //   textDesc = countdownTime/1000 + "″";
                //countdownTime = countdownTime-1000;
                // Log.e("time",countdownTime+"");
                //刷新view
                invalidate();
            }

            @Override
            public void onFinish() {
                //textDesc = 0 + "″";
                // textDesc = "时间到";
                //同时隐藏小球
                if (textDesc.equals("100%")) {
                    smallCirclePaint.setColor(getResources().getColor(android.R.color.transparent));
                    smallCircleSolidePaint.setColor(getResources().getColor(android.R.color.transparent));
                }

                //刷新view
                invalidate();
            }
        }.start();
    }

    //通过自定义接口通知UI去处理其他业务逻辑
    public interface OnCountdownFinishListener {
        void countdownFinished();
    }

}
