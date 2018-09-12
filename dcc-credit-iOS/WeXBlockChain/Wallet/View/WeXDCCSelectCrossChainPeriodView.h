//
//  WeXDCCSelectCrossChainPeriodView.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^ConfirmButtonBlock)(NSString *startTime,NSString *endTime,NSString *title);
@interface WeXDCCSelectCrossChainPeriodView : UIView<UIGestureRecognizerDelegate>
@property (nonatomic,copy)ConfirmButtonBlock confirmButtonBlock;
@end
