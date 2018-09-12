//
//  WeXDCCPublicToPrivateConfirmView.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef void(^ConfirmBtnBlock)(void);

@interface WeXDCCPublicToPrivateConfirmView : UIView

@property (nonatomic,strong)UILabel *valueLabel;
@property (nonatomic,strong)UILabel *balanceLabel;
@property (nonatomic,strong)UILabel *ethBalanceLabel;
@property (nonatomic,strong)UILabel *coastLabel;

@property (nonatomic,strong)WeXCustomButton *confirmBtn;

@property (nonatomic,copy)ConfirmBtnBlock confirmBtnBlock;

- (void)dismiss;

@end
