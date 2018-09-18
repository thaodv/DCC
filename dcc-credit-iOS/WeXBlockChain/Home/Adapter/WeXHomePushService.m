//
//  WeXHomePushService.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXHomePushService.h"

@implementation WeXHomePushService

+ (void)pushFromVC:(UIViewController *)from
              toVC:(UIViewController *)to {
    __block BOOL isPush = NO;
    if (!isPush) {
        dispatch_async(dispatch_get_main_queue(), ^{
            to.hidesBottomBarWhenPushed = YES;
            [from.navigationController pushViewController:to animated:YES];
        });
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.4 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        isPush = YES;
    });
}

@end
