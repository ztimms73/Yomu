package org.xtimms.yomu.adapter.explore;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.lucasurbas.listitemview.ListItemView;

import org.xtimms.yomu.R;
import org.xtimms.yomu.models.ProviderHeader;
import org.xtimms.yomu.models.ProviderHeaderDetailed;
import org.xtimms.yomu.ui.activities.mangalist.MangaListActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public final  class ExploreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private final ArrayList<Object> mDataset;

    public ExploreAdapter(ArrayList<Object> dataset) {
        mDataset = dataset;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @ItemViewType int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case ItemViewType.TYPE_ITEM_WITH_ICON:
                holder = new DetailsProviderHolder(inflater.inflate(R.layout.item_two_lines_icon, parent, false));
                break;
            case ItemViewType.TYPE_HEADER:
                return new HeaderHolder(inflater.inflate(R.layout.header_group, parent, false));
            case ItemViewType.TYPE_ITEM_DEFAULT:
                holder = new ProviderHolder(inflater.inflate(R.layout.item_single_line, parent, false));
                break;
            default:
                throw new AssertionError("Unknown viewType");
        }
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DetailsProviderHolder) {
            ProviderHeaderDetailed item = (ProviderHeaderDetailed) mDataset.get(position);
            ((DetailsProviderHolder) holder).list.setTitle(item.dName);
            holder.itemView.setTag(item.cName);
            ((DetailsProviderHolder) holder).list.setIconDrawable(item.icon);
            ((DetailsProviderHolder) holder).list.setSubtitle(item.summary);
        } else if (holder instanceof ProviderHolder) {
            ProviderHeader item = (ProviderHeader) mDataset.get(position);
            holder.itemView.setTag(item.cName);
            ((ProviderHolder) holder).text1.setText(item.dName);
        } else if (holder instanceof HeaderHolder) {

            ((HeaderHolder) holder).textView.setText((String) mDataset.get(position));
        }
    }

    @ItemViewType
    @Override
    public int getItemViewType(int position) {
        final Object item = mDataset.get(position);
        if (item instanceof String) {
            return ItemViewType.TYPE_HEADER;
        } else if (item instanceof ProviderHeaderDetailed) {
            return ItemViewType.TYPE_ITEM_WITH_ICON;
        } else if (item instanceof ProviderHeader) {
            return ItemViewType.TYPE_ITEM_DEFAULT;
        } else {
            throw new AssertionError("Unknown ItemViewType");
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onClick(View view) {
        final String cname = String.valueOf(view.getTag());
        final Context context = view.getContext();
        context.startActivity(new Intent(context.getApplicationContext(), MangaListActivity.class)
                .putExtra("provider.cname", cname));
    }

    static class ProviderHolder extends RecyclerView.ViewHolder {

        final TextView text1;

        ProviderHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }

    static class DetailsProviderHolder extends ProviderHolder {

        final ListItemView list;

        DetailsProviderHolder(View itemView) {
            super(itemView);
            list = itemView.findViewById(R.id.list);
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public HeaderHolder(View itemView) {
            super(itemView);
            int accentColor = ThemeStore.accentColor(itemView.getContext());
            textView = itemView.findViewById(R.id.textView);
            textView.setTextColor(accentColor);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ItemViewType.TYPE_ITEM_DEFAULT, ItemViewType.TYPE_ITEM_WITH_ICON, ItemViewType.TYPE_HEADER})
    public @interface ItemViewType {
        int TYPE_ITEM_DEFAULT = 0;
        int TYPE_ITEM_WITH_ICON = 1;
        int TYPE_HEADER = 3;
    }

}
