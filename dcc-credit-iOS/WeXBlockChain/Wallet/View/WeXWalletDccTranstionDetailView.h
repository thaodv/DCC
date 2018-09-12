//
//  WeXWalletDccTranstionDetailView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger,WeXWalletTranstionViewType) {
    WeXWalletTranstionViewTypeDefault = 0, //默认支付详情
    WeXWalletTranstionViewTypeCPBuy = 1,   //币生息支付详情
};




typedef void(^CancelBtnBlock)(void);
typedef void(^ConfirmBtnBlock)(void);

@interface WeXWalletDccTranstionDetailView : UIView

@property (nonatomic,strong)UILabel *toLabel;
@property (nonatomic,strong)UILabel *fromLabel;
@property (nonatomic,strong)UILabel *valueLabel;
@property (nonatomic,strong)UILabel *costLabel;
@property (nonatomic,strong)UILabel *balanceLabel;
@property (nonatomic,strong)UILabel *balanceEthLabel;
@property (nonatomic,strong)UILabel *receiveLabel;
@property (nonatomic,strong)UILabel *feeLabel;

@property (nonatomic,strong)WeXCustomButton *confirmBtn;

@property (nonatomic,copy)CancelBtnBlock cancelBtnBlock;
@property (nonatomic,copy)ConfirmBtnBlock confirmBtnBlock;

- (void)setTranstionViewType:(WeXWalletTranstionViewType)type;

- (void)dismiss;

@end

