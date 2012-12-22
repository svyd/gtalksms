package com.googlecode.xmppremote.xmpp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.googlecode.xmppremote.XmppManager;

import android.content.Context;

public class ClientOfflineMessages {
    private static final String DIRECTORY = "clientOfflineMessagesData";
    private static File sDirFile;
    private static XmppMuc sXmppMuc;
    private static XMPPConnection sXMPPConnection;

    private static ClientOfflineMessages sClientOfflineMessages;
    
    private ClientOfflineMessages(Context ctx) {
        sDirFile = new File(ctx.getFilesDir(), DIRECTORY);
        if (!sDirFile.exists()) {
            sDirFile.mkdir();
        }
        sXmppMuc = XmppMuc.getInstance(ctx);
        cleanUp();
    }

    public static ClientOfflineMessages getInstance(Context ctx) {
        if (sClientOfflineMessages == null) {
            sClientOfflineMessages = new ClientOfflineMessages(ctx);
        }
        return sClientOfflineMessages;
    }
    
    public void registerListener(XmppManager xmppMgr) {
        XmppConnectionChangeListener listener = new XmppConnectionChangeListener() {
            public void newConnection(XMPPConnection connection) {
                sXMPPConnection = connection;
            }            
        };
        xmppMgr.registerConnectionChangeListener(listener);
    }
    
    
    
    private static void cleanUp() {
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date date = cal.getTime();
        
    }
    
}
