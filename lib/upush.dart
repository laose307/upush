import 'dart:async';
import 'dart:io';
import 'package:flutter/services.dart';

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';


typedef Future<dynamic> EventHandler(Map<String, dynamic> event);

class UpushPlugin {
  static const MethodChannel _channel =
  const MethodChannel('upush');
  static UpushPlugin _instance;

  factory UpushPlugin() => _getInstance();

  UpushPlugin._internal() {
    _channel.setMethodCallHandler(_handler);
  }

  static UpushPlugin _getInstance() {
    if (_instance == null) {
      _instance = UpushPlugin._internal();
    }
    return _instance;
  }


  //自定义推送消息
  EventHandler onReceiveCustomMessage = null;

  //推送消息
  EventHandler onReceiveNotification = null;

  //在线通知栏的点击
  EventHandler onNotificatiClickHandler = null;

  //接受离线通知的点击
  EventHandler onReceiveOffLineNotification = null;

  void addEventHandle(
      {EventHandler onNotificatiClickHandler,
        EventHandler onReceiveCustomMessage,
        EventHandler onReceiveNotification,
        EventHandler onReceiveOffLineNotification}) {
    this.onNotificatiClickHandler = onNotificatiClickHandler;
    this.onReceiveCustomMessage = onReceiveCustomMessage;
    this.onReceiveNotification = onReceiveNotification;
    this.onReceiveOffLineNotification=onReceiveOffLineNotification;
  }

  /**
   *ios初始化
   * appKey：申请的值
   * channel：通道的名字
   * debug：默认false(不输出log); 设置为true, 输出可供调试参考的log信息. 发布产品时必须设置为false.
   *badgeClear：否开启角标清空
   */
  void setupIOS(String appKey,{String channel="flutter",bool debug=false,bool badgeClear=true}) async {

    if (!Platform.isIOS) {
      return;
    }
    final Map<dynamic,dynamic> map ={"appkey": appKey, "channel": channel, "debug": debug,"badgeClear":badgeClear};

    _channel.invokeMethod("setupIOS", map) ;

  }

  /**
   * 否开启弹出框
   */
  void autoAlertIOS(bool autoAlert) async {

    if (!Platform.isIOS) {
      return;
    }
    final Map<dynamic,dynamic> map ={"autoAlert": autoAlert};

    _channel.invokeMethod("autoAlertIOS", map) ;

  }

  //是否开启角标清空
  void badgeClearIOS(bool badgeClear) async {

    if (!Platform.isIOS) {
      return;
    }
    final Map<dynamic,dynamic> map ={"badgeClear": badgeClear};

    _channel.invokeMethod("badgeClearIOS", map) ;

  }

  Future<Map<dynamic,dynamic>> addTags(List<String> list) async {
    final Map<dynamic,dynamic> result = await (_channel.invokeMethod("addTags", list)) ;

    debugPrint("************添加标签 addTags****${result}");

    return result;
  }


  Future<Map<dynamic,dynamic>> deleteTags(List<String> list) async {
    final Map<dynamic,dynamic> result =
    await _channel.invokeMethod("deleteTags", list);
    return result;
  }



  Future<List<dynamic>> getTags() async {
    final List<dynamic> result =
    await _channel.invokeMethod("getTags");
    return result;
  }



  Future<Map<dynamic,dynamic>> addAlias(String aliasId,String aliasType) async {
    final Map<dynamic,dynamic> result =
    await _channel.invokeMethod("addAlias",{"aliasId":aliasId,"aliasType":aliasType});
    return result;
  }


  Future<Map<dynamic,dynamic>> setAlias(String aliasId,String aliasType) async {
    final Map<dynamic,dynamic> result =
    await _channel.invokeMethod("setAlias",{"aliasId":aliasId,"aliasType":aliasType});
    return result;
  }

  Future<Map<dynamic,dynamic>> deleteAlias(String aliasId,String aliasType) async {
    final Map<dynamic,dynamic> result =
    await _channel.invokeMethod("deleteAlias",{"aliasId":aliasId,"aliasType":aliasType});
    return result;
  }

  /**
   * 注册id
   */
  Future<String> getRegistrationId() async {
    final String result =
    await _channel.invokeMethod("getRegistrationId");
    return result;
  }




  Future _handler(MethodCall call) {
    String method = call.method;
    Map map = call.arguments;
    debugPrint("************添加标签 map****${map}");
    if (method == "onNotificatiClickHandler") {
      //注册回调
      onNotificatiClickHandler(call.arguments.cast<String, dynamic>());
    } else if (method == "onReceiveCustomMessage") {
      //用户自定义消息
      onReceiveCustomMessage(call.arguments.cast<String, dynamic>());
    } else if (method == "onReceiveNotification") {
      //正常推送消息
      onReceiveNotification(call.arguments.cast<String, dynamic>());
    }else if(method=="onReceiveOffLineNotification"){
      //离线通知的点击
      debugPrint("**********enter **onOffLineMsgClickHandler ****");

      onReceiveOffLineNotification(call.arguments.cast<String, dynamic>());
    }
    else {
      throw new UnsupportedError("Unrecognized Event");
    }
  }
}
