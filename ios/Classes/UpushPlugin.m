#import "UpushPlugin.h"

@implementation UpushPlugin



+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"upush"
                                     binaryMessenger:[registrar messenger]];
    UpushPlugin* instance = [[UpushPlugin alloc] init];
    instance.channel = channel;
    
    [registrar addApplicationDelegate:instance];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    
    if([@"setupIOS" isEqualToString:call.method]){
        [self setupIOS:call result:result];
    }else if([@"autoAlertIOS" isEqualToString:call.method]){
        [self autoAlertIOS:call result:result];
    }else if([@"addTags" isEqualToString:call.method]){
        [self addTags:call result:result];
    }
    else if([@"deleteTags" isEqualToString:call.method]){
        [self deleteTags:call result:result];
    }else if([@"getTags" isEqualToString:call.method]){
        [self getTags:call result:result];
    }
    else if([@"addAlias" isEqualToString:call.method]){
        [self addAlias:call result:result];
    }else if([@"deleteAlias" isEqualToString:call.method]){
        [self deleteAlias:call result:result];
    }
    else if([@"setAlias" isEqualToString:call.method]){
        [self setAlias:call result:result];
    }
    else if([@"getRegistrationId" isEqualToString:call.method]){
        [self getRegistrationId:call result:result];
    }
    else if([@"badgeClearIOS" isEqualToString:call.method]){
        [self badgeClearIOS:call result:result];
    }
    
    else {
        result(FlutterMethodNotImplemented);
    }
    
    
    
    
}

- (void)setupIOS:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSDictionary* arguments=call.arguments;
    NSLog(@"setupIOS:%@",arguments);
    NSNumber* debug=arguments[@"debug"];
    NSNumber* debgeClear=arguments[@"badgeClear"];
    //默认NO(不输出log); 设置为YES, 输出可供调试参考的log信息. 发布产品时必须设置为NO.
    [UMConfigure setLogEnabled:[debug boolValue]];
    
    [UMConfigure initWithAppkey:arguments[@"appkey"] channel:arguments[@"channel"]];
    
    //是否开启角标清空
    [UMessage setBadgeClear:[debgeClear boolValue]];
}

//是否开启弹出框
- (void)autoAlertIOS:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSDictionary* arguments=call.arguments;
    NSNumber* autoAlert=arguments[@"autoAlert"];
    [UMessage setAutoAlert:[autoAlert boolValue]];
}
 //是否开启角标清空
- (void)badgeClearIOS:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSDictionary* arguments=call.arguments;
    NSNumber* debgeClear=arguments[@"badgeClear"];
    [UMessage setBadgeClear:[debgeClear boolValue]];
}


#pragma mark - Tag

-(void)addTags:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSSet* tagSet;
    
    if(call.arguments!=NULL){
        tagSet=[NSSet setWithArray:call.arguments];
    }
    [UMessage addTags:tagSet response:^(id  _Nonnull responseObject, NSInteger remain, NSError * _Nonnull error) {
        if (responseObject) {
            if ([[responseObject objectForKey:@"success"] isEqualToString:@"ok"]) {
                result(@{@"isSuccess":@"true",
                         @"message":@"添加成功"});
            }else
            {
                result(@{@"isSuccess":@"false",
                         @"message":[NSString stringWithFormat:@"%@",responseObject]});
            }
        }else
        {
            result(@{@"isSuccess":@"false",
                     @"message":error.localizedDescription});
        }
    }];
    
}

-(void)deleteTags:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSSet* tagSet;
    
    if(call.arguments!=NULL){
        tagSet=[NSSet setWithArray:call.arguments];
    }
    [UMessage deleteTags:tagSet response:^(id  _Nonnull responseObject, NSInteger remain, NSError * _Nonnull error) {
        
        if (responseObject) {
            if ([[responseObject objectForKey:@"success"] isEqualToString:@"ok"]) {
                result(@{@"isSuccess":@"true",
                         @"message":@"删除成功"});
            }else
            {
                result(@{@"isSuccess":@"false",
                         @"message":[NSString stringWithFormat:@"%@",responseObject]});
            }
        }else
        {
            result(@{@"isSuccess":@"false",
                     @"message":error.localizedDescription});
        }
    }];
}


