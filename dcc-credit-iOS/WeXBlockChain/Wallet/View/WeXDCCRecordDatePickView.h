//
//  WeXDCCRecordDatePickView.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^DatePickConfirmButtonBlock)(NSString *year,NSString *month,NSString*day);

@interface WeXDCCRecordDatePickView : UIView

@property (nonatomic,strong)NSMutableArray *yearArray;
@property (nonatomic,strong)NSMutableArray *monthArray;
@property (nonatomic,strong)NSMutableArray *dayArray;

@property (nonatomic,copy)DatePickConfirmButtonBlock confirmButtonBlock;
@end
