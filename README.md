# ListView基础与拓展

---

## 基本的ListView

1. 首先在布局文件中添加ListView控件，指定id并将宽/高都设为match_parent。
    ```java
    <ListView
        android:id="@+id/list_item"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </ListView>
    
    ```
2. 创建项目实体类，例如想让ListView显示水果名字与图片，就新建Fruit类，实现相应的构造函数与get方法。
    ```java
    public class Fruit {
        private String name;
        private int imageId;
    
        public Fruit(String name, int imageId){
            this.name = name;
            this.imageId = imageId;
        }
    
        public String getName() {
            return name;
        }
    
        public int getImageId() {
            return imageId;
        }
    
    }
    
    ```
3. 为ListView子项建立布局，如为Fruit新建一个LinearLayout，水平方向显示一个ImageView与一个TextView。
    ```java
    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="horizontal" android:layout_width="match_parent"
        android:layout_height="match_parent">
    
        <ImageView
            android:id="@+id/fruit_image"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />
    
        <TextView
            android:id="@+id/fruit_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_marginLeft="10dp"
            />
    </LinearLayout>
    
    ```
4. 建立自定义适配器，继承自ArrayAdapter，将泛型指定为2的实体类，如Fruit类。
    - 在适配器中重写构造函数，将context、子项布局id和数据传进来。
    - 重写getView方法，此方法在每个子项被滚动到屏幕内时被调用。首先通过``getItem(position)``得到当前项的实例，然后使用LayoutInflate为子项加载布局，然后调用View的``findViewById()``获取ImageView和TextView的实例，并分别调用``setImageResource()`` ``setText()``设置内容，然后将布局返回。
    ```java
    public class FruitAdapter extends ArrayAdapter<Fruit> {
        private int resourceId;
        public FruitAdapter(Context context, int textViewResourceId, List<Fruit> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Fruit fruit = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            TextView fruitName = (TextView) view.findViewById(R.id.fruit_name);
            fruitImage.setImageResource(fruit.getImageId());
            fruitName.setText(fruit.getName());
            return view;
        }
    }
    
    ```
5. 在MainActivity中创建实例列表fruitList，创建Fruit实例并将其加入fruitList；创建适配器实例和ListView实例，将适配器传入ListView。
    ```java
    public class MainActivity extends AppCompatActivity {
        private List<Fruit> fruitList = new ArrayList<>();
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            FruitAdapter adapter = new FruitAdapter(MainActivity.this, R.layout.fruit_item, fruitList);
            initFruits();
            ListView listView = (ListView) findViewById(R.id.list_item);
            listView.setAdapter(adapter);
    
        }
    
        private void initFruits() {
            Fruit apple = new Fruit("apple", R.mipmap.apple);
            fruitList.add(apple);
            Fruit banana = new Fruit("banana", R.mipmap.banana);
            fruitList.add(banana);
            ...
        }
    }
    
    ```
6. 为子项添加点击事件
    ```java
    listView.setOnItemClickListener(new OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Fruit fruit = fruitList.get(position); //获取点击的子项
            Toast.makeText(MainActivity.this, fruit.getName(), Toast.LENGTH_SHORT).show();
        }
    });
    
    ```
    
---

## ListView的优化

### 提升ListView的运行效率

- 在适配器的``getView()``方法中每次都将布局重新加载了一遍，当ListView快速滚动时就会成为性能的瓶颈。可以使用convertView参数将之前加载好的布局进行缓存重用:
    ```java
    public class FruitAdapter extends ArrayAdapter<Fruit> {
        ...
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Fruit fruit = getItem(position);
            
            //View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            } else {
                view = convertView;
            }
            
            ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            TextView fruitName = (TextView) view.findViewById(R.id.fruit_name);
            fruitImage.setImageResource(fruit.getImageId());
            fruitName.setText(fruit.getName());
            return view;
        }
    }
    
    ```
- 现在已不会再重复加载布局，但在getView中每次还是会调用View的findViewById方法获取一次控件的实现。可以新建一个ViewHolder类对控件的实例进行缓存，从而对这部分性能进行优化：
    ```java
    public class FruitAdapter extends ArrayAdapter<Fruit> {
        ...
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Fruit fruit = getItem(position);
            View view;
            
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                viewHolder = new ViewHolder();
                viewHolder.fruitImage = (ImageView)findViewById(R.id.fruit_image);
                viewHolder.fruitName = (TextView)findViewById(R.id.fruit_name);
                view.setTag(viewHolder); //将viewHolder存储在View中
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag(); //重新获取ViewHolder
            }
            viewHolder.fruitImage.setImageResource(fruit.getImageId());
            viewHolder.fruitName.setText(fruit.getName());
            return view;
        }
        
        class ViewHolder{
            ImageView fruitImage;
            TextView fruitName;
        }
    }
    
    ```
    
### 一些额外设置

1. 设置项目分割线：
    ```java
    android:divider="@android:color/darker_gray"
    android:dividerHeight="10dp"
    
    ```
    - ``android:divider="@null"``将分割线设为透明
2. 隐藏滚动条
    ```java
    android:scrollbars="none"
    
    ```
3. 取消默认的Item点击效果
    ```java
    android:listSelector="#00000000" //或直接使用自带的透明色"@android:color/transparent"
    
    ```
4. 动态修改ListView
    ```java
    //按钮动态添加Item，添加的Item必须与原Item是同类型
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
    
    ```
5. 便利ListView中所有Item
    ```java
    for(int i = 0; i < listView.getChildCount(); i++){
        View view = listView.getChildAt(i)
    }
    
    ```

---

## ListView的拓展

---

### ListView的滑动监听

1. OnTouchListener
    ```java
    listView.setOnTouchListener(new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    ...
                    break;
                case MotionEvent.ACTION_MOVE:
                    ...
                    break;
                case MotionEvent.ACTION_UP:
                    ...
                    break;
            }
            return false;
        }
    }
    
    ```
2. OnScrollListener
    ```java
    listView.setOnScrollListener(new OnScrollListener(){
       @Override
       public void onScrollStateChanged(AbsListView view, int scrollState){
            switch(scrollState){
                case OnScrollListener.SCROLL_STATE_IDLE:
                    //滑动停止时
                    break；
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    //正在滚动时
                    break；
                case OnScrollListener.SCROLL_STATE_FLING:
                    //用力抛动，手指离开后ListView由于惯性继续滚动
                    break；
            }
       }
       
       @Override
       //后三个int类型的参数分别为 当前可见的第一个Item的ID，可见的Item总数，整个List的Item总数
       public void onScroll(AbsListView view, int firstVisibleItem, 
            int VisibleItemCount, int totalItemCount){
                //滑动时一直调用
                //可通过判断三个int参数是否达到某条件，来做出相应的响应。如是否到达最后一个Item等。
            }
    });
    
    ```

---

### 具有弹性的ListView
- 可以通过增加HeaderView或使用ScrollView嵌套实现，这里使用一种更为简单的方法来实现，即重写overScrollBy方法，将maxOverScrollY改为设置的值即可。但可定制性与显示效果不如其他方式。
    ```java
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollRangeX, int scrollRangeY, 
                int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent){
            return super.overScrollBy(deltaX, deltaY, scrollRangeX, scrollRangeY, 
                maxOverScrollX, maxOverScrollY, isTouchEvent);        
        }
    
    ```
    
### 自动隐藏/显示Toolbar

- 主要是通过监听当前滑动状态，判断是否隐藏/显示标题栏和按钮。
- 隐藏/显示Toolbar可以采用简单的动画来实现。
    
---

参考：
《第一行代码》
《Android群英传》
