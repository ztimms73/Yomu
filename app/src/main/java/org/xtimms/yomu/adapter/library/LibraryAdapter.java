package org.xtimms.yomu.adapter.library;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;

import org.xtimms.yomu.R;
import org.xtimms.yomu.listeners.OnTipsActionListener;
import org.xtimms.yomu.misc.DataViewHolder;
import org.xtimms.yomu.misc.Dismissible;
import org.xtimms.yomu.models.ListHeader;
import org.xtimms.yomu.models.MangaFavourite;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.models.MangaHistory;
import org.xtimms.yomu.models.UserTip;
import org.xtimms.yomu.source.MangaProvider;
import org.xtimms.yomu.ui.fragments.main.library.LibraryContent;
import org.xtimms.yomu.util.ImageUtil;
import org.xtimms.yomu.util.ResourceUtil;

import java.util.ArrayList;

public final class LibraryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<Object> mDataset;
    private final OnTipsActionListener mActionListener;

    public LibraryAdapter(OnTipsActionListener listener) {
        mDataset = new ArrayList<>();
        mActionListener = listener;
        setHasStableIds(true);
    }

    public void updateData(ArrayList<Object> data) {
        mDataset.clear();
        mDataset.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @LibraryItemType int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case LibraryItemType.TYPE_HEADER:
                return new HeaderHolder(inflater.inflate(R.layout.header_group_button, parent, false));
            case LibraryItemType.TYPE_ITEM_DEFAULT:
                return new MangaHolder(inflater.inflate(R.layout.item_manga, parent, false));
            case LibraryItemType.TYPE_RECENT:
                return new RecentHolder(inflater.inflate(R.layout.item_manga_recent, parent, false));
            case LibraryItemType.TYPE_ITEM_SMALL:
                return new MangaHolder(inflater.inflate(R.layout.item_manga_small, parent, false));
            case LibraryItemType.TYPE_TIP:
                return new TipHolder(inflater.inflate(R.layout.item_tip, parent, false));
            default:
                throw new AssertionError("Unknown viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TipHolder) {
            ((TipHolder) holder).bind((UserTip) mDataset.get(position));
            ((TipHolder) holder).setColors(ThemeStore.accentColor(holder.itemView.getContext()), (TipHolder) holder);
        } else if (holder instanceof HeaderHolder) {
            ListHeader item = (ListHeader) mDataset.get(position);
            if (item.text != null) {
                ((HeaderHolder) holder).textView.setText(item.text);
            } else if (item.textResId != 0) {
                ((HeaderHolder) holder).textView.setText(item.textResId);
            } else {
                ((HeaderHolder) holder).textView.setText(null);
            }
            holder.itemView.setTag(item.extra);
        } else if (holder instanceof MangaHolder) {
            MangaHeader item = (MangaHeader) mDataset.get(position);
            ImageUtil.setThumbnail(((MangaHolder) holder).imageViewThumbnail, item.thumbnail, MangaProvider.getDomain(item.provider));
            ((MangaHolder) holder).textViewTitle.setText(item.name);
            holder.itemView.setTag(item);
            if (holder instanceof RecentHolder) {
                MangaHistory history = (MangaHistory) item;
                ((RecentHolder) holder).textViewSubtitle.setText(item.summary);
                if (item.summary == null || item.summary.isEmpty()) {
                    ((RecentHolder) holder).textViewSubtitle.setVisibility(View.GONE);
                }
                ((RecentHolder) holder).textViewStatus.setText(ResourceUtil.formatTimeRelative(history.updatedAt));
            }
        }
    }

    @LibraryItemType
    @Override
    public int getItemViewType(int position) {
        Object item = mDataset.get(position);
        if (item instanceof ListHeader) {
            return LibraryItemType.TYPE_HEADER;
        } else if (item instanceof UserTip) {
            return LibraryItemType.TYPE_TIP;
        } else if (item instanceof MangaHistory) {
            return LibraryItemType.TYPE_RECENT;
        } else if (item instanceof MangaFavourite) {
            return LibraryItemType.TYPE_ITEM_DEFAULT;
        } else if (item instanceof MangaHeader) {
            return LibraryItemType.TYPE_ITEM_SMALL;
        } else {
            throw new AssertionError("Unknown viewType");
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        Object item = mDataset.get(position);
        if (item instanceof MangaHeader) {
            return ((MangaHeader) item).id;
        } else if (item instanceof ListHeader) {
            final String text = ((ListHeader) item).text;
            return text != null ? text.hashCode() : ((ListHeader) item).textResId;
        } else if (item instanceof UserTip) {
            return ((UserTip) item).title.hashCode() + ((UserTip) item).content.hashCode();
        } else {
            throw new AssertionError("Unknown viewType");
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof MangaHolder) {
            ImageUtil.recycle(((MangaHolder) holder).imageViewThumbnail);
        }
        super.onViewRecycled(holder);
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final Button buttonMore;

        HeaderHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            buttonMore = itemView.findViewById(R.id.button_more);
        }
    }

    public class TipHolder extends DataViewHolder<UserTip> implements Dismissible {

        private final ConstraintLayout background;
        private final TextView textViewTitle;
        private final TextView textViewContent;
        private final ImageView imageViewIcon;
        private final Button buttonAction;
        private final Button buttonDismiss;

        TipHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            textViewTitle = itemView.findViewById(android.R.id.text1);
            textViewContent = itemView.findViewById(android.R.id.text2);
            buttonAction = itemView.findViewById(android.R.id.button1);
            buttonDismiss = itemView.findViewById(android.R.id.closeButton);
            imageViewIcon = itemView.findViewById(android.R.id.icon);
        }

        @Override
        public void bind(UserTip userTip) {
            super.bind(userTip);
            int accentColor = ThemeStore.accentColor(itemView.getContext());
            background.setBackgroundColor(accentColor);
            textViewTitle.setText(userTip.title);
            textViewContent.setText(userTip.content);
            if (userTip.hasIcon()) {
                imageViewIcon.setImageResource(userTip.icon);
                imageViewIcon.setVisibility(View.VISIBLE);
            } else {
                imageViewIcon.setVisibility(View.GONE);
            }
            if (userTip.hasAction()) {
                buttonAction.setText(userTip.actionText);
                buttonAction.setTag(userTip.actionId);
                buttonAction.setVisibility(View.VISIBLE);
            } else {
                buttonAction.setVisibility(View.GONE);
            }
            buttonDismiss.setVisibility(userTip.hasDismissButton() ? View.VISIBLE : View.GONE);
        }

        protected void setColors(int color, TipHolder holder) {
            if (holder.background != null) {
                holder.background.setBackgroundColor(color);
                if (holder.textViewTitle != null) {
                    holder.textViewTitle.setTextColor(MaterialValueHelper.getPrimaryTextColor(holder.getContext(), ColorUtil.isColorLight(color)));
                }
                if (holder.textViewContent != null) {
                    holder.textViewContent.setTextColor(MaterialValueHelper.getSecondaryTextColor(holder.getContext(), ColorUtil.isColorLight(color)));
                }
                if (holder.imageViewIcon != null) {
                    holder.imageViewIcon.setColorFilter(MaterialValueHelper.getPrimaryTextColor(holder.getContext(), ColorUtil.isColorLight(color)));
                }
                if (holder.buttonAction != null) {
                    holder.buttonAction.setTextColor(MaterialValueHelper.getPrimaryTextColor(holder.getContext(), ColorUtil.isColorLight(color)));
                }
                if (holder.buttonDismiss != null) {
                    holder.buttonDismiss.setTextColor(MaterialValueHelper.getPrimaryTextColor(holder.getContext(), ColorUtil.isColorLight(color)));
                }
            }
        }

        public boolean isDismissible() {
            final UserTip data = getData();
            return data != null && data.isDismissible();
        }

        @Override
        public void dismiss() {
            Object tag = buttonAction.getTag();
            if (tag instanceof Integer) {
                mActionListener.onTipDismissed((Integer) tag);
            }
            mDataset.remove(getAdapterPosition());
            notifyDataSetChanged();
        }
    }

    static class MangaHolder extends RecyclerView.ViewHolder {

        final ImageView imageViewThumbnail;
        final TextView textViewTitle;

        MangaHolder(View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }
    }

    static class RecentHolder extends MangaHolder {

        final TextView textViewStatus;
        final TextView textViewSubtitle;

        RecentHolder(View itemView) {
            super(itemView);
            textViewStatus = itemView.findViewById(R.id.textView_status);
            textViewSubtitle = itemView.findViewById(R.id.textView_subtitle);
        }
    }
}