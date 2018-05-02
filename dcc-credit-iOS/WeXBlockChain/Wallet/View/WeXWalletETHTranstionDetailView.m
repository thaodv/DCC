//
//  WeXWalletETHTranstionDetailView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletETHTranstionDetailView.h"

@implementation WeXWalletETHTranstionDetailView


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
    [cancelBtn setImage:[UIImage imageNamed:@"digital_cha"] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.leading.equalTo(self).offset(0);
        make.size.mas_equalTo(CGSizeMake(45, 45));
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"支付详情";
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = ColorWithLabelDescritionBlack;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self).offset(10);
        make.centerX.equalTo(self);
        make.height.equalTo(@20);
    }];
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = @"订单信息:";
    label1.font = [UIFont systemFontOfSize:14];
    label1.textColor = ColorWithLabelDescritionBlack;
    label1.textAlignment = NSTextAlignmentLeft;
    [self addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(cancelBtn.mas_bottom).offset(20);
        make.leading.equalTo(self).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *label2 = [[UILabel alloc] init];
    label2.text = @"转账";
    label2.font = [UIFont systemFontOfSize:14];
    label2.textColor = ColorWithLabelDescritionBlack;
    label2.textAlignment = NSTextAlignmentLeft;
    [self addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = ColorWithLabelDescritionBlack;
    line1.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.top.equalTo(label2.mas_bottom).offset(10);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UILabel *toTitleLabel = [[UILabel alloc] init];
    toTitleLabel.text = @"接收地址:";
    toTitleLabel.font = [UIFont systemFontOfSize:14];
    toTitleLabel.textColor = ColorWithLabelDescritionBlack;
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
    toLabel.textColor = ColorWithLabelDescritionBlack;
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
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UILabel *fromTitleLabel = [[UILabel alloc] init];
    fromTitleLabel.text = @"付款地址:";
    fromTitleLabel.font = [UIFont systemFontOfSize:14];
    fromTitleLabel.textColor = ColorWithLabelDescritionBlack;
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
    fromLabel.textColor = ColorWithLabelDescritionBlack;
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
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UILabel *highPayLabel = [[UILabel alloc] init];
    highPayLabel.text = @"--";
    highPayLabel.font = [UIFont systemFontOfSize:20];
    highPayLabel.textColor = ColorWithLabelDescritionBlack;
    highPayLabel.textAlignment = NSTextAlignmentLeft;
    highPayLabel.numberOfLines = 2;
    [self addSubview:highPayLabel];
    [highPayLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line3.mas_bottom).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@20);
    }];
    _highPayLabel = highPayLabel;
    
    //    订单金额
    UILabel *valueTitleLabel = [[UILabel alloc] init];
    valueTitleLabel.text = @"订单金额:";
    valueTitleLabel.font = [UIFont systemFontOfSize:12];
    valueTitleLabel.textColor = ColorWithLabelDescritionBlack;
    valueTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:valueTitleLabel];
    [valueTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highPayLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.mas_centerX).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *valueLabel = [[UILabel alloc] init];
    valueLabel.text = @"--";
    valueLabel.font = [UIFont systemFontOfSize:12];
    valueLabel.textColor = ColorWithLabelDescritionBlack;
    valueLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:valueLabel];
    [valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueTitleLabel);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@20);
    }];
    _valueLabel =valueLabel;
    
    //   最高矿工费
    UILabel *costTitleLabel = [[UILabel alloc] init];
    costTitleLabel.text = @"最高矿工费:";
    costTitleLabel.font = [UIFont systemFontOfSize:12];
    costTitleLabel.textColor = [UIColor lightGrayColor];
    costTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:costTitleLabel];
    [costTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.mas_centerX).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *costLabel = [[UILabel alloc] init];
    costLabel.text = @"--";
    costLabel.font = [UIFont systemFontOfSize:12];
    costLabel.textColor = ColorWithLabelDescritionBlack;
    costLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:costLabel];
    [costLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(costTitleLabel);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@20);
    }];
    _costLabel = costLabel;
    
    UIView *line4 = [[UIView alloc] init];
    line4.backgroundColor = ColorWithLabelDescritionBlack;
    line4.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line4];
    [line4 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(costTitleLabel);
        make.trailing.equalTo(self).offset(-10);
        make.top.equalTo(costTitleLabel.mas_bottom).offset(5);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    //  持有量
    UILabel *balanceTitleLabel = [[UILabel alloc] init];
    balanceTitleLabel.text = @"持有量:";
    balanceTitleLabel.font = [UIFont systemFontOfSize:12];
    balanceTitleLabel.textColor = ColorWithLabelDescritionBlack;
    balanceTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:balanceTitleLabel];
    [balanceTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line4.mas_bottom).offset(10);
        make.leading.equalTo(self.mas_centerX).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *balanceLabel = [[UILabel alloc] init];
    balanceLabel.text = @"--";
    balanceLabel.font = [UIFont systemFontOfSize:12];
    balanceLabel.textColor = ColorWithLabelDescritionBlack;
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
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    
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
