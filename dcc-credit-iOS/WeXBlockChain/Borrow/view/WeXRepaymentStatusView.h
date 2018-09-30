//
//  WeXRepaymentStatusView.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/8.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger,WeXRepaymentStatus) {
    WeXRepaymentStatusRunning = 0, //还币中
    WeXRepaymentStatusFail    = 1, //还币失败
};

@interface WeXRepaymentStatusView : UIView

+ (instancetype)createRepaymentStatusView:(CGRect)frame
                                   status:(WeXRepaymentStatus)status;


@end
