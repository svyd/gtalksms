package com.googlecode.xmppremote.panels.wizard;

import com.googlecode.xmppremote.R;

import android.view.View;
import android.view.View.OnClickListener;

public class BackToMainClickListener implements OnClickListener {
    
    private Wizard mWizard;

    /**
     * 
     * @param w
     */
    protected BackToMainClickListener(Wizard w) {
        this.mWizard = w;
    }
    
    
    public void onClick(View v) {
        mWizard.setContentView(R.layout.main);
    }

}
