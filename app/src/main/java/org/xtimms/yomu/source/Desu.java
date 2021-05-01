package org.xtimms.yomu.source;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xtimms.yomu.R;
import org.xtimms.yomu.annotations.MangaStatus;
import org.xtimms.yomu.models.MangaChapter;
import org.xtimms.yomu.models.MangaDetails;
import org.xtimms.yomu.models.MangaGenre;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.models.MangaPage;
import org.xtimms.yomu.util.NetworkUtil;

import java.util.ArrayList;

public final class Desu extends MangaProvider {

    public static final String CNAME = "network/desu.me";
    public static final String DNAME = "Desu";

    public Desu(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        String url = String.format(
                "http://desu.me/manga/api/?limit=20&order=%s&page=%d&genres=%s&search=%s",
                "popular",
                page + 1,
                TextUtils.join(",", genres),
                search == null ? "" : search
        );
        JSONArray ja = NetworkUtil.getJSONObject(url).getJSONArray("response");
        ArrayList<MangaHeader> list = new ArrayList<>(ja.length());
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            int status = MangaStatus.STATUS_UNKNOWN;
            switch (jo.getString("status")) {
                case "released":
                    status = MangaStatus.STATUS_COMPLETED;
                    break;
                case "ongoing":
                    status = MangaStatus.STATUS_ONGOING;
                    break;
            }
            list.add(new MangaHeader(
                    jo.getString("name"),
                    jo.getString("russian"),
                    jo.getString("genres"),
                    "http://desu.me/manga/api/" + jo.getInt("id"),
                    jo.getJSONObject("image").getString("x225"),
                    CNAME,
                    status,
                    (byte) (jo.getDouble("score") * 10)
            ));
        }
        return list;
    }

    @NonNull
    @Override
    public MangaDetails getDetails(MangaHeader header) throws Exception {
        assert header.url != null;
        JSONObject jo = NetworkUtil.getJSONObject(header.url).getJSONObject("response");
        MangaDetails details = new MangaDetails(
                header,
                jo.getString("description"),
                jo.getJSONObject("image").getString("original"),
                "" //not supported by desu.me
        );
        JSONArray ja = jo.getJSONObject("chapters").getJSONArray("list");
        final int total = ja.length();
        for (int i = 0; i < total; i++) {
            JSONObject chapter = ja.getJSONObject(total - i - 1);
            final String ch = chapter.getString("ch");
            details.chapters.add(new MangaChapter(
                    chapter.isNull("title") ? "Глава " + ch : ch + " - " + chapter.getString("title"),
                    i,
                    details.url + "/chapter/" + chapter.getInt("id"),
                    CNAME,
                    "",
                    chapter.getLong("date")*1000
            ));
        }
        return details;
    }

    @NonNull
    @Override
    public ArrayList<MangaPage> getPages(String chapterUrl) throws Exception {
        JSONObject jo = NetworkUtil.getJSONObject(chapterUrl).getJSONObject("response");
        JSONArray ja = jo.getJSONObject("pages").getJSONArray("list");
        ArrayList<MangaPage> pages = new ArrayList<>(ja.length());
        for (int i = 0; i < ja.length(); i++) {
            jo = ja.getJSONObject(i);
            pages.add(new MangaPage(
                    jo.getString("img"),
                    CNAME
            ));
        }
        return pages;
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.url;
    }

}
