package com.ricky.md.customwidget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


//轮播图广告

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private int[] imageResIds;
    private String[] contentDescs;
    private List<ImageView> list;
    private Context mContext;
    private TextView tv_desc;
    private LinearLayout ll_point_container;
    private List<View> pointList;
    private int lastPosition=0;
    private boolean isRunning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        initUI();
        initData();
        initAdapter();
        //自动轮询
        new Thread(new Runnable() {
            @Override
            public void run() {

                isRunning=true;

                while (true){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //在主线程更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        isRunning=false;
        super.onDestroy();
    }

    private void initAdapter() {

        viewPager.setAdapter(new MyAdapter());


        // 默认设置到中间的某个位置,将其放到Integer.MAX_VALUE中间值，并且起始位置%5为0
        viewPager.setCurrentItem(50000);



        //设置viewPage的滚动监听器
        viewPager.setOnPageChangeListener(this);

    }

    private void initData() {
        imageResIds = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
        contentDescs = new String[]{
                "巩俐不低俗，我就不能低俗",
                "扑树又回来啦！再唱经典老歌引万人大合唱",
                "揭秘北京电影如何升级",
                "乐视网TV版大派送",
                "热血屌丝的反杀"
        };

        list = new ArrayList<ImageView>();
        ImageView imageView;

        View pointView;
        pointList = new ArrayList<View>();

        int i=0;
        for (int image:imageResIds){
            imageView = new ImageView(mContext);
            imageView.setImageResource(image);

            pointView=new View(mContext);
            pointView.setBackgroundResource(R.drawable.selector_bg_point);

            pointList.add(pointView);
            list.add(imageView);

            //注意：此时是在LinearLayout布局中添加子控件，所以可是对该控件进行参数设置
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10,10);
            if (i!=0&&i!=list.size()){
                layoutParams.leftMargin=20;
            }
            if (i==0){
                pointView.setEnabled(true);
                tv_desc.setText(contentDescs[0]);
            }else {
                pointView.setEnabled(false);
            }
            ll_point_container.addView(pointView,layoutParams);

            i++;
        }

    }

    private void initUI() {
        viewPager = findViewById(R.id.viewpager);

        //设置1：左1 中1 右1              object数组中保存2个view对象
        //设置2：左边2个 中间1个 右边2个    object数组中保存5个view对象
//        viewPager.setOffscreenPageLimit(1);

        tv_desc = findViewById(R.id.tv_desc);
        ll_point_container = findViewById(R.id.ll_point_container);


    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        // 滚动时调用
    }

    @Override
    public void onPageSelected(int i) {
        // 新的条目被选中时调用

        int newPosition=i%5;

        tv_desc.setText(contentDescs[newPosition]);

        pointList.get(newPosition).setEnabled(true);
        pointList.get(lastPosition).setEnabled(false);
        lastPosition=newPosition;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        // 滚动状态变化时调用
    }

    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view==o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView=list.get(position%5);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
