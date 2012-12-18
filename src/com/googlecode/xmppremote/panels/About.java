package com.googlecode.xmppremote.panels;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.googlecode.xmppremote.R;
import com.googlecode.xmppremote.tools.StringFmt;
import com.googlecode.xmppremote.tools.Tools;

public class About extends Activity {
    
    @Override
    public void onPause() {
        super.onPause();
    }
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView label = (TextView) findViewById(R.id.VersionLabel);
        label.setText(StringFmt.Style(Tools.APP_NAME + " " + Tools.getVersionName(this), Typeface.BOLD));

        updateConsole();
        
    }
    
    public void updateConsole() {
      TextView console = (TextView) findViewById(R.id.Text);
      console.setText("");
      console.append(StringFmt.Fmt(getString(R.string.about_name) + "\n", 0xFFFF0000, 1.3, Typeface.BOLD));
      console.append(StringFmt.Fmt(getString(R.string.about_department) + "\n\n", Color.LTGRAY, 1.0, Typeface.NORMAL));
      console.append(StringFmt.Fmt("Student:" + "\n", Color.LTGRAY, 1.0, Typeface.NORMAL));
      console.append(StringFmt.Fmt("Svydenko Valerij" + "\n\n", Color.GREEN, 1.2, Typeface.BOLD_ITALIC));
      console.append(StringFmt.Fmt("December, 2012", Color.LTGRAY, 1.0, Typeface.NORMAL));
    }
}
