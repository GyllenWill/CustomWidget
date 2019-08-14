package com.ricky.md.refreshlisetview;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.ricky.md.refreshlisetview.view.Refreshlistview;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Refreshlistview listview;
    private List<String> list;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.listview);
        initData();
        initAdapter();

        listview.setRefreshListener(new Refreshlistview.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        list.add(0,"加载了一条数据");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();
                                listview.onRefreshComplete();
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void loadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        list.add("加载更多");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();
                                listview.onRefreshComplete();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void initAdapter() {
        myAdapter = new MyAdapter();
        listview.setAdapter(myAdapter);
    }

    private void initData() {
        list = new ArrayList<String>();
        for (int i=0;i<30;i++){
            list.add("list条目"+i);
        }
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
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView=new TextView(getApplicationContext());
            textView.setText(list.get(position));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(40);
            return textView;
        }
    }

}
