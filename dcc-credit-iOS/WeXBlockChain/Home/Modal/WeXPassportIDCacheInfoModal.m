//
//  WeXPassportIDCacheInfoModal.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDCacheInfoModal.h"

@implementation WeXPassportIDCacheInfoModal

+ (instancetype)sharedInstance{
    static dispatch_once_t onceToken;
    static WeXPassportIDCacheInfoModal *sharedInstance = nil;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

@end
