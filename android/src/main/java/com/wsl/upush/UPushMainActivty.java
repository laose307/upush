package com.wsl.upush;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import io.flutter.embedding.android.FlutterActivity;

/**
 * @package com.wsl.flutterupushplugin
 * @fileName MainActivty
 * @date 2020/4/1 4:45 PM
 * @auther 老色
 * @describe TODO
 */
public class UPushMainActivty extends FlutterActivity {

    Handler handler=new Handler(Looper.myLooper());
    String pushJson="";
    long delayMillis=3000;//默认延迟发送的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras()!=null){
            pushJson= getIntent().getExtras().getString("pushJson");
            Log.d("UPushMainActivty","umeng 离线推送");

        }


    }

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //启动界面要等待引擎加载，延迟3秒发送到界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pushJson!=null){
                    UpushPlugin.instance.onOffLineMsgClickHandler(pushJson);
                    pushJson=null;
                }
            }
        },delayMillis);

    }


}
