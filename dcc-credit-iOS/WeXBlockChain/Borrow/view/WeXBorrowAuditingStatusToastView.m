//
//  WeXBorrowAuditingStatusToastView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowAuditingStatusToastView.h"

@implementation WeXBorrowAuditingStatusToastView

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
        make.height.equalTo(@140);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"存在审核中订单,无法再次申请借款";
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.adjustsFontSizeToFitWidth = YES;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(contentView).offset(0);
        make.top.equalTo(contentView).offset(25);
    }];
   
    
    UIButton *cancelBtn = [WeXCustomButton button];
    [cancelBtn setTitle:@"确认" forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@40);
        make.bottom.equalTo(contentView).offset(-20);
    }];
    
}

- (void)waitingBtnClick
{
    [self dismiss];
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
