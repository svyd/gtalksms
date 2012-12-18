package com.googlecode.xmppremote.cmd.cameraCmd;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EmailCallback extends ExtentedPictureCallback {

    public EmailCallback(File path, Context ctx, String recipient) {
        super(path, ctx, recipient);
    }

    @Override
    protected boolean onPictureSaved(File picture) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "xmppremote Picture");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "xmppremote Picture");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { recipient });
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picture));
        emailIntent.setType("image/jpeg");
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(emailIntent);
        return true;
    }
}
