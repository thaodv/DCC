//
//  WeXWalletTranstionDetailView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^CancelBtnBlock)(void);
typedef void(^ConfirmBtnBlock)(void);

@interface WeXWalletTranstionDetailView : UIView

@property (nonatomic,strong)UILabel *toLabel;
@property (nonatomic,strong)UILabel *fromLabel;
@property (nonatomic,strong)UILabel *valueLabel;
@property (nonatomic,strong)UILabel *costLabel;
@property (nonatomic,strong)UILabel *balanceLabel;
@property (nonatomic,strong)UILabel *balanceEthLabel;


@property (nonatomic,copy)CancelBtnBlock cancelBtnBlock;
@property (nonatomic,copy)ConfirmBtnBlock confirmBtnBlock;

- (void)dismiss;

@end
