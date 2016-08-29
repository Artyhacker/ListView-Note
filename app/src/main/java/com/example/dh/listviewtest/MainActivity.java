package com.example.dh.listviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private List<Fruit> fruitList = new ArrayList<>();
    private Button addItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FruitAdapter adapter = new FruitAdapter(MainActivity.this, R.layout.fruit_item, fruitList);
        initFruits();
        final ListView listView = (ListView) findViewById(R.id.list_item);
        listView.setAdapter(adapter);

        //默认显示在第三项
        listView.setSelection(2);

        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Fruit fruit = fruitList.get(position);
                Toast.makeText(MainActivity.this, fruit.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        //滑动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                switch(scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //滑动停止时
                        Log.e("MainActivity","SCROLL_STATE_IDLE");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        //正在滚动时
                        Log.e("MainActivity","SCROLL_STATE_TOUCH_SCROLL");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        Log.e("MainActivity","SCROLL_STATE_FLING");
                        //用力抛动，手指离开后ListView由于惯性继续滚动
                        break;
                }
            }

            @Override
            //后三个int类型的参数分别为 当前可见的第一个Item的ID，可见的Item总数，整个List的Item总数
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int VisibleItemCount, int totalItemCount){
                Log.e("MainActivity","onScroll");
                //滑动时一直调用
                //可通过判断三个int参数是否达到某条件，来做出相应的响应。如是否到达最后一个Item等。
            }
        });


        //按钮动态添加
        addItem = (Button) findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fruit newFruit = new Fruit("new fruit", 0);
                fruitList.add(newFruit);
                adapter.notifyDataSetChanged();
                listView.setSelection(fruitList.size()-1);
            }
        });


    }



    private void initFruits() {
        Fruit apple = new Fruit("apple", R.mipmap.apple);
        fruitList.add(apple);
        Fruit banana = new Fruit("banana", R.mipmap.banana);
        fruitList.add(banana);
        Fruit blackberry = new Fruit("blackberry", R.mipmap.blackberry);
        fruitList.add(blackberry);
        Fruit cherries = new Fruit("cherries", R.mipmap.cherries);
        fruitList.add(cherries);
        Fruit coconut = new Fruit("coconut", R.mipmap.coconut);
        fruitList.add(coconut);
        Fruit grapes = new Fruit("grapes", R.mipmap.grapes);
        fruitList.add(grapes);
        Fruit kiwi = new Fruit("kiwi", R.mipmap.kiwi);
        fruitList.add(kiwi);
        Fruit lemon = new Fruit("lemon", R.mipmap.lemon);
        fruitList.add(lemon);
        Fruit lime = new Fruit("lime", R.mipmap.lime);
        fruitList.add(lime);
        Fruit orange = new Fruit("orange", R.mipmap.orange);
        fruitList.add(orange);
        Fruit peach = new Fruit("peach", R.mipmap.peach);
        fruitList.add(peach);
        Fruit strawberry = new Fruit("strawberry", R.mipmap.strawberry);
        fruitList.add(strawberry);
        Fruit watermelon = new Fruit("watermelon", R.mipmap.watermelon);
        fruitList.add(watermelon);
    }
}
