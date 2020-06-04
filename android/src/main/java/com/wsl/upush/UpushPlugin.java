package com.wsl.upush;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** UpushPlugin */
public class UpushPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private static final String SHARED_PREFERENCES_KEY = "notification_plugin_cache";
  private static final String DRAWABLE = "drawable";
  private final  static  String DEFAULT_ICON="DEFAULT_ICON";
  private static final String INVALID_ICON_ERROR_CODE = "INVALID_ICON";
  public MethodChannel channel;
  private Context context;
  private static String TAG = "FlutterupushpluginPlugin";
  public static final String UPDATE_STATUS_ACTION = "com.umeng.message.flutter.action.UPDATE_STATUS";
  private static final String INVALID_DRAWABLE_RESOURCE_ERROR_MESSAGE = "The resource %s could not be found. Please make sure it has been added as a drawable resource to your Android head project.";
  private PushAgent mPushAgent;
  public static UpushPlugin instance;
  private Handler handler = new Handler();

  @Override
  public void onAttachedToEngine( FlutterPluginBinding flutterPluginBinding) {
    Log.d(TAG, "*****************onAttachedToEngine****************");
    UpushPlugin mFlutterupushpluginPlugin = new UpushPlugin();
    mFlutterupushpluginPlugin.context = flutterPluginBinding.getApplicationContext();
    mFlutterupushpluginPlugin.channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "upush");
    mFlutterupushpluginPlugin.channel.setMethodCallHandler(mFlutterupushpluginPlugin);
    instance = mFlutterupushpluginPlugin;
    mFlutterupushpluginPlugin.mPushAgent = PushAgent.getInstance(flutterPluginBinding.getApplicationContext());
  }


  public static void registerWith(Registrar registrar) {
    Log.d(TAG, "*****************registerWith****************");
    if (registrar.activity() != null) {
      UpushPlugin mFlutterupushpluginPlugin = new UpushPlugin();
      mFlutterupushpluginPlugin.context = registrar.context();
      final MethodChannel channel = new MethodChannel(registrar.messenger(), "upush");
      channel.setMethodCallHandler(mFlutterupushpluginPlugin);
      mFlutterupushpluginPlugin.mPushAgent = PushAgent.getInstance(registrar.context().getApplicationContext());
      instance = mFlutterupushpluginPlugin;
    }


  }


  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    String method = call.method;

   if (method.equals("addTags")) {
      addTags(call, result);
    } else if (method.equals("deleteTags")) {
      deleteTags(call, result);
    } else if (method.equals("getTags")) {
      getTags(call, result);
    }
    else if (method.equals("addAlias")) {
      addAlias(call, result);
    } else if (method.equals("deleteAlias")) {
      deleteAlias(call, result);
    } else if (method.equals("setAlias")) {
      setAlias(call, result);
    } else if (method.equals("getRegistrationId")) {
      getRegistrationId(call, result);
    }


  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }


  private void init(MethodCall call, Result result) {
    Map<String, Object> arguments = call.arguments();
    String defaultIcon = (String) arguments.get(DEFAULT_ICON);
    if (!isValidDrawableResource(context, defaultIcon, result, INVALID_ICON_ERROR_CODE)) {
      return;
    }
    SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(DEFAULT_ICON, defaultIcon);
    editor.commit();

    result.success(true);
  }
  private static boolean isValidDrawableResource(Context context, String name, Result result, String errorCode) {
    int resourceId = context.getResources().getIdentifier(name, DRAWABLE, context.getPackageName());
    if (resourceId == 0) {
      result.error(errorCode, String.format(INVALID_DRAWABLE_RESOURCE_ERROR_MESSAGE, name), null);
      return false;
    }
    return true;
  }

  /**
   * 初始化
   */

  private void initUpush(MethodCall call, Result result) {
    Map dataMap = (Map) call.arguments;

    Log.d(TAG, "****************enter *initUpush****************");

    //使用完全自定义处理
//    mPushAgent.setPushIntentServiceClass(UmengNotificationService.class);

    //小米通道
//        MiPushRegistar.register(UApplication.this, XIAOMI_ID, XIAOMI_KEY);
    //华为通道
    //HuaWeiRegister.register(this);
    //魅族通道
    //MeizuRegister.register(this, MEIZU_APPID, MEIZU_APPKEY);

  }


  @UiThread
  private void  sendResult(Result result,Object object){
    result.success(object);
  }

  /**
   * 添加标签 示例：将“标签1”绑定至该设备
   */
  private void addTags(MethodCall call, final Result result) {
    List<String> tagList = call.arguments();
    final String[] tags = tagList.toArray(new String[tagList.size()]);

    UPushApplication.mPushAgent.getTagManager().addTags(new TagManager.TCallBack() {

      @Override
      public void onMessage(final boolean isSuccess, final ITagManager.Result ITagManager_result) {
        Log.d(TAG, "**********addTags:" + isSuccess + " " + ITagManager_result.toString());
        handler.post(new Runnable() {
          @Override
          public void run() {
            Map resultMap=new HashMap();
            resultMap.put("isSuccess",isSuccess);
            resultMap.put("message",ITagManager_result.msg);
            result.success(resultMap);
          }
        });

      }

    }, tags);


  }

  /**
   * 删除标签,将之前添加的标签中的一个或多个删除
   */
  private void deleteTags(MethodCall call, final  Result result) {
    List<String> tagList = call.arguments();
    final String[] tags = tagList.toArray(new String[tagList.size()]);



    UPushApplication.mPushAgent.getTagManager().deleteTags(new TagManager.TCallBack() {


      @Override
      public void onMessage(final boolean isSuccess, final ITagManager.Result ITagManagerresult) {



        handler.post(new Runnable() {
          @Override
          public void run() {
            Map resultMap=new HashMap();
            resultMap.put("isSuccess",isSuccess);
            resultMap.put("message",ITagManagerresult.msg);
            result.success(resultMap);
          }
        });
      }

    }, tags);

  }

  /**
   * 离线消息的点击
   */
  public void onOffLineMsgClickHandler( final String json) {

    if (instance != null&& !TextUtils.isEmpty(json)) {
      handler.post(new Runnable() {
        @Override
        public void run() {

          if (channel != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("json", json);
            Log.d(TAG, "**********onOffLineMsgClickHandler:" + json + " " + json.toString());
            instance.channel.invokeMethod("onReceiveOffLineNotification", map);
          }
        }
      });
    }


  }


  /**
   * 获取服务器端的所有标签
   */
  private void getTags(MethodCall call, final Result result) {
//        NotificationManagerUtils.startNotificationManager("title",R.drawable.umeng_push_notification_default_small_icon,context) ;

    UPushApplication.mPushAgent.getTagManager().getTags(new TagManager.TagListCallBack() {

      @Override
      public void onMessage(boolean isSuccess,final List<String> _result) {

        handler.post(new Runnable() {
          @Override
          public void run() {
            result.success(_result);
          }
        });



      }

    });
  }


  /**
   * 别名增加，将某一类型的别名ID绑定至某设备，老的绑定设备信息还在，别名ID和device_token是一对多的映射关系
   */
  private void addAlias(MethodCall call, final Result result) {
    Map map = call.arguments();
    String aliasId= map.get("aliasId").toString();
    String aliasType= map.get("aliasType").toString();
    mPushAgent.addAlias(aliasId,aliasType, new UTrack.ICallBack() {

      @Override
      public void onMessage(final boolean isSuccess, final String message) {
        handler.post(new Runnable() {
          @Override
          public void run() {
            Log.d(TAG," isSuccess :"+isSuccess +" message:"+message );
            Map resultMap=new HashMap();
            resultMap.put("isSuccess",isSuccess);
            resultMap.put("message",message);
            result.success(resultMap);
          }
        });
      }

    });


  }

  /**
   * //移除别名ID
   *
   */
  private void deleteAlias(MethodCall call, final Result result) {
    Map map = call.arguments();
    String aliasId= map.get("aliasId").toString();
    String aliasType= map.get("aliasType").toString();
    mPushAgent.deleteAlias(aliasId, aliasType, new UTrack.ICallBack() {

      @Override
      public void onMessage(final boolean isSuccess, final String message) {


        handler.post(new Runnable() {
          @Override
          public void run() {
            Log.d(TAG," isSuccess :"+isSuccess +" message:"+message );
            Map resultMap=new HashMap();
            resultMap.put("isSuccess",isSuccess);
            resultMap.put("message",message);
            result.success(resultMap);
          }
        });
      }

    });
  }

  /**
   * //别名绑定，将某一类型的别名ID绑定至某设备，老的绑定设备信息被覆盖，别名ID和deviceToken是一对一的映射关系
   *
   */
  private void setAlias(MethodCall call, final Result result) {
    Map map = call.arguments();
    String aliasId= map.get("aliasId").toString();
    String aliasType= map.get("aliasType").toString();
    mPushAgent.setAlias(aliasId, aliasType, new UTrack.ICallBack() {

      @Override
      public void onMessage(final boolean isSuccess, final String message) {
        handler.post(new Runnable() {
          @Override
          public void run() {
            Log.d(TAG," isSuccess :"+isSuccess +" message:"+message );
            Map resultMap=new HashMap();
            resultMap.put("isSuccess",isSuccess);
            resultMap.put("message",message);
            result.success(resultMap);
          }
        });
      }

    });
  }

  private void getRegistrationId(MethodCall call, final Result result) {
    result.success(mPushAgent.getRegistrationId());
  }

}
