package com.hiroshi.cimoc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiroshi.cimoc.R;
import com.hiroshi.cimoc.model.Chapter;
import com.hiroshi.cimoc.model.Comic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hiroshi on 2016/7/2.
 */
public class ChapterAdapter extends BaseAdapter<Chapter> {

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.item_chapter_title) TextView chapterTitle;

        public ViewHolder(View view) {
            super(view);
        }

    }

    public ChapterAdapter(Context context, List<Chapter> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chapter chapter = mDataSet.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.chapterTitle.setText(chapter.getTitle());
    }


}
