//
//  WeXDeleteTouchIDFloatView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/12/13.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXDeleteTouchIDFloatView.h"

@implementation WeXDeleteTouchIDFloatView

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
        make.height.equalTo(@150);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"开启本地安全保护";
    titleLabel.font = [UIFont systemFontOfSize:19];
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
    descriptionLabel.text = @"指纹密码已被关闭，请您重新开启本地安全保护";
    descriptionLabel.font = [UIFont systemFontOfSize:16];
    descriptionLabel.textColor = [UIColor lightGrayColor];
    descriptionLabel.textAlignment = NSTextAlignmentCenter;
    descriptionLabel.numberOfLines = 0;
    [contentView addSubview:descriptionLabel];
    [descriptionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(20);
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
        make.height.equalTo(@50);
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
  
    UIButton *openBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [openBtn setTitle:@"开启" forState:UIControlStateNormal];
    [openBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [openBtn addTarget:self action:@selector(openBtnClick) forControlEvents:UIControlEventTouchUpInside];
    openBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:openBtn];
    [openBtn mas_makeConstraints:^(MASConstraintMaker *make) {
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

- (void)openBtnClick
{
    
    [self dismiss];
    
    if ([self.delegate respondsToSelector:@selector(touchIDFloatViewDidClickOpenButtoon)]) {
        [self.delegate touchIDFloatViewDidClickOpenButtoon];
    }
    
    
}

- (void)cancelBtnClick
{
    if ([self.delegate respondsToSelector:@selector(touchIDFloatViewDidClickCancelButton)]) {
        [self.delegate touchIDFloatViewDidClickCancelButton];
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
