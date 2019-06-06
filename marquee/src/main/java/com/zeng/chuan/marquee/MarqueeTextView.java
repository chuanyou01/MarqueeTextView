package com.zeng.chuan.marquee;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;


public class MarqueeTextView extends FrameLayout {

    private static int TIME_REFRESH = 30;
    private static int TIME_WAIT = 4000;
    public static final int INFINITE = -1;

    private boolean mIsNeadMarquee;
    private int mOffset;
    private int mSpeed = 1;//使用step的地方改为dp2px
    private boolean isUpdate;
    private int mAlign;
    private float mTextSize;
    private CharSequence mText = "";
    private int mTextColor = Color.YELLOW;
    private int textWidth;

    private boolean mAutoStart = true;//默认开始滚动，可以在settext之前设置控制
    private int scrollTime = -1;//滚动一遍所需时间,毫秒,文字短则不需要滚动
    private boolean loop = true;//是否循环，默认循环，可通过settext控制
    private int mMarqueeTimes;
    private int mCurPlayTimes = 0;


    private int mShadowColor;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;

    private FrameLayout mFrameLayout;
    private TextView mTextView;
    private Handler mHandler;

    public interface ScrollListener {
        void onScrollStart();

        void onScrllEnd();
    }

    public MarqueeTextView(Context context) {
        super(context);
        init(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        try {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeText);
            mAlign = a.getInteger(R.styleable.MarqueeText_textAlign, 0);
            float defalutSize = spToPixel(getContext(), 12f);
            mTextSize = a.getDimension(R.styleable.MarqueeText_textSize, defalutSize);
            mTextColor = a.getColor(R.styleable.MarqueeText_textColor, mTextColor);

            mAutoStart = a.getBoolean(R.styleable.MarqueeText_autoStart, true);
            mSpeed = a.getDimensionPixelSize(R.styleable.MarqueeText_speed, 1);
            mMarqueeTimes = a.getInt(R.styleable.MarqueeText_marqueeTimes, -1);
            mText = a.getText(R.styleable.MarqueeText_text);
            mShadowColor = a.getColor(R.styleable.MarqueeText_shadowColor, Color.BLACK);
            mShadowDx = a.getFloat(R.styleable.MarqueeText_shadowDx, 0);
            mShadowDy = a.getFloat(R.styleable.MarqueeText_shadowDy, 0);
            mShadowRadius = a.getFloat(R.styleable.MarqueeText_shadowRadius, 0);


            a.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mHandler == null) {
            mHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    play();
                    return false;

                }
            });
        }

        if (mTextView == null) {
            mTextView = new TextView(getContext());
            mTextView.setTextColor(mTextColor);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            mTextView.setTextAlignment(mAlign);
            mTextView.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
            mTextView.setSingleLine();

            mFrameLayout = new FrameLayout(getContext());
            mFrameLayout.addView(mTextView);
            addView(mFrameLayout);
            setText(mText);
        }


    }

    protected void play() {
        if (scrollTime == -1) {
            scrollTime = (textWidth + getWidth()) / mSpeed * TIME_REFRESH;
        }

        if (isUpdate) {
            mIsNeadMarquee = isNeadMarquee(getText());
        }
        if (mIsNeadMarquee) {
            if (mOffset < -1 * (textWidth)) {
                mOffset = getWidth() - getWidth() % mSpeed;//文字移动到最后一个之后，重新从右边进来，形成跑马灯效果
            } else {
                mOffset -= mSpeed;//改变偏移量，形成左移效果
            }

            Log.e("text", "offset: " + mOffset);
            if (mOffset == 0) {
                if (INFINITE != mMarqueeTimes) {
                    mCurPlayTimes++;
                    if (mCurPlayTimes == mMarqueeTimes) {
                        if (scrollListener != null)
                            scrollListener.onScrllEnd();
                        return;
                    }
                }
            }

            if (mAutoStart) {
                selfInvalidateDelay(mOffset == 0 ? TIME_WAIT : TIME_REFRESH);
            }
        }

        mTextView.setTranslationX(mOffset);
    }


    public void setText(int res, int times) {
        mMarqueeTimes = times;
        setText(res, INFINITE);
    }

    public void setText(int res) {
        setText(getContext().getString(res));
    }

    public void setText(CharSequence str, int times) {
        mMarqueeTimes = times;
        setText(str);
    }

    public void setText(CharSequence str) {
        scrollTime = -1;
        mOffset = 0;
        mTextView.setText(str);

        mText = TextUtils.isEmpty(str) ? "" : str;

        textWidth = (int) measureText();
        isUpdate = true;
        LayoutParams layoutParams = (LayoutParams) mFrameLayout.getLayoutParams();
        layoutParams.width = textWidth;
        layoutParams.height = -1;
        mFrameLayout.setLayoutParams(layoutParams);

        selfInvalidateDelay(TIME_WAIT);
    }

    private void selfInvalidateDelay(long minisecond) {
        if (mHandler != null) {
            if (!mHandler.hasMessages(0)) {
                mHandler.sendEmptyMessageDelayed(0, minisecond);
            }

        }
    }

    private boolean isNeadMarquee(CharSequence text) {
        if (getWidth() == 0) {
            return false;
        }
        isUpdate = false;
        mCurPlayTimes = 0;
        return textWidth > getWidth();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsNeadMarquee = false;
        isUpdate = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

    }


    private float spToPixel(Context context, Float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }

    public void startScroll() {
        mAutoStart = true;
        selfInvalidateDelay(30);
        if (scrollListener != null) {
            scrollListener.onScrollStart();
        }
    }

    public void stopScroll() {
        mAutoStart = false;
        if (scrollListener != null) {
            scrollListener.onScrllEnd();
        }
    }

    public void setTextColor(String color) {
        try {
            if (mTextView != null) {
                int textColor = Color.parseColor(color);
                mTextView.setTextColor(textColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTextColor(@ColorRes int color) {
        if (mTextView != null) {
            mTextView.setTextColor(getContext().getResources().getColor(color));
        }
    }

    private float measureText() {
        return Layout.getDesiredWidth(mTextView.getText(), mTextView.getPaint());
    }

    public CharSequence getText() {
        if (mTextView != null) {
            return mTextView.getText();
        }
        return "";
    }


    /**
     * 获得跑马灯时间
     *
     * @return
     */
    public long calcScrollTime() {
        float viewWidth = getMeasuredWidth();
        float textlength = measureText();
        //文字长度小于控件宽度，不循环
        if (viewWidth >= textlength) {
            return 0;
        }
        //文字长度大于控件宽度循环，且跑马灯路程为文字长度(循环到最后一个字出现就停止)
        return (int) (textlength - viewWidth) / mSpeed * TIME_REFRESH;
    }

    public long getScrollTime(float totalDistances) {
        return (int) totalDistances / mSpeed * TIME_REFRESH;
    }

    public int getScrollTime() {
        return scrollTime;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        selfInvalidateDelay(30);
    }

    public void setShadowLayer(@ColorInt int color, float radius, float dx, float dy, int shadowColor) {
        if (mTextView != null) {
            mTextView.setTextColor(color);
            mTextView.setShadowLayer(radius, dx, dy, shadowColor);
        }
    }


    public ScrollListener getScrollListener() {
        return scrollListener;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    private ScrollListener scrollListener;

    /**
     * 每一步的步长，需要dp单位改为px的值
     *
     * @param speed 步长的px
     */
    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public TextView getTextView() {
        return mTextView;
    }
}
