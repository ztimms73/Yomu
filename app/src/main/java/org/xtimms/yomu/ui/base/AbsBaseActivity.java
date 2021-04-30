package org.xtimms.yomu.ui.base;

public abstract class AbsBaseActivity extends AbsThemeActivity {

    private int navigationbarColor;
    private boolean lightStatusbar;

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
