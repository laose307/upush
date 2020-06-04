package com.wsl.upush;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

import java.util.ArrayList;
import java.util.List;

public class UPushOffLineNotificationActivity extends UmengNotifyClickActivity {
    private static String TAG = UPushOffLineNotificationActivity.class.getName();
    private TextView mipushTextView;
    ArrayList<String> list = new ArrayList<String>();
    private List<ResolveInfo> mApps;
    private ResolveInfo info;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

    }

    @Override
    protected void a(Intent intent) {
        super.a(intent);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);


        final String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);


            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
             mainIntent.setPackage(this.getPackageName());
            mApps = getPackageManager().queryIntentActivities(mainIntent, 0);


            for (int i = 0; i < mApps.size(); i++) {
                info = mApps.get(i);
                String appLabel = info.loadLabel(getPackageManager()).toString();
                String packagename = info.activityInfo.packageName;
                String appname = info.activityInfo.name;

                ComponentName mComponentName=new ComponentName(packagename,appname);
                Intent intent1 =new Intent();
                intent1.setComponent(mComponentName);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle mExtras = new Bundle();
                mExtras.putString("pushJson",body);
                intent1.putExtras(mExtras);
                startActivity(intent1);
                Log.d("pushJson","MipushTestActivity:"+body);
                UPushApplication.mUPushApplication.onOffLineMsgClickHandler(body);
                finish();

            }








    }
}
