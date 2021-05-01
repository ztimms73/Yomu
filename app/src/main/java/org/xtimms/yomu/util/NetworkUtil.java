package org.xtimms.yomu.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xtimms.yomu.BuildConfig;
import org.xtimms.yomu.misc.CloudflareInterceptor;
import org.xtimms.yomu.misc.CookieStore;
import org.xtimms.yomu.misc.FileLogger;
import org.xtimms.yomu.misc.SSLSocketFactoryExtended;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.client.StrongOkHttpClientBuilder;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class NetworkUtil {

    public static final String TAG = "NetworkUtils";
    public static final String TAG_REQUEST = TAG + "-request";
    public static final String TAG_RESPONSE = TAG + "-response";
    public static final String TAG_ERROR = TAG + "-error";

    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String USER_AGENT_DEFAULT = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:57.0) Gecko/20100101 Firefox/57.0";

    private static final CacheControl CACHE_CONTROL_DEFAULT = new CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build();

    public static final Headers HEADERS_DEFAULT = new Headers.Builder()
            .add(HEADER_USER_AGENT, USER_AGENT_DEFAULT)
            .build();

    private static OkHttpClient sHttpClient = null;

    @NonNull
    private static OkHttpClient.Builder getClientBuilder() {
        return new OkHttpClient.Builder()
                /*.connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)*/
                .addInterceptor(CookieStore.getInstance())
                .addInterceptor(new CloudflareInterceptor());
    }

    public static void init(Context context, boolean useTor) {
        OkHttpClient.Builder builder = getClientBuilder();
        if (useTor && OrbotHelper.get(context).init()) {
            try {
                StrongOkHttpClientBuilder.forMaxSecurity(context)
                        .applyTo(builder, new Intent()
                                .putExtra(OrbotHelper.EXTRA_STATUS, "ON")); //TODO wtf
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sHttpClient = builder.build();
    }

    public static Document httpGet(@NonNull String url, @Nullable String cookie) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        SSLSocketFactoryExtended factory = null;
        try {
            factory = new SSLSocketFactoryExtended();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        final SSLSocketFactoryExtended finalFactory = factory;
        InputStream is = null;
        try {
            requestLog(url, cookie);
            HttpURLConnection con = NetCipher.getHttpURLConnection(url);
            if (con instanceof HttpsURLConnection) {
                ((HttpsURLConnection) con).setDefaultSSLSocketFactory(finalFactory);
                con.setRequestProperty("charset", "utf-8");
            }
            //con.setDoOutput(true);
            if (!TextUtils.isEmpty(cookie)) {
                con.setRequestProperty("Cookie", cookie);
            }
            con.setConnectTimeout(15000);
            is = con.getInputStream();
            return parseHtml(url, is, con);
        } catch (Exception error) {
            Timber.tag(TAG_ERROR).e(error);
            FileLogger.getInstance().report("HTTP", error);
            throw error;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        return isNetworkAvailable(context, true);
    }

    public static boolean isNetworkAvailable(Context context, boolean allowMetered) {
        final ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        final NetworkInfo network = manager.getActiveNetworkInfo();
        return network != null && network.isConnected() && (allowMetered || isNotMetered(network));
    }

    private static boolean isNotMetered(NetworkInfo networkInfo) {
        if(networkInfo.isRoaming()) return false;
        final int type = networkInfo.getType();
        return type == ConnectivityManager.TYPE_WIFI
                || type == ConnectivityManager.TYPE_WIMAX
                || type == ConnectivityManager.TYPE_ETHERNET;
    }

    private static void requestLog(String url, String cookie) {
        Timber.tag(TAG_REQUEST).d("request: %s", url);
        Timber.tag(TAG_REQUEST).d("cookie: %s", cookie);
    }

    private static Document parseHtml(@NonNull final String url, final InputStream is,
                                      final HttpURLConnection con) throws IOException {
        Document document = Jsoup.parse(is, con.getContentEncoding(), url);
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG_RESPONSE).d(document.html());
        }
        return document;
    }

    @NonNull
    public static JSONObject getJSONObject(@NonNull String url) throws IOException, JSONException {
        return new JSONObject(getString(url));
    }

    @NonNull
    public static String getString(@NonNull String url) throws IOException {
        return getString(url, HEADERS_DEFAULT);
    }

    @NonNull
    public static String getString(@NonNull String url, @NonNull Headers headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .headers(headers)
                .cacheControl(CACHE_CONTROL_DEFAULT)
                .get();
        try (Response response = sHttpClient.newCall(builder.build()).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("ResponseBody is null");
            } else {
                return body.string();
            }
        }
    }

}
