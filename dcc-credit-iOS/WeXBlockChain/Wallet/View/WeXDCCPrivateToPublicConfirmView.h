//
//  WeXDCCPrivateToPublicConfirmView.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^CancelBtnBlock)(void);
typedef void(^ConfirmBtnBlock)(void);

@interface WeXDCCPrivateToPublicConfirmView : UIView
@property (nonatomic,strong)UILabel *toLabel;
@property (nonatomic,strong)UILabel *valueLabel;
@property (nonatomic,strong)UILabel *balanceLabel;
@property (nonatomic,strong)UILabel *receiveLabel;
@property (nonatomic,strong)UILabel *feeLabel;

@property (nonatomic,strong)WeXCustomButton *confirmBtn;

@property (nonatomic,copy)CancelBtnBlock cancelBtnBlock;
@property (nonatomic,copy)ConfirmBtnBlock confirmBtnBlock;

- (void)dismiss;
@end
