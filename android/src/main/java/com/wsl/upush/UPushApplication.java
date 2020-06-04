package com.wsl.upush;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.BuildConfig;
import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterMain;

/**
 * @package com.wsl.flutterupushplugin
 * @fileName UApplication
 * @date 2020/3/18 10:28 AM
 * @auther 老色
 * @describe TODO
 */
public class UPushApplication extends Application {
    Handler handler;
    String TAG = "UApplication";
    public static PushAgent mPushAgent;
    private final static String suffix = "@";
    private String XIAOMI_ID = null;
    private String XIAOMI_KEY = null;

    private String MEIZU_APPID;
    private String MEIZU_APPKEY;

    private String HUAWEI_APPID;


    private String OPPO_KEY;
    private String OPPO_SECRET;

    private String VIVO_KEY;
    private String VIVO_APPID;
    long delayMillis=4000;
    public  static UPushApplication mUPushApplication;
    @Override
    @CallSuper
    public void onCreate() {
        Log.i(TAG, "≈：**************onCreat********");
        super.onCreate();
        FlutterMain.startInitialization(this);

        //初始化友盟
        initUpush();
        mUPushApplication=this;
    }

    /**
     * 设置延迟
     * @param delayMillis
     */
    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    private void initUpush() {


        //todo 获取mannifest.xml配置
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Log.d(TAG, " metaData == " + (appInfo.metaData == null) + " UMENG_MESSAGE_SECRET");
            String UMENG_APPKEY = appInfo.metaData.getString("UMENG_APPKEY").replace(suffix, "");
            String UMENG_MESSAGE_SECRET = appInfo.metaData.getString("UMENG_MESSAGE_SECRET").replace(suffix, "");
            String UMENG_CHANNEL = appInfo.metaData.getString("UMENG_CHANNEL").replace(suffix, "");
            Log.d(TAG, "****************umeng通道****************UMENG_APPKEY:" + UMENG_APPKEY + " UMENG_MESSAGE_SECRET:" + UMENG_MESSAGE_SECRET);

            if(appInfo.metaData.containsKey("XIAOMI_APPID")){
                XIAOMI_ID = appInfo.metaData.getString("XIAOMI_APPID").replace(suffix, "");
                XIAOMI_KEY = appInfo.metaData.getString("XIAOMI_APPKEY").replace(suffix, "");

            }




            if(appInfo.metaData.containsKey("com.huawei.hms.client.appid")){
                HUAWEI_APPID = appInfo.metaData.getString("com.huawei.hms.client.appid").replace(suffix, "");

            }


            if(appInfo.metaData.containsKey("MEIZU_APPID")&&appInfo.metaData.containsKey("MEIZU_APPKEY")){
                MEIZU_APPID = appInfo.metaData.getString("MEIZU_APPID").replace(suffix, "");
                MEIZU_APPKEY = appInfo.metaData.getString("MEIZU_APPKEY").replace(suffix, "");
                Log.d(TAG, "****************meizu通道****************MEIZU_APPID:" + MEIZU_APPID +" MEIZU_APPKEY："+MEIZU_APPKEY );
            }

            if(appInfo.metaData.containsKey("OPPO_APPKEY")&&appInfo.metaData.containsKey("OPPO_APPSECRET")){
                OPPO_KEY = appInfo.metaData.getString("OPPO_APPKEY").replace(suffix, "");
                OPPO_SECRET = appInfo.metaData.getString("OPPO_APPSECRET").replace(suffix, "");
                Log.d(TAG, "****************oppo通道****************MEIZU_APPID:" + MEIZU_APPID +" MEIZU_APPKEY："+MEIZU_APPKEY );
            }

            if(appInfo.metaData.containsKey("com.vivo.push.api_key")&&appInfo.metaData.containsKey("com.vivo.push.app_id")){
                VIVO_KEY = appInfo.metaData.getString("com.vivo.push.api_key").replace(suffix, "");
                VIVO_APPID = appInfo.metaData.getString("com.vivo.push.app_id").replace(suffix, "");
                Log.d(TAG, "****************oppo通道****************MEIZU_APPID:" + MEIZU_APPID +" MEIZU_APPKEY："+MEIZU_APPKEY );
            }



            UMConfigure.init(this, UMENG_APPKEY, UMENG_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE,
                    UMENG_MESSAGE_SECRET);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setResourcePackageName("com.wsl.flutterupushplugin");
        handler = new Handler(getMainLooper());
        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        mPushAgent.setDisplayNotificationNumber(10);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, final UMessage msg) {
                //调用super，会展示通知，不调用super，则不展示通知。
                super.dealWithNotificationMessage(context, msg);

                if (UpushPlugin.instance != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("title", msg.title);
                            notification.put("alias", msg.alias);
                            notification.put("extras", msg.extra);
                            notification.put("text", msg.text);
                            notification.put("url", msg.url);
                            Log.i(TAG, "**************通知的回调方法: " + notification);
                            final MethodChannel channel = UpushPlugin.instance.channel;
                            channel.invokeMethod("onReceiveNotification", notification);
                        }
                    });
                }


            }

            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (UpushPlugin.instance != null) {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("custom", msg.custom);
                            notification.put("extras", msg.extra);
                            Log.i(TAG, "**************自定义消息的回调方法: " + notification);
                            final MethodChannel channel = UpushPlugin.instance.channel;
                            Log.i(TAG, "**************自定义消息的回调方法  channel==null: " + (channel == null));
                            channel.invokeMethod("onReceiveCustomMessage", notification, new MethodChannel.Result() {
                                @Override
                                public void success(Object result) {
                                    Log.i(TAG, "*************自定义消息的回调方法 *success: ");
                                }

                                @Override
                                public void error(String errorCode, String errorMessage, Object errorDetails) {
                                    Log.i(TAG, "**************自定义消息的回调方法: " + errorCode + " " + errorMessage + "  " + errorDetails);
                                }

                                @Override
                                public void notImplemented() {

                                }
                            });
                        }


                    }
                });


            }

            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                Log.i(TAG, "**************getNotification: " + msg.title);


                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder = new Notification.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon,
                                getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);
                        return builder.getNotification();

                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override

            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                Log.i(TAG, "**************launchApp: " + msg.title);
                if (UpushPlugin.instance != null) {
                    onClickHandler(msg, "launchApp");
                }

            }

            @Override
            public void openUrl(Context context, UMessage msg) {

                Log.i(TAG, "**************openUrl: " + msg.title);
                if (UpushPlugin.instance != null) {
                    onClickHandler(msg, "openUrl");
                } else {
                    super.openUrl(context, msg);
                }


            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                Log.i(TAG, "**************openActivity: " + msg);


                if (UpushPlugin.instance != null) {
                    onClickHandler(msg, "openActivity");
                } else {
                    super.openActivity(context, msg);
                }
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
//                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                onClickHandler(msg, "dealWithCustomAction");
            }
        };
        //使用自定义的NotificationHandler
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(final String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG, "≈：**************deviceToken：-------->  " + deviceToken);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        final MethodChannel channel = FlutterupushpluginPlugin.instance.channel;
//                        if(channel!=null){
//                            Map map=new HashMap();
//                            map.put("deviceToken",deviceToken);
//                            map.put("msg","注册成功");
//                            channel.invokeMethod("onRegisterCallback",map);
//                        }
//                    }
//                });

            }

            @UiThread
            @Override
            public void onFailure(final String s, final String s1) {
                Log.e(TAG, "******************注册失败：-------->  " + "s:" + s + ",s1:" + s1);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        final MethodChannel channel = FlutterupushpluginPlugin.instance.channel;
//                        if(channel!=null){
//                            Map map=new HashMap();
//                            map.put("error",s +" "+s1);
//                            map.put("msg","注册失败");
//                            channel.invokeMethod("RegisterCallback",map);
//                        }
//                    }
//                });

            }
        });


        //使用完全自定义处理
