package com.googlecode.xmppremote.receivers;

import com.googlecode.xmppremote.xmpp.XmppEntityCapsCache;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StorageLowReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        XmppEntityCapsCache.emptyCache();        
    }

}
