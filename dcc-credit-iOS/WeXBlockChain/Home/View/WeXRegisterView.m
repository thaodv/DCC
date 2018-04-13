//
//  WeXRegisterView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/13.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXRegisterView.h"
@interface WeXRegisterView()<UITextFieldDelegate>
{
}



@end

@implementation WeXRegisterView


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
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [cancelBtn setImage:[UIImage imageNamed:@"cha"] forState:UIControlStateNormal];
    [cancelBtn setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.leading.equalTo(self).offset(0);
        make.size.mas_equalTo(CGSizeMake(45, 45));
    }];
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor lightGrayColor];
    line1.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.trailing.equalTo(self);
        make.top.equalTo(cancelBtn.mas_bottom);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UIButton *showBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [showBtn setImage:[UIImage imageNamed:@"eye2"] forState:UIControlStateNormal];
    [showBtn setImage:[UIImage imageNamed:@"eye1"] forState:UIControlStateSelected];
    [showBtn setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
    [showBtn addTarget:self action:@selector(showBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    showBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self addSubview:showBtn];
    [showBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(5);
        make.trailing.equalTo(self).offset(-20);
        make.width.equalTo(@30);
        make.height.equalTo(@40);
    }];
    
    //密码输入框
    _passwordTextField = [[UITextField alloc] init];
    _passwordTextField.delegate = self;
    _passwordTextField.textColor = [UIColor lightGrayColor];
    _passwordTextField.backgroundColor = [UIColor whiteColor];
    _passwordTextField.borderStyle = UITextBorderStyleNone;
    _passwordTextField.keyboardType = UIKeyboardTypeASCIICapable;
    _passwordTextField.secureTextEntry = YES;
    UILabel *leftLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(0, 0,130,50)];
    leftLabel1.text = @"请输入口袋密码:";
    leftLabel1.font = [UIFont systemFontOfSize:17];
    leftLabel1.textColor = [UIColor lightGrayColor];
    leftLabel1.backgroundColor = [UIColor clearColor];
    _passwordTextField.leftViewMode = UITextFieldViewModeAlways;
    _passwordTextField.leftView = leftLabel1;
    _passwordTextField.font = [UIFont systemFontOfSize:17];
    [self addSubview:_passwordTextField];
    [_passwordTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(0);
        make.leading.equalTo(self).offset(40);
        make.trailing.equalTo(showBtn.mas_leading);
        make.height.equalTo(@50);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = [UIColor lightGrayColor];
    line2.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(_passwordTextField.mas_leading);
        make.trailing.equalTo(showBtn);
        make.top.equalTo(_passwordTextField.mas_bottom);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    //图形码按钮
    _graphBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _graphBtn.backgroundColor = [UIColor whiteColor];
    _graphBtn.layer.borderWidth = 1;
    _graphBtn.layer.borderColor = ColorWithRGB(250, 31, 118).CGColor;
    [_graphBtn addTarget:self action:@selector(graphBtnClick) forControlEvents:UIControlEventTouchUpInside];
    _graphBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [self addSubview:_graphBtn];
    [_graphBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom).offset(10);
        make.trailing.equalTo(line2);
        make.width.equalTo(@60);
        make.height.equalTo(@30);
    }];
    
   //图形码输入框
    _graphTextField = [[UITextField alloc] init];
    _graphTextField.delegate = self;
    _graphTextField.textColor = [UIColor lightGrayColor];
    _graphTextField.backgroundColor = [UIColor whiteColor];
    _graphTextField.borderStyle = UITextBorderStyleNone;
    _graphTextField.keyboardType = UIKeyboardTypeASCIICapable;
    UILabel *leftLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(0,0,115,50)];
    leftLabel2.text = @"请输入验证码:";
    leftLabel2.font = [UIFont systemFontOfSize:17];
    leftLabel2.textColor = [UIColor lightGrayColor];
    leftLabel2.backgroundColor = [UIColor clearColor];
    _graphTextField.leftViewMode = UITextFieldViewModeAlways;
    _graphTextField.font = [UIFont systemFontOfSize:17];
    _graphTextField.leftView = leftLabel2;;
    [self addSubview:_graphTextField];
    [_graphTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom);
        make.leading.equalTo(line2);
        make.trailing.equalTo(_graphBtn.mas_leading);
        make.height.equalTo(@50);
    }];
    
    UIView *line3 = [[UIView alloc] init];
    line3.backgroundColor = [UIColor lightGrayColor];
    line3.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line3];
    [line3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(_graphTextField.mas_bottom);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UIButton *createBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    [createBtn setTitle:@"生成口袋" forState:UIControlStateNormal];
    [createBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [createBtn addTarget:self action:@selector(createBtnClick) forControlEvents:UIControlEventTouchUpInside];
    createBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self addSubview:createBtn];
    [createBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self);
        make.trailing.equalTo(self);
        make.leading.equalTo(self);
        make.top.equalTo(line3.mas_bottom);
    }];
    
}

- (void)cancelBtnClick{
     [self endEditing:YES];
    if (self.cancelBtnBlock) {
        self.cancelBtnBlock();
    }
    
}

- (void)showBtnClick:(UIButton *)btn{
    btn.selected = !btn.selected;
    _passwordTextField.secureTextEntry = !btn.selected;
}

- (void)graphBtnClick{
    if (self.graphBtnBlock) {
        self.graphBtnBlock();
    }
}

- (void)createBtnClick{
    
    
    

    if (_passwordTextField.text.length < 2||_passwordTextField.text.length >20) {
        [WeXPorgressHUD showText:@"密码长度应为2-20位!" onView:self.window];
         return;
    }
    
    if (![WexCommonFunc checkoutString:_passwordTextField.text withRegularExpression:@"^[^\\u4e00-\\u9fa5]+$"]) {
        [WeXPorgressHUD showText:@"密码里面不能包含汉字!" onView:self.window];
        return;
    }
    
    if (_graphTextField.text.length != 4) {
        [WeXPorgressHUD showText:@"验证码格式不对!" onView:self.window];
        return;
    }
    
    [self endEditing:YES];
    
    if (self.createBtnBlock) {
        self.createBtnBlock();
    }
}


- (void)dismiss{
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        for (UIView *view in self.subviews) {
            [view removeFromSuperview];
        }
        
        [self removeFromSuperview];
    });
    
   
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
        
        if (textField == _passwordTextField) {
            if (comment.length > 20) {
                [WeXPorgressHUD showText:@"密码长度最多为20位" onView:self.window];
                return NO;
            }
        }
        else if (textField == _graphTextField){
            if (comment.length > 4) {
                [WeXPorgressHUD showText:@"长度最多为4位" onView:self.window];
                return NO;
            }
        }
    }
    else
    {
        comment = [textField.text substringToIndex:textField.text.length -range.length];
    }
    return YES;
}


-(void)dealloc{
    NSLog(@"%s",__func__);
}


@end
