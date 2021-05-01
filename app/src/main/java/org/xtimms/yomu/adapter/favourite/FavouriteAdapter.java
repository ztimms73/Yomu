package org.xtimms.yomu.adapter.favourite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.xtimms.yomu.R;
import org.xtimms.yomu.annotations.MangaStatus;
import org.xtimms.yomu.models.MangaFavourite;
import org.xtimms.yomu.util.ImageUtil;

import java.util.ArrayList;

public final class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteHolder> {

    private final ArrayList<MangaFavourite> mDataset;

    public FavouriteAdapter(ArrayList<MangaFavourite> dataset) {
        setHasStableIds(true);
        mDataset = dataset;
    }

    @Override
    public FavouriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavouriteHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manga_grid_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FavouriteHolder holder, int position) {
        MangaFavourite item = mDataset.get(position);
        holder.text1.setText(item.name);
        //LayoutUtils.setTextOrHide(holder.text2, item.summary);
        holder.itemView.setTag(item);
    }

    public ArrayList<MangaFavourite> getDataSet() {
        return mDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).id;
    }

    @Override
    public void onViewRecycled(FavouriteHolder holder) {
        ImageUtil.recycle(holder.imageView);
        super.onViewRecycled(holder);
    }

    class FavouriteHolder extends RecyclerView.ViewHolder {

        final TextView text1;
        final ImageView imageView;

        FavouriteHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.title);
            imageView = itemView.findViewById(R.id.thumbnail);
        }
    }

}
