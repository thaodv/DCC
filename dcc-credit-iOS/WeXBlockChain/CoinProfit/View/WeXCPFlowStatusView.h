//
//  WeXCPFlowStatusView.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WeXBaseView.h"

typedef NS_ENUM(NSInteger,WeXCPFlowStatusViewType) {
    WeXCPFlowStatusViewTypeBuy   = 0, // 申购日
    WeXCPFlowStatusViewTypeStart = 1, // 起息日
    WeXCPFlowStatusViewTypeEnd   = 2, // 到期日
};

@interface WeXCPFlowStatusView : WeXBaseView

- (void)setBuyTitle:(NSString *)buyTitle
             buyDay:(NSString *)buyDay
         startTitle:(NSString *)startTitle
           startDay:(NSString *)startDay
           endTitle:(NSString *)endTitle
             endDay:(NSString *)endDay;

- (void)setFlowStatusViewType:(WeXCPFlowStatusViewType)type;



@end
