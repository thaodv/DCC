//
//  WeXWalletAlertWithCancelButtonView.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletAlertWithCancelButtonView.h"

@implementation WeXWalletAlertWithCancelButtonView

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
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    [self addGestureRecognizer:tap];
    
}

- (void)tapClick{
    [self endEditing:YES];
    [self dismiss];
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
    contentView.layer.cornerRadius = 12;
    contentView.layer.masksToBounds = YES;
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(20);
        make.trailing.equalTo(self).offset(-20);
        make.centerY.equalTo(self);
        make.height.equalTo(@150);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"提示";
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = [UIColor blackColor];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(0);
        make.centerX.equalTo(contentView).offset(0);
        make.height.equalTo(@40);
    }];
    
    UIButton *closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [closeBtn setImage:[UIImage imageNamed:@"Group 2"] forState:UIControlStateNormal];
    [closeBtn addTarget:self action:@selector(closeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:closeBtn];
    [closeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(titleLabel);
        make.trailing.equalTo(contentView).offset(-20);
        make.height.equalTo(@25);
        make.width.equalTo(@25);
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
    
    
    UILabel *contentLabel = [[UILabel alloc] init];
    contentLabel.font = [UIFont systemFontOfSize:16];
    contentLabel.textColor = COLOR_LABEL_DESCRIPTION;
    contentLabel.textAlignment = NSTextAlignmentLeft;
    contentLabel.numberOfLines = 0;
    [contentView addSubview:contentLabel];
    [contentLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(20);
        make.leading.equalTo(contentView).offset(40);
        make.trailing.equalTo(contentView).offset(-10);
    }];
    _contentLabel = contentLabel;
    
    UIImageView *coinImageView = [[UIImageView alloc] init];
    coinImageView.image = [UIImage imageNamed:@"ic_warning_black_24dp_2x"];
    [self addSubview:coinImageView];
    [coinImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(contentLabel).offset(0);
        make.trailing.equalTo(contentLabel.mas_leading).offset(-5);
        make.width.equalTo(@17);
        make.height.equalTo(@14);
    }];
    
  
    
   
    
  
    
   
    
    
    
    
}

- (void)closeBtnClick
{
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
