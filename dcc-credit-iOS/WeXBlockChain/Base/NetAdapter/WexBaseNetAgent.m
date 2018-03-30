//
//  WexBaseNetAgent.m
//  WeXFin
//
//  Created by Mark on 16/5/23.
//  Copyright © 2016年 SinaPay. All rights reserved.
//

#import "WexBaseNetAgent.h"
#import "AFHTTPSessionManager.h"

//#import "WexCommonCoreDefine.h"
#import "WexBaseNetAdapter.h"
#import "WexBaseNetCache.h"
#import "WexBaseNetMask.h"

@interface WexBaseNetAgent ()
{
    AFHTTPSessionManager* _manager;
    dispatch_queue_t _requestProcessingQueue;
}

@end

@implementation WexBaseNetAgent

+ (WexBaseNetAgent*)sharedInstance
{
    static id sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (id)init
{
    self = [super init];
    if (self)
    {
        _manager = [AFHTTPSessionManager manager];
        
        [_manager.requestSerializer setValue:@"application/json"
                          forHTTPHeaderField:@"Accept"];
        _manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/html",nil];
        [_manager.requestSerializer setValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
        _requestProcessingQueue = dispatch_queue_create("com.wexfin.wexnetwork.request.processing", DISPATCH_QUEUE_SERIAL);
        _requestProcessingQueue = dispatch_queue_create("com.wexfin.wexnetwork.request.processing", DISPATCH_QUEUE_CONCURRENT);
    }
    return self;
}

- (void)addRequest:(WexBaseNetAdapter*)request
{
    __weak typeof(self) weakSelf = self;
    
    dispatch_async(_requestProcessingQueue, ^{
        [weakSelf wex_addRequest:request];
    });
}

- (void)wex_addRequest:(WexBaseNetAdapter*)request
{
//#if defined(WEX_ALLOW_INVALID_SSL)
//    _manager.securityPolicy.allowInvalidCertificates = YES; // 验证环境需要加上
//#else
    
//#endif
    
    // 设置超时时间
    _manager.requestSerializer.timeoutInterval = [request getTimeoutInterval];
    
    // 目前用于手势密码弹出，在有接口调用时后延
    [[NSNotificationCenter defaultCenter] postNotificationName:WEX_NET_ADAPTER_START_NOTIFY object:nil userInfo:nil];
    
    __weak typeof(self) weakSelf = self;
    
    [_manager POST:[request getFullRequestUrl] parameters:[self getPostContent:request] progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        [weakSelf requestSuccessFinish:request withResponse:responseObject];

    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        [weakSelf requestErrorFinish:request withError:error];

    }];
}

- (void)requestSuccessFinish:(WexBaseNetAdapter*)request withResponse:(id)responseObject
{
    NSLog(@"\nURL: %@\nContent: %@", [request getRequestUrl], [responseObject description]);
    
    // 目前用于手势密码弹出，在有接口调用时后延
    [[NSNotificationCenter defaultCenter] postNotificationName:WEX_NET_ADAPTER_END_NOTIFY object:nil userInfo:nil];
    
    [request requestSuccessFinish:responseObject];
}

- (void)requestErrorFinish:(WexBaseNetAdapter*)request withError:(NSError*)error
{
    NSLog(@"\nURL: %@\nError: %@", [request getRequestUrl], error);
    
    // 目前用于手势密码弹出，在有接口调用时后延
    [[NSNotificationCenter defaultCenter] postNotificationName:WEX_NET_ADAPTER_END_NOTIFY object:nil userInfo:nil];
    
    [request requestErrorFinish:error];
}

#pragma mark - Parameters

- (NSMutableDictionary*)getPostContent:(WexBaseNetAdapter*)request
{
    NSMutableDictionary* parameters = [NSMutableDictionary dictionary];
 
    if (request.paramModal)
    {
        [parameters addEntriesFromDictionary:[request.paramModal toDictionary]];
    }
    
    NSLog(@"\nURL: %@\nPost body: %@", [request getRequestUrl], parameters);
    
    return parameters;
}

@end
