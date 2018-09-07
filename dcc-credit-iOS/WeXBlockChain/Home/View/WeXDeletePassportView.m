//
//  WeXDeletePassportView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/29.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXDeletePassportView.h"

@implementation WeXDeletePassportView

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
    backView.backgroundColor = [UIColor blackColor];
    backView.alpha = COVER_VIEW_ALPHA;
    [self addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(self);
        make.bottom.equalTo(self);
    }];
    
    UIView *contentView = [[UIView alloc] init];
    contentView.backgroundColor = [UIColor whiteColor];
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.centerY.equalTo(self);
        make.height.equalTo(@200);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = WeXLocalizedString(@"删除钱包");
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.numberOfLines = 0;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UILabel * descriptionLabel = [[UILabel alloc] init];
    //DeletePassportViewTipsMessage
    descriptionLabel.text = WeXLocalizedString(@"DeletePassportViewTipsMessage");
    descriptionLabel.font = [UIFont systemFontOfSize:15];
    descriptionLabel.textColor = COLOR_LABEL_DESCRIPTION;
    descriptionLabel.textAlignment = NSTextAlignmentLeft;
    descriptionLabel.numberOfLines = 0;
    [contentView addSubview:descriptionLabel];
    [descriptionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(15);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
    }];
    
    
    UIButton *backUpBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backUpBtn setTitle:WeXLocalizedString(@"点击这里备份钱包") forState:UIControlStateNormal];
    [backUpBtn setTitleColor:COLOR_THEME_ALL forState:UIControlStateNormal];
    [backUpBtn addTarget:self action:@selector(backUpBtnClick) forControlEvents:UIControlEventTouchUpInside];
    backUpBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [contentView addSubview:backUpBtn];
    [backUpBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.trailing.equalTo(contentView).offset(-15);
        make.top.equalTo(descriptionLabel.mas_bottom).offset(10);
        make.width.equalTo(@140);
        make.height.equalTo(@20);
    }];
    
    
    UIButton *cancelBtn = [WeXCustomButton button];
    [cancelBtn setTitle:WeXLocalizedString(@"取消") forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(15);
        make.bottom.equalTo(contentView).offset(-15);
        make.height.equalTo(@40);
    }];
    
    
    
    UIButton *deleteBtn = [WeXCustomButton button];
    [deleteBtn setTitle:WeXLocalizedString(@"删除钱包") forState:UIControlStateNormal];
    [deleteBtn addTarget:self action:@selector(deleteBtnClick) forControlEvents:UIControlEventTouchUpInside];
    deleteBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:deleteBtn];
    [deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(cancelBtn.mas_trailing).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
        make.width.equalTo(cancelBtn);
        make.height.equalTo(cancelBtn);
        make.bottom.equalTo(cancelBtn);
    }];
    
   
  
    
  
    

    
}

- (void)backUpBtnClick
{
    if ([self.delegate respondsToSelector:@selector(deletePassportViewDidBackup)]) {
        [self.delegate deletePassportViewDidBackup];
    }
    
}

- (void)deleteBtnClick
{
    if ([self.delegate respondsToSelector:@selector(deletePassportViewDidDeletePassport)]) {
        [self.delegate deletePassportViewDidDeletePassport];
    }
    
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
