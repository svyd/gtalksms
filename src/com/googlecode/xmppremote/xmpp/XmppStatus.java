package com.googlecode.xmppremote.xmpp;

import java.io.File;

import com.googlecode.xmppremote.XmppManager;

import android.content.Context;

/**
 * This class provides an interface to the keyValue database
 * the last known state of the XMPP connection is saved. This helps
 * MainService to detect an unintentional restart of xmppremote and restore 
 * the last known state.
 *
 */
public class XmppStatus {
    
    private static final String STATEFILE_NAME = "xmppStatus";
    
    private static XmppStatus sXmppStatus;
    
    private File mStatefile;
    
    
    private XmppStatus(Context ctx) {
        File filesDir = ctx.getFilesDir();
        mStatefile = new File(filesDir, STATEFILE_NAME);
        // Delete the old statefile
        // TODO remove this check with a future release
        if (mStatefile.isFile()) {
            mStatefile.delete();
        }
    }
    
    public static XmppStatus getInstance(Context ctx) {
        if (sXmppStatus == null) {
            sXmppStatus = new XmppStatus(ctx);            
        }
        return sXmppStatus;
    }
    
    /**
     * Gets the last known XMPP status from the statefile
     * if there is no statefile the status for DISCONNECTED is returned
     * 
     * @return integer representing the XMPP status as defined in XmppManager
     */
    public int getLastKnowState() {
        int res = XmppManager.DISCONNECTED;
        return res;        
    }
    
    /**
     * Writes the current status int into the statefile
     * 
     * @param status
     */
    public void setState(int status) {
        String value = Integer.toString(status);
    }
}
