//
//  WeXInviteCodeViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXInviteCodeViewController.h"
#import "WeXBorrowGetNonceAdapter.h"
#import "WeXRegisterMemberAdapter.h"
#import "WeXScanViewController.h"

@interface WeXInviteCodeViewController ()<UITextFieldDelegate>
{
    UITextField *_codeTextField;
    NSString *_nonce;
}

@property (nonatomic,strong)WeXBorrowGetNonceAdapter *getNonceAdapter;
@property (nonatomic,strong)WeXRegisterMemberAdapter *registerAdapter;

@end
@implementation WeXInviteCodeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"填写邀请码");
    self.navigationItem.hidesBackButton = YES;
    [self setupSubViews];
    [self setupNavgationType];
}

- (void)setupNavgationType{
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setTitle:WeXLocalizedString(@"跳过") forState:UIControlStateNormal];
    btn.frame = CGRectMake(0, 0, 50, 20);
    [btn setTitleColor:COLOR_THEME_ALL forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(rightBtnClick) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithCustomView:btn];
    self.navigationItem.rightBarButtonItem = rihgtItem;
}

- (void)rightBtnClick
{
    _codeTextField.text = nil;
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self createGetNonceRequest];
}

- (void)scanBtnClick{
    WeXScanViewController *ctrl = [[WeXScanViewController alloc]  init];
    ctrl.handleType = WeXScannerHandleTypeManagerInviteFriend;
    ctrl.responseBlock = ^(NSString *content) {
        if (content) {
            _codeTextField.text = content;
        }
    };
    [self.navigationController pushViewController:ctrl animated:YES];
}

#pragma -mark 发送请求
- (void)createGetNonceRequest{
    _getNonceAdapter = [[WeXBorrowGetNonceAdapter alloc] init];
    _getNonceAdapter.delegate = self;
    WeXBorrowGetNonceParamModal* paramModal = [[WeXBorrowGetNonceParamModal alloc] init];
    [_getNonceAdapter run:paramModal];
}

#pragma -mark 获取数据发送请求
- (void)createRegisterRequest{
    _registerAdapter = [[WeXRegisterMemberAdapter alloc] init];
    _registerAdapter.delegate = self;
    WeXRegisterMemberParamModal *model = [[WeXRegisterMemberParamModal alloc] init];
    model.nonce = _nonce;
    model.address = [WexCommonFunc getFromAddress];
    model.loginName = [WexCommonFunc getFromAddress];
    model.inviteCode = _codeTextField.text;
    NSLog(@"text=%d",[_codeTextField.text isEqualToString:@""]);
    [_registerAdapter run:model];
    
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getNonceAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXBorrowGetNonceResponseModal *model = (WeXBorrowGetNonceResponseModal *)response;
            NSLog(@"model=%@",model);
            _nonce = model.result;
            if (_nonce) {
                [self createRegisterRequest];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:[UIApplication sharedApplication].keyWindow];
        }
    }
 else  if (adapter == _registerAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            
            WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
            model.hasMemberId = YES;
            [WexCommonFunc savePassport:model];
            
            WeXRegisterSuccessViewController *ctrl = [[WeXRegisterSuccessViewController alloc] init];
            ctrl.type = self.type;
            [self.navigationController pushViewController:ctrl animated:YES];
            
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"])
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    
    
}

//初始化滚动视图
-(void)setupSubViews{
    
    _codeTextField = [[UITextField alloc] init];
    _codeTextField.delegate = self;
    _codeTextField.textColor = [UIColor lightGrayColor];
    _codeTextField.backgroundColor = [UIColor clearColor];
    _codeTextField.borderStyle = UITextBorderStyleNone;
    _codeTextField.font = [UIFont systemFontOfSize:17];
    _codeTextField.placeholder = WeXLocalizedString(@"请输入邀请码");
    _codeTextField.leftViewMode = UITextFieldViewModeAlways;
    [self.view addSubview:_codeTextField];
    [_codeTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-50);
        make.height.equalTo(@50);
    }];
    
    //扫一扫按钮
    UIButton *scanBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [scanBtn setImage:[UIImage imageNamed:@"scan"] forState:UIControlStateNormal];
    [scanBtn addTarget:self action:@selector(scanBtnClick) forControlEvents:UIControlEventTouchUpInside];
    scanBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:scanBtn];
    [scanBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(_codeTextField).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
        make.width.equalTo(@30);
        make.height.equalTo(@30);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = COLOR_ALPHA_LINE;
    [self.view addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(_codeTextField.mas_bottom).offset(5);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *desTitleLabel = [[UILabel alloc] init];
    desTitleLabel.text = WeXLocalizedString(@"输入邀请码可以获得更多奖励");
    desTitleLabel.font = [UIFont systemFontOfSize:15];
    desTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    desTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:desTitleLabel];
    [desTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(line2.mas_bottom).offset(5);
        make.height.equalTo(@20);
    }];
    //
    UIButton *authenBtn = [WeXCustomButton button];
    [authenBtn setTitle:WeXLocalizedString(@"确定") forState:UIControlStateNormal];
    [authenBtn addTarget:self action:@selector(authenBtnClick) forControlEvents:UIControlEventTouchUpInside];
    authenBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:authenBtn];
    [authenBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(desTitleLabel.mas_bottom).offset(30);
        make.height.equalTo(@40);
    }];
    
}

- (void)authenBtnClick
{
    [self.view endEditing:YES];
    
    if (![self verifyJumpCondition]) return;
    
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    
    [self createGetNonceRequest];
}

- (BOOL)verifyJumpCondition
{
    
    if (!_codeTextField.text||_codeTextField.text.length == 0)
    {
        [WeXPorgressHUD showText:WeXLocalizedString(@"邀请码不能为空") onView:self.view];
        return NO;
    }
    
    if (_codeTextField.text.length > 10)
    {
        [WeXPorgressHUD showText:WeXLocalizedString(@"邀请码格式不对") onView:self.view];
        return NO;
    }
    
    return YES;
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}



@end
