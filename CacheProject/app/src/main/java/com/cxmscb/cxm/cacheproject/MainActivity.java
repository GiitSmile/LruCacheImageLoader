package com.cxmscb.cxm.cacheproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Book> mBooks  = null;
    private ListView listView ;
    private int mStart,mEnd;
    private String[] urls;
    private boolean mFirstIn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mBooks = new ArrayList<>();

        initData();
        mFirstIn = true;

        listView = (ListView) findViewById(R.id.listview);

        listView.setAdapter(new BookAdapter(this,mBooks));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(i==SCROLL_STATE_IDLE){
                   LruCacheImageLoader.getInstance().loadImageIntoTagImageViewInListView(mStart,mEnd,urls,listView);
                }else {
                    LruCacheImageLoader.getInstance().cancelAllTask();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                mStart = i;
                mEnd = i + i1;
                if(mFirstIn && i1 >0){
                    LruCacheImageLoader.getInstance().loadImageIntoTagImageViewInListView(mStart,mEnd,urls,listView);
                    mFirstIn = false;
                }
            }
        });


    }

    private void initData() {

        String[] bookPic = {"http://202.114.9.17/bibimage/zycover.php?isbn=9787201080987",
                "http://202.114.9.17/bibimage/zycover.php?isbn=9787532152759",
                "http://202.114.9.17/bibimage/zycover.php?isbn=9787532749522",
                "http://202.114.9.17/bibimage/zycover.php?isbn=9787532153756",
                "http://202.114.9.17/bibimage/zycover.php?isbn=9787563379071",
                "http://202.114.9.17/bibimage/zycover.php?isbn=9787506365680",
                "http://202.114.9.17/bibimage/zytest1.php?isbn=9787549549733",
                "http://202.114.9.17/bibimage/zytest1.php?isbn=9787542644954",
                "http://202.114.9.17/bibimage/zytest1.php?isbn=9787562053514"
        };
        urls = bookPic;
        String[] books = {"像少年啦飞驰 / 韩寒", "渴求真爱的幽灵 /  文野村美月",
                "十一字杀人 / (日) 东野圭吾著", "替身S /  绫辻行人", "原来你非不快乐 /  林夕 ",
                "许三观卖血记 / 余华", "大唐李白 / 张大春", "李白诗集新注 / 管士光注", "李白与道统 / 张佩著 "};

        for (int i = 0; i < books.length; i++) {
            mBooks.add(new Book(books[i], bookPic[i]));
        }

    }
	
	@Override
    protected void onStop(){
        super.onStop();
        LruCacheImageLoader.getInstance().cancelAllTask();
    }

}

