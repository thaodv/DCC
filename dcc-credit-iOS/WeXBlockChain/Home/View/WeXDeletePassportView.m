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
        make.height.equalTo(@180);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"删除口袋";
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = [UIColor blackColor];
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
    descriptionLabel.text = @"如果您还没有对口袋进行过备份，删除口袋可能会导致您的数字资产永远无法找回，请您自行检查口袋备份情况。";
    descriptionLabel.font = [UIFont systemFontOfSize:15];
    descriptionLabel.textColor = [UIColor lightGrayColor];
    descriptionLabel.textAlignment = NSTextAlignmentLeft;
    descriptionLabel.numberOfLines = 0;
    [contentView addSubview:descriptionLabel];
    [descriptionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(15);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
    }];
    
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = [UIColor lightGrayColor];
    line2.alpha = LINE_VIEW_ALPHA;
    [self addSubview: line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(contentView);
        make.bottom.equalTo(contentView);
        make.height.equalTo(@45);
        make.width.equalTo(@LINE_VIEW_Width);
    }];
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = [UIColor lightGrayColor];
    line.alpha = LINE_VIEW_ALPHA;
    [self addSubview: line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.bottom.equalTo(line2.mas_top);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    
    UIButton *backUpBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backUpBtn setTitle:@"点击这里备份口袋" forState:UIControlStateNormal];
    [backUpBtn setTitleColor:ColorWithRGB(252, 31, 120) forState:UIControlStateNormal];
    [backUpBtn addTarget:self action:@selector(backUpBtnClick) forControlEvents:UIControlEventTouchUpInside];
    backUpBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [contentView addSubview:backUpBtn];
    [backUpBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.trailing.equalTo(contentView).offset(-15);
        make.bottom.equalTo(line.mas_top).offset(-10);
        make.width.equalTo(@140);
        make.height.equalTo(@20);
    }];
    
    
    UIButton *deleteBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [deleteBtn setTitle:@"删除口袋" forState:UIControlStateNormal];
    [deleteBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [deleteBtn addTarget:self action:@selector(deleteBtnClick) forControlEvents:UIControlEventTouchUpInside];
    deleteBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:deleteBtn];
    [deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(line2);
        make.trailing.equalTo(contentView);
        make.bottom.equalTo(contentView);
        make.top.equalTo(line);
    }];
    
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [cancelBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView);
        make.trailing.equalTo(line2);
        make.bottom.equalTo(contentView);
        make.top.equalTo(line);
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
