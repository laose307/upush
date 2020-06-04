#import <Flutter/Flutter.h>
#import <UMCommon/UMCommon.h>
#import <UMPush/UMessage.h>
@interface UpushPlugin : NSObject<FlutterPlugin>
@property FlutterMethodChannel *channel;
@property NSString *deviceToken;
@end
