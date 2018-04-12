//
//  WeXGraphView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/27.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXGraphView.h"

@implementation WeXGraphView

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
        make.height.equalTo(@145);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"验证验证码";
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
    
    //图形码按钮
    _graphBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _graphBtn.backgroundColor = [UIColor whiteColor];
    _graphBtn.layer.borderWidth = 1;
    _graphBtn.layer.borderColor = ColorWithRGB(250, 31, 118).CGColor;
    [_graphBtn addTarget:self action:@selector(graphBtnClick) forControlEvents:UIControlEventTouchUpInside];
    _graphBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [contentView addSubview:_graphBtn];
    [_graphBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(20);
        make.trailing.equalTo(contentView).offset(-20);
        make.width.equalTo(@60);
        make.height.equalTo(@25);
    }];
    
    //图形码输入框
    _graphTextField = [[UITextField alloc] init];
    _graphTextField.delegate = self;
    _graphTextField.textColor = [UIColor lightGrayColor];
    _graphTextField.backgroundColor = [UIColor whiteColor];
    _graphTextField.borderStyle = UITextBorderStyleNone;
    UILabel *leftLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(0,0,100,45)];
    leftLabel2.text = @"请输入验证码:";
    leftLabel2.font = [UIFont systemFontOfSize:14];
    leftLabel2.textColor = [UIColor lightGrayColor];
    leftLabel2.backgroundColor = [UIColor clearColor];
    _graphTextField.leftViewMode = UITextFieldViewModeAlways;
    _graphTextField.leftView = leftLabel2;;
    [contentView addSubview:_graphTextField];
    [_graphTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(20);
        make.trailing.equalTo(_graphBtn.mas_leading);
        make.height.equalTo(@45);
    }];
    
    UIView *line = [[UIView alloc] init];
     line.backgroundColor = [UIColor lightGrayColor];
     line.alpha = 0.3;
    [self addSubview: line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(_graphTextField.mas_bottom).offset(10);
        make.height.equalTo(@1);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = [UIColor lightGrayColor];
    line2.alpha = 0.3;
    [self addSubview: line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(contentView);
        make.bottom.equalTo(contentView);
        make.top.equalTo(line.mas_bottom);
        make.width.equalTo(@1);
    }];
    
    UIButton *confirmBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [confirmBtn setTitle:@"确认" forState:UIControlStateNormal];
    [confirmBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    confirmBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
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

- (void)graphBtnClick{
    if (self.graphBtnBlock) {
        self.graphBtnBlock();
    }
}

- (void)confirmBtnClick{
    if (self.comfirmBtnBlock) {
        self.comfirmBtnBlock();
    }
    [self dismiss];
}

- (void)cancelBtnClick{
    [self dismiss];
}

- (void)dismiss{
    
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    
    [self removeFromSuperview];
   
    
    
}
    

@end
