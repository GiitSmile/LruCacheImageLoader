package com.cxmscb.cxm.cacheproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cxm on 2016/8/25.
 */
public class LruCacheImageLoader {

    private LruCache<String,Bitmap> mCache;

    // 存储异步任务的集合
    private Set<LruCacheAsyncTask> mTaskSet;

    /*ImageLoader的单例*/
    private static LruCacheImageLoader mImageLoader;

    private Context mContext;

    private LruCacheImageLoader(Context context){

        this.mContext = context;
        mTaskSet = new HashSet<>();

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 初始化LruCache
        mCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }


    // 获取LruCacheImageLoader的实例(带同步锁)
    public static LruCacheImageLoader getInstance(Context context){


        if(mImageLoader==null){
            synchronized (LruCacheImageLoader.class){
                if(mImageLoader==null)
                    mImageLoader = new LruCacheImageLoader(context);
            }
        }
        return  mImageLoader;
    }


    // 根据key值获取缓存中的图片
    private Bitmap getBitmapFromMemory(String url){
        return mCache.get(url);
    }




    //将一张图片存储到LruCache中。
    private void putBitmapToMemory(String url, Bitmap bitmap) {
        if (getBitmapFromMemory(url) == null) {
            mCache.put(url, bitmap);
        }
    }


    /*------------以上的LruCache的使用-------------*/

    /*
     * 普通地加载图片到imageView中
     */
    public void displayImage(ImageView iv, final String url) {

            //从缓存中取出图片
            Bitmap bitmap = getBitmapFromMemory(url);
            //如果缓存中没有，先设为默认图片
            if (bitmap == null) {
                LruCacheAsyncTask task = new LruCacheAsyncTask(iv);
                task.execute(url);
                mTaskSet.add(task);
            } else {
                //如果缓存中有 直接设置
                iv.setImageBitmap(bitmap);
            }

    }


    /**
     * 为listview加载从start到end的所有的Image
     *
     */
    public void loadTagImageViewInListView(int start, int end, String[] tagUrls, ListView mListView) {

        Drawable.ConstantState aConstantState = mContext.getResources().getDrawable(R.drawable.loading).getConstantState();

        for (int i = start; i < end; i++) {
            String url = tagUrls[i];
            ImageView imageView = (ImageView) mListView.findViewWithTag(url);
            // 判断图片是否加载过，以免新建多个asynctask
            if (imageView.getDrawable().getConstantState().equals(aConstantState)) {
                displayImage(imageView, url);
            }
        }
        Log.i("num of asynctask","  "+mTaskSet.size());
    }


    private class LruCacheAsyncTask extends AsyncTask<String,Void,Bitmap>{


        private ImageView imageView;



        public LruCacheAsyncTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = getBitmapFromUrl(strings[0]);

            // 将bitmap缓存到LruCache中
            if(bitmap!=null){
                putBitmapToMemory(strings[0],bitmap);
            }

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTaskSet.remove(this);
            Log.i("num of asynctask","  "+mTaskSet.size());
        }

        private Bitmap getBitmapFromUrl(String urlPath) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(urlPath);
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream in;
                in = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                // TODO Auto-generated catch block
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

    }


    /**
     * 停止所有当前正在运行的任务
     */
    public void cancelAllTask() {

        if (mTaskSet != null) {
            for (LruCacheAsyncTask task : mTaskSet) {
                task.cancel(false);
            }
            mTaskSet.clear();
            Log.i("num of asynctask","  "+mTaskSet.size());
        }
    }


}
