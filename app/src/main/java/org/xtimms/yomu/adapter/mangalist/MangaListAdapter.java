package org.xtimms.yomu.adapter.mangalist;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kabouzeid.appthemehelper.ThemeStore;

import org.xtimms.yomu.R;
import org.xtimms.yomu.adapter.InfiniteAdapter;
import org.xtimms.yomu.annotations.MangaStatus;
import org.xtimms.yomu.misc.PagedList;
import org.xtimms.yomu.misc.ThumbSize;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.util.ImageUtil;

public class MangaListAdapter extends InfiniteAdapter<MangaHeader, MangaListAdapter.MangaViewHolder> {

    private boolean mGrid;
    private ThumbSize mThumbSize;

    public MangaListAdapter(PagedList<MangaHeader> dataset, RecyclerView recyclerView) {
        super(dataset, recyclerView);
    }

    public boolean setGrid(boolean grid) {
        if (mGrid != grid) {
            mGrid = grid;
            notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    public void setThumbnailsSize(@NonNull ThumbSize size) {
        if (!size.equals(mThumbSize)) {
            mThumbSize = size;
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    @Override
    public MangaViewHolder onCreateHolder(ViewGroup parent) {

        return new MangaViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mGrid ? R.layout.item_manga_grid : R.layout.item_manga_detailed, parent, false));
    }

    @Override
    public long getItemId(MangaHeader data) {
        return data.id;
    }

    @Override
    public void onBindHolder(MangaViewHolder viewHolder, MangaHeader data, int position) {
        viewHolder.fill(data, mThumbSize, false);
    }

    public static class MangaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private static int PADDING_16 = 0;
        private static int PADDING_4 = 0;
        private static int HEIGHT_96 = 0;
        private static int HEIGHT_72 = 0;

        private final TextView textViewTitle;
        private final TextView textViewSubtitle;
        private final TextView ratingView;
        private final ImageView imageView;
        private final TextView status;
        private MangaHeader mData;
        private final View cellFooter;
        private int viewMode;

        public MangaViewHolder(final View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(android.R.id.text1);
            textViewSubtitle = itemView.findViewById(android.R.id.text2);
            ratingView = itemView.findViewById(R.id.ratingText);
            imageView = itemView.findViewById(R.id.imageView);
            cellFooter = itemView.findViewById(R.id.gradient);
            status = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public MangaHeader getData() {
            return mData;
        }

        private void updateViewMode(ThumbSize thumbSize) {
            int mode = 3; //large grid
            if (cellFooter == null) {
                mode = 0; //list
            } else if (thumbSize.getWidth() <= ThumbSize.THUMB_SIZE_SMALL.getWidth()) {
                mode = 1; //small grid
            } else if (thumbSize.getWidth() <= ThumbSize.THUMB_SIZE_MEDIUM.getWidth()) {
                mode = 2; //medium grid
            }
            if (viewMode == mode) {
                return;
            }
            viewMode = mode;
            switch (viewMode) {
                case 0:
                    textViewTitle.setMaxLines(2);
                    break;
                case 1:
                    textViewTitle.setMaxLines(2);
                    cellFooter.getLayoutParams().height = HEIGHT_72;
                    cellFooter.setPadding(PADDING_4, PADDING_4, PADDING_4, PADDING_4);
                    break;
                case 2:
                    textViewTitle.setMaxLines(2);
                    cellFooter.getLayoutParams().height = HEIGHT_96;
                    cellFooter.setPadding(PADDING_4, PADDING_4, PADDING_4, PADDING_4);
                    break;
                case 3:
                    textViewTitle.setMaxLines(2);
                    cellFooter.getLayoutParams().height = HEIGHT_96;
                    cellFooter.setPadding(PADDING_16, PADDING_16, PADDING_16, PADDING_16);
            }
        }

        public void fill(MangaHeader data, ThumbSize thumbSize, boolean checked) {
            mData = data;
            updateViewMode(thumbSize);
            int accentColor = ThemeStore.accentColor(itemView.getContext());
            if (itemView instanceof Checkable) {
                ((Checkable) itemView).setChecked(checked);
            }
            textViewTitle.setText(mData.name);
            if (textViewSubtitle != null) {
                if (TextUtils.isEmpty(mData.summary)) {
                    textViewSubtitle.setVisibility(View.GONE);
                } else {
                    textViewSubtitle.setText(mData.summary);
                    textViewSubtitle.setVisibility(View.VISIBLE);
                }
            }
            ImageUtil.setThumbnailWithSize(imageView, data.thumbnail, thumbSize);
            if (ratingView != null) {
                ratingView.setText(String.valueOf((float) mData.rating / 20));
                if (mData.rating == 0) {
                    ratingView.setVisibility(View.GONE);
                } else {
                    ratingView.setVisibility(View.VISIBLE);
                }
            }
            status.setTextColor(accentColor);
            if (status != null) {
                switch (mData.status) {
                    case MangaStatus.STATUS_ONGOING:
                        status.setVisibility(View.VISIBLE);
                        status.setText(R.string.status_ongoing);
                        break;
                    case MangaStatus.STATUS_COMPLETED:
                        status.setVisibility(View.VISIBLE);
                        status.setText(R.string.status_completed);
                        break;
                    case MangaStatus.STATUS_LICENSED:
                        status.setVisibility(View.VISIBLE);
                        status.setText(R.string.status_licensed);
                        break;
                    default:
                        status.setVisibility(View.GONE);
                        break;
                    case MangaStatus.STATUS_UNKNOWN:
                        break;
                }
            }
        }

        @Override
        public void onClick(View v) {
            MangaHeader mangaHeader = getData();
            Context context = v.getContext();
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
