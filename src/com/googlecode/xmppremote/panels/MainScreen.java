package com.googlecode.xmppremote.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.googlecode.xmppremote.MainService;
import com.googlecode.xmppremote.R;
import com.googlecode.xmppremote.SettingsManager;
import com.googlecode.xmppremote.XmppManager;
import com.googlecode.xmppremote.tools.StringFmt;
import com.googlecode.xmppremote.tools.Tools;
import com.googlecode.xmppremote.xmpp.XmppAccountManager;
import com.googlecode.xmppremote.xmpp.XmppFriend;

public class MainScreen extends Activity {

    
    private MainService mMainService;
    private SettingsManager mSettingsMgr;
    private BroadcastReceiver mXmppreceiver;
    private ArrayList<HashMap<String, String>> mFriends = new ArrayList<HashMap<String, String>>();
    ListView mBuddiesListView;

    private ServiceConnection _mainServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mMainService = ((MainService.LocalBinder) service).getService();
            updateStatus(mMainService.getConnectionStatus(), mMainService.getTLSStatus(), mMainService.getCompressionStatus());
            mMainService.updateBuddies();
        }

        public void onServiceDisconnected(ComponentName className) {
            mMainService = null;
        }
    };

    private void updateStatus(int status, boolean tls, boolean compression) {
        ImageView statusImg = (ImageView) findViewById(R.id.StatusImage);
        ImageView tlsStatus = (ImageView) findViewById(R.id.TLSsecured);
        ImageView compressionStatus = (ImageView) findViewById(R.id.compression);

        switch (status) {
            case XmppManager.CONNECTED:
                statusImg.setImageResource(R.drawable.led_green);
                break;
            case XmppManager.DISCONNECTED:
                statusImg.setImageResource(R.drawable.led_red);
                break;
            case XmppManager.CONNECTING:
                statusImg.setImageResource(R.drawable.led_orange_con);
                break;
            case XmppManager.DISCONNECTING:
                statusImg.setImageResource(R.drawable.led_orange_discon);
                break;
            case XmppManager.WAITING_TO_CONNECT:
                statusImg.setImageResource(R.drawable.led_orange_timewait);
                break;
            case XmppManager.WAITING_FOR_NETWORK:
                statusImg.setImageResource(R.drawable.no_network);
                break;
            default:
                throw new IllegalStateException();
        }

        tlsStatus.setVisibility(tls ? View.VISIBLE : View.INVISIBLE);
        compressionStatus.setVisibility(compression ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        unbindService(_mainServiceConnection);
        unregisterReceiver(mXmppreceiver);
    }

    private static String getStateImg(int stateType) {
        String state = String.valueOf(R.drawable.buddy_offline);
        switch (stateType) {
            case XmppFriend.AWAY:
            case XmppFriend.EXAWAY:
                state = String.valueOf(R.drawable.buddy_away);
                break;
            case XmppFriend.BUSY:
                state = String.valueOf(R.drawable.buddy_busy);
                break;
            case XmppFriend.ONLINE:
                state = String.valueOf(R.drawable.buddy_available);
                break;
        }
        
        return state;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mXmppreceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(MainService.ACTION_XMPP_PRESENCE_CHANGED)) {
                    int stateInt = intent.getIntExtra("state", XmppFriend.OFFLINE);
                    String userId = intent.getStringExtra("userid");
                    String userFullId = intent.getStringExtra("fullid");  // TODO check if fullid contains only the bare resource
                    String name = intent.getStringExtra("name");
                    String status = intent.getStringExtra("status");
                    String stateImg = getStateImg(stateInt);

                    boolean exist = false;
                    for (HashMap<String, String> map : mFriends) {
                        if (map.get("userid").equals(userId)) {
                            exist = true;                          
                            if (stateInt == XmppFriend.OFFLINE) {
                                map.remove("location_" + userFullId);
                                
                                for (String key : map.keySet()) {
                                    if (key.startsWith("location_")) {
                                        try {
                                            stateImg = getStateImg(stateInt);
                                            break; 
                                        } catch (Exception e) {}
                                    }
                                }
                            } else if (userFullId != null) {
                                map.put("location_" + userFullId, XmppFriend.stateToString(stateInt));
                            }
                            map.put("state", stateImg);
                            map.put("status", status);
                            break;
                        }
                    }

                    if (!exist) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("name", name);
                        map.put("status", status);
                        map.put("userid", userId);
                        map.put("state", stateImg);
                        if (userFullId != null && stateInt != XmppFriend.OFFLINE) {
                            map.put("location_" + userFullId, XmppFriend.stateToString(stateInt)+ "\n");
                        }
                        
                        mFriends.add(map);
                    }
                    if (mSettingsMgr.debugLog) Log.i(Tools.LOG_TAG, "Update presence: " + userId + " - " + XmppFriend.stateToString(stateInt));
                    updateBuddiesList();

                } else if (action.equals(MainService.ACTION_XMPP_CONNECTION_CHANGED)) {
                    updateStatus(intent.getIntExtra("new_state", 0), intent.getBooleanExtra("TLS", false), intent.getBooleanExtra("Compression", false));
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(MainService.ACTION_XMPP_PRESENCE_CHANGED);
        intentFilter.addAction(MainService.ACTION_XMPP_CONNECTION_CHANGED);
        registerReceiver(mXmppreceiver, intentFilter);
        Intent intent = new Intent(MainService.ACTION_CONNECT);
        bindService(intent, _mainServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingsMgr = SettingsManager.getSettingsManager(this);
        if(TextUtils.isEmpty(mSettingsMgr.serverHost)){
        	XmppAccountManager.saveCredentialsPreferences(mSettingsMgr);
        }
        createView();
        
    }

    /** Called when the activity is first created. */
    @Override
    public void onDestroy() {
        mSettingsMgr.Destroy();
        super.onDestroy();
    }

    private void createView() {
    	if (mSettingsMgr.connectOnMainscreenShow) {
    	    Tools.startSvcIntent(this, MainService.ACTION_CONNECT);
    	}
    	
        Tools.setLocale(mSettingsMgr, this);
        
        setContentView(R.layout.main);

        TextView label = (TextView) findViewById(R.id.VersionLabel);
        label.setText(StringFmt.Style(Tools.APP_NAME + " " + Tools.getVersionName(getBaseContext()), Typeface.BOLD));

        Button prefBtn = (Button) findViewById(R.id.Preferences);
        prefBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                openOptionsMenu();
            }
        });

        Button aboutBtn = (Button) findViewById(R.id.About);
        aboutBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), About.class));
            }
        });

        
        Button startStopButton = (Button) findViewById(R.id.StartStop);
        startStopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainService.sendToServiceHandler(new Intent(MainService.ACTION_TOGGLE));
            }
        });

        mBuddiesListView = (ListView) findViewById(R.id.ListViewBuddies);

        mBuddiesListView.setOnItemClickListener(new OnItemClickListener() {
            
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) mBuddiesListView.getItemAtPosition(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(MainScreen.this);
                adb.setTitle(map.get("name"));
                
                String user = map.get("userid");
                StringBuilder sb = new StringBuilder(user);
                sb.append(Tools.LineSep);
                sb.append(Tools.LineSep);
                for (String key : map.keySet()) {
                    try {
                        if (key.startsWith("location_")) {
                            sb.append(key.substring(10 + user.length()));
                            sb.append(": ");
                            sb.append(map.get(key));
                            sb.append(Tools.LineSep);
                        }
                    } catch(Exception e) {
                        Log.e(Tools.LOG_TAG, "Failed to decode buddy name", e);
                    }
                }
                adb.setMessage(sb.toString());
                adb.setPositiveButton("Ok", null);
                adb.show();
            }
        });
    }

    private void updateBuddiesList() {
        Collections.sort(mFriends, new Comparator<HashMap<String, String>> () {
            public int compare(HashMap<String, String> object1, HashMap<String, String> object2) {
                if (object1.get("name") != null && object2.get("name") != null) {
                    return object1.get("name").compareTo(object2.get("name"));
                }
                return object1.get("userid").compareTo(object2.get("userid"));
            }});
        
        SimpleAdapter mSchedule = new SimpleAdapter(getBaseContext(), mFriends, R.layout.buddyitem, new String[] { "state", "name", "status" }, new int[] {
                R.id.buddyState, R.id.buddyName, R.id.buddyStatus });

        mBuddiesListView.setAdapter(mSchedule);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Force menu update on each opening for localization issue
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_menu, menu);

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int prefs_id;
        Intent intent;
        
        switch (item.getItemId()) {
            case R.id.connection_settings:
                prefs_id = R.xml.prefs_connection;
                break;
            case R.id.cmd_manager:
                intent = new Intent(MainScreen.this, CmdManager.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        intent = new Intent(MainScreen.this, Preferences.class);
        intent.putExtra("panel", prefs_id);
        startActivity(intent);
        return true;
    }
}
