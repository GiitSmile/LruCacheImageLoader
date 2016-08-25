package com.cxmscb.cxm.cacheproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



import java.util.List;

/**
 * Created by Administrator on 2015/7/28 0028.
 */
public class BookAdapter extends BaseAdapter {

    private  Context mContext;
    private  List<Book> mdata;

    public BookAdapter(Context mContext, List<Book> mdata) {

        this.mContext = mContext;
        this.mdata = mdata;

    }

    public void refresh(List<Book> list) {
        mdata = list;
        notifyDataSetChanged();
    }

    public List<Book> getDataList() {
        return mdata;
    }

    public void setDataList(List<Book> dataList) {
        this.mdata = dataList;
    }

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Book getItem(int position) {
        return mdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_book, null);
            holder = new Holder();
            holder.tv_book = (TextView)convertView.findViewById(R.id.book_text);
            holder.iv = (ImageView) convertView.findViewById(R.id.book_logo);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();

        }
        holder.tv_book.setText(mdata.get(position).getName());

        holder.iv.setTag(mdata.get(position).getPic());
        // LruCacheImageLoader.getInstance().displayTagedImageView(holder.iv,mdata.get(position).getPic());
        return convertView;
    }

    class Holder {
        private TextView tv_book;
        private ImageView iv;
    }

}