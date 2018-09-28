//
//  WeXPassportModifyPasswordController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPassportModifyPasswordController.h"
#import "WeXPassportBackupViewController.h"

#import "WeXPasswordModifySuccessFloatView.h"
#import "WeXPromptAddressView.h"

@interface WeXPassportModifyPasswordController ()<WeXPasswordManagerDelegate,WeXPasswordModifySuccessFloatViewDelegate>
{
    WeXPasswordCacheModal *_model;
    
    WeXPasswordManager *_manager;
    
    WeXPasswordModifySuccessFloatView *_successView;
}

@property (nonatomic,strong)UITextField *oldPsdTextField;

@property (nonatomic,strong)UITextField *newestPsdTextField;

@property(nonatomic,strong)WeXPromptAddressView *promptView;
@property(nonatomic,strong)UIView *blackView;

@end

@implementation WeXPassportModifyPasswordController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"钱包密码修改");
    [self setNavigationNomalBackButtonType];
    [self commonInit];
    [self setupSubViews];
}

- (void)commonInit{
    _model = [WexCommonFunc getPassport];
    NSLog(@"_model=%@",_model);
    
}

//初始化滚动视图
-(void)setupSubViews{
    //显示密码按钮
    UIButton *showBtn1 = [UIButton buttonWithType:UIButtonTypeCustom];
    [showBtn1 setImage:[UIImage imageNamed:@"eye2"] forState:UIControlStateNormal];
    [showBtn1 setImage:[UIImage imageNamed:@"eye1"] forState:UIControlStateSelected];
    [showBtn1 addTarget:self action:@selector(showBtn1Click:) forControlEvents:UIControlEventTouchUpInside];
    showBtn1.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.view addSubview:showBtn1];
    [showBtn1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+40);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@30);
        make.height.equalTo(@45);
    }];
    //原始密码输入框
    _oldPsdTextField = [[UITextField alloc] init];
    _oldPsdTextField.borderStyle = UITextBorderStyleNone;
    _oldPsdTextField.secureTextEntry = YES;
    UILabel *leftLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(0, 0,170,45)];
    leftLabel1.text = WeXLocalizedString(@"原钱包密码:");
    leftLabel1.font = [UIFont systemFontOfSize:17];
    leftLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    _oldPsdTextField.backgroundColor = [UIColor clearColor];
    _oldPsdTextField.leftViewMode = UITextFieldViewModeAlways;
    _oldPsdTextField.leftView = leftLabel1;
    _oldPsdTextField.textColor = COLOR_LABEL_DESCRIPTION;
    _oldPsdTextField.font = [UIFont systemFontOfSize:17];
    [self.view addSubview:_oldPsdTextField];
    [_oldPsdTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(showBtn1).offset(0);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(showBtn1.mas_leading);
        make.height.equalTo(@45);
    }];
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = COLOR_ALPHA_LINE;
    [self.view addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(_oldPsdTextField.mas_leading);
        make.trailing.equalTo(showBtn1);
        make.top.equalTo(_oldPsdTextField.mas_bottom);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    
    //显示密码按钮
    UIButton *showBtn2 = [UIButton buttonWithType:UIButtonTypeCustom];
    [showBtn2 setImage:[UIImage imageNamed:@"eye2"] forState:UIControlStateNormal];
    [showBtn2 setImage:[UIImage imageNamed:@"eye1"] forState:UIControlStateSelected];
    [showBtn2 addTarget:self action:@selector(showBtn2Click:) forControlEvents:UIControlEventTouchUpInside];
    showBtn2.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.view addSubview:showBtn2];
    [showBtn2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(0);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@30);
        make.height.equalTo(@45);
    }];
    
    //新密码输入框
    _newestPsdTextField = [[UITextField alloc] init];
    _newestPsdTextField.borderStyle = UITextBorderStyleNone;
    _newestPsdTextField.secureTextEntry = YES;
    UILabel *leftLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(0,0,170,45)];
    leftLabel2.text = WeXLocalizedString(@"新钱包密码:");
    leftLabel2.font = [UIFont systemFontOfSize:17];
    leftLabel2.textColor = COLOR_LABEL_DESCRIPTION;
    leftLabel2.backgroundColor = [UIColor clearColor];
    _newestPsdTextField.leftViewMode = UITextFieldViewModeAlways;
    _newestPsdTextField.leftView = leftLabel2;;
    _newestPsdTextField.textColor = COLOR_LABEL_DESCRIPTION;
    _newestPsdTextField.font = [UIFont systemFontOfSize:17];
    [self.view addSubview:_newestPsdTextField];
    [_newestPsdTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom);
        make.leading.equalTo(line1);
        make.trailing.equalTo(showBtn2.mas_leading);
        make.height.equalTo(@45);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = COLOR_ALPHA_LINE;