-(void)getTags:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSSet* tagSet;
    
    if(call.arguments!=NULL){
        tagSet=[NSSet setWithArray:call.arguments];
    }
    [UMessage getTags:^(NSSet * _Nonnull responseTags, NSInteger remain, NSError * _Nonnull error) {
        if (responseTags) {
            
            //
            NSSortDescriptor *sd = [[NSSortDescriptor alloc] initWithKey:nil ascending:YES];
            NSArray *sortDescriptors = [NSArray arrayWithObjects:sd, nil];
            NSArray *userArray = [responseTags sortedArrayUsingDescriptors:sortDescriptors];
            
            
            result(userArray);
        }else
        {
            NSArray *array = [[NSArray alloc] initWithArray:nil];
            result(array);
        }
    }];
}

#pragma mark - Tag

-(void)addAlias:(FlutterMethodCall*)call result:(FlutterResult)result {
    
    NSString* aliasId=call.arguments[@"aliasId"];
    NSString* aliasType=call.arguments[@"aliasType"];
    
    
    
    [UMessage addAlias:aliasId type:aliasType response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
        if (responseObject) {
            if ([[responseObject objectForKey:@"success"] isEqualToString:@"ok"]) {
                
                result(@{@"isSuccess":@"true",
                         @"message":[NSString stringWithFormat:@"aliasName:%@ aliasType:%@",aliasId,aliasType]});
                
            }else
            {
                result(@{@"isSuccess":@"false",
                         @"message":[NSString stringWithFormat:@"%@",responseObject]});
                
            }
        }else
        {
            result(@{@"isSuccess":@"false",
                     @"message":error.localizedDescription});
        }
    }];
    
    
    
}

-(void)setAlias:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSString* aliasId=call.arguments[@"aliasId"];
    NSString* aliasType=call.arguments[@"aliasType"];
    
    [UMessage setAlias:aliasId type:aliasType response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
        if (responseObject) {
            if ([[responseObject objectForKey:@"success"] isEqualToString:@"ok"]) {
                
                result(@{@"isSuccess":@"true",
                         @"message":[NSString stringWithFormat:@"aliasName:%@ aliasType:%@",aliasId,aliasType]});
                
            }else
            { result(@{@"isSuccess":@"false",
                       @"message":[NSString stringWithFormat:@"%@",responseObject]});
                
            }
        }else
        {
            result(@{@"isSuccess":@"false",
                     @"message":error.localizedDescription});
        }
    }];
    
}

-(void)deleteAlias:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSString* aliasId=call.arguments[@"aliasId"];
    NSString* aliasType=call.arguments[@"aliasType"];
    
    
    [UMessage removeAlias:aliasId type:aliasType response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
        if (responseObject) {
            if ([[responseObject objectForKey:@"success"] isEqualToString:@"ok"]) {
                
                result(@{@"isSuccess":@"true",
                         @"message":[NSString stringWithFormat:@"aliasName:%@ aliasType:%@",aliasId,aliasType]});
                
            }else
            { result(@{@"isSuccess":@"false",
                       @"message":[NSString stringWithFormat:@"%@",responseObject]});
            }
        }else
        {
            result(@{@"isSuccess":@"false",
                     @"message":error.localizedDescription});
        }
    }];
    
    
    
    [UMessage removeAlias:aliasId type:aliasType response:^(id  _Nullable responseObject, NSError * _Nullable error) {
        if(error==NULL){
            result(@{@"isSuccess":@"true",@"message":[responseObject allObjects] ?: @[]});
        }else{
            result(@{@"isSuccess":@"false",@"message":[NSString stringWithFormat:@"%ld",(long)error.localizedDescription]});
        }
    }];
}


-(void)getRegistrationId:(FlutterMethodCall*)call result:(FlutterResult)result {
    
    result(self.deviceToken);
}


#pragma mark - AppDelegate

