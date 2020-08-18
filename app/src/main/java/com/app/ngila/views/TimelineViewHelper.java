package com.app.ngila.views;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ngila.R;
import com.bumptech.glide.Glide;
import com.github.vipulasri.timelineview.TimelineView;

public class TimelineViewHelper {

    public static void InitList(RecyclerView contentRecyclerView,TimeLineItem[] timeLineItems){
        LinearLayoutManager listManager = new LinearLayoutManager(contentRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);

        contentRecyclerView.setLayoutManager(listManager);

        contentRecyclerView.setAdapter(new ContentAdapter(timeLineItems));
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public TimelineView timeline;
        public AppCompatTextView text_timeline_date,text_timeline_title;
        public View item;
        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            timeline = itemView.findViewById(R.id.timeline);
            text_timeline_date = itemView.findViewById(R.id.text_timeline_date);
            text_timeline_title = itemView.findViewById(R.id.text_timeline_title);

            timeline.initLine(viewType);
            item = itemView;
        }
    }
    private static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private TimeLineItem[] timeLineItems;
        public ContentAdapter(TimeLineItem[] timeLineItems){
            this.timeLineItems = timeLineItems;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_timeline, null);
            return new ViewHolder(view, viewType);
        }
        @Override
        public int getItemViewType(int position) {
            return TimelineView.getTimeLineViewType(position, getItemCount());
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            TimeLineItem timeLineItem = timeLineItems[position];
            holder.text_timeline_date.setText(timeLineItem.subTitle);
            holder.text_timeline_title.setText(timeLineItem.title);


        }
        @Override
        public int getItemCount() {
            return timeLineItems.length;
        }
    }

    public static class TimeLineItem{
        private String title,subTitle;
        private int status;
        public TimeLineItem(){}
        public TimeLineItem(String title,String subTitle,int status){
            this.title =title;
            this.subTitle = subTitle;
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
