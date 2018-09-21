//
//  WeXBorrowConfirmCell.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowConfirmCell.h"

#define BUTTON_NORMAL_COLOR ColorWithRGB(135, 136, 137)

@implementation WeXBorrowConfirmCell

@end

@implementation WeXBorrowConfirmMoneyCell

-(void)awakeFromNib
{
    [super awakeFromNib];
    
    self.titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    self.titleLabel.font = [UIFont systemFontOfSize:17];
    
    [self.moneyFirstButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.moneyFirstButton setBackgroundImage:[WexCommonFunc imageWithColor:BUTTON_NORMAL_COLOR] forState:UIControlStateNormal];
    [self.moneyFirstButton setBackgroundImage:[WexCommonFunc imageWithColor:COLOR_THEME_ALL] forState:UIControlStateSelected];
    
    [self.moneySecondButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.moneySecondButton setBackgroundImage:[WexCommonFunc imageWithColor:BUTTON_NORMAL_COLOR] forState:UIControlStateNormal];
    [self.moneySecondButton setBackgroundImage:[WexCommonFunc imageWithColor:COLOR_THEME_ALL] forState:UIControlStateSelected];
    
    [self.moneyThirdButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.moneyThirdButton setBackgroundImage:[WexCommonFunc imageWithColor:BUTTON_NORMAL_COLOR] forState:UIControlStateNormal];
    [self.moneyThirdButton setBackgroundImage:[WexCommonFunc imageWithColor:COLOR_THEME_ALL] forState:UIControlStateSelected];
    
    [self.otherMoneyButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.otherMoneyButton setBackgroundImage:[WexCommonFunc imageWithColor:BUTTON_NORMAL_COLOR] forState:UIControlStateNormal];
    [self.otherMoneyButton setBackgroundImage:[WexCommonFunc imageWithColor:COLOR_THEME_ALL] forState:UIControlStateSelected];
    [self.otherMoneyButton setTitle:WeXLocalizedString(@"其他金额") forState:UIControlStateNormal];
    
    self.otherMoneyTextField.keyboardType = UIKeyboardTypeASCIICapableNumberPad;
}

@end

@implementation WeXBorrowConfirmTimeCell

-(void)awakeFromNib
{
    [super awakeFromNib];
    
    self.titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    self.titleLabel.font = [UIFont systemFontOfSize:17];
    
    [self.periodFirstButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.periodFirstButton setBackgroundImage:[WexCommonFunc imageWithColor:BUTTON_NORMAL_COLOR] forState:UIControlStateNormal];
    [self.periodFirstButton setBackgroundImage:[WexCommonFunc imageWithColor:COLOR_THEME_ALL] forState:UIControlStateSelected];
    
    [self.periodSecondButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.periodSecondButton setBackgroundImage:[WexCommonFunc imageWithColor:BUTTON_NORMAL_COLOR] forState:UIControlStateNormal];
    [self.periodSecondButton setBackgroundImage:[WexCommonFunc imageWithColor:COLOR_THEME_ALL] forState:UIControlStateSelected];
    
    [self.periodThirdButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.periodThirdButton setBackgroundImage:[WexCommonFunc imageWithColor:BUTTON_NORMAL_COLOR] forState:UIControlStateNormal];
    [self.periodThirdButton setBackgroundImage:[WexCommonFunc imageWithColor:COLOR_THEME_ALL] forState:UIControlStateSelected];
    
}


@end

@implementation WeXBorrowConfirmFeeCell



@end

@implementation WeXBorrowConfirmAddressCell

-(void)awakeFromNib
{
    [super awakeFromNib];
    
    self.titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    self.titleLabel.font = [UIFont systemFontOfSize:17];
    
}

@end

@implementation WeXBorrowConfirmConditionCell

-(void)awakeFromNib
{
    [super awakeFromNib];
    
    self.titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    self.titleLabel.font = [UIFont systemFontOfSize:17];
    
}

@end
