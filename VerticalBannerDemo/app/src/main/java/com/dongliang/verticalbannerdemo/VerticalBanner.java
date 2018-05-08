package com.dongliang.verticalbannerdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 竖直轮播广告 （未优化）
 * created by dongliang
 * 2018/5/7  13:30
 */
public class VerticalBanner extends FrameLayout {
    private static String tag="VerticalBanner";
    private FrameLayout flFirst, flSecond;//用于存放view的FrameLayout
    private int during = 3000; //两秒轮播一次
    private int animTime = 1500; //滚动时间
    private int width, height;// banner宽高
    private Scroller mScroller;  //用于滚动scroller
    private Timer timer;
    private BannerAdapter bannerAdapter;
    private ItemClickListener itemClickListener;
    private int curPosition;//当前Item位置
    @SuppressWarnings("handlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int count =bannerAdapter.getCount();
            if (count < 2) {
                return;
            }
            //如果banner数量是奇数 需要特殊处理 如count=3
            // 则下标为0,1,2 想要循环起来 需要当下标到2时重新进行Update
            if (count%2!=0&&curPosition==count-1){
                updateView(flFirst);
                curPosition++;
                updateView(flSecond);
                mScroller.startScroll(0, 0, 0, height, animTime);
                return;
            }

            if (curPosition % 2 != 0) {
                curPosition++;
                updateView(flSecond);
            } else {
                curPosition++;
                updateView(flFirst);
            }
            mScroller.startScroll(0, 0, 0, height, animTime);

        }
    };

    public VerticalBanner(Context context) {
        super(context);
        init(context);
    }

    public VerticalBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerticalBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        flFirst = initContainerFrame(context);
        flSecond = initContainerFrame(context);
        addView(flFirst);
        addView(flSecond);

    }


    /**
     * 创建 frameLayout
     * created by dongliang
     * 2018/5/7  13:26
     */
    private FrameLayout initContainerFrame(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(lp);
        return frameLayout;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();

    }
    /**
     * onlayout未考虑 padding
     * created by dongliang
     *  2018/5/8  10:38
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (bannerAdapter != null) {
            Log.d(tag,"cur"+curPosition);
            if (curPosition % 2 == 0) {
                flFirst.layout(0, 0, r, height);  //
                flSecond.layout(0, height, r, height * 2);
            } else {
                flSecond.layout(0, 0, r, height);  //
                flFirst.layout(0, height, r, height * 2);
            }
        } else {
            flFirst.layout(0, 0, r, height);  //
            flSecond.layout(0, height, r, height * 2);
        }

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }

    }

    /**
     * 添加适配器
     * created by dongliang
     * 2018/5/7  16:21
     */
    public void setBannerAdapter(@NonNull BannerAdapter bannerAdapter) {
        stopLoop();
        curPosition = 0;
        this.bannerAdapter = bannerAdapter;
        flFirst.addView(bannerAdapter.getView(0));
        flSecond.addView(bannerAdapter.getView(0));
        startLoop(); //开始轮播
    }

    /**
     * 开始轮播
     * created by dongliang
     * 2018/5/7  13:46
     */
    public void startLoop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        },during, during + animTime);
    }

    /**
     * 暂停循环滚动 可以在onpause onstop等生命周期使用
     * created by dongliang
     * 2018/5/7  16:15
     */
    public void stopLoop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    private void updateView(FrameLayout frameLayout) {
        frameLayout.removeAllViews();
        curPosition = (curPosition) % bannerAdapter.getCount();

        frameLayout.addView(bannerAdapter.getView(curPosition));

    }


    public void setOnItemClickListener(final ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(curPosition);
            }
        });
    }
    public int getAnimTime() {
        return animTime;
    }

    public void setAnimTime(int animTime) {
        this.animTime = animTime;
    }

    public int getDuring() {
        return during;
    }

    public void setDuring(int during) {
        this.during = during;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public interface BannerAdapter {

        int getCount();
        @NonNull
        View getView(int pos);

    }
    public interface ItemClickListener{

       void onItemClick(int pos);
    }
}