//    mPushAgent.setPushIntentServiceClass(UmengNotificationService.class);

        if (XIAOMI_ID != null && !XIAOMI_ID.equals("") && XIAOMI_KEY != null && !XIAOMI_KEY.equals("")) {
//            //小米通道
            Log.d(TAG, "");
            MiPushRegistar.register(this, XIAOMI_ID, XIAOMI_KEY);
            Log.d(TAG, "****************小米通道****************XIAOMI_ID:" + XIAOMI_ID + " XIAOMI_KEY:" + XIAOMI_KEY);

        }

        //华为通道
        if(HUAWEI_APPID!=null&&!HUAWEI_APPID.equals("")){
            HuaWeiRegister.register(this);
            Log.d(TAG, "****************华为通道****************HUAWEI_APPID:" + HUAWEI_APPID );
        }


        //魅族通道
        if (MEIZU_APPID != null && !MEIZU_APPID.equals("") && MEIZU_APPKEY != null && !MEIZU_APPKEY.equals("")) {
            MeizuRegister.register(this, MEIZU_APPID, MEIZU_APPKEY);


        }

        //opop
       //OPPO通道，参数1为app key，参数2为app secret

        if (OPPO_KEY != null && !OPPO_KEY.equals("") && OPPO_SECRET != null && !OPPO_SECRET.equals("")) {

            OppoRegister.register(this, OPPO_KEY, OPPO_SECRET);

        }

        //vivo
        if (VIVO_APPID != null && !VIVO_APPID.equals("") && VIVO_KEY != null && !VIVO_KEY.equals("")) {
            VivoRegister.register(this);
        }





    }


    private void onClickHandler(final UMessage msg, final String type) {

         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 if (UpushPlugin.instance == null) {
                     return;
                 }
                 final MethodChannel channel = UpushPlugin.instance.channel;
                 if (channel != null) {
                     Map<String, Object> map = new HashMap<>();
                     map.put("title", msg.title);
                     map.put("alias", msg.alias);
                     map.put("extras", msg.extra);
                     map.put("text", msg.text);
                     map.put("url", msg.url);
                     map.put("activity", msg.activity);
                     map.put("custom", msg.custom);
                     map.put("task_id", msg.task_id);
                     map.put("methodType", type);
                     channel.invokeMethod("onNotificatiClickHandler", map);
                 }
             }
         },delayMillis);

    }



    public void onOffLineMsgClickHandler(final String pushJson){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pushJson!=null){
                    UpushPlugin.instance.onOffLineMsgClickHandler(pushJson);
                }
            }
        },delayMillis);
    }

}