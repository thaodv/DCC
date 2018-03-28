//
//  WexBaseNetAdapter.h
//  WeXFin
//
//  Created by Mark on 15/7/27.
//  Copyright (c) 2015年 SinaPay. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WexBaseNetAdapterModal.h"
#import "WeXBaseNetImageModal.h"

#pragma mark - enum

typedef NS_ENUM(NSInteger,WexBaseUrlType) {
    WexBaseUrlTypeNomal,
    WexBaseUrlTypeDigitalAsset,//数字资产接口
    WexBaseUrlTypeInfura,
    WexBaseUrlTypeEtherscan,
    WexBaseUrlTypeEthplorer,
    WexBaseUrlTypeLogic,
    WexBaseUrlTypeAuthen,
    WexBaseUrlTypeAll,
    WexBaseUrlTypeFTC
};

typedef NS_ENUM(NSInteger,WexNetAdapterRequestType) {
    WexNetAdapterRequestTypePOST,
    WexNetAdapterRequestTypeGET
};


typedef enum : NSUInteger {
    WexNetAdapterStatusRun, // 接口调用中
    WexNetAdapterStatusIdle, // 接口空闲
} WexNetAdapterStatus;

@interface WexBaseNetAdapter : NSObject

@property (nonatomic, weak) id<WexBaseNetAdapterDelegate> delegate;

@property (nonatomic) BOOL bDefaultRequestUIHandle; // 是否启用默认的接口请求UI回调处理（这里仅仅是标识，具体的处理由UI类去处理）
@property (nonatomic) BOOL bDefaultResponseUIHandle; // 是否启用默认的接口返回UI回调处理（这里仅仅是标识，具体的处理由UI类去处理）

#pragma mark - 子类必须覆写

// 接口地址，子类必须覆写
- (NSString*)getRequestUrl;

#pragma mark - 子类非必须覆写
//获取基本请求类型
- (WexBaseUrlType)getBasetUrlType;

//获取请求类型 post or get
- (WexNetAdapterRequestType)getNetAdapterRequestType;

// 设置相关
// 设置超时时间，默认是30s，子类覆写（非必须）
- (NSTimeInterval)getTimeoutInterval;
// Response类型，子类覆写（非必须，对于body为空的不用覆写）
// 特殊说明：如果data返回的直接是数组，那ResponseClass从body开始定义，可参考“WexGetBannersAdapterModal.h”
- (Class)getResponseClass;

// 调用完成后数据处理相关
// 成功和失败后的数据处理，子类覆写（非必须）
- (void)adapterSuccessResponse:(JSONModel*)response;
- (void)adapterFailResponse:(JSONModel*)response head:(WexBaseNetAdapterResponseHeadModal*)head;

#pragma mark - URL

// 获得完整的URL
- (NSString*)getFullRequestUrl;
// 得到基本的URL
- (NSMutableString*)getBaseRequestUrl;

#pragma mark - 调用接口

// 调用接口
- (void)run:(JSONModel*)paramModal;

- (void)runImage:(NSMutableDictionary *)paramDict;

// 获得Adapter的运行状态
- (WexNetAdapterStatus)getNetAdapterStatus;

#pragma mark - Cookies相关（用于恢复登录状态）

// 保存Cookies
+ (void)saveNetCookies;
// 恢复Cookies
+ (void)restoreNetCookies;
// 清除Cookies
+ (void)deleteNetCookies;
// 删除保存的Cookies
+ (void)deleteSavedNetCookies;

/********************************************
 仅供WexBaseNetAgent调用
********************************************/

@property (nonatomic, strong) JSONModel* paramModal;

- (void)requestSuccessFinish:(id)responseObject;
- (void)requestErrorFinish:(NSError*)error;

@end
