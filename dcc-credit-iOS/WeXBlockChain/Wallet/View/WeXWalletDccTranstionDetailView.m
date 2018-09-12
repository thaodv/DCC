//
//  WeXWalletDccTranstionDetailView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDccTranstionDetailView.h"

@interface WeXWalletDccTranstionDetailView ()
@property (nonatomic, weak) UILabel *fromTitleLab;
@property (nonatomic, weak) UILabel *toTitleLab;

@property (nonatomic, weak) UILabel *valueTitleLabel;
@property (nonatomic, weak) UILabel *balanceTitleLabel;
@property (nonatomic, weak) UIView *line3;
@property (nonatomic, weak) UILabel *valueLab;
@end


@implementation WeXWalletDccTranstionDetailView

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
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [cancelBtn setImage:[UIImage imageNamed:@"dcc_cha"] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.leading.equalTo(self).offset(0);
        make.size.mas_equalTo(CGSizeMake(45, 45));
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = WeXLocalizedString(@"支付详情");
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
    toTitleLabel.text = WeXLocalizedString(@"接收地址:");
    toTitleLabel.font = [UIFont systemFontOfSize:14];
    toTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    toTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:toTitleLabel];
    self.toTitleLab = toTitleLabel;
    [self.toTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(10);
        make.leading.equalTo(self).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    
    UILabel *toLabel = [[UILabel alloc] init];
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
    fromTitleLabel.text = WeXLocalizedString(@"付款地址:");
    fromTitleLabel.font = [UIFont systemFontOfSize:14];
    fromTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    fromTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:fromTitleLabel];
    self.fromTitleLab = fromTitleLabel;
    [self.fromTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
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
    _fromLabel = fromLabel;
    [self.fromLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromTitleLabel);
        make.leading.equalTo(fromTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.height.equalTo(@40);
    }];
    
    UIView *line3 = [[UIView alloc] init];
    line3.backgroundColor = [UIColor lightGrayColor];
    line3.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line3];
    self.line3 = line3;
    [self.line3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.top.equalTo(fromLabel.mas_bottom).offset(10);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    //    订单金额
    UILabel *valueTitleLabel = [[UILabel alloc] init];
    valueTitleLabel.text = WeXLocalizedString(@"支付金额:");
    valueTitleLabel.font = [UIFont systemFontOfSize:12];
    valueTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    valueTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:valueTitleLabel];
    self.valueTitleLabel = valueTitleLabel;
    [self.valueTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.line3.mas_bottom).offset(20);
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
    balanceTitleLabel.text = WeXLocalizedString(@"持有量:");
    balanceTitleLabel.font = [UIFont systemFontOfSize:12];
    balanceTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    balanceTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:balanceTitleLabel];
    self.balanceTitleLabel = balanceTitleLabel;
    [self.balanceTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.valueLabel.mas_bottom).offset(10);
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
    
  
    WeXCustomButton *confirmBtn = [WeXCustomButton button];
    [confirmBtn setTitle:WeXLocalizedString(@"确认") forState:UIControlStateNormal];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    confirmBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self).offset(-20);
        make.height.equalTo(@50);
        make.leading.equalTo(self).offset(15);
        make.trailing.equalTo(self).offset(-15);
    }];
    _confirmBtn = confirmBtn;
}
- (void)setTranstionViewType:(WeXWalletTranstionViewType)type {
    switch (type) {
        case WeXWalletTranstionViewTypeDefault: {
            [self.valueTitleLabel mas_updateConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.line3.mas_bottom).offset(20);
                make.leading.equalTo(self.mas_centerX).offset(-20);
                make.height.equalTo(@20);
            }];
            [self.balanceTitleLabel mas_updateConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.valueLabel.mas_bottom).offset(10);
                make.leading.equalTo(self.mas_centerX).offset(-20);
                make.height.equalTo(@20);
            }];
            
            [self.valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.valueTitleLabel);
                make.trailing.equalTo(self).offset(-10);
                make.height.equalTo(@20);
            }];
            [self.balanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.balanceTitleLabel);
                make.trailing.equalTo(self).offset(-10);
                make.height.equalTo(@20);
            }];
        }
            
            break;
        case WeXWalletTranstionViewTypeCPBuy: {
            [self.valueTitleLabel setText:WeXLocalizedString(@"认购数量:")];
            self.valueTitleLabel.font = WexFont(14.0);
            self.balanceTitleLabel.font = WexFont(14.0);
            self.valueLabel.font     = WexFont(14.0);
            self.balanceLabel.font   = WexFont(14.0);
            [self.balanceTitleLabel setText:WeXLocalizedString(@"持有量:")];
            self.valueLab.textAlignment     = NSTextAlignmentLeft;
            self.balanceLabel.textAlignment = NSTextAlignmentLeft;

            [self.valueTitleLabel mas_updateConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.line3.mas_bottom);
                make.leading.equalTo(self.fromTitleLab);
                make.height.equalTo(@20);
            }];
            [self.balanceTitleLabel mas_updateConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.valueLabel.mas_bottom).offset(10);
                make.leading.equalTo(self.fromTitleLab);
                make.height.equalTo(@20);
            }];
            
            [self.valueLabel mas_updateConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.valueTitleLabel);
                make.left.equalTo(self.fromLabel.mas_left).offset(0);
                make.height.equalTo(@20);
            }];
            [self.balanceLabel mas_updateConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.balanceTitleLabel);
                make.left.equalTo(self.fromLabel.mas_left).offset(0);
                make.height.equalTo(@20);
            }];
            
        }
            break;
            
        default:
            break;
    }
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
