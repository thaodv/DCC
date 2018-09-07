//
//  WeXGetRedPacketView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXGetRedPacketView.h"

@interface WeXGetRedPacketView()
{
    UIImageView *_packetImageView;
    NSString *_amount;
}


@end

@implementation WeXGetRedPacketView

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
    
    UIImage *packetImage = [UIImage imageNamed:@"activity_home_packet"];

    UIView *contentView = [[UIView alloc] init];
    contentView.backgroundColor = [UIColor whiteColor];
    contentView.layer.cornerRadius = 12;
    contentView.layer.masksToBounds = YES;
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.centerY.equalTo(self);
        make.size.mas_equalTo(packetImage.size);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.font = [UIFont systemFontOfSize:20];
    titleLabel.textColor = ColorWithRGB(240, 151, 75);
    titleLabel.textAlignment = NSTextAlignmentCenter;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(30);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    _amountLabel = titleLabel;
    
    UIImageView *coinImageView = [[UIImageView alloc] init];
    coinImageView.image = [UIImage imageNamed:@"activity_home_coin"];
    [self addSubview:coinImageView];
    [coinImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(contentView);
        make.centerY.equalTo(contentView);
    }];
    
    
    UIButton *confirmBtn = [WeXCustomButton button];
    [confirmBtn setTitle:@"确认" forState:UIControlStateNormal];
    confirmBtn.titleLabel.font = [UIFont systemFontOfSize:16];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(15);
        make.bottom.equalTo(contentView.mas_bottom).offset(-15);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@45);
    }];
    
    
    
    UIImageView *packetImageView = [[UIImageView alloc] initWithImage:packetImage];
    packetImageView.userInteractionEnabled = YES;
    [self addSubview:packetImageView];
    [packetImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(contentView);
    }];
    _packetImageView = packetImageView;
    
    UIButton *jupmBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    jupmBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    [jupmBtn setTitle:@"跳过" forState:UIControlStateNormal];
    [jupmBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [jupmBtn addTarget:self action:@selector(jupmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [packetImageView addSubview:jupmBtn];
    [jupmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.trailing.equalTo(contentView).offset(-5);
        make.top.equalTo(contentView).offset(5);
        make.width.equalTo(@40);
        make.height.equalTo(@30);
    }];
    
    
    UIButton *getBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    getBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [getBtn setTitle:@"点击领取红包" forState:UIControlStateNormal];
    [getBtn setTitleColor:[UIColor yellowColor] forState:UIControlStateNormal];
    [getBtn addTarget:self action:@selector(getBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [packetImageView addSubview:getBtn];
    [getBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(contentView);
        make.bottom.equalTo(contentView.mas_bottom).offset(-15);
        make.width.equalTo(@200);
        make.height.equalTo(@40);
    }];
    
    
    
    
}

- (void)jupmBtnClick{
    WEXNSLOG(@"%s",__func__);
    if (_jumpBtnBlock) {
        _jumpBtnBlock();
    }
    
    [self dismiss];
}

- (void)getBtnClick
{
    
    if (_confirmBtnBlock) {
        _confirmBtnBlock();
    }
   
    
}

- (void)removeFrontRedPacketView
{
    WEXNSLOG(@"%s",__func__);
    for (UIView *view in _packetImageView.subviews) {
        [view removeFromSuperview];
    }
    
    [_packetImageView removeFromSuperview];
}

- (void)confirmBtnClick
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
