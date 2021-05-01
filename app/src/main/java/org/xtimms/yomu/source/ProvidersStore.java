package org.xtimms.yomu.source;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.SparseBooleanArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.yomu.models.ProviderHeader;
import org.xtimms.yomu.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.Iterator;

public final class ProvidersStore {

    private static final ProviderHeader[] sProviders = new ProviderHeader[]{					//2
            new ProviderHeader(Desu.CNAME, Desu.DNAME, "Один из лучших каталогов манги. Хорош тем, что на сайт быстро заливают новые главы.")
    };

    private final SharedPreferences mPreferences;

    public ProvidersStore(Context context) {
        mPreferences = context.getSharedPreferences("providers", Context.MODE_PRIVATE);
    }

    public ArrayList<ProviderHeader> getAllProvidersSorted() {
        final ArrayList<ProviderHeader> list = new ArrayList<>(sProviders.length);
        final int[] order = CollectionsUtil.convertToInt(mPreferences.getString("order", "").split("\\|"), -1);
        for (int o : order) {
            ProviderHeader h = CollectionsUtil.getOrNull(sProviders, o);
            if (h != null) {
                list.add(h);
            }
        }
        for (ProviderHeader h : sProviders) {
            if (!list.contains(h)) {
                list.add(h);
            }
        }
        return list;
    }

    public ArrayList<ProviderHeader> getUserProviders() {
        final ArrayList<ProviderHeader> list = getAllProvidersSorted();
        final int[] disabled = getDisabledIds();
        Iterator<ProviderHeader> iterator = list.iterator();
        while (iterator.hasNext()) {
            ProviderHeader h = iterator.next();
            if (CollectionsUtil.indexOf(disabled, h.hashCode()) != -1) {
                iterator.remove();
            }
        }
        return list;
    }

    @Nullable
    public static ProviderHeader getByCName(String cName) {
        for (ProviderHeader o : sProviders) {
            if (cName.equals(o.cName)) {
                return o;
            }
        }
        return null;
    }

    public void save(ArrayList<ProviderHeader> providers, SparseBooleanArray enabled) {
        final Integer[] order = new Integer[providers.size()];
        for (int i = 0; i < sProviders.length; i++) {
            ProviderHeader h = sProviders[i];
            int p = providers.indexOf(h);
            if (p != -1) {
                order[i] = p;
            }
        }
        final ArrayList<Integer> disabled = new ArrayList<>();
        for (int i=0;i<providers.size();i++) {
            if (!enabled.get(i, true)) {
                disabled.add(providers.get(i).hashCode());
            }
        }
        mPreferences.edit()
                .putString("order", TextUtils.join("|", order))
                .putString("disabled", TextUtils.join("|", disabled))
                .apply();
    }

    @NonNull
    public int[] getDisabledIds() {
        return CollectionsUtil.convertToInt(mPreferences.getString("disabled", "").split("\\|"), -1);
    }
}
