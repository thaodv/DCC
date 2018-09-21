//
//  WeXConfirmRepaymentView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/8.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXConfirmRepaymentView.h"

@interface WeXConfirmRepaymentView ()

@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) WeXCustomButton *confirmButton;
@property (nonatomic,copy) void (^ConfirmEvent)(void);

@end

@implementation WeXConfirmRepaymentView

+ (instancetype)createConfirmRepayView:(CGRect)frame
                                amount:(NSString *)amount
                                symbol:(NSString *)symbol
                               okEvent:(void(^)(void))okEvent {
    WeXConfirmRepaymentView *repayView = [[WeXConfirmRepaymentView alloc] initWithFrame:frame];
    [repayView setRepayviewAmount:amount symbol:symbol];
    repayView.ConfirmEvent = okEvent;
    return repayView;

}
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self wex_addsubviews];
        [self wex_addconstraints];
    }
    return self;
}
- (void)wex_addsubviews {
    UILabel *titleLab = [UILabel new];
    titleLab.font = [UIFont systemFontOfSize:15];
    titleLab.textColor = ColorWithHex(0x333333);
    titleLab.numberOfLines = 0;
    titleLab.textAlignment = NSTextAlignmentLeft;
    [self addSubview:titleLab];
    self.titleLab = titleLab;

    WeXCustomButton *confirmButton = [WeXCustomButton button];
    confirmButton.titleLabel.font = [UIFont systemFontOfSize:15.0];
    [confirmButton setTitle:@"确认还币" forState:UIControlStateNormal];
    [confirmButton addTarget:self action:@selector(confirmEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:confirmButton];
    self.confirmButton = confirmButton;
    
}

- (void)wex_addconstraints {
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(20);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
    }];
    
    [self.confirmButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.titleLab.mas_bottom).offset(55);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
        make.height.mas_equalTo(40);
    }];
}

- (void)setRepayviewAmount:(NSString *)amount symbol:(NSString *)symbol {
    NSString *text = [NSString stringWithFormat:@"%@%@%@%@",@"还币金额",amount,symbol,@"已到账,请点击\"确认还币\"完成还币"];
    [self.titleLab setText:text];
}

- (void)confirmEvent:(UIButton *)sender {
    !self.ConfirmEvent ? : self.ConfirmEvent();
}

@end
