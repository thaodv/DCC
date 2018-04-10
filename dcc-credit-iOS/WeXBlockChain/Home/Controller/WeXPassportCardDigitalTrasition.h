//
//  WeXPassportCardDigitalTrasition.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
 *  动画过渡代理管理的是push还是pop
 */
typedef NS_ENUM(NSUInteger, WeXPassportCardTrasitonType) {
    WeXPassportCardTrasitonPush = 0,
    WeXPassportCardTrasitonPop
};


@interface WeXPassportCardDigitalTrasition : NSObject<UIViewControllerAnimatedTransitioning>

/**
 *  初始化动画过渡代理
 */
+ (instancetype)transitionWithType:(WeXPassportCardTrasitonType)type;
- (instancetype)initWithTransitionType:(WeXPassportCardTrasitonType)type;

@end
