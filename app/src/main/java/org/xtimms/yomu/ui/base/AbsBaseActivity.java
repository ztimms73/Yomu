package org.xtimms.yomu.ui.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

public abstract class AbsBaseActivity extends AbsThemeActivity {

    private int navigationbarColor;
    private boolean lightStatusbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createContentView());
    }

    protected abstract View createContentView();

    public boolean handleBackPress() {
        return false;
    }

    @Override
    public void setLightStatusbar(boolean enabled) {
        lightStatusbar = enabled;
        super.setLightStatusbar(enabled);
    }

    @Override
    public void setNavigationbarColor(int color) {
        this.navigationbarColor = color;
        super.setNavigationbarColor(color);
    }

}
