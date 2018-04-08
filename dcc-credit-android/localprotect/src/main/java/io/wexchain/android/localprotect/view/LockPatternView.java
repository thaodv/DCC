package io.wexchain.android.localprotect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import io.wexchain.android.localprotect.R;

public class LockPatternView extends View {
    private float viewWidth, viewHeight;
    private float dis              = 0;
    
    private Drawable local_circle_original;
    private Drawable local_circle_click;
    private Drawable local_circle_click_error;
    
    private Paint       mPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Point[][]   mPoints = new Point[3][3];
    private List<Point> sPoints = new ArrayList<>();
    
    private boolean moveNoPoint = false;
    
    private float moveX, moveY;
    
    private LockPatternChecker patternChecker;
    
    public static final int LENGTH_SHORT   = 0;
    public static final int DRAW_FALSE     = 2;
    public static final int DRAW_SUCCESS   = 3;
    public static final int UNLOCK_SUCCESS = 4;
    public static final int DRAW_FIVE_MORE = 5;
    
    public LockPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public LockPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public LockPatternView(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        local_circle_original = ContextCompat.getDrawable(context, R.drawable.ges_normal);
        local_circle_click = ContextCompat.getDrawable(context, R.drawable.ges_select);
        local_circle_click_error = ContextCompat.getDrawable(context, R.drawable.ges_error);
        
        for (Point[] points : mPoints) {
            for (int j = 0; j < points.length; j++) {
                points[j] = new Point();
            }
        }
        int k = 0;
        for (Point[] ps : mPoints) {
            for (Point p : ps) {
                p.index = k;
                k++;
            }
        }
        mPaint.setColor(ContextCompat.getColor(context,R.color.lightPink));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2f,context.getResources().getDisplayMetrics()));
        initDraw();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDraw();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCanvas(canvas);
    }
    
    private void initDraw() {
        
        viewWidth = this.getWidth();
        viewHeight = this.getHeight();
        
        float x = 0;
        float y = 0;
        
        float canvasMinWidth = viewWidth;
        
        if (viewWidth > viewHeight) {
            x = (viewWidth - viewHeight) / 2;
            //noinspection SuspiciousNameCombination
            viewWidth = viewHeight;
            //noinspection SuspiciousNameCombination
            canvasMinWidth = viewHeight;
        }
        if (viewWidth < viewHeight) {
            y = (viewHeight - viewWidth) / 2;
            //noinspection SuspiciousNameCombination
            viewHeight = viewWidth;
        }
        
        float circleMinWidth = canvasMinWidth / 6.0f;
        float circleWidth = canvasMinWidth / 2.f;
        float deviation = canvasMinWidth % (8 * 2) / 2;
        // x += deviation;
        y += deviation;
        
        mPoints[0][0].set(x + circleMinWidth, y + circleMinWidth);
        mPoints[0][1].set(x + viewWidth / 2, y + circleMinWidth);
        mPoints[0][2].set(x + viewWidth - circleMinWidth, y + circleMinWidth);
        mPoints[1][0].set(x + circleMinWidth, y + viewHeight / 2);
        mPoints[1][1].set(x + viewWidth / 2, y + viewHeight / 2);
        mPoints[1][2].set(x + viewWidth - circleMinWidth, y + viewHeight / 2);
        mPoints[2][0].set(x + circleMinWidth, y + viewHeight - circleMinWidth);
        mPoints[2][1].set(x + viewWidth / 2, y + viewHeight - circleMinWidth);
        mPoints[2][2].set(x + viewWidth - circleMinWidth, y + viewHeight - circleMinWidth);
        dis = local_circle_click.getIntrinsicHeight() / 2;
    }
    
    private void drawCanvas(Canvas canvas) {
        for (Point[] mPoint : mPoints) {
            for (Point p : mPoint) {
                if (p.state == Point.STATE_CHECK) {
                    drawCenter(canvas, local_circle_click, p);
                } else if (p.state == Point.STATE_CHECK_ERROR) {
                    drawCenter(canvas, local_circle_click_error, p);
                } else {
                    drawCenter(canvas, local_circle_original, p);
                }
            }
        }
        
        if (sPoints.size() > 0) {
            Point tp = sPoints.get(0);
            for (int i = 1; i < sPoints.size(); i++) {
                Point p = sPoints.get(i);
                drawLine(canvas, tp, p);
                tp = p;
            }
            if (moveNoPoint) {
                drawLine(canvas, tp, new Point((int) moveX, (int) moveY));
            }
        }
        
    }
    
    private void drawCenter(Canvas canvas, Drawable drawable, Point p) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int l = (int) p.x - width / 2;
        int t = (int) p.y - height / 2;
        drawable.setBounds(l, t, l + width, t + height);
        drawable.draw(canvas);
    }
    
    private void drawLine(Canvas canvas, Point start, Point stop) {
        canvas.drawLine(start.x, start.y, stop.x, stop.y, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        moveNoPoint = false;
        boolean finishedInput = false;
        float ex = event.getX();
        float ey = event.getY();
        Point p = null;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // isTouch = true;
                p = selectPoint(ex, ey);
                break;
            case MotionEvent.ACTION_MOVE:
                p = selectPoint(ex, ey);
                if (p == null) {
                    moveNoPoint = true;
                    moveX = ex;
                    moveY = ey;
                }
                break;
            case MotionEvent.ACTION_UP:
                p = selectPoint(ex, ey);
                finishedInput = true;
                break;
            default:
                break;
        }

        if (p != null && !finishedInput) {
            int k = repetitivePoint(p);
            if (k == 1) {
                moveNoPoint = true;
                moveX = ex;
                moveY = ey;
            }
            if (k == 0) {
                p.state = Point.STATE_CHECK;
                addPoint(p);
            }
        }

        if (finishedInput) {
            final int[] input = currentInput();
            if (patternChecker != null && patternChecker.checkInput(input)) {
                reset();
                post(new Runnable() {
                    @Override
                    public void run() {
                        onFinishedInput(input);
                    }
                });
            } else {
                showErrorAndReset();
            }
        }

        this.invalidate();
        return true;
    }
    
    private void onFinishedInput(int[] input) {
        if (patternChecker != null) {
            patternChecker.onCompleteInput(input);
        }
    }
    
    private void reset() {
        for (Point tp : sPoints) {
            tp.state = Point.STATE_NORMAL;
        }
        sPoints.clear();
    }
    
    /**
     * 显示错误输入状态,持续0.1秒,而后重置
     */
    public void showErrorAndReset() {
        for (Point tp : sPoints) {
            tp.state = Point.STATE_CHECK_ERROR;
        }
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
                invalidate();
            }
        }, 100L);
    }
    
    private int repetitivePoint(Point p) {
        if (sPoints.contains(p)) {
            return 1;
        } else {
            return 0;
        }
    }
    
    private void addPoint(Point p) {
        this.sPoints.add(p);
    }
    
    private Point selectPoint(float ex, float ey) {
        Point p;
        for (Point[] mPoint : mPoints) {
            for (Point aMPoint : mPoint) {
                p = aMPoint;
                if (p != null) {
                    if (distance(p.x, p.y, ex, ey) < dis) {
                        return p;
                    }
                }
            }
        }
        
        return null;
    }
    
    private static float distance(float tpx, float tpy, float px, float py) {
        return (float) Math.hypot(tpx - px, tpy - py);
    }
    
    /**
     * 获取当前用户手势序列
     *
     * @return 选中点的index序列
     */
    private int[] currentInput() {
        int[] result = new int[sPoints.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = sPoints.get(i).index;
        }
        return result;
    }
    
    public void setPatternChecker(LockPatternChecker patternChecker) {
        this.patternChecker = patternChecker;
    }
    
    public interface LockPatternChecker {
        /**
         * 已完成一次手势输入,检查其结果
         *
         * @param input 输入
         */
        void onCompleteInput(int[] input);
        
        /**
         * 此方法在{@link #onTouchEvent(MotionEvent)}中调用,
         * 检查输入以决定是否显示错误闪动
         *
         * @param input 输入
         * @return 是否符合要求 false则闪动输入结果并重置, true直接重置并回调#onCompleteInput
         */
        boolean checkInput(int[] input);
    }
    
    private static class Point {
        static final int STATE_NORMAL      = 0;
        static final int STATE_CHECK       = 1;
        static final int STATE_CHECK_ERROR = 2;
        float x, y;
        int state = 0;
        int index = 0;
        
        Point() {
            
        }
        
        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        public void set(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
