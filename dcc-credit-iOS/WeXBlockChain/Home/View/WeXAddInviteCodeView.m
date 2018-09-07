//
//  WeXAddInviteCodeView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAddInviteCodeView.h"

@interface WeXAddInviteCodeView()<UITextFieldDelegate>
{
    UITextField  *_codeTextField;
}

@end

@implementation WeXAddInviteCodeView

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
    contentView.backgroundColor = [UIColor whiteColor];
    contentView.layer.cornerRadius = 12;
    contentView.layer.masksToBounds = YES;
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(15);
        make.trailing.equalTo(self).offset(-15);
        make.centerY.equalTo(self);
        make.height.equalTo(@220);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"为了您正常使用，需进行账号体系升级";
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.adjustsFontSizeToFitWidth = YES;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(30);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    

    _codeTextField = [[UITextField alloc] init];
    _codeTextField.delegate = self;
    _codeTextField.textColor = [UIColor lightGrayColor];
    _codeTextField.backgroundColor = [UIColor clearColor];
    _codeTextField.borderStyle = UITextBorderStyleNone;
    _codeTextField.font = [UIFont systemFontOfSize:17];
    _codeTextField.placeholder = @"请输入邀请码(选填)";
    _codeTextField.leftViewMode = UITextFieldViewModeAlways;
    _codeTextField.returnKeyType = UIReturnKeyDone;
    [self addSubview:_codeTextField];
    [_codeTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(contentView).offset(-10);
        make.leading.equalTo(contentView).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
        make.height.equalTo(@50);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = COLOR_ALPHA_LINE;
    [self addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(15);
        make.trailing.equalTo(self).offset(-15);
        make.top.equalTo(_codeTextField.mas_bottom).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
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
  
    
    
}



- (void)confirmBtnClick
{
    if (_inviteConfirmBtnBlock) {
        _inviteConfirmBtnBlock(_codeTextField.text);
    }
}


- (void)dismiss
{
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    
    [self removeFromSuperview];
}

#pragma mark - TextField代理方法
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self endEditing:YES];
    return YES;
}

-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    
    NSString *comment;
    if(range.length == 0)
    {
        comment = [NSString stringWithFormat:@"%@%@",textField.text, string];
        
        if (comment.length > 10) {
            [WeXPorgressHUD showText:@"验证码格式不对" onView:self];
            return NO;
        }
    }
    else
    {
        comment = [textField.text substringToIndex:textField.text.length -range.length];
    }
    return YES;
}


-(void)dealloc
{
    NSLog(@"%s",__func__);
}



@end
