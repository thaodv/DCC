//
//  WeXCreatePassportChooseView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCreatePassportChooseView.h"

@implementation WeXCreatePassportChooseView

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
    
    UIView *contentView = [[UIView alloc] init];
    contentView.backgroundColor = COLOR_BACKGROUND_ALL;
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.centerY.equalTo(self);
        make.height.equalTo(@180);
    }];
    
    
    UIButton *createBtn = [WeXCustomButton button];
    [createBtn setTitle:NSLocalizedString(@"WeXCreatePassportChooseView_createButtonTitle", @"") forState:UIControlStateNormal];
    [createBtn addTarget:self action:@selector(createBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [contentView addSubview:createBtn];
    [createBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(contentView);
        make.centerY.equalTo(contentView).offset(-30);
        make.width.equalTo(@140);
        make.height.equalTo(@40);
    }];
    
    
    UIButton *importBtn = [WeXCustomButton button];
    [importBtn setTitle:NSLocalizedString(@"WeXCreatePassportChooseView_importButtonTitle", @"") forState:UIControlStateNormal];
    [importBtn addTarget:self action:@selector(importBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [contentView addSubview:importBtn];
    [importBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(contentView);
        make.centerY.equalTo(contentView).offset(30);
        make.width.equalTo(@140);
        make.height.equalTo(@40);
    }];
    
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    cancelBtn.userInteractionEnabled = YES;
    [cancelBtn setImage:[UIImage imageNamed:@"dcc_choose_cancel"] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.top.equalTo(contentView.mas_bottom).offset(20);
        make.width.equalTo(@30);
        make.height.equalTo(@30);
    }];
    
    
}

- (void)createBtnClick
{
    if ([self.delegate respondsToSelector:@selector(clickCreatePassportButton)]) {
        [self.delegate clickCreatePassportButton];
    }
    [self dismiss];
    
}

- (void)importBtnClick
{
    if ([self.delegate respondsToSelector:@selector(clickImportPassportButton)]) {
        [self.delegate clickImportPassportButton];
    }
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
