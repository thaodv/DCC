//
//  WexBaseNetExceptionStatus.h
//  WeXFin
//
//  Created by Mark on 16/5/23.
//  Copyright © 2016年 SinaPay. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface WexBaseNetExceptionStatus : NSObject

+ (WexBaseNetExceptionStatus*)getInstance;

// 压入adapter
- (void)pushAdapter:(NSObject*)adapter;
// 弹出adapter
- (void)popAdapter:(NSObject*)adapter;
// adapter缓存是否存在
- (BOOL)isAdapterExist:(NSObject*)adapter;
// 清空adapter缓存
- (void)clearAdapterArray;

// 是否处于异常状态
- (BOOL)isInExceptionStatus;
- (void)enterExceptionStatus;
- (void)leaveExceptionStatus;

@end
