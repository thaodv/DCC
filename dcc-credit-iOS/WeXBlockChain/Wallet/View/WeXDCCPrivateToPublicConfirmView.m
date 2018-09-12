//
//  WeXDCCPrivateToPublicConfirmView.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDCCPrivateToPublicConfirmView.h"

@implementation WeXDCCPrivateToPublicConfirmView
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
    toLabel.text = @"To DCC@Ethereum";
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
    _toLabel = toLabel;
    
    
    //    订单金额
    UILabel *valueTitleLabel = [[UILabel alloc] init];
    valueTitleLabel.text = @"转移数量:";
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
    valueLabel.font = [UIFont systemFontOfSize:14];
    valueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    valueLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:valueLabel];
    [valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    _valueLabel =valueLabel;
    
    //  持有量
    UILabel *feeTitleLabel = [[UILabel alloc] init];
    feeTitleLabel.text = @"手  续  费:";
    feeTitleLabel.font = [UIFont systemFontOfSize:14];
    feeTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    feeTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:feeTitleLabel];
    [feeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(toTitleLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *feeLabel = [[UILabel alloc] init];
    feeLabel.text = @"--";
    feeLabel.font = [UIFont systemFontOfSize:14];
    feeLabel.textColor = COLOR_LABEL_DESCRIPTION;
    feeLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:feeLabel];
    [feeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(feeTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    _feeLabel = feeLabel;
    
    UILabel *receiveTitleLabel = [[UILabel alloc] init];
    receiveTitleLabel.text = @"到账数量:";
    receiveTitleLabel.font = [UIFont systemFontOfSize:14];
    receiveTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    receiveTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:receiveTitleLabel];
    [receiveTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(feeTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(toTitleLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *receiveLabel = [[UILabel alloc] init];
    receiveLabel.text = @"--";
    receiveLabel.font = [UIFont systemFontOfSize:14];
    receiveLabel.textColor = COLOR_LABEL_DESCRIPTION;
    receiveLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:receiveLabel];
    [receiveLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(receiveTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    _receiveLabel = receiveLabel;
    
    //  持有量
    UILabel *balanceTitleLabel = [[UILabel alloc] init];
    balanceTitleLabel.text = @"持  有  量:";
    balanceTitleLabel.font = [UIFont systemFontOfSize:14];
    balanceTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    balanceTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:balanceTitleLabel];
    [balanceTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(receiveTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(toTitleLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *balanceLabel = [[UILabel alloc] init];
    balanceLabel.text = @"--";
    balanceLabel.font = [UIFont systemFontOfSize:14];
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