- (BOOL)application:(UIApplication *)application
didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    //TODO: ios 程序杀死后 收到APNs推送 点击通知栏获取通知详细
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if (launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey]) {
            
            // 当被杀死状态收到本地通知时执行的跳转代码
            NSDictionary *remoteCotificationDic = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
            
            NSLog(@"离线消息推送点击通知栏");
            [self.channel invokeMethod:@"onReceiveOffLineNotification" arguments:remoteCotificationDic];
            
            
        }
    });
    
    
    
    
    // Push组件基本功能配置
    UMessageRegisterEntity * entity = [[UMessageRegisterEntity alloc] init];
    //type是对推送的几个参数的选择，可以选择一个或者多个。默认是三个全部打开，即：声音，弹窗，角标
    entity.types = UMessageAuthorizationOptionBadge|UMessageAuthorizationOptionSound|UMessageAuthorizationOptionAlert;
    [UNUserNotificationCenter currentNotificationCenter].delegate=self;
    [UMessage registerForRemoteNotificationsWithLaunchOptions:launchOptions Entity:entity     completionHandler:^(BOOL granted, NSError * _Nullable error) {
        if (granted) {
            NSLog(@"**********已经授权成功************");
        }else{
            NSLog(@"**********授权失败************");
        }
    }];
    
    
    
    
    return YES;
}
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken{
    // 方式1
    
    NSMutableString *deviceTokenString = [NSMutableString string];
    
    const char *bytes = deviceToken.bytes;
    
    int iCount = deviceToken.length;
    
    for (int i = 0; i < iCount; i++) {
        
        [deviceTokenString appendFormat:@"%02x", bytes[i]&0x000000FF];
    }
    NSLog(@"deviceToken：%@", deviceTokenString);
    
    [UMessage registerDeviceToken: deviceToken];
    
    self.deviceToken=deviceTokenString;
    
    
    
}

//iOS10以下使用这两个方法接收通知，
-(void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler
{
    [UMessage setAutoAlert:NO];
    if([[[UIDevice currentDevice] systemVersion]intValue] < 10){
        [UMessage didReceiveRemoteNotification:userInfo];
        
        [_channel invokeMethod:@"onReceiveNotification" arguments:userInfo];
        completionHandler(UIBackgroundFetchResultNewData);
    }
}



//iOS10新增：处理前台收到通知的代理方法
-(void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler{
    NSDictionary * userInfo = notification.request.content.userInfo;
    if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        //应用处于前台时的远程推送接受
        //关闭U-Push自带的弹出框
        [UMessage setAutoAlert:NO];
        //必须加这句代码
        [UMessage didReceiveRemoteNotification:userInfo];
        
        NSLog(@"应用处于前台时的远程推送接受：%@", userInfo);
        
        
    }else{
        //应用处于前台时的本地推送接受
        NSLog(@"应用处于前台时的远程推送接受：%@", userInfo);
    }
    
    
    [self.channel invokeMethod:@"onReceiveNotification" arguments:userInfo];
    
    
    
    //当应用处于前台时提示设置，需要哪个可以设置哪一个
    completionHandler(UNNotificationPresentationOptionSound|UNNotificationPresentationOptionBadge|UNNotificationPresentationOptionAlert);
}

// iOS 10 以下点击本地通知
-(void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    NSString *title = @"";
    if (@available(iOS 8.2, *)) {
        title = notification.alertTitle;
    } else {
        // Fallback on earlier versions
    }
    
    NSString *body = notification.alertBody;
    NSString *action = notification.alertAction;
    
    [dic setValue:title?:@"" forKey:@"title"];
    [dic setValue:body?:@"" forKey:@"body"];
    [dic setValue:action?:@"" forKey:@"action"];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.channel invokeMethod:@"onNotificatiClickHandler" arguments:dic];
    });
}


//iOS10新增：处理后台点击通知的代理方法
-(void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler{
    //    completionHandler();
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        //应用处于后台时的远程推送接受
        //必须加这句代码
        [UMessage didReceiveRemoteNotification:userInfo];
        NSLog(@"应用处于后台时的远程推送接受：%@", userInfo);
        
    }else{
        //应用处于后台时的本地推送接受
        NSLog(@"应用处于后台时的本地推送接受：%@", userInfo);
    }
    
    
    UIApplicationState state = [UIApplication sharedApplication].applicationState;
    NSLog(@"应用处于后台时的本地推送接受：%ld", (long)state);
    
    if(state == UIApplicationStateBackground){
        
        NSLog(@"离线消息推送点击通知栏");
        [self.channel invokeMethod:@"onReceiveOffLineNotification" arguments:userInfo];
    }else if (state == UIApplicationStateInactive){
        
        NSLog(@"在线推送点击通知栏");
        [self.channel invokeMethod:@"onNotificatiClickHandler" arguments:userInfo];
    }
}



@end
