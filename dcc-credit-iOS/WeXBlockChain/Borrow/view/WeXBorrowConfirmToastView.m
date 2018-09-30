//
//  WeXBorrowConfirmToastView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowConfirmToastView.h"

@implementation WeXBorrowConfirmToastView

-(instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        [self setupSubViews];
    }
    return self;
    
}

- (void)commonInit{
    self.backgroundColor = [UIColor clearColor];
    
}


- (void)setupSubViews{
    
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = COLOR_ALPHA_VIEW_COVER;
    [self addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(self);
        make.bottom.equalTo(self);
    }];
    
    UIView *contentView = [[UIView alloc] init];
    contentView.backgroundColor = COLOR_BACKGROUND_ALL;
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.bottom.equalTo(self);
        make.height.equalTo(@420);
    }];
    
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [cancelBtn setImage:[UIImage imageNamed:@"dcc_cha"] forState:UIControlStateNormal];
    [cancelBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:16];
    [contentView addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(5);
        make.leading.equalTo(contentView).offset(5);
        make.size.mas_equalTo(CGSizeMake(40, 40));
    }];
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = COLOR_ALPHA_LINE;
    [contentView addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(0);
        make.trailing.equalTo(contentView).offset(0);
        make.top.equalTo(cancelBtn.mas_bottom).offset(5);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *borrowBalanceTitleLabel = [[UILabel alloc] init];
    borrowBalanceTitleLabel.text = WeXLocalizedString(@"借币金额:");
    borrowBalanceTitleLabel.textAlignment = NSTextAlignmentCenter;
    borrowBalanceTitleLabel.font = [UIFont systemFontOfSize:18];
    borrowBalanceTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [contentView addSubview:borrowBalanceTitleLabel];
    [borrowBalanceTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(15);
        make.top.equalTo(line1.mas_bottom).offset(15);
        make.height.equalTo(@20);
    }];
    
    UILabel *borrowBalanceLabel = [[UILabel alloc] init];
    borrowBalanceLabel.textAlignment = NSTextAlignmentCenter;
    borrowBalanceLabel.font = [UIFont systemFontOfSize:18];
    borrowBalanceLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [contentView addSubview:borrowBalanceLabel];
    [borrowBalanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(borrowBalanceTitleLabel).offset(0);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@20);
    }];
    _borrowBalanceLabel = borrowBalanceLabel;
    
    UILabel *periodTitleLabel = [[UILabel alloc] init];
    periodTitleLabel.text = WeXLocalizedString(@"借币周期:");
    periodTitleLabel.textAlignment = NSTextAlignmentCenter;
    periodTitleLabel.font = [UIFont systemFontOfSize:18];
    periodTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [contentView addSubview:periodTitleLabel];
    [periodTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(15);
        make.top.equalTo(borrowBalanceLabel.mas_bottom).offset(15);
        make.height.equalTo(@20);
    }];
    
    
    UILabel *periodLabel = [[UILabel alloc] init];
    periodLabel.textAlignment = NSTextAlignmentCenter;
    periodLabel.font = [UIFont systemFontOfSize:18];
    periodLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [contentView addSubview:periodLabel];
    [periodLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(periodTitleLabel).offset(0);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@20);
    }];
    _periodLabel = periodLabel;
    
    UILabel *addressTitleLabel = [[UILabel alloc] init];
    addressTitleLabel.text = WeXLocalizedString(@"收币地址:");
    addressTitleLabel.textAlignment = NSTextAlignmentCenter;
    addressTitleLabel.font = [UIFont systemFontOfSize:18];
    addressTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [contentView addSubview:addressTitleLabel];
    [addressTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(15);
        make.top.equalTo(periodTitleLabel.mas_bottom).offset(15);
        make.height.equalTo(@20);
    }];
    
    UILabel *addressLabel = [[UILabel alloc] init];
    addressLabel.textAlignment = NSTextAlignmentCenter;
    addressLabel.font = [UIFont systemFontOfSize:18];
    addressLabel.textColor = COLOR_LABEL_DESCRIPTION;
    addressLabel.adjustsFontSizeToFitWidth = YES;
    [contentView addSubview:addressLabel];
    [addressLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(addressTitleLabel).offset(0);
        make.leading.equalTo(addressTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@20);
    }];
    _addressLabel = addressLabel;
    
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = COLOR_ALPHA_LINE;
    [contentView addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
        make.top.equalTo(addressTitleLabel.mas_bottom).offset(15);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *payTotalTitleLabel = [[UILabel alloc] init];
    payTotalTitleLabel.text = WeXLocalizedString(@"应还总额:");
    payTotalTitleLabel.textAlignment = NSTextAlignmentCenter;
    payTotalTitleLabel.font = [UIFont systemFontOfSize:18];
    payTotalTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [contentView addSubview:payTotalTitleLabel];
    [payTotalTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.mas_centerX).offset(-70);
        make.top.equalTo(line2.mas_bottom).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *payTotalLabel = [[UILabel alloc] init];
    payTotalLabel.textAlignment = NSTextAlignmentCenter;
    payTotalLabel.font = [UIFont systemFontOfSize:18];
    payTotalLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [contentView addSubview:payTotalLabel];
    [payTotalLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payTotalTitleLabel).offset(0);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@20);
    }];
    _payTotalLabel = payTotalLabel;
    
    
    UILabel *payCapitalTitleLabel = [[UILabel alloc] init];
    payCapitalTitleLabel.text = WeXLocalizedString(@"应还本金:");
    payCapitalTitleLabel.textAlignment = NSTextAlignmentCenter;
    payCapitalTitleLabel.font = [UIFont systemFontOfSize:16];
    payCapitalTitleLabel.textColor = COLOR_LABEL_WEAK;
    [contentView addSubview:payCapitalTitleLabel];
    [payCapitalTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(payTotalTitleLabel).offset(0);
        make.top.equalTo(payTotalLabel.mas_bottom).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *payCapitalLabel = [[UILabel alloc] init];
    payCapitalLabel.textAlignment = NSTextAlignmentCenter;
    payCapitalLabel.font = [UIFont systemFontOfSize:16];
    payCapitalLabel.textColor = COLOR_LABEL_WEAK;
    [contentView addSubview:payCapitalLabel];
    [payCapitalLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payCapitalTitleLabel).offset(0);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@20);
    }];
    _payCapitalLabel = payCapitalLabel;
    
    UILabel *payInterestTitleLabel = [[UILabel alloc] init];
    payInterestTitleLabel.text = WeXLocalizedString(@"应还利息:");
    payInterestTitleLabel.textAlignment = NSTextAlignmentCenter;
    payInterestTitleLabel.font = [UIFont systemFontOfSize:16];
    payInterestTitleLabel.textColor = COLOR_LABEL_WEAK;
    [contentView addSubview:payInterestTitleLabel];
    [payInterestTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(payTotalTitleLabel).offset(0);
        make.top.equalTo(payCapitalLabel.mas_bottom).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *payInterestLabel = [[UILabel alloc] init];
    payInterestLabel.textAlignment = NSTextAlignmentCenter;
    payInterestLabel.font = [UIFont systemFontOfSize:16];
    payInterestLabel.textColor = COLOR_LABEL_WEAK;
    [contentView addSubview:payInterestLabel];
    [payInterestLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payInterestTitleLabel).offset(0);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@20);
    }];
    _payInterestLabel = payInterestLabel;
    
    UILabel *feeTitleLabel = [[UILabel alloc] init];
    feeTitleLabel.text = WeXLocalizedString(@"借币申请手续费:");
    feeTitleLabel.textAlignment = NSTextAlignmentCenter;
    feeTitleLabel.font = [UIFont systemFontOfSize:16];
    feeTitleLabel.textColor = COLOR_LABEL_WEAK;
    [contentView addSubview:feeTitleLabel];
    [feeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(payTotalTitleLabel).offset(0);
        make.top.equalTo(payInterestLabel.mas_bottom).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *feeLabel = [[UILabel alloc] init];
    feeLabel.textAlignment = NSTextAlignmentCenter;
    feeLabel.font = [UIFont systemFontOfSize:16];
    feeLabel.textColor = COLOR_LABEL_WEAK;
    [contentView addSubview:feeLabel];
    [feeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(feeTitleLabel).offset(0);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@20);
    }];
    _feeLabel = feeLabel;
    
    UILabel *balanceTitleLabel = [[UILabel alloc] init];
    balanceTitleLabel.text = WeXLocalizedString(@"持有量:");
    balanceTitleLabel.textAlignment = NSTextAlignmentCenter;
    balanceTitleLabel.font = [UIFont systemFontOfSize:16];
    balanceTitleLabel.textColor = COLOR_LABEL_WEAK;
    [contentView addSubview:balanceTitleLabel];
    [balanceTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(payTotalTitleLabel).offset(0);
        make.top.equalTo(feeLabel.mas_bottom).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *balanceLabel = [[UILabel alloc] init];
    balanceLabel.textAlignment = NSTextAlignmentCenter;
    balanceLabel.font = [UIFont systemFontOfSize:16];
    balanceLabel.textColor = COLOR_LABEL_WEAK;
    [contentView addSubview:balanceLabel];
    [balanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(balanceTitleLabel).offset(0);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@20);
    }];
    _balanceLabel = balanceLabel;
    
    
    UIButton *confirmBtn = [WeXCustomButton button];
    [confirmBtn setTitle:WeXLocalizedString(@"确认") forState:UIControlStateNormal];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    confirmBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@40);
        make.bottom.equalTo(contentView).offset(-20);
    }];
    
}

- (void)confirmBtnClick
{
    if ([self.delegate respondsToSelector:@selector(borrowConfirmToastViewDidClickConfirmButtoon)]) {
        [self.delegate borrowConfirmToastViewDidClickConfirmButtoon];
    }
    
}


- (void)cancelBtnClick{
    [self dismiss];
}

- (void)dismiss
{
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    
    [self removeFromSuperview];
}

-(void)dealloc
{
    NSLog(@"%s",__func__);
}


@end
