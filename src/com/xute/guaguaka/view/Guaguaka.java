/*
 * Copyright (C), 2014-2015, 联创车盟汽车服务有限公司
 * FileName: Guaguaka.java
 * Author:   xutework
 * Date:     2015-4-11 下午7:32:38
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.xute.guaguaka.view;

import com.xute.guaguaka.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 * 
 * @author xutework
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Guaguaka extends View {

    private Paint mOutterPaint;

    private Path mPath;

    private Canvas mCanvas;

    private Bitmap mBitmap;

    private int mLastX;
    private int mLastY;

    // -------------------------
    private Bitmap bitmap;

    private String mText;

    private Paint mBackPaint;

    /**
     * 记录刮奖信息文本的宽和高
     */
    private Rect mTextBound;

    private int mTextSize;
    private int mTextColor;

    private volatile boolean mCompelte = false;

    public interface OnGuaGuaKaCompleteListener {
        void complete();
    }

    private OnGuaGuaKaCompleteListener listener;

    public void setOnGuaGuaKaCompleteListener(OnGuaGuaKaCompleteListener listener) {
        this.listener = listener;
    }

    private Bitmap mOutImage;

    /**
     * @param context
     * @param attrs
     */
    public Guaguaka(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public Guaguaka(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        init();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GuaGuaKa, defStyleAttr, 0);

        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.GuaGuaKa_text:

                    mText = a.getString(attr);

                    break;
                case R.styleable.GuaGuaKa_textSize:

                    mTextSize = (int) a.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22,
                            getResources().getDisplayMetrics()));

                    break;
                case R.styleable.GuaGuaKa_textColor:
                    
                    mTextColor = a.getColor(attr, 0x000000);

                    break;

                default:
                    break;
            }
        }

        a.recycle();

        
    }

    /**
     * @param context
     */
    public Guaguaka(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        setupOutpaint();
        setupBackPaint();

        // mCanvas.drawColor(Color.parseColor("#c0c0c0"));
        mCanvas.drawRoundRect(new RectF(0, 0, width, height), 30, 30, mOutterPaint);
        mCanvas.drawBitmap(mOutImage, null, new Rect(0, 0, width, height), null);

    }

    /*
     * (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        // canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawText(mText, getWidth() / 2 - mTextBound.width() / 2, getHeight() / 2 + mTextBound.height() / 2,
                mBackPaint);

        if (mCompelte) {
            if (listener != null) {
                listener.complete();
            }
        }

        if (!mCompelte) {
            drawPath();
            canvas.drawBitmap(mBitmap, 0, 0, null);

        }
    }

    /**
     * 功能描述: <br>
     * 〈功能详细描述〉
     * 
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private void drawPath() {
        // TODO Auto-generated method stub
        mOutterPaint.setStyle(Style.STROKE);
        mOutterPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));

        mCanvas.drawPath(mPath, mOutterPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int action = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = Math.abs(x - mLastX);
                int dy = Math.abs(y - mLastY);

                if (dx > 3 || dy > 3) {
                    mPath.lineTo(x, y);
                }

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                new Thread(mRunnable).start();

                break;
            default:
                break;
        }

        invalidate();
        return true;
    }

    /**
     * 功能描述: <br>
     * 〈功能详细描述〉
     * 
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private void setupOutpaint() {
        mOutterPaint.setColor(Color.parseColor("#c0c0c0"));
        mOutterPaint.setAntiAlias(true);
        mOutterPaint.setDither(true);
        mOutterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOutterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutterPaint.setStyle(Style.FILL);
        mOutterPaint.setStrokeWidth(20);
    }

    private void setupBackPaint() {
        mBackPaint.setColor(mTextColor);
        mBackPaint.setStyle(Style.FILL);
        mBackPaint.setTextSize(mTextSize);
        mBackPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    /**
     * 功能描述: <br>
     * 〈功能详细描述〉
     * 
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private void init() {
        mOutterPaint = new Paint();
        mPath = new Path();

        mOutImage = BitmapFactory.decodeResource(getResources(), R.drawable.fg_guaguaka);

        mText = "谢谢惠顾";
        mTextBound = new Rect();
        mBackPaint = new Paint();

        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22,
                getResources().getDisplayMetrics());
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int w = getWidth();
            int h = getHeight();

            float wipeArea = 0;
            float totalArea = w * h;
            Bitmap bitmap = mBitmap;
            int[] mPiexls = new int[w * h];

            bitmap.getPixels(mPiexls, 0, w, 0, 0, w, h);

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int index = i + j * w;

                    if (mPiexls[index] == 0) {
                        wipeArea++;
                    }
                }
            }

            if (wipeArea > 0 && totalArea > 0) {
                int percent = (int) (wipeArea * 100 / totalArea);
                Log.v("TAG", percent + "");
                if (percent > 60) {
                    mCompelte = true;
                    postInvalidate();

                }
            }
        }
    };
}
