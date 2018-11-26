//
//  WeXBackUpAlertView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/12.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBackUpAlertView.h"

@interface WeXBackUpAlertView ()

@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *content;
@property (nonatomic, copy) NSString *leftButtonTitle;
@property (nonatomic, copy) NSString *rightButtonTitle;

@property (nonatomic, strong) UIView *parentView;
@property (nonatomic, strong) UIView *containerView;

@property (nonatomic,copy) WeXBackUpEventBlock block;
@property (nonatomic, assign) WeXBackUpAlertViewType alertType;

@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UIButton *dismissButton;
@property (nonatomic, strong) UILabel *contentLab;
@property (nonatomic, strong) WeXCustomButton *leftButton;
@property (nonatomic, strong) WeXCustomButton *rightButton;

@end

@implementation WeXBackUpAlertView

+ (instancetype)createAlertInView:(UIView *)superView
                    alertViewType:(WeXBackUpAlertViewType)type
                            event:(WeXBackUpEventBlock)block {
    WeXBackUpAlertView *alertView = [[WeXBackUpAlertView alloc] initWithAlertViewType:type];
    alertView.parentView = superView;
    alertView.block = block;
    return alertView;
}

- (id)initWithAlertViewType:(WeXBackUpAlertViewType)type {
    if (self = [super init]) {
        [self setBackgroundColor:ColorWithRGBA(74, 74, 74, 0.5)];
        [self setAlertType:type];
        [self wex_loadViews];
        [self wex_layoutConstraints];
    }
    return self;
}

- (void)setAlertType:(WeXBackUpAlertViewType)alertType {
    _alertType = alertType;
    switch (alertType) {
        case WeXBackUpAlertViewBackUp: {
            [self setTitle:@"备份钱包"];
            [self setContent:@"点击【去备份钱包】完成备份钱包任务。备份完成，点击【已备份领阳光】，获得50阳光！"];
            [self setLeftButtonTitle:@"去备份钱包"];
            [self setRightButtonTitle:@"已备份领阳光"];
        }
            break;
            
        default:
            break;
    }
}
- (void)wex_loadViews {
    _containerView = [UIView new];
    [_containerView setBackgroundColor:[UIColor whiteColor]];
    _containerView.layer.cornerRadius = 12;
    _containerView.clipsToBounds = true;
    [self addSubview:_containerView];
    
    _titleLab = CreateCenterAlignmentLabel(WeXPFFont(16), WexDefault4ATitleColor);
    [_titleLab setText:self.title];
    [self.containerView addSubview:_titleLab];
    
    self.dismissButton = [[UIButton alloc] init];
    [self.dismissButton setTitle:@"X" forState:UIControlStateNormal];
    [self.dismissButton setTitleColor:ColorWithHex(0x7B40FF) forState:UIControlStateNormal];
    self.dismissButton.titleLabel.font = WexFont(16);
    [self.dismissButton addTarget:self action:@selector(dismissEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.containerView addSubview:self.dismissButton];
    
    _contentLab = CreateLeftAlignmentLabel(WeXPFFont(15), WexDefault4ATitleColor);
    _contentLab.numberOfLines = 0;
    [_contentLab setText:self.content];
    [self.containerView addSubview:_contentLab];
    
    _leftButton = [WeXCustomButton createWithType:WeXCustomButtonBlue];
    [_leftButton setTitle:self.leftButtonTitle forState:UIControlStateNormal];
    [_leftButton addTarget:self action:@selector(leftButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.containerView addSubview:_leftButton];
    
    _rightButton = [WeXCustomButton createWithType:WeXCustomButtonBlue];
    [_rightButton setTitle:self.rightButtonTitle forState:UIControlStateNormal];
    [_rightButton addTarget:self action:@selector(rightButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.containerView addSubview:_rightButton];
    
}

- (void)dismissEvent:(UIButton *)sender {
    [self dismiss];
}

- (void)leftButtonEvent:(UIButton *)sender {
    !self.block ? : self.block (WeXBackUpAlertEventBackUp);
}

- (void)rightButtonEvent:(UIButton *)sender {
    !self.block ? : self.block (WeXBackUpAlertEventReward);
}

- (void)wex_layoutConstraints {
    [_containerView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self);
        make.left.mas_equalTo(28);
        make.right.mas_equalTo(-28);
    }];
    
    [_titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.top.equalTo(self.containerView).offset(15);
    }];
    
    [self.dismissButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-15);
        make.centerY.equalTo(_titleLab);
        make.size.mas_equalTo(CGSizeMake(24, 24));
    }];
    
    [self.contentLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_titleLab.mas_bottom).offset(25);
        make.left.mas_equalTo(20);
        make.right.mas_equalTo(-20);
    }];
    
    [self.leftButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(25);
        make.top.equalTo(self.contentLab.mas_bottom).offset(20);
        make.right.equalTo(self.containerView.mas_centerX).offset(-20);
        make.bottom.equalTo(self.containerView.mas_bottom).offset(-15);
        make.height.mas_equalTo(40);
    }];
    [self.rightButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-25);
        make.top.equalTo(self.leftButton);
        make.left.equalTo(self.containerView.mas_centerX).offset(20);
        make.height.mas_equalTo(40);
    }];
    
}

- (void)dismiss {
    [self removeFromSuperview];
}

- (void)show {
    [self.parentView addSubview:self];
    [self mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(0).priorityHigh();
        make.right.bottom.left.mas_equalTo(0);
        make.edges.mas_equalTo(0);
    }];
}

@end
