//
//  WexBaseNetAgent.h
//  WeXFin
//
//  Created by Mark on 16/5/23.
//  Copyright © 2016年 SinaPay. All rights reserved.
//

/********************************************
 - 基类说明：
 
 - 修改说明：
 1. 使用AFHTTPSessionManager替换AFHTTPRequestOperationManager
 2. AFHTTPSessionManager仅创建一次，提高接口请求速度

 - 参考示例：
 
 - 其他说明：
 
 ********************************************/

#import <Foundation/Foundation.h>

@class WexBaseNetAdapter;

@interface WexBaseNetAgent : NSObject

+ (WexBaseNetAgent*)sharedInstance;
- (void)addRequest:(WexBaseNetAdapter*)request;

@end
