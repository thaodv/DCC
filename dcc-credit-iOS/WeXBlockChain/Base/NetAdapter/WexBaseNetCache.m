//
//  WexBaseNetCache.m
//  WeXFin
//
//  Created by Mark on 15/8/18.
//  Copyright (c) 2015年 SinaPay. All rights reserved.
//

#import "WexBaseNetCache.h"
//#import "WexCommonFunc.h"

__strong static id _sharedWexBaseNetCacheObject = nil;

@interface WexBaseNetCache ()

@end

@implementation WexBaseNetCache

+ (WexBaseNetCache*)getInstance
{
    static dispatch_once_t predWexBaseNetCache = 0;

    dispatch_once(&predWexBaseNetCache, ^
                  {
                      _sharedWexBaseNetCacheObject = [[self alloc] init];
                      [_sharedWexBaseNetCacheObject commonInit];
                  });
    return _sharedWexBaseNetCacheObject;
}

- (void)commonInit
{
    _sv = @"";
    _sk = @"";
    _token = @"";
}

- (void)saveCacheObject:(NSDictionary*)head
{
//    NSString* token = [WexCommonFunc JsonStringFromDict:head andKey:@"token"];
//    
//    if ([token isEqualToString:@""] == NO)
//    {
//        _token = token;
//    }
//    
//    NSString* sv = [WexCommonFunc JsonStringFromDict:head andKey:@"sv"];
//    
//    if ([sv isEqualToString:@""] == NO)
//    {
//        _sv = sv;
//    }
//    
//    NSString* sk = [WexCommonFunc JsonStringFromDict:head andKey:@"sk"];
//    
//    if ([sk isEqualToString:@""] == NO)
//    {
//        _sk = sk;
//    }
}

@end
