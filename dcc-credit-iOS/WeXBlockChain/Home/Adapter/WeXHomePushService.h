//
//  WeXHomePushService.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface WeXHomePushService : NSObject

+ (void)pushFromVC:(UIViewController *)from
              toVC:(UIViewController *)to;

@end