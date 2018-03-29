//
//  WexBaseNetAdapter.m
//  WeXFin
//
//  Created by Mark on 15/7/27.
//  Copyright (c) 2015年 SinaPay. All rights reserved.
//

#import "WexBaseNetAdapter.h"
#import "AFHTTPSessionManager.h"
#import "WexBaseNetCache.h"
#import "WexBaseNetExceptionStatus.h"
#import "WexBaseNetAgent.h"


#define WEX_NET_COOKIES_ARRAY @"WEX_NET_COOKIES_ARRAY"

@interface WexBaseNetAdapter ()

@property (nonatomic) WexNetAdapterStatus status;

@end

@implementation WexBaseNetAdapter

#pragma mark - Public Methods

- (id)init
{
    self = [super init];
    if (self) {
        _bDefaultRequestUIHandle = YES;
        _bDefaultResponseUIHandle = YES;
        _status = WexNetAdapterStatusIdle;
    }
    return self;
}

- (NSString*)getRequestUrl
{
    return nil;
}

- (NSTimeInterval)getTimeoutInterval
{
    return 30.0;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeNomal;
}

- (WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass
{
    return nil;
}

- (void)adapterSuccessResponse:(JSONModel*)response
{
    
}

- (void)adapterFailResponse:(JSONModel*)response head:(WexBaseNetAdapterResponseHeadModal*)head
{
    
}

#pragma mark - URL

// 获得完整的URL
- (NSString*)getFullRequestUrl
{
    NSMutableString* url = [self getBaseRequestUrl];
    
    [url appendString:[self getRequestUrl]];
    
    return url;
}

- (NSMutableString*)getBaseRequestUrl
{
    
    NSMutableString* url = nil;
    WexBaseUrlType type = [self getBasetUrlType];
    if (type == WexBaseUrlTypeNomal) {
        NSString *baseUrl = WEX_BASE_NORMAL_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    else if (type == WexBaseUrlTypeDigitalAsset)
    {
        NSString *baseUrl = WEX_BASE_DIGITAL_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    else if (type == WexBaseUrlTypeInfura)
    {
        NSString *baseUrl = WEX_BASE_INFURA_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    else if (type == WexBaseUrlTypeEtherscan)
    {
        NSString *baseUrl = WEX_BASE_ETHERSCAN_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    else if (type == WexBaseUrlTypeEthplorer)
    {
        NSString *baseUrl = WEX_BASE_ETHPLORER_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    else if(type == WexBaseUrlTypeLogic)
    {
        NSString *baseUrl = WEX_BASE_LOGIC_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    else if(type == WexBaseUrlTypeAuthen)
    {
        NSString *baseUrl = WEX_BASE_AUTHEN_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    else if(type == WexBaseUrlTypeAll)
    {
        NSString *baseUrl = WEX_BASE_ALL_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    else if(type == WexBaseUrlTypeFTC)
    {
        NSString *baseUrl = WEX_BASE_FTC_URL;
        url = [NSMutableString stringWithString:baseUrl];
    }
    NSLog(@"BaseUrl:%@",url);

    
#if defined(WEX_TEST_SERVER) // 测试环境
#elif defined(WEX_DEVELOP_SERVER) // 开发环境
#elif defined(WEX_LINK_SERVER) // 联调环境
#else // 生产环境
#endif
    return url;
}

- (NSMutableDictionary*)getParameters
{
    NSMutableDictionary* parameters = [NSMutableDictionary dictionary];
    
    if (_paramModal)
    {
        [parameters addEntriesFromDictionary:[_paramModal toDictionary]];
    }
    
    NSLog(@"\nURL: %@\nPost body: %@", [self getFullRequestUrl], parameters);
    
    return parameters;
}

#pragma mark - Cookies相关（用于恢复登录状态）

// 保存Cookies
+ (void)saveNetCookies
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    NSArray* cookies = cookieStorage.cookies;
    
    // 将cookie值转为Data数据保存
    NSMutableArray *dataCookieArray = [[NSMutableArray alloc] init];
    
    for (NSHTTPCookie *cookie in cookies)
    {
        NSDictionary *dic = [cookie properties];
        [dataCookieArray addObject:dic];
    }
    
    [userDefaults setObject:dataCookieArray forKey:WEX_NET_COOKIES_ARRAY];
    
    [userDefaults synchronize];
}

// 恢复Cookies
+ (void)restoreNetCookies
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSArray* cookies = [userDefaults objectForKey:WEX_NET_COOKIES_ARRAY];
    
    if (cookies &&
        [cookies count] > 0)
    {
        NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
        
        for (NSDictionary* dict in cookies)
        {
            NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:dict];
            [cookieStorage setCookie:cookie];
        }
    }
}

// 清除Cookies
+ (void)deleteNetCookies
{
    NSHTTPCookieStorage* storage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    for (NSHTTPCookie* cookie in [storage cookies])
    {
        [storage deleteCookie:cookie];
    }
}

// 删除保存的Cookies
+ (void)deleteSavedNetCookies
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
    [userDefaults removeObjectForKey:WEX_NET_COOKIES_ARRAY];
    
    [userDefaults synchronize];
}

#pragma mark - Private Methods

- (void)run:(JSONModel*)paramModal
{
    _paramModal = paramModal;
    
    if (_delegate &&
        [_delegate respondsToSelector:@selector(wexBaseNetAdapterDelegate:beforeRun:)])
    {
        [_delegate wexBaseNetAdapterDelegate:self beforeRun:_paramModal];
    }
    
    _status = WexNetAdapterStatusRun;
    
    // 将adapter缓存起来，用于后面处理SESSION超时异常
    [[WexBaseNetExceptionStatus getInstance] pushAdapter:self];
    
   
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];

    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/html",nil];
    
    [manager.requestSerializer setValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    // 设置超时时间
    manager.requestSerializer.timeoutInterval = [self getTimeoutInterval];
    
    // 目前用于手势密码弹出，在有接口调用时后延
    [[NSNotificationCenter defaultCenter] postNotificationName:WEX_NET_ADAPTER_START_NOTIFY object:nil userInfo:nil];
    
    if ([self getNetAdapterRequestType]==WexNetAdapterRequestTypePOST) {
        [manager POST:[self getFullRequestUrl] parameters:[self getParameters] progress:^(NSProgress * _Nonnull uploadProgress) {
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            NSLog(@"\nURL: %@\nContent: %@", [self getRequestUrl], [responseObject description]);
            [self requestSuccessFinish:responseObject ];
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            NSLog(@"\nURL: %@\nError: %@", [self getRequestUrl], error);
            [self requestErrorFinish:error];
        }];
    }
    else if ([self getNetAdapterRequestType]==WexNetAdapterRequestTypeGET)
    {
       
        [manager GET:[self getFullRequestUrl] parameters:[self getParameters] progress:^(NSProgress * _Nonnull downloadProgress) {
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            NSLog(@"\nURL: %@\nContent: %@", [self getRequestUrl], [responseObject description]);
            [self requestSuccessFinish:responseObject ];
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            NSLog(@"\nURL: %@\nError: %@", [self getRequestUrl], error);
            [self requestErrorFinish:error];
        }];
    }
}

- (void)runImage:(NSMutableDictionary *)paramDict
{
    
    _status = WexNetAdapterStatusRun;
    
    // 将adapter缓存起来，用于后面处理SESSION超时异常
    [[WexBaseNetExceptionStatus getInstance] pushAdapter:self];
    
    //2. 利用时间戳当做图片名字
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyyMMddHHmmss";
    NSString *imageName = [formatter stringFromDate:[NSDate date]];
    NSString *fileName = [NSString stringWithFormat:@"%@.jpg",imageName];
    
    //4. 发起网络请求
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    
    manager.responseSerializer = [AFJSONResponseSerializer serializer];

    NSData *uploadImageData = [paramDict objectForKey:@"uploadImageData"];
    
    [paramDict removeObjectForKey:@"uploadImageData"];
    
    [manager POST:[self getFullRequestUrl] parameters:paramDict constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
        // 上传图片，以文件流的格式，这里注意：name是指服务器端的文件夹名字
      [formData appendPartWithFileData:uploadImageData name:@"file" fileName:fileName mimeType:@"image/jpeg"];
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"\nURL: %@\nContent: %@", [self getRequestUrl], [responseObject description]);
        [self requestSuccessFinish:responseObject ];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"\nURL: %@\nError: %@", [self getRequestUrl], error);
        [self requestErrorFinish:error];
    }];
    
}

// 获得Adapter的运行状态
- (WexNetAdapterStatus)getNetAdapterStatus
{
    return _status;
}

- (void)requestSuccessFinish:(NSDictionary *)responseObject
{
    _status = WexNetAdapterStatusIdle;
    
    // 该adapter因为SESSION超时已被清空
    // 1. 防止重入
    // 2. 截断此前已提交的所有接口
    if ([[WexBaseNetExceptionStatus getInstance] isAdapterExist:self] == NO)
    {
        return;
    }
    
    NSError* err = nil;
    
    WexBaseNetAdapterResponseHeadModal *headModal = [[WexBaseNetAdapterResponseHeadModal alloc] initWithDictionary:responseObject error:&err];
    
    WeXBaseNetModal *responseModal = nil;
    
    NSDictionary* data = nil;
    
    if ([self getBasetUrlType] == WexBaseUrlTypeDigitalAsset) {
       data = [responseObject objectForKey:@"data"];
    }
    else if ([self getBasetUrlType] == WexBaseUrlTypeEthplorer)
    {
         data = [responseObject objectForKey:@"operations"];
    }
    else
    {
         data = [responseObject objectForKey:@"result"];
    }
   
    
    if (headModal == nil)
    {
        
    }
    // body不为空
    else if ([self getResponseClass])
    {
//         data为空
        if (data == nil ||
            [data class] == [NSNull class])
        {
            responseModal = [[[self getResponseClass] alloc] initWithDictionary:responseObject error:&err];
        }
        else
        {
            if([data isKindOfClass:[NSDictionary class]])
            {
                responseModal = [[[self getResponseClass] alloc] initWithDictionary:data error:&err];
            }
            else
            {
                responseModal = [[[self getResponseClass] alloc] initWithDictionary:responseObject error:&err];
            }
        }
    }

    // 将缓存的adapter释放
    [[WexBaseNetExceptionStatus getInstance] popAdapter:self];
    
    // delegate调用
    if (_delegate &&
        [_delegate respondsToSelector:@selector(wexBaseNetAdapterDelegate:head:response:)])
    {
        [_delegate wexBaseNetAdapterDelegate:self head:headModal response:responseModal];
    }
}

- (void)requestErrorFinish:(NSError*)error
{
    _status = WexNetAdapterStatusIdle;
    
    // 该adapter因为SESSION超时已被清空
    // 1. 防止重入
    // 2. 截断此前已提交的所有接口
    if ([[WexBaseNetExceptionStatus getInstance] isAdapterExist:self] == NO)
    {
        return;
    }
    
    WexBaseNetAdapterResponseHeadModal* headModal = [[WexBaseNetAdapterResponseHeadModal alloc] init];
    headModal.systemCode = [NSString stringWithFormat:@"%ld", (long)error.code];
    
    [WeXPorgressHUD hideLoading];
    
    if (error.code == NSURLErrorTimedOut)
    {
        headModal.message = @"请求超时，请稍后重试!";
        
    }
    else
    {
        headModal.message = @"网络异常，请检查网络连接!";
    }
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    [WeXPorgressHUD showText:headModal.message onView:window];
    
    // 接口调用失败
    [self adapterFailResponse:nil head:headModal];
    
    // 将缓存的adapter释放
    [[WexBaseNetExceptionStatus getInstance] popAdapter:self];
    
    // delegate调用
    if (_delegate &&
        [_delegate respondsToSelector:@selector(wexBaseNetAdapterDelegate:head:response:)])
    {
        [_delegate wexBaseNetAdapterDelegate:self head:nil response:nil];
    }
}

- (void)sendSessionTimeoutNotify
{
    [[NSNotificationCenter defaultCenter] postNotificationName:WEX_NET_SESSION_TIMEOUT_NOTIFY object:nil userInfo:nil];
}

- (void)sendDeviceChangeNotify
{
    [[NSNotificationCenter defaultCenter] postNotificationName:WEX_NET_CHANGE_DEVICE_NOTIFY object:nil userInfo:nil];
}








@end
