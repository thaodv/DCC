//
//  WeXBorrowCreatedStatusToastView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>


@protocol WeXBorrowCreatedStatusToastViewDelegate<NSObject>

@optional

- (void)borrowCreatedStatusToastViewDidClickCancelOrderButtoon;

@end


@interface WeXBorrowCreatedStatusToastView : UIView

@property (nonatomic,strong)UILabel *borrowBalanceLabel;
@property (nonatomic,strong)UILabel *periodLabel;
@property (nonatomic,strong)UILabel *payTotalLabel;
@property (nonatomic,strong)UILabel *payCapitalLabel;
@property (nonatomic,strong)UILabel *payInterestLabel;
@property (nonatomic,strong)UILabel *feeLabel;
@property (nonatomic,strong)UILabel *balanceLabel;

@property (nonatomic,weak)id<WeXBorrowCreatedStatusToastViewDelegate> delegate;

- (void)dismiss;
@end
