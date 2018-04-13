//
//  WeXPasswordCacheModal.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPasswordCacheModal.h"

@implementation WeXPasswordCacheModal

+ (instancetype)sharedInstance{
    static dispatch_once_t onceToken;
    static WeXPasswordCacheModal *sharedInstance = nil;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

@end
