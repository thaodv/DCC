//
//  WeXBindWeChatAlertView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBindWeChatAlertView.h"
#import "AppDelegate.h"

@interface WeXBindWeChatAlertView ()

@property (nonatomic, strong) UIView *backView;
@property (nonatomic, strong) UIView *containerView;
@property (nonatomic, strong) UILabel *tipsLab;
@property (nonatomic, strong) UILabel *contentLab;
@property (nonatomic, strong) UIButton *dismissButton;
@property (nonatomic, strong) WeXCustomButton *bindButton;
@property (nonatomic,copy) void (^CompleteBlock)(BOOL,BOOL);

@end

@implementation WeXBindWeChatAlertView

+ (instancetype)createAlertViewWithComplteEvent:(void(^)(BOOL isOk,BOOL isCancel))complete {
    WeXBindWeChatAlertView *alertView = [[WeXBindWeChatAlertView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    alertView.CompleteBlock = complete;
    return alertView;
}

- (void)wex_loadViews {
    _backView = [[UIView alloc] init];
    _backView.backgroundColor = ColorWithRGBA(74, 74, 74, 0.5);
    [self addSubview:_backView];
    
    _containerView = [[UIView alloc] init];
    _containerView.backgroundColor = [UIColor whiteColor];
    _containerView.layer.cornerRadius = 12.0;
    _containerView.clipsToBounds = true;
    [_backView addSubview:_containerView];
    
    _tipsLab = CreateLeftAlignmentLabel(WeXPFFont(18), WexDefault4ATitleColor);
    [_tipsLab setText:@"友情提示："];
    [_containerView addSubview:_tipsLab];
    
    _dismissButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [_dismissButton setImage:[UIImage imageNamed:@"dismiss"] forState:UIControlStateNormal];
    [_dismissButton setImage:[UIImage imageNamed:@"dismiss"] forState:UIControlStateHighlighted];
    [_dismissButton setImage:[UIImage imageNamed:@"dismiss"] forState:UIControlStateSelected];
    [_dismissButton addTarget:self action:@selector(dismissEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_containerView addSubview:_dismissButton];
    
    _contentLab = CreateLeftAlignmentLabel(WeXPFFont(14), WexDefault4ATitleColor);
    [_contentLab setText:@"新版本将使用阳光值计算奖励，不再使用挖矿值。\n\n• APP老用户请绑定微信，系统将自动把原挖矿值导入阳光值。否则老用户将无法继续领取奖励！\n\n• 新用户绑定微信可获得10阳光，阳光越多，可收取的奖励越多！"];
    _contentLab.numberOfLines = 0;
    [_containerView addSubview:_contentLab];
    
    _bindButton = [WeXCustomButton createWithType:WeXCustomButtonBlue];
    [_bindButton setTitle:@"微信授权" forState:UIControlStateNormal];
    [_bindButton addTarget:self action:@selector(bindWeChat:) forControlEvents:UIControlEventTouchUpInside];
    [_containerView addSubview:_bindButton];
}

- (void)wex_layoutConstraints {
    [self.backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.bottom.mas_equalTo(0);
    }];
    
    [self.containerView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.right.mas_equalTo(-14);
        make.centerY.mas_equalTo(0);
    }];
    
    [self.tipsLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(14);
        make.left.mas_equalTo(14);
    }];
    [self.dismissButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.tipsLab).offset(-3);
        make.right.mas_equalTo(-14);
        make.size.mas_equalTo(CGSizeMake(30, 30));
    }];
    
    [self.contentLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.tipsLab.mas_bottom).offset(20);
        make.left.mas_equalTo(14);
        make.right.mas_equalTo(-14);
    }];
    
    [self.bindButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.right.mas_equalTo(-14);
        make.height.mas_equalTo(40);
        make.bottom.mas_equalTo(-14);
        make.top.equalTo(self.contentLab.mas_bottom).offset(30);
    }];
}


- (void)dismissEvent:(UIButton *)sender {
    !self.CompleteBlock ? : self.CompleteBlock (NO,YES);
}
- (void)bindWeChat:(UIButton *)sender {
    [self dismiss];
    !self.CompleteBlock ? : self.CompleteBlock (YES,NO);
}

- (void)show {
    UIWindow *keyWindow = [[UIApplication sharedApplication].delegate window];
    [keyWindow addSubview:self];
}

- (void)dismiss {
    [self removeFromSuperview];
}


@end
