package com.example.android_places.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android_places.R;
import com.example.android_places.utils.AttributedPhoto;

import java.util.ArrayList;


/**
 * Created by savita on 26/4/16.
 */
public class PhotosListAdapter extends RecyclerView.Adapter<PhotosListAdapter.ViewHolder> {
    private OnItemClickListner listner;
    private Activity mActivity;
    private ArrayList<AttributedPhoto> mDataList;

    public PhotosListAdapter(OnItemClickListner listner, Activity activity, ArrayList<AttributedPhoto> dataList) {
        this.listner = listner;
        mActivity = activity;
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_adapter_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AttributedPhoto attributedPhoto = mDataList.get(position);
        holder.imageView.setImageBitmap(attributedPhoto.bitmap);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onItemClicked(attributedPhoto);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.fragment_map_iv);
        }
    }

    public interface OnItemClickListner {
        void onItemClicked(AttributedPhoto attributedPhoto);
    }
}
