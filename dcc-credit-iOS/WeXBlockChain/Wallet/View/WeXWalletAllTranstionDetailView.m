//
//  WeXWalletAllTranstionDetailView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletAllTranstionDetailView.h"

@implementation WeXWalletAllTranstionDetailView

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
    
    
}

- (void)setupSubViews{
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [cancelBtn setImage:[UIImage imageNamed:@"dcc_cha"] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.leading.equalTo(self).offset(0);
        make.size.mas_equalTo(CGSizeMake(45, 45));
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"支付详情";
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self).offset(10);
        make.centerX.equalTo(self);
        make.height.equalTo(@20);
    }];
   
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor lightGrayColor];
    line1.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.top.equalTo(titleLabel.mas_bottom).offset(20);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *toTitleLabel = [[UILabel alloc] init];
    toTitleLabel.text = @"接收地址:";
    toTitleLabel.font = [UIFont systemFontOfSize:14];
    toTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    toTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:toTitleLabel];
    [toTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(10);
        make.leading.equalTo(self).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    
    UILabel *toLabel = [[UILabel alloc] init];
    //    toLabel.text = @"99";
    toLabel.font = [UIFont systemFontOfSize:14];
    toLabel.textColor = COLOR_LABEL_DESCRIPTION;
    toLabel.textAlignment = NSTextAlignmentLeft;
    toLabel.numberOfLines = 2;
    [self addSubview:toLabel];
    [toLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@40);
    }];
    _toLabel = toLabel;
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = [UIColor lightGrayColor];
    line2.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.top.equalTo(toLabel.mas_bottom).offset(10);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *fromTitleLabel = [[UILabel alloc] init];
    fromTitleLabel.text = @"付款地址:";
    fromTitleLabel.font = [UIFont systemFontOfSize:14];
    fromTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    fromTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:fromTitleLabel];
    [fromTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom).offset(10);
        make.leading.equalTo(self).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel);
    }];
    
    UILabel *fromLabel = [[UILabel alloc] init];
    //    fromLabel.text = self.recordModel.from;;
    fromLabel.font = [UIFont systemFontOfSize:14];
    fromLabel.textColor = COLOR_LABEL_DESCRIPTION;
    fromLabel.textAlignment = NSTextAlignmentLeft;
    fromLabel.numberOfLines = 2;
    [self addSubview:fromLabel];
    [fromLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromTitleLabel);
        make.leading.equalTo(fromTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@40);
    }];
    _fromLabel = fromLabel;
    
    UIView *line3 = [[UIView alloc] init];
    line3.backgroundColor = [UIColor lightGrayColor];
    line3.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line3];
    [line3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.top.equalTo(fromLabel.mas_bottom).offset(10);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    //    订单金额
    UILabel *valueTitleLabel = [[UILabel alloc] init];
    valueTitleLabel.text = @"订单金额:";
    valueTitleLabel.font = [UIFont systemFontOfSize:12];
    valueTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    valueTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:valueTitleLabel];
    [valueTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line3.mas_bottom).offset(10);
        make.leading.equalTo(self.mas_centerX).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *valueLabel = [[UILabel alloc] init];
    valueLabel.text = @"--";
    valueLabel.font = [UIFont systemFontOfSize:12];
    valueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    valueLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:valueLabel];
    [valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueTitleLabel);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@20);
    }];
    _valueLabel =valueLabel;
    
   
    //  持有量
    UILabel *balanceTitleLabel = [[UILabel alloc] init];
    balanceTitleLabel.text = @"持有量:";
    balanceTitleLabel.font = [UIFont systemFontOfSize:12];
    balanceTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    balanceTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:balanceTitleLabel];
    [balanceTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.mas_centerX).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *balanceLabel = [[UILabel alloc] init];
    balanceLabel.text = @"--";
    balanceLabel.font = [UIFont systemFontOfSize:12];
    balanceLabel.textColor = COLOR_LABEL_DESCRIPTION;
    balanceLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:balanceLabel];
    [balanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(balanceTitleLabel);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@20);
    }];
    _balanceLabel = balanceLabel;
    
    
    UIView *line5 = [[UIView alloc] init];
    line5.backgroundColor = [UIColor lightGrayColor];
    line5.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line5];
    [line5 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(-10);
        make.trailing.equalTo(self).offset(-10);
        make.top.equalTo(balanceLabel.mas_bottom).offset(10);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    //  持有量
    UILabel *receiveTitleLabel = [[UILabel alloc] init];
    receiveTitleLabel.text = @"接收金额:";
    receiveTitleLabel.font = [UIFont systemFontOfSize:12];
    receiveTitleLabel.textColor = COLOR_THEME_ALL;
    receiveTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:receiveTitleLabel];
    [receiveTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line5.mas_bottom).offset(10);
        make.leading.equalTo(self.mas_centerX).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *receiveLabel = [[UILabel alloc] init];
    receiveLabel.text = @"--";
    receiveLabel.font = [UIFont systemFontOfSize:12];
    receiveLabel.textColor = COLOR_THEME_ALL;
    receiveLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:receiveLabel];
    [receiveLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(receiveTitleLabel);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@20);
    }];
    _receiveLabel = receiveLabel;
    
    //  持有量
    UILabel *feeTitleLabel = [[UILabel alloc] init];
    feeTitleLabel.text = @"手续费:";
    feeTitleLabel.font = [UIFont systemFontOfSize:12];
    feeTitleLabel.textColor = COLOR_THEME_ALL;
    feeTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:feeTitleLabel];
    [feeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(receiveTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.mas_centerX).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *feeLabel = [[UILabel alloc] init];
    feeLabel.text = @"--";
    feeLabel.font = [UIFont systemFontOfSize:12];
    feeLabel.textColor = COLOR_THEME_ALL;
    feeLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:feeLabel];
    [feeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(feeTitleLabel);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@20);
    }];
    _feeLabel = feeLabel;
    
    WeXCustomButton *confirmBtn = [WeXCustomButton button];
    [confirmBtn setTitle:@"确认" forState:UIControlStateNormal];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    confirmBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self).offset(-20);
        make.height.equalTo(@50);
        make.width.equalTo(@180);
        make.centerX.equalTo(self);
    }];
    _confirmBtn = confirmBtn;

    
    
    
    
    
}

- (void)confirmBtnClick{
    if (self.confirmBtnBlock) {
        self.confirmBtnBlock();
    }
}

- (void)dismiss{
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        for (UIView *view in self.subviews) {
            [view removeFromSuperview];
        }
        
        [self removeFromSuperview];
    });
    
    
}

- (void)cancelBtnClick{
    if (self.cancelBtnBlock) {
        self.cancelBtnBlock();
    }
    
}




@end