//    line2.alpha = LINE_VIEW_ALPHA;
    [self.view addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.top.equalTo(_newestPsdTextField.mas_bottom);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    //修改钱包密码按钮
    UIButton *modifyBtn = [WeXCustomButton button];
    [modifyBtn setTitle:WeXLocalizedString(@"修改钱包密码") forState:UIControlStateNormal];
    [modifyBtn addTarget:self action:@selector(modifyBtnClick) forControlEvents:UIControlEventTouchUpInside];
    modifyBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:modifyBtn];
    [modifyBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-40);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@40);
    }];
}

- (void)showBtn1Click:(UIButton *)btn{
    btn.selected = !btn.selected;
    _oldPsdTextField.secureTextEntry = !btn.selected;
}

- (void)showBtn2Click:(UIButton *)btn{
    btn.selected = !btn.selected;
    _newestPsdTextField.secureTextEntry = !btn.selected;
}

- (void)modifyBtnClick{
    
     //密码长度错误
    if (self.oldPsdTextField.text.length<8|| self.oldPsdTextField.text.length>20) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"密码长度为8-20位!") onView:self.view];
        return;
    }
     //密码长度错误
    if (self.newestPsdTextField.text.length<8|| self.newestPsdTextField.text.length>20) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"密码长度为8-20位!") onView:self.view];
        return;
    }
    //密码验证错误
    _model = [WexCommonFunc getPassport];
    if (![self.oldPsdTextField.text isEqualToString:_model.passportPassword]) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"您的密码有误!") onView:self.view];
        return;
    }
    
    [self configLocalSafetyView];
}

#pragma mark - TextField代理方法
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self.view endEditing:YES];
    return YES;
}

-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    
    NSString *comment;
    if(range.length == 0)
    {
        comment = [NSString stringWithFormat:@"%@%@",textField.text, string];
        if (comment.length > 20) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"密码长度最多为20位") onView:self.view];
            return NO;
        }
    }
    else
    {
        comment = [textField.text substringToIndex:textField.text.length -range.length];
    }
    NSLog(@"comment=%@",comment);
    return YES;
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

- (void)createSuccessFloatView{
    
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response) {
            NSLog(@"nitProvide=%@",response);
            [[WXPassHelper instance] keystoreCreateWithPrivateKey:_model.walletPrivateKey password:self.newestPsdTextField.text responseBlock:^(id response) {
                
                [WeXPorgressHUD hideLoading];
                
                _model = [WexCommonFunc getPassport];
                _model.keyStore = response;
                _model.passportPassword = self.newestPsdTextField.text;
                [WexCommonFunc savePassport:_model];
                
//                _successView = [[WeXPasswordModifySuccessFloatView alloc] initWithFrame:self.view.bounds];
//                _successView.delegate = self;
//                [self.view addSubview:_successView];
                [self addPromptView];
                
                _oldPsdTextField.text = @"";
                _newestPsdTextField.text = @"";
                
            }];
            
        }];
    }];
    
    
    
  
  
}


- (void)configLocalSafetyView{
    
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    
    if (model.passwordType == WeXPasswordTypeNone)
    {
        [self createSuccessFloatView];
    }
    else
    {
        WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
        manager.delegate = self;
        [manager loadPassword];
        _manager = manager;
        
    }
    
}

#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerVerifySuccess{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self createSuccessFloatView];

    });
    
}

//点击备份
- (void)passwordModifySuccessFloatViewDidBackup{
    WeXPassportBackupViewController *ctrl = [[WeXPassportBackupViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}


//弹框提示
- (void)addPromptView{
    if (_blackView) {
        return ;
    }
    UIView *backT = [[UIView alloc]init];
    //  backT.backgroundColor = [UIColor blackColor];
    backT.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    backT.frame =  CGRectMake(0, 0, kScreenWidth, kScreenHeight);
    self.blackView = backT;
    [self.view addSubview:backT];
    
    _promptView = [[WeXPromptAddressView alloc]init];
    [self.blackView addSubview:_promptView];
    _promptView.titleStr = WeXLocalizedString(@"修改成功");
    _promptView.contentStr = WeXLocalizedString(@"钱包密码修改成功,KEYSTORE信息变更,建议您立即重新备份钱包。");
    _promptView.cancelBtnStr = WeXLocalizedString(@"暂不备份");
    _promptView.sureBtnStr = WeXLocalizedString(@"备份钱包");
    __weak __typeof(self)weakSelf = self;
    _promptView.cancelBtnBlock = ^{
        [weakSelf removePromptView];
    };
    _promptView.sureBtnBlock = ^{
        [weakSelf removePromptView];
        [weakSelf passwordModifySuccessFloatViewDidBackup];
        
    };
    _promptView.frame = CGRectMake(52, (kScreenHeight-180)/2, kScreenWidth-104, 180);
    
}

//移除弹框提示
- (void)removePromptView{
    [self.blackView removeFromSuperview];
    self.blackView = nil;
}



@end
