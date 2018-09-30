//
//  WeXBorrowConfirmToastView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol WeXBorrowConfirmToastViewDelegate<NSObject>

@optional

- (void)borrowConfirmToastViewDidClickConfirmButtoon;

@end


@interface WeXBorrowConfirmToastView : UIView

@property (nonatomic,strong)UILabel *borrowBalanceLabel;
@property (nonatomic,strong)UILabel *periodLabel;
@property (nonatomic,strong)UILabel *addressLabel;
@property (nonatomic,strong)UILabel *payTotalLabel;
@property (nonatomic,strong)UILabel *payCapitalLabel;
@property (nonatomic,strong)UILabel *payInterestLabel;
@property (nonatomic,strong)UILabel *feeLabel;
@property (nonatomic,strong)UILabel *balanceLabel;

@property (nonatomic,weak)id<WeXBorrowConfirmToastViewDelegate> delegate;

- (void)dismiss;
@end
