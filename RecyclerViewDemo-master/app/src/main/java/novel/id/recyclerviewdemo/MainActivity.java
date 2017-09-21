package novel.id.recyclerviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<CategoryBean> mCategoryBean = new ArrayList<>();

    private RelativeLayout mRlBoy, mRlGirl, mRlEnd, mRlUpdate;
    private CategoryAdapter mCategoryAdapter;

    private View headerView;
    private Handler handler = new Handler();
    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initRecyclerView();
        initListener();
    }

    boolean isLoading;

    private void initRecyclerView() {

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        
         //下方注释的代码用来解决headerview和footerview加载到头一个或者最后一个item  而不是占据一行的bug
        /*final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // gridLayoutManager  布局管理器
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //如果是第一个(添加HeaderView)   还有就是最后一个(FooterView)
                return position == mCategoryBean.size() + 1 || position == 0 ? gridLayoutManager.getSpanCount() : 1;
            }
        });*/
        
        mCategoryAdapter = new CategoryAdapter(mCategoryBean);
        mRecyclerView.setAdapter(mCategoryAdapter);
        
        

        setHeader(mRecyclerView);
        mCategoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position, CategoryBean categoryBean) {
                Toast.makeText(MainActivity.this, "我是第" + position + "项", Toast.LENGTH_SHORT).show();
            }
        });

        refresh_mRecyclerView();


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisiableItemPosition = manager.findLastVisibleItemPosition();
                if (lastVisiableItemPosition + 1 == mCategoryAdapter.getItemCount()){
                    if (!isLoading){
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //requestData();
                                requestLoadMoreData();
                                //    Toast.makeText(MainActivity.this, "已经没有新的了", Toast.LENGTH_SHORT).show();
                                isLoading = false;
                                // adapter.notifyItemRemoved(adapter.getItemCount());
                            }
                        },2000);
                    }
                }
            }
        });


    }

    private void refresh_mRecyclerView() {
        //设置刷新的颜色
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.background_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
        //拖动多长的时候开始刷新
        mSwipeRefreshLayout.setDistanceToTriggerSync(100);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorAccent));

        //设置大小
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        //刷新监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCategoryBean.clear();
                        initData();
                        //通知刷新
                        mCategoryAdapter.notifyItemRangeChanged(0,0);
                        index = 1;
                        mRecyclerView.scrollToPosition(0);//刷新完成时候显示位置
                        mSwipeRefreshLayout.setRefreshing(false);//完成后调用完成的方法
                    }
                },3000);
            }
        });

    }

    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(this).inflate(R.layout.category_item_header, view, false);

        mRlBoy = (RelativeLayout) header.findViewById(R.id.rl_boy);
        mRlGirl = (RelativeLayout) header.findViewById(R.id.rl_girl);
        mRlEnd = (RelativeLayout) header.findViewById(R.id.rl_end);
        mRlUpdate = (RelativeLayout) header.findViewById(R.id.rl_update);

        mCategoryAdapter.setHeaderView(header);
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            CategoryBean bean = new CategoryBean();
            if (i % 2 == 0) {
                bean.setImgUrl(R.mipmap.book_pic_3);
            } else if (i%3==0){
                bean.setImgUrl(R.mipmap.book_pic_5);
            }
            bean.setImgUrl(R.mipmap.book_pic_1);
            bean.setCategory_des(getString(R.string.app_des));
            bean.setCategory_title("都市言情");
            mCategoryBean.add(bean);
        }
    }

    private void initListener() {
        mRlBoy.setOnClickListener(this);
        mRlGirl.setOnClickListener(this);
        mRlEnd.setOnClickListener(this);
        mRlUpdate.setOnClickListener(this);
    }


    private void requestLoadMoreData(){

        index++;

        if (index <= 3) {
            initData();
            mCategoryAdapter.notifyItemChanged(1,1);
        } else {
            Toast.makeText(MainActivity.this, "已经没有新的了", Toast.LENGTH_SHORT).show();
            mCategoryAdapter.notifyItemRemoved(mCategoryAdapter.getItemCount());
        }
        // swipeRefreshLayout.setRefreshing(false);
//        mCategoryAdapter.notire
    }


    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_boy:
                Toast.makeText(this, "男生专区", Toast.LENGTH_SHORT).show();
                break;

            case R.id.rl_girl:
                Toast.makeText(this, "女生专区", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_end:
                Toast.makeText(this, "完本专区", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_update:
                Toast.makeText(this, "最近更新", Toast.LENGTH_SHORT).show();
                break;

        }
    }

}
