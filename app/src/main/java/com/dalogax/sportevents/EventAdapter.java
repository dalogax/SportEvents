package com.dalogax.sportevents;


import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventInfo> eventList;

    public EventAdapter(List<EventInfo> eventList) {
        this.eventList = eventList;
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        EventInfo ci = eventList.get(i);
        eventViewHolder.title.setText(ci.title);
        ContextWrapper cw = new ContextWrapper(MyApplication.getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File imgFile=new File(directory,ci.getObjectId()+".jpg");
        if(imgFile.exists()){
            Bitmap image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            eventViewHolder.image.setImageBitmap(image);
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new EventViewHolder(itemView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected ImageView image;

        public EventViewHolder(View v) {
            super(v);
            title =  (TextView) v.findViewById(R.id.title);
            image =  (ImageView) v.findViewById(R.id.eventImage);
        }
    }
}