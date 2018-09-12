//
//  WeXDCCPublicToPrivateConfirmView.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDCCPublicToPrivateConfirmView.h"

@implementation WeXDCCPublicToPrivateConfirmView
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        [self setupSubViews];
    }
    return self;
    
}

- (void)commonInit{
    
    
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
    contentView.backgroundColor = [UIColor whiteColor];
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(0);
        make.trailing.equalTo(self).offset(0);
        make.bottom.equalTo(self);
        make.height.equalTo(@300);
    }];
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [cancelBtn setImage:[UIImage imageNamed:@"dcc_cha"] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [contentView addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.leading.equalTo(contentView).offset(0);
        make.size.mas_equalTo(CGSizeMake(45, 45));
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"交易确认";
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(0);
        make.centerX.equalTo(contentView);
        make.height.equalTo(@45);
    }];
    
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor lightGrayColor];
    line1.alpha = LINE_VIEW_ALPHA;
    [contentView addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.top.equalTo(titleLabel.mas_bottom).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *toTitleLabel = [[UILabel alloc] init];
    toTitleLabel.text = @"交易方向:";
    toTitleLabel.font = [UIFont systemFontOfSize:14];
    toTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    toTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:toTitleLabel];
    [toTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    
    UILabel *toLabel = [[UILabel alloc] init];
    toLabel.text = @"To DCC@Distributed Credit Chain";
    toLabel.font = [UIFont systemFontOfSize:14];
    toLabel.textColor = COLOR_LABEL_DESCRIPTION;
    toLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:toLabel];
    [toLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
  
    UILabel *valueTitleLabel = [[UILabel alloc] init];
    valueTitleLabel.text = @"订单金额:";
    valueTitleLabel.font = [UIFont systemFontOfSize:14];
    valueTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    valueTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:valueTitleLabel];
    [valueTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(toTitleLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *valueLabel = [[UILabel alloc] init];
    valueLabel.text = @"--";
    valueLabel.font = [UIFont systemFontOfSize:12];
    valueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    valueLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:valueLabel];
    [valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    _valueLabel = valueLabel;
    
    UILabel *coastTitleLabel = [[UILabel alloc] init];
    coastTitleLabel.text = @"最高矿工费:";
    coastTitleLabel.font = [UIFont systemFontOfSize:14];
    coastTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    coastTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:coastTitleLabel];
    [coastTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(toTitleLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *coastLabel = [[UILabel alloc] init];
    coastLabel.text = @"--";
    coastLabel.font = [UIFont systemFontOfSize:12];
    coastLabel.textColor = COLOR_LABEL_DESCRIPTION;
    coastLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:coastLabel];
    [coastLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(coastTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    _coastLabel = coastLabel;
    
    UILabel *balanceTitleLabel = [[UILabel alloc] init];
    balanceTitleLabel.text = @"持有量:";
    balanceTitleLabel.font = [UIFont systemFontOfSize:14];
    balanceTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    balanceTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:balanceTitleLabel];
    [balanceTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(coastTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(toTitleLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *balanceLabel = [[UILabel alloc] init];
    balanceLabel.text = @"--";
    balanceLabel.font = [UIFont systemFontOfSize:12];
    balanceLabel.textColor = COLOR_LABEL_DESCRIPTION;
    balanceLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:balanceLabel];
    [balanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(balanceTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    _balanceLabel = balanceLabel;
    
    UILabel *ethBalanceLabel = [[UILabel alloc] init];
    ethBalanceLabel.text = @"--";
    ethBalanceLabel.font = [UIFont systemFontOfSize:12];
    ethBalanceLabel.textColor = COLOR_LABEL_DESCRIPTION;
    ethBalanceLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:ethBalanceLabel];
    [ethBalanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(balanceLabel.mas_bottom);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    _ethBalanceLabel =ethBalanceLabel;
    
    
    
    WeXCustomButton *confirmBtn = [WeXCustomButton button];
    [confirmBtn setTitle:@"确认" forState:UIControlStateNormal];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    confirmBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(contentView).offset(-20);
        make.height.equalTo(@50);
        make.leading.equalTo(contentView).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
    }];
    _confirmBtn = confirmBtn;
    
    
}

- (void)confirmBtnClick{
    if (self.confirmBtnBlock) {
        self.confirmBtnBlock();
    }
}

- (void)dismiss{
    
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    [self removeFromSuperview];
    
}

- (void)cancelBtnClick{
    [self dismiss];
}




@end
