package com.crittermap.backcountrynavigator.xe.ui.settings;

class BCSettingsViewModel {
    private int titleId;
    private String subtitle;

    public BCSettingsViewModel(int titleId) {
        this.titleId = titleId;
    }

    public BCSettingsViewModel(int titleId, String subtitle) {
        this.titleId = titleId;
        this.subtitle = subtitle;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
