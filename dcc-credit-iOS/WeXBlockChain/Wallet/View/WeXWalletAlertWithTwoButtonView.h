//
//  WeXWalletAlertWithTwoButtonView.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^ConfirmButtonBlock)(void);

@interface WeXWalletAlertWithTwoButtonView : UIView

@property (nonatomic,strong)UILabel *contentLabel;
@property (nonatomic,copy)ConfirmButtonBlock confirmButtonBlock;
@end
