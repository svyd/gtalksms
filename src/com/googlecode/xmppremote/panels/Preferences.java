package com.googlecode.xmppremote.panels;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.googlecode.xmppremote.SettingsManager;
import com.googlecode.xmppremote.tools.Tools;

public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.setLocale(SettingsManager.getSettingsManager(this), this);
        
        getPreferenceManager().setSharedPreferencesName(Tools.APP_NAME);
        Intent intent = getIntent();
        int prefs_id = intent.getIntExtra("panel", 0);
        addPreferencesFromResource(prefs_id);
    }
}
