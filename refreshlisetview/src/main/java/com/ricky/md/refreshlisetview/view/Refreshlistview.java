package com.ricky.md.refreshlisetview.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ricky.md.refreshlisetview.R;

import java.text.SimpleDateFormat;

public class Refreshlistview extends ListView implements AbsListView.OnScrollListener {

    private View headerView;
    private float downY;
    private float moveY;
    private int height;

    public static final int PULL_TO_REFRESH = 0;// 下拉刷新
    public static final int RELEASE_REFRESH = 1;// 释放刷新
    public static final int REFRESHING = 2; // 刷新中
    private int currentState = PULL_TO_REFRESH; // 当前刷新模式
    private RotateAnimation uprotateAnimation;
    private RotateAnimation downrotateAnimation;

    private View mArrowView;		// 箭头布局
    private TextView mTitleText;	// 头布局标题
    private ProgressBar pb;			// 进度指示器
    private TextView mLastRefreshTime; // 最后刷新时间
    private OnRefreshListener onRefreshListener=null;
    private View mFooterView;
    private int mFooterViewHeight;

    private boolean isLoadingMore=false; // 是否正在加载更多

    public Refreshlistview(Context context) {
        super(context);
        init();
    }
    public Refreshlistview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Refreshlistview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Refreshlistview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //创建头部加载
        initHeaderView();

        //初始化动画对象
        initAnimation();

        //创建脚部加载
        initFooterView();

        //设置listView的滑动监听事件		setOnScrollListener(this);
        setOnScrollListener(this);
    }

    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.layout_footer_list, null);

        //获取高度
        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();

        // 隐藏脚布局
        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
        //将布局添加进去
        addFooterView(mFooterView);
    }

    private void initAnimation() {
        //下拉动画-->向上箭头
        uprotateAnimation = new RotateAnimation(0f,-180f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        uprotateAnimation.setDuration(300);
        uprotateAnimation.setFillAfter(true); // 动画停留在结束位置
        //上-->下箭头
        downrotateAnimation = new RotateAnimation(-180f,-360f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        downrotateAnimation.setDuration(300);
        downrotateAnimation.setFillAfter(true); // 动画停留在结束位置
    }

    private void initHeaderView() {

        headerView = View.inflate(getContext(), R.layout.layout_header_list,null);
        mArrowView = headerView.findViewById(R.id.iv_arrow);
        pb = headerView.findViewById(R.id.pb);
        mTitleText =  headerView.findViewById(R.id.tv_title);
        mLastRefreshTime =  headerView.findViewById(R.id.tv_desc_last_refresh);

        //因为所有继承View、viewGroup的控件，onMeasure（获得尺寸）都是在onResume之后获得的，而该方法被构造函数调用，所以需要提前获取控件尺寸
        // 前提：提前手动测量宽高
        headerView.measure(0, 0);// 按照设置的规则测量
        //1.该高度是在页面中真是显示的高度
//        headerView.getHeight();
        //2.该方法获取的是其图片自身的高度
        height = headerView.getMeasuredHeight();

        // 设置内边距, 可以隐藏当前控件 , -自身高度
        headerView.setPadding(0,-height,0,0);

        // 在设置数据适配器之前执行添加 头布局/脚布局 的方法.
        addHeaderView(headerView);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = event.getRawY();

                //判断状态是否是 正在刷新中
                if (currentState==REFRESHING){
                    //直接执行父类方法
                    return super.onTouchEvent(event);
                }

                float offset=moveY-downY;
                float movePadding=(-height)+offset;
                //只有往下移动，并且是在list第一个条目时
                if (offset>0&&getFirstVisiblePosition()==0){
                    headerView.setPadding(0, (int) movePadding,0,0);

                    if (movePadding>=0&&currentState!=RELEASE_REFRESH){
                        //将模式改为释放刷新
                        currentState=RELEASE_REFRESH;
                        updateHeader(); // 根据最新的状态值更新头布局内容
                    }else if (movePadding<0&&currentState!=PULL_TO_REFRESH){
                        //改为下拉刷新
                        currentState=PULL_TO_REFRESH;
                        updateHeader(); // 根据最新的状态值更新头布局内容
                    }
                    // 当前事件被我们处理并消费
                    return true;

                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentState==RELEASE_REFRESH){
                    //用户要求了刷新
                    headerView.setPadding(0, 0,0,0);

                    currentState=REFRESHING;
                    updateHeader();

                }else if (currentState==PULL_TO_REFRESH){
                    headerView.setPadding(0, -height,0,0);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateHeader() {
        switch (currentState){
            case RELEASE_REFRESH:
                // 做动画, 改标题
                mArrowView.startAnimation(uprotateAnimation);
                pb.setVisibility(View.INVISIBLE);
                mTitleText.setText("释放刷新");
                break;
            case PULL_TO_REFRESH:
                mArrowView.startAnimation(downrotateAnimation);
                mTitleText.setText("下拉刷新");
                pb.setVisibility(View.INVISIBLE);
                break;
            case REFRESHING:
                //更新刷新动画
                //1.删除箭头动画
                mArrowView.clearAnimation();
                mArrowView.setVisibility(View.INVISIBLE);

                //设置圆圈动画效果
                pb.setVisibility(View.VISIBLE);

                //2.修改声明
                mTitleText.setText("正在刷新中.......");

                //进行网络数据的加载
                if (onRefreshListener!=null){
                    onRefreshListener.onRefresh();
                }

                break;
        }
    }

    public void onRefreshComplete() {
        if (isLoadingMore){
            mFooterView.setPadding(0,-mFooterViewHeight,0,0);
            isLoadingMore=false;
        }else {
            currentState=PULL_TO_REFRESH;
            mTitleText.setText("下拉刷新");
            headerView.setPadding(0, -height,0,0);
            pb.setVisibility(View.INVISIBLE);
            mArrowView.setVisibility(View.VISIBLE);

            mLastRefreshTime.setText("最后刷新时间: " + getTime());
        }

    }

    //获取当前时间
    private String getTime() {

        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(currentTimeMillis);
    }


//    public static int SCROLL_STATE_IDLE = 0; // 空闲  手离开了屏幕
//    public static int SCROLL_STATE_TOUCH_SCROLL = 1; // 触摸滑动
//    public static int SCROLL_STATE_FLING = 2; // 滑翔
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        System.out.println("---------------------scrollState="+scrollState);
        if (scrollState==SCROLL_STATE_IDLE&&getLastVisiblePosition() >= (getCount() - 1)){
            if (isLoadingMore){
                return;
            }
            isLoadingMore = true;
            mFooterView.setPadding(0,0,0,0);

            setSelection(getCount()); // 跳转到最后一条, 使其显示出加载更多.

            if (onRefreshListener!=null){
                onRefreshListener.loadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public interface OnRefreshListener{
        void onRefresh(); // 下拉刷新

        //加载更多
        void loadMore();
    }
    public void setRefreshListener(OnRefreshListener onRefreshListener){
        this.onRefreshListener=onRefreshListener;
    }




}
