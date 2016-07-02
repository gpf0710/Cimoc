package com.hiroshi.cimoc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiroshi.cimoc.R;
import com.hiroshi.cimoc.model.Comic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hiroshi on 2016/7/1.
 */
public class ComicAdapter extends BaseAdapter<Comic> {

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.item_comic_img) ImageView comicImg;
        @BindView(R.id.item_comic_title) TextView comicTitle;
        @BindView(R.id.item_comic_res) TextView comicRes;

        public ViewHolder(View view) {
            super(view);
        }

    }

    public ComicAdapter(Context context, List<Comic> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_comic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Comic comic = mDataSet.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.comicImg.setImageResource(comic.getImg());
        viewHolder.comicTitle.setText(comic.getTitle());
        viewHolder.comicRes.setText(comic.getRes());
    }

}
