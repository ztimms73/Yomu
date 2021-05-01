package org.xtimms.yomu.ui.activities.preview.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.lucasurbas.listitemview.ListItemView;

import org.xtimms.yomu.R;
import org.xtimms.yomu.annotations.MangaStatus;
import org.xtimms.yomu.misc.PageHolder;
import org.xtimms.yomu.models.MangaDetails;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.source.MangaProvider;
import org.xtimms.yomu.util.ErrorUtil;
import org.xtimms.yomu.util.ImageUtil;
import org.xtimms.yomu.util.TextUtil;

public final class DetailsPage extends PageHolder {

    private ImageView mImageViewCover;
    private ImageView mBackdrop;
    private TextView mTextViewSummary;
    private RatingBar mRatingBar;
    private ListItemView mDescription;
    private ListItemView mGenres;
    private ListItemView mReadTime;

    public Button buttonRead;
    public ImageButton buttonFavourite;

    public DetailsPage(@NonNull ViewGroup parent) {
        super(parent, R.layout.page_manga_details);
    }

    @Override
    protected void onViewCreated(@NonNull View view) {
        int accentColor = ThemeStore.accentColor(getContext());
        mImageViewCover = view.findViewById(R.id.imageView_cover);
        mBackdrop = view.findViewById(R.id.backdrop);
        mTextViewSummary = view.findViewById(R.id.textView_summary);
        mRatingBar = view.findViewById(R.id.ratingBar);
        buttonRead = view.findViewById(R.id.button_read);
        buttonFavourite = view.findViewById(R.id.button_favourite);
        mDescription = view.findViewById(R.id.description);
        mGenres = view.findViewById(R.id.genres);
        mReadTime = view.findViewById(R.id.average_read_time);

        Drawable progress = mRatingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, accentColor);
        buttonRead.getBackground().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
    }

    public void updateContent(@NonNull MangaHeader mangaHeader, @Nullable MangaDetails mangaDetails) {
        final MangaProvider provider = MangaProvider.get(getContext(), mangaHeader.provider);
        if (mangaDetails == null) { //full info wasn't loaded yet
            ImageUtil.setThumbnail(mImageViewCover, mangaHeader.thumbnail, MangaProvider.getDomain(mangaHeader.provider));
            ImageUtil.setThumbnail(mBackdrop, mangaHeader.thumbnail, MangaProvider.getDomain(mangaHeader.provider));
            //LayoutUtils.setTextOrHide(mTextViewGenres, mangaHeader.genres);
            mGenres.setSubtitle(mangaHeader.genres);
            if (mGenres.getSubtitle().isEmpty()) {
                mGenres.setVisibility(View.GONE);
            }
            if (mangaHeader.rating == 0) {
                mRatingBar.setVisibility(View.GONE);
            } else {
                mRatingBar.setVisibility(View.VISIBLE);
                mRatingBar.setRating((float)mangaHeader.rating / 20);
            }
            mTextViewSummary.setText(formatSummary(null, -1, provider.getName(), mangaHeader.status));
        } else {
            ImageUtil.updateImage(mImageViewCover, mangaDetails.cover, MangaProvider.getDomain(mangaDetails.provider));
            ImageUtil.setThumbnail(mBackdrop, mangaDetails.cover, MangaProvider.getDomain(mangaDetails.provider));
            //LayoutUtils.setTextOrHide(mTextViewGenres, mangaDetails.genres);
            //mTextViewDescription.setText(TextUtils.fromHtmlCompat(mangaDetails.description));
            mDescription.setSubtitle(String.valueOf(TextUtil.fromHtmlCompat(mangaDetails.description)));
            mGenres.setSubtitle(mangaDetails.genres);
            if (mangaDetails.rating != 0) {
                mRatingBar.setVisibility(View.VISIBLE);
                mRatingBar.setRating((float)mangaDetails.rating / 20);
            }
            calculateTime(mangaDetails);
            mTextViewSummary.setText(formatSummary(mangaDetails.author, mangaDetails.chapters.size(), provider.getName(), mangaDetails.status));
            buttonRead.setEnabled(true);
        }
    }

    private void calculateTime(@Nullable MangaDetails details){
        assert details != null;
        int averageTime = (19 * 17) * details.chapters.size();
        int hours = averageTime / 3600;
        int minutes = (averageTime % 3600) / 60;
        String hourString = this.getContext().getResources().getQuantityString(R.plurals.hour, hours, hours);
        String minuteString = this.getContext().getResources().getQuantityString(R.plurals.minute, minutes, minutes);
        mReadTime.setSubtitle(hourString + " " + minuteString);
    }

    public void setError(@Nullable Throwable error) {
        mDescription.setSubtitle(ErrorUtil.getErrorMessage(error));
        mReadTime.setSubtitle(ErrorUtil.getErrorMessage(error));
    }

    @NonNull
    private CharSequence formatSummary(@Nullable String author, int chapters, String provider, @MangaStatus int status) {
        final StringBuilder builder = new StringBuilder();
        if (!android.text.TextUtils.isEmpty(author)) {
            builder.append("<b>").append(getString(R.string.author_)).append("</b> ");
            builder.append(author).append("<br/>");
        }
        //if (!android.text.TextUtils.isEmpty(artist)) {
        //	builder.append("<b>").append(getString(R.string.artist_)).append("</b> ");
        //	builder.append(artist).append("<br/>");
        //}
        //if (!android.text.TextUtils.isEmpty(year)) {
        //	builder.append("<b>").append(getString(R.string.year_)).append("</b> ");
        //	builder.append(year).append("<br/>");
        //}
        //if (!android.text.TextUtils.isEmpty(publisher)) {
        //	builder.append("<b>").append(getString(R.string.publisher_)).append("</b> ");
        //	builder.append(publisher).append("<br/>");
        //}
        builder.append("<b>").append(getString(R.string.chapters_count_)).append("</b> ");
        if (chapters == -1) {
            builder.append("?");
        } else {
            builder.append(chapters);
        }
        builder.append("<br/>").append("<b>").append(getString(R.string.source_)).append("</b> ");
        builder.append(provider);
        switch (status) {
            case MangaStatus.STATUS_COMPLETED:
                builder.append("<br/>").append("<b>").append(getString(R.string.status_)).append("</b> ");
                builder.append(getString(R.string.status_completed));
                break;
            case MangaStatus.STATUS_ONGOING:
                builder.append("<br/>").append("<b>").append(getString(R.string.status_)).append("</b> ");
                builder.append(getString(R.string.status_ongoing));
                break;
            case MangaStatus.STATUS_LICENSED:
                builder.append("<br/>").append("<b>").append(getString(R.string.status_)).append("</b> ");
                builder.append(getString(R.string.status_licensed));
                break;
            case MangaStatus.STATUS_UNKNOWN:
                break;
        }
        return TextUtil.fromHtmlCompat(builder.toString());
    }
}
