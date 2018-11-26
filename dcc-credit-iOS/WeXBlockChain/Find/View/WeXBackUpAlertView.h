//
//  WeXBackUpAlertView.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/12.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseView.h"

typedef NS_ENUM(NSInteger,WeXBackUpAlertEventType) {
    WeXBackUpAlertEventBackUp = 0, //去备份钱包
    WeXBackUpAlertEventReward = 1, //领取奖励
};

typedef NS_ENUM(NSInteger,WeXBackUpAlertViewType) {
    WeXBackUpAlertViewBackUp = 0, //备份钱包样式
};


typedef void (^WeXBackUpEventBlock)(WeXBackUpAlertEventType type);

@interface WeXBackUpAlertView : WeXBaseView

+ (instancetype)createAlertInView:(UIView *)superView
                    alertViewType:(WeXBackUpAlertViewType)type
                            event:(WeXBackUpEventBlock)block;

- (void)dismiss;

- (void)show;

@end


