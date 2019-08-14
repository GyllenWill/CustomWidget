package com.ricky.md.popupwindow;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private ListView listView;
    private List<String> list;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.et_input);

        findViewById(R.id.ib_dropdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

    }

    private void initPopupWindow() {

        //初始化listView
        initListView();

        popupWindow = new PopupWindow(listView, editText.getWidth(), 700);

        //点击popupWindow外部区域，可隐藏
        popupWindow.setOutsideTouchable(true);

        //设置空的背景，相应点击事件
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        //PopupWindow默认是不会获取焦点的，需要手动设置
        popupWindow.setFocusable(true);

        //位置设置，将popupWindow设置在editText控件的下方
        popupWindow.showAsDropDown(editText,0,-1);
    }

    private void initListView() {

        listView = new ListView(this);
        //取消条目之间的线
        listView.setDividerHeight(0);
        listView.setBackgroundResource(R.drawable.listview_background);

        list = new ArrayList<String>();
        for (int i=0;i<30;i++){
            String str="10086"+i;
            list.add(str);
        }

        listView.setAdapter(new MyAdapter());

        //设置listView的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //将条目中的数据写入EditView中
                editText.setText(list.get(position));

                //设置PopupWindow消失
                popupWindow.dismiss();

            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView= View.inflate(MainActivity.this,R.layout.item_number,null);
            }
            TextView tv_number =convertView.findViewById(R.id.tv_number);
            ImageButton imageButton=convertView.findViewById(R.id.ib_delete);
            tv_number.setText(list.get(position));

            //因为在这里获取的ImageButton，所以在这里设置点击事件
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击条目，将删除指定数据从list数据集中
                    list.remove(position);
                    //更新(刷新)数据集  之前是对adapter适配器进行刷新
                    notifyDataSetChanged();

                    //用户体验，判断数据是否为空，为空，则不展示popupWindow
                    if(list==null){
                        popupWindow.dismiss();
                    }

                }
            });


            return convertView;
        }
    }
}
