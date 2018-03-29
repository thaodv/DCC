//
//  WexBaseNetCache.h
//  WeXFin
//
//  Created by Mark on 15/8/18.
//  Copyright (c) 2015年 SinaPay. All rights reserved.
//

#import <Foundation/Foundation.h>

// 统一的接口缓存
@interface WexBaseNetCache : NSObject

@property (nonatomic, strong) NSString* sv;
@property (nonatomic, strong) NSString* sk;
@property (nonatomic, strong) NSString* token;

+ (WexBaseNetCache*)getInstance;
- (void)saveCacheObject:(NSDictionary*)head;

@end
