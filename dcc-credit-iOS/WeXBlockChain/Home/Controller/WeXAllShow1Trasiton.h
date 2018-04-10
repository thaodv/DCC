//
//  WeXAllShow1Trasiton.h
//  WeXBlockChain
//
//  Created by wcc on 2018/3/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 *  动画过渡代理管理的是push还是pop
 */
typedef NS_ENUM(NSUInteger, WeXPassportCardTrasitonType) {
    WeXPassportCardTrasitonPush = 0,
    WeXPassportCardTrasitonPop
};


@interface WeXAllShow1Trasiton : NSObject<UIViewControllerAnimatedTransitioning>

/**
 *  初始化动画过渡代理
 */
+ (instancetype)transitionWithType:(WeXPassportCardTrasitonType)type;
- (instancetype)initWithTransitionType:(WeXPassportCardTrasitonType)type;

@end

