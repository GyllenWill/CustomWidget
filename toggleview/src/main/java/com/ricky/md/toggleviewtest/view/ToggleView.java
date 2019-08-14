package com.ricky.md.toggleviewtest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 自定义开关
 * @author poplar
 *
 * Android 的界面绘制流程
 * 测量			 摆放		绘制
 * measure	->	layout	->	draw
 * 	  | 		  |			 |
 * onMeasure -> onLayout -> onDraw 重写这些方法, 实现自定义控件
 *
 * onResume()之后执行
 *
 * View
 * onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)
 *
 * ViewGroup
 * onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)
 */

public class ToggleView extends View {

    private Bitmap bitmapBackground;
    private Bitmap bitmapSlide;
    private boolean open=false;
    private float rawX;
    private Paint paint;
    private OnSwitchStateUpdateListener onSwitchStateUpdateListener;

    /**
     * 用于代码创建控件
     * @param context
     */
    public ToggleView(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        paint = new Paint();

        //获取尺寸
        setMeasuredDimension(bitmapBackground.getWidth(),bitmapBackground.getHeight());
    }


    //设置触摸事件

    boolean isTouchMode=false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                System.out.println("-----------------进来onTouchEvent");
                isTouchMode=true;
                rawX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                rawX=event.getX();
                break;
            case MotionEvent.ACTION_UP:
                rawX=event.getX();
                isTouchMode=false;

                boolean status=rawX<bitmapBackground.getWidth()/2;

                if (onSwitchStateUpdateListener!=null&&status!=open){
                    onSwitchStateUpdateListener.onStateUpdate(status);
                }

                open=status;
                break;
        }

        // 重绘界面
        invalidate(); // 会引发onDraw()被调用, 里边的变量会重新生效.界面会更新

        //只有返回true，该事件才会被接收执行
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        canvas.drawBitmap(bitmapBackground,0,0, paint);

        //绘制滑块
        //判断用户的触摸的方式：拖着移动，移到哪里，滑块就跟着移动
        if (isTouchMode){
            //设置滑块不越界
            float newleft=rawX-bitmapSlide.getWidth()/2;
            float maxleft=bitmapBackground.getWidth()-bitmapSlide.getWidth();
            if (newleft<0){
                newleft=0;
            }else if (newleft>maxleft){
                newleft=maxleft;
            }
            canvas.drawBitmap(bitmapSlide,newleft,0,paint);
        }else {
            System.out.println("--------------open="+open);
            if (open){
                System.out.println("-------------------绘制滑块位置 open="+open);
                //绘制滑块,参数二、三：相对于左上角的顶点的距离
                canvas.drawBitmap(bitmapSlide,0,0, paint);
            }else {
                canvas.drawBitmap(bitmapSlide,bitmapBackground.getWidth()-bitmapSlide.getWidth(),0, paint);
            }
        }

    }

    /**
     * 用于在xml里使用, 可指定自定义属性
     * @param context
     * @param attrs
     */
    public ToggleView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        // 获取配置的自定义属性
        String namespace = "http://schemas.android.com/apk/com.ricky.md.toggleviewtest";
//        String switchBackgroundResource =attrs.getAttributeValue(namespace,"switch_background");
//        String slideButtonResource =attrs.getAttributeValue(namespace,"slide_button");
        String switch_background = attrs.getAttributeValue(namespace, "switch_background");
        int backgroud=Integer.valueOf(switch_background);
        String slide_button = attrs.getAttributeValue(namespace, "slide_button");
        int slidebutton=Integer.valueOf(slide_button);
//        int switchBackgroundResource = attrs.getAttributeResourceValue(namespace , "switch_background", -1);
//        int slideButtonResource = attrs.getAttributeResourceValue(namespace , "slide_button", -1);

        //获取属性值，进行操作
        open = attrs.getAttributeBooleanValue(namespace, "switch_state", false);
        setSwitchBackgroundResource(backgroud);
        setSlideButtonResource(slidebutton);
    }

    /**
     * 用于在xml里使用, 可指定自定义属性, 如果指定了样式(Style), 则走此构造函数
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ToggleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ToggleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 设置背景图
     * @param switchBackground
     */
    public void setSwitchBackgroundResource(int switchBackground) {

        bitmapBackground = BitmapFactory.decodeResource(getResources(),switchBackground);

    }

    /**
     * 设置滑块图片资源
     * @param slideButton
     */
    public void setSlideButtonResource(int slideButton) {

        bitmapSlide = BitmapFactory.decodeResource(getResources(),slideButton);
    }

    /**
     * 设置开关状态
     * @param open
     */
    public void setSwitchState(boolean open) {
        this.open=open;
        System.out.println("--------setSwitchState赋值---------open="+open);
    }


    //为控件设置点击事件监听
    public void setOnSwitchStateUpdateListener(OnSwitchStateUpdateListener onSwitchStateUpdateListener){
        //创建接口实例，进行逻辑处理，并对调用类进行状态反馈----------具体原理待学习------------------------------------------------
        this.onSwitchStateUpdateListener=onSwitchStateUpdateListener;

    }
    public interface OnSwitchStateUpdateListener{
        void onStateUpdate(boolean state);
    }

}
