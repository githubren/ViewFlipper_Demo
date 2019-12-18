package com.example.y.viewflipper_demo;

import android.annotation.SuppressLint;
import android.gesture.GestureOverlayView;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity /*implements View.OnTouchListener*/ {
    private ViewFlipper mViewFlipperTop;
    private ViewFlipper mViewFlipperBot;
    private LinearLayout mIndicatorContainer;
    private GestureDetector mDetector;

    private static final int FLING_MIN_DISTANCE = 100;
    private static final int FLING_MIN_VELOCITY = 200;
    private float mStartX;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initTopViewData();
        setTopViewConfig();
        initBotViewData();
        setBotViewConfig();
    }

    /**
     * 设置左右切换ViewFlipper控件配置
     */
    private void setTopViewConfig() {
        mViewFlipperTop.setInAnimation(this,R.anim.anim_right_in);//进场动画
        mViewFlipperTop.setOutAnimation(this,R.anim.anim_left_out);//出场动画
        mViewFlipperTop.setFlipInterval(2000);//设置切换之间间隔2s
        mViewFlipperTop.startFlipping();//开始

//        mViewFlipperTop.setOnTouchListener(this);
//        mDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
//
//            @Override
//            public boolean onDown(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {// Fling left
//                    mViewFlipperTop.showNext();
//                    Toast.makeText(MainActivity.this, "Fling Left", Toast.LENGTH_SHORT).show();
//                } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {// Fling right
//                    mViewFlipperTop.showPrevious();
//                    Toast.makeText(MainActivity.this, "Fling Right", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        });

        /*
            ViewFlipper控件没有子view切换监听
            所以在这对动画进行监听 实现和指示器的联动
         */
        mViewFlipperTop.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {//在动画开始监听中执行相关的逻辑
                for (int i = 0; i < mIndicatorContainer.getChildCount(); i++) {
                    if (i == mViewFlipperTop.getDisplayedChild()) {//获取当前展示的子View
                        int finalI = i;
                        //开启一个子线程 发送一个延迟事件 设置指示器被选中的效果
                        new Thread(() -> {
                            mHandler.postDelayed(()-> mIndicatorContainer.getChildAt(finalI).setSelected(true),500);
                        }).start();
                    } else {//其余的指示器设置为未选中效果
                        int finalI1 = i;
                        new Thread(() -> {
                            mHandler.postDelayed(()-> mIndicatorContainer.getChildAt(finalI1).setSelected(false),500);
                        }).start();
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 设置上下切换控件配置
     */
    private void setBotViewConfig() {
        mViewFlipperBot.setInAnimation(this,R.anim.anim_in);
        mViewFlipperBot.setOutAnimation(this,R.anim.anim_out);
        mViewFlipperBot.setFlipInterval(2000);
        mViewFlipperBot.startFlipping();
    }

    /**
     * 加载下面控件的数据
     */
    private void initBotViewData() {
        List<String> datas = new ArrayList<>();
        datas.add("语文");
        datas.add("数学");
        datas.add("外语");
        datas.add("物理");
        datas.add("化学");
        datas.add("生物");
        for (String data:datas) {
            View view = getLayoutInflater().inflate(R.layout.item_bot_view_flipper,null);
            TextView textView = view.findViewById(R.id.item_text);
            textView.setText(data);
            mViewFlipperBot.addView(view);
        }
    }

    /**
     * 加载上面控件的数据
     */
    private void initTopViewData(){
        List<Integer> datas = new ArrayList<>();
        datas.add(R.drawable.image1);
        datas.add(R.drawable.image2);
        datas.add(R.drawable.image3);
        datas.add(R.drawable.image4);
        datas.add(R.drawable.image5);
        for (int i = 0;i<datas.size();i++) {
            int data = datas.get(i);
            View view = getLayoutInflater().inflate(R.layout.item_top_view_flipper,null);
            int finalI = i;
            //设置子View的点击事件
            view.setOnClickListener(v -> {
                Toast.makeText(this, "position:"+ finalI, Toast.LENGTH_SHORT).show();
            });
            ImageView imageView = view.findViewById(R.id.item_top_image);
            //采用第三方库Glide加载图片
            Glide.with(this)
                    .load(data)
                    .into(imageView);
            mViewFlipperTop.addView(view);
            //根据数据size装填指示器
            initIndicator();
        }
        mIndicatorContainer.getChildAt(0).setSelected(true);
    }

    /**
     * 初始化指示器
     */
    private void initIndicator() {
        ImageView indicator = new ImageView(this);//创建ImageView对象
        indicator.setImageDrawable(getDrawable(R.drawable.circle_indicator_selector));
        //动态设置控件布局
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                         LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,0,0,0);
        indicator.setLayoutParams(params);
        mIndicatorContainer.addView(indicator);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mViewFlipperTop = findViewById(R.id.view_flipper_top);
        mViewFlipperBot = findViewById(R.id.view_flipper_bottom);
        mIndicatorContainer = findViewById(R.id.circle_indicator_container);
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return mDetector.onTouchEvent(event);
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                mStartX = event.getX();
//                mViewFlipperTop.stopFlipping();
//                break;
//            case MotionEvent.ACTION_UP:
//                if (event.getX() - mStartX > 100) {
//                    mViewFlipperTop.setInAnimation(this,R.anim.anim_left_in);
//                    mViewFlipperTop.setOutAnimation(this,R.anim.anim_right_out);
//                    mViewFlipperTop.showPrevious();
//                } else if (mStartX - event.getX() > 100) {
//                    mViewFlipperTop.setInAnimation(this,R.anim.anim_right_in);
//                    mViewFlipperTop.setOutAnimation(this,R.anim.anim_left_out);
//                    mViewFlipperTop.showNext();
//                }
//                new Thread(()->{
//                    mHandler.postDelayed(()->{
//                        mViewFlipperTop.setInAnimation(this,R.anim.anim_right_in);
//                        mViewFlipperTop.setOutAnimation(this,R.anim.anim_left_out);
//                        mViewFlipperTop.startFlipping();
//                    },2000);
//                }).start();
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
}
