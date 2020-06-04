import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:upush/upush.dart';


void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int index = 0;
  GlobalKey key;
  var mapview1 = null;
  UpushPlugin mUpushPlugin = UpushPlugin();
  String result = "返回消息提示";

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    //消息的回调
    mUpushPlugin.addEventHandle(
        onReceiveCustomMessage: (Map<String, dynamic> event) {
          setState(() {
            result = "用户自定义消息内容： ${event} ";
          });
        }, onNotificatiClickHandler: (Map<String, dynamic> event) {
      setState(() {
        result = "点击通知栏" + event.toString();
      });
    }, onReceiveNotification: (Map<String, dynamic> event) {
      setState(() {
        result = "在线接收通知" + event.toString();
      });
    }, onReceiveOffLineNotification: (Map<String, dynamic> event) {
      setState(() {
        result = "离线通知：" + event.toString();
      });
    });

    //ios 初始化 key
    mUpushPlugin.setupIOS("5ecc86c7895cca40e60003e7");


  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: Center(
            child: Column(
              children: <Widget>[
                Text("返回结果：" + result),
                RaisedButton(
                  onPressed: () {
                    mUpushPlugin.addTags(["tags1", "tags2"]).then((value) {
                      debugPrint(" 添加标签 ${value} ");
                      setState(() {
                        result = " 添加标签  结果：${value} ";
                      });
                    });
                  },
                  child: Text("添加标签值:tags1, tags2"),
                ),
                RaisedButton(
                  onPressed: () {
                    mUpushPlugin.deleteTags(["tags1", "tags2"]).then((value) {
                      debugPrint(" 删除标签 结果：${value} ");
                      setState(() {
                        result = " 删除标签 结果：${value} ";
                      });
                    });
                  },
                  child: Text("删除标签  tags1, tags2"),
                ),
                RaisedButton(
                  onPressed: () {
                    mUpushPlugin.getTags().then((value) {
                      debugPrint(" 获取标签  结果：${value} ");
                      setState(() {
                        result = " 获取标签 结果：${value} ";
                      });
                    });
                  },
                  child: Text("获取标签"),
                ),
                RaisedButton(
                  onPressed: () {
                    mUpushPlugin.addAlias("flutter", "android").then((value) {
                      debugPrint(" 添加别名  结果：${value} ");
                      setState(() {
                        result = " 添加别名 结果：${value} ";
                      });
                    });
                  },
                  child: Text("添加别名:aliasId:flutter，  aliasType:android"),
                ),
                RaisedButton(
                  onPressed: () {
                    mUpushPlugin.setAlias("flutter", "ios").then((value) {
                      debugPrint(" 设置别名  结果：${value} ");
                      setState(() {
                        result = " 设置别名  结果：${value} ";
                      });
                    });
                  },
                  child: Text("设置别名 aliasId:flutter,aliasType:ios"),
                ),
                RaisedButton(
                  onPressed: () {
                    mUpushPlugin.deleteAlias("flutter", "ios").then((value) {
                      debugPrint(" 删除别名 结果：${value} ");
                      setState(() {
                        result = " 删除别名 结果：${value} ";
                      });
                    });
                  },
                  child: Text("删除别名 aliasId:flutter，aliasType:ios "),
                ),
                RaisedButton(
                  onPressed: () {
                    mUpushPlugin.getRegistrationId().then((value) {
                      debugPrint(" 获取注册id ${value} ");
                      setState(() {
                        result = " 获取注册id ${value} ";
                      });
                    });
                  },
                  child: Text("获取注册id "),
                ),
              ],
            ),
          ),
        ));
  }
}
