//
//  WeXBorrowCreatedStatusToastView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowCreatedStatusToastView.h"

@implementation WeXBorrowCreatedStatusToastView

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
        make.centerY.equalTo(self);
        make.height.equalTo(@160);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"订单可能已经超时，建议您取消订单后重新申请，借币申请手续费将原路退还";
    titleLabel.textAlignment = NSTextAlignmentLeft;
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.numberOfLines = 0;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
        make.top.equalTo(contentView).offset(25);
    }];
    
    UIButton *waitingBtn = [WeXCustomButton button];
    [waitingBtn setTitle:@"继续等待" forState:UIControlStateNormal];
    [waitingBtn addTarget:self action:@selector(waitingBtnClick) forControlEvents:UIControlEventTouchUpInside];
    waitingBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:waitingBtn];
    [waitingBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(15);
        make.height.equalTo(@40);
        make.bottom.equalTo(contentView).offset(-20);
    }];
    
    
    
    UIButton *cancelBtn = [WeXCustomButton button];
    [cancelBtn setTitle:@"取消订单" forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(waitingBtn.mas_trailing).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@40);
        make.bottom.equalTo(contentView).offset(-20);
        make.width.equalTo(waitingBtn);
    }];
    
}

- (void)waitingBtnClick
{
    [self dismiss];
}


- (void)cancelBtnClick{
    if ([self.delegate respondsToSelector:@selector(borrowCreatedStatusToastViewDidClickCancelOrderButtoon)]) {
        [self.delegate borrowCreatedStatusToastViewDidClickCancelOrderButtoon];
    }
    
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
