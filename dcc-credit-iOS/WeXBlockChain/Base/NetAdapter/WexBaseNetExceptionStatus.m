//
//  WexBaseNetExceptionStatus.m
//  WeXFin
//
//  Created by Mark on 16/5/23.
//  Copyright © 2016年 SinaPay. All rights reserved.
//

#import "WexBaseNetExceptionStatus.h"

@interface WexBaseNetExceptionStatus ()

@property (nonatomic, strong) NSMutableArray* adapterArray;
@property (nonatomic) BOOL bGlobalException;

@end

@implementation WexBaseNetExceptionStatus

+ (WexBaseNetExceptionStatus*)getInstance
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
        _adapterArray = [NSMutableArray array];
        _bGlobalException = NO;
    }
    return self;
}

// 压入adapter
- (void)pushAdapter:(NSObject*)adapter
{
    [_adapterArray addObject:adapter];
}

// 弹出adapter
- (void)popAdapter:(NSObject*)adapter
{
    [_adapterArray removeObject:adapter];
}

// adapter缓存是否存在
- (BOOL)isAdapterExist:(NSObject*)adapter
{
    if ([_adapterArray indexOfObject:adapter] == NSNotFound)
    {
        return NO;
    }
    else
    {
        return YES;
    }
}

// 清空adapter缓存
- (void)clearAdapterArray
{
    [_adapterArray removeAllObjects];
}

- (BOOL)isInExceptionStatus
{
    return _bGlobalException;
}

- (void)enterExceptionStatus
{
    _bGlobalException = YES;
}

- (void)leaveExceptionStatus
{
    _bGlobalException = NO;
}

@end
