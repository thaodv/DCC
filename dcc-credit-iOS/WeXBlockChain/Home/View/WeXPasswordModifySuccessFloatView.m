//
//  WeXPasswordModifySuccessFloatView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/29.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPasswordModifySuccessFloatView.h"

@implementation WeXPasswordModifySuccessFloatView
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
        make.height.equalTo(@160);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"修改成功";
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
    descriptionLabel.text = @"口袋密码修改成功，KEYSTORE信息变更，建议您立即重新备份口袋。";
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
    
    
    UIButton *backupBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backupBtn setTitle:@"备份口袋" forState:UIControlStateNormal];
    [backupBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [backupBtn addTarget:self action:@selector(backupBtnClick) forControlEvents:UIControlEventTouchUpInside];
    backupBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:backupBtn];
    [backupBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(line2);
        make.trailing.equalTo(contentView);
        make.bottom.equalTo(contentView);
        make.top.equalTo(line);
    }];
    
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [cancelBtn setTitle:@"暂不备份" forState:UIControlStateNormal];
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

- (void)backupBtnClick
{
    if ([self.delegate respondsToSelector:@selector(passwordModifySuccessFloatViewDidBackup)]) {
        [self.delegate passwordModifySuccessFloatViewDidBackup];
    }
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self dismiss];
    });
    
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
