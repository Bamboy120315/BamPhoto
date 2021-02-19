package com.bamboy.bimage.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.bamboy.bimage.R;

public class NowelSeekBar extends View {

    /**
     * 进度改变时的监听
     */
    private OnProgressListener mListener;
    /**
     * 当前进度
     */
    private long progress;
    /**
     * 总进度
     */
    private long mProgressMax;

    /**
     * View宽度
     */
    private int mViewWidth;
    /**
     * View高度
     */
    private int mViewHeight;

    /**
     * 进度条的长度
     */
    private float mProgressLenth;
    /**
     * 进度条绿色部分的长度
     */
    private int mFinishedLenth;
    /**
     * 最大阻力值
     */
    private int mMaxNowel = 151;
    /**
     * 阻力值
     */
    private int mNowel = 0;

    /**
     * 节点的颜色
     */
    private int mNounColor;
    /**
     * 完成的进度条的颜色
     */
    private int mFinishedColor;
    /**
     * 未完成的进度条的颜色
     */
    private int mUnfinishedColor;
    private float endRight;

    public NowelSeekBar(Context context) {
        super(context);
        initAttr(context, null);
    }

    public NowelSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public NowelSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            mProgressMax = 100;
            mFinishedColor = 0xFF009E96;
            mUnfinishedColor = 0xFFDDDDDD;
            return;
        }

        // -------------------- 获取自定义属性 --------------------
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NowelProgressBar);
        progress = typedArray.getInteger(R.styleable.NowelProgressBar_progress, 0);
        mProgressMax = typedArray.getInteger(R.styleable.NowelProgressBar_progressMax, 100);
        mNounColor = typedArray.getColor(R.styleable.NowelProgressBar_colorNoun, 0xFF009E96);
        mFinishedColor = typedArray.getColor(R.styleable.NowelProgressBar_colorFinished, 0xFF009E96);
        mUnfinishedColor = typedArray.getColor(R.styleable.NowelProgressBar_colorUnfinished, 0xFFDDDDDD);

        //initTouchListener();
    }

    private float eX = 0;
    private long nowProgtess;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 手指按下
            case MotionEvent.ACTION_DOWN:
                if (mFinishedLenth < event.getX() && event.getX() < mFinishedLenth + mViewHeight) {
                    mNowel = 1;
                    eX = event.getX();
                    nowProgtess = getProgress();

                    // 更新UI
                    invalidate();
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                break;

            // 手指移动
            case MotionEvent.ACTION_MOVE:
                if (mNowel == 0) {
                    break;
                }

                int nowel = 1;
                float eY = Math.abs(event.getY());
                if (eY > mViewHeight * 2) {
                    eY -= mViewHeight * 2;
                    nowel = (int) (eY * 10) / mViewHeight + 1;
                    if (nowel < 5)
                        nowel = 2;
                    else if (nowel < 10)
                        nowel = 3;
                    else if (nowel < 30)
                        nowel = nowel / 5 * 5;
                    else
                        nowel = nowel / 10 * 10 + 1;
                }

                if (nowel <= mMaxNowel && nowel != mNowel) {
                    mNowel = nowel;
                    eX = event.getX();
                    nowProgtess = getProgress();
                    // 更新UI
                    invalidate();
                } else {
                    int progtess = (int) (((event.getX() - eX) / (float) mNowel / (float) mViewWidth) * mProgressMax);
                    setProgress(nowProgtess + progtess);
                }

                return true;

            // 手指抬起
            case MotionEvent.ACTION_UP:
                if (mNowel != 0) {
                    mNowel = 0;

                    // 更新UI
                    invalidate();
                    return true;
                }
                break;
            default:
                break;
        }
        // super.onTouchEvent(event);
        return false;
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 手指按下
            case MotionEvent.ACTION_DOWN:
                if (mFinishedLenth < event.getX() && event.getX() < mFinishedLenth + mViewHeight){
                    mNowel = 1;
                    eX = event.getX();
                    nowProgtess = getProgress();

                    // 更新UI
                    invalidate();
                    return true;
                }
                break;

            // 手指移动
            case MotionEvent.ACTION_MOVE:
                if (mNowel == 0){
                    break;
                }

                int nowel = (int) (Math.abs(event.getY()) / mViewHeight + 1);
                if (nowel <= mMaxNowel && nowel != mNowel){
                    mNowel = nowel;
                    eX = event.getX();
                    nowProgtess = getProgress();
                } else {
                    int progtess = (int) (((event.getX() - eX) / (float) mNowel / (float) mViewWidth) * mProgressMax);
                    Log.i("-=-=-=-=", ""
                            +"\t\tevent.getX():"+event.getX()
                            +"\t\teX:"+eX
                            +"\t\tmNowel:"+mNowel
                            +"\t\tmViewWidth:"+mViewWidth
                            +"\t\tprogtess:"+progtess
                    );
                    setProgress(nowProgtess + progtess);
                }

                return true;

            // 手指抬起
            case MotionEvent.ACTION_UP:
                if (mNowel != 0) {
                    mNowel = 0;

                    // 更新UI
                    invalidate();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }*/

    private void initTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    // 手指按下
                    case MotionEvent.ACTION_DOWN:
                        if (mFinishedLenth < event.getX() && event.getX() < mFinishedLenth + mViewHeight) {
                            mNowel = 1;
                            eX = event.getX();
                            nowProgtess = getProgress();

                            // 更新UI
                            invalidate();
                            return true;
                        }
                        break;

                    // 手指移动
                    case MotionEvent.ACTION_MOVE:
                        if (mNowel == 0) {
                            break;
                        }

                        int nowel = (int) (Math.abs(event.getY()) / mViewHeight + 1);
                        if (nowel <= mMaxNowel && nowel != mNowel) {
                            mNowel = nowel;
                            eX = event.getX();
                            nowProgtess = getProgress();
                        } else {
                            int progtess = (int) (((event.getX() - eX) / (float) mNowel / (float) mViewWidth) * mProgressMax);
                            /*Log.i("-=-=-=-=", ""
                                    + "\t\tevent.getX():" + event.getX()
                                    + "\t\teX:" + eX
                                    + "\t\tmNowel:" + mNowel
                                    + "\t\tmViewWidth:" + mViewWidth
                                    + "\t\tprogtess:" + progtess
                            );*/
                            setProgress(nowProgtess + progtess);
                        }

                        return true;

                    // 手指抬起
                    case MotionEvent.ACTION_UP:
                        if (mNowel != 0) {
                            mNowel = 0;

                            // 更新UI
                            invalidate();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        // 计算进度条的长度
        mProgressLenth = mViewWidth - mViewHeight;
        // 计算进度条绿色部分的长度
        mFinishedLenth = (int) (mProgressLenth * ((float) progress / (float) mProgressMax));

        this.post(new Runnable() {
            @Override
            public void run() {
                // 更新UI
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 先重新计算进度条绿色部分长度
        mFinishedLenth = (int) ((mViewWidth - mViewHeight) * ((float) progress / (float) mProgressMax));
        // 线条的高度
        int lineHeight = (int) (((mNowel + mMaxNowel * 0.1f) / (mMaxNowel + mMaxNowel * 0.1f)) * mViewHeight);

        // -------------------- 先画灰色部分 --------------------
        // 构造灰色画笔
        Paint paintUnFinished = initPaint(mUnfinishedColor, Paint.Style.FILL, lineHeight);
        // 画进度条线条
        onDrawUnfinishedProgres(canvas, paintUnFinished);

        // -------------------- 然后画绿色部分 --------------------
        // 构造绿色画笔
        Paint paintFinished = initPaint(mFinishedColor, Paint.Style.FILL, lineHeight);
        // 画绿色进度条线条
        onDrawProgres(canvas, paintFinished);

        // 构造节点画笔
        Paint paintNoun = initPaint(mNounColor, Paint.Style.FILL, 0);
        // 画绿色节点
        onDrawNoun(canvas, paintNoun, 0, 1);
    }

    /**
     * 初始化画笔
     *
     * @param color 颜色
     * @param style 样式
     * @param width 轮廓宽度
     * @return 画笔
     */
    private Paint initPaint(int color, Paint.Style style, int width) {
        Paint paint = new Paint();
        // 设置消除锯齿
        paint.setAntiAlias(true);
        // 设置颜色
        paint.setColor(color);
        // 设置实心
        paint.setStyle(style);
        // 设置画笔的宽度
        paint.setStrokeWidth(width);
        // 设置线条圆角
        paint.setStrokeCap(Paint.Cap.ROUND);
        // 修改画笔的宽度
        paint.setStrokeWidth(width);
        return paint;
    }

    /**
     * 画未完成的灰色线条
     *
     * @return
     */
    private void onDrawUnfinishedProgres(Canvas canvas, Paint paint) {
        // 计算左上右下坐标
        float startX = mViewHeight / 2 + mFinishedLenth;
        float startY = mViewHeight / 2;
        float endX = mViewWidth - mViewHeight / 2;
        float endY = startY;

        // 绘制直线
        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * 画绿色进度条线条
     *
     * @return
     */
    private void onDrawProgres(Canvas canvas, Paint paint) {

        // 计算左上右下坐标
        float startX = mViewHeight / 2;
        float startY = startX;
        float endX = startX + mFinishedLenth;
        float endY = startY;

        // 绘制直线
        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * 画节点
     *
     * @return
     */
    private void onDrawNoun(Canvas canvas, Paint paint, int startIndex, int endIndex) {

        // 节点高度
        float nounHeight = mNowel == 0 ? mViewHeight * 0.65f : mViewHeight;

        // 计算左上右下坐标
        float startTop = (mViewHeight - nounHeight) / 2f;
        float endBottom = startTop + nounHeight;
        float startLeft = mViewHeight / 2f + mFinishedLenth - nounHeight / 2f;
        float endRight = startLeft + nounHeight;

        // 构造圆点
        RectF rectF = new RectF(startLeft, startTop, endRight, endBottom);

        // 画出圆点
        canvas.drawArc(rectF, 0, 360, false, paint);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(long progress) {
        setProgress(progress, -1, true);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(long progress, long progressMax) {
        setProgress(progress, -1, true);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(long progress, long progressMax, boolean triggerListener) {
        if (progressMax >= 0) {
            mProgressMax = progressMax;
        }

        if (progress < 0)
            progress = 0;

        if (progress > mProgressMax)
            progress = mProgressMax;

        if (progress == this.progress)
            return;

        this.progress = (int) progress;

        if (triggerListener && mListener != null)
            mListener.onProgress(this, this.progress);


        // Log.i("-=-=-=-= Player", "更新进度  progress：" + progress);

        // 更新UI
        invalidate();
    }

    /**
     * 获取进度
     *
     * @return 当前进度
     */
    public long getProgress() {
        return progress;
    }

    /**
     * 获取最大值
     *
     * @return 最大进度
     */
    public long getProgressMax() {
        return mProgressMax;
    }

    /**
     * 设置最大值
     *
     * @return 最大进度
     */
    public void setProgressMax(long progressMax) {
        mProgressMax = progressMax;

        // 更新UI
        invalidate();
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setProgressListener(OnProgressListener listener) {
        mListener = listener;
    }

    /**
     * 进度改变时的监听
     * <p>
     * Created by Bamboy on 2018/4/4.
     */
    public interface OnProgressListener {

        /**
         * 监听变化时回调
         *
         * @param progressBar
         * @param progress
         */
        void onProgress(NowelSeekBar progressBar, long progress);
    }
}
