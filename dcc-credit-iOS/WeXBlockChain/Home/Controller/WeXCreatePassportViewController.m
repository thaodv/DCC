//
//  WeXCreatePassportViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCreatePassportViewController.h"
#import "WeXRegisterSuccessViewController.h"
#import "YYKeyboardManager.h"
#import "WeXGetTicketAdapter.h"
#import "WeXGetContractAddressAdapter.h"
#import "WeXUploadPubKeyAdapter.h"
#import "WeXGetReceiptResultAdapter.h"
#import "WeXGetPubKeyAdapter.h"

@interface WeXCreatePassportViewController ()<UITextFieldDelegate,YYKeyboardObserver,CAAnimationDelegate>
{
    
    RSA *_publicKey;
    RSA *_privateKey;
    
    NSString *_rsaPublicKey;
    NSString *_rsaPrivateKey;
    NSString *_walletAddress;//钱包地址
    NSString *_walletPrivateKey;//钱包私钥
    NSDictionary *_keyStroe;//口袋
    
    NSString *_rawTransaction;
    
    NSString *_txHash;
    
    NSString *_contractAddress;//合约地址
    
    NSInteger _requestCount;//查询上链结果请求的次数
}

@property (nonatomic,strong)UITextField *passwordTextField;

@property (nonatomic,strong)UITextField *invitationTextField;

@property (nonatomic,strong)UIButton *graphBtn;

@property (nonatomic,strong)WeXGetTicketAdapter *getTicketAdapter;
@property (nonatomic,strong)WeXGetContractAddressAdapter *getContractAddressAdapter;
@property (nonatomic,strong)WeXUploadPubKeyAdapter *uoloadPubKeyAdapter;
@property (nonatomic,strong)WeXGetReceiptResultAdapter *getReceiptAdapter;
@property (nonatomic,strong)WeXGetTicketResponseModal *getTicketModel;
@property (nonatomic,strong)WeXGetPubKeyAdapter *getPubKeyAdapter;

@end

@implementation WeXCreatePassportViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = NSLocalizedString(@"WeXCreatePassportViewController_navTitle", @"");
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}


- (void)setupSubViews{
    
//    UIView *desTitleBackView = [[UIView alloc] init];
//    desTitleBackView.backgroundColor = [UIColor lightGrayColor];
//    desTitleBackView.alpha = LINE_VIEW_ALPHA;
//    [self.view addSubview:desTitleBackView];
//    [desTitleBackView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.leading.trailing.equalTo(self.view).offset(15);
//        make.top.equalTo(self.view).offset(kNavgationBarHeight);
//        make.height.equalTo(@60);
//    }];
    
    UILabel *desTitleLabel = [[UILabel alloc] init];
    desTitleLabel.text = NSLocalizedString(@"WeXCreatePassportViewController_description1", @"");
    desTitleLabel.font = [UIFont systemFontOfSize:15];
    desTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    desTitleLabel.backgroundColor = COLOR_LABEL_DES_BACKGROUND;
    desTitleLabel.textAlignment = NSTextAlignmentCenter;
    desTitleLabel.numberOfLines = 0;
    [self.view addSubview:desTitleLabel];
    [desTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.height.equalTo(@70);
    }];
    
    
    //密码输入框
    _passwordTextField = [[UITextField alloc] init];
    _passwordTextField.delegate = self;
    _passwordTextField.textColor = [UIColor lightGrayColor];
    _passwordTextField.backgroundColor = [UIColor whiteColor];
    _passwordTextField.borderStyle = UITextBorderStyleNone;
    _passwordTextField.keyboardType = UIKeyboardTypeASCIICapable;
    _passwordTextField.secureTextEntry = YES;
    _passwordTextField.placeholder = NSLocalizedString(@"WeXCreatePassportViewController_passwordTextFieldPlacehoder", @"");
    _passwordTextField.font = [UIFont systemFontOfSize:17];
    [self.view addSubview:_passwordTextField];
    [_passwordTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(desTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(15);
        make.height.equalTo(@50);
    }];
    
    UIButton *showBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [showBtn setImage:[UIImage imageNamed:@"eye2"] forState:UIControlStateNormal];
    [showBtn setImage:[UIImage imageNamed:@"eye1"] forState:UIControlStateSelected];
    [showBtn setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
    [showBtn addTarget:self action:@selector(showBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    showBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.view addSubview:showBtn];
    [showBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(_passwordTextField);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@30);
        make.height.equalTo(@40);
        make.leading.equalTo(_passwordTextField.mas_trailing).offset(-10);
    }];

    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = COLOR_ALPHA_LINE;
    [self.view addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(_passwordTextField.mas_bottom);
        make.height.equalTo(@HEIGHT_LINE);
    }];
//
    UILabel *desCentreLabel = [[UILabel alloc] init];
    desCentreLabel.text = NSLocalizedString(@"WeXCreatePassportViewController_description2", @"");
    desCentreLabel.font = [UIFont systemFontOfSize:15];
    desCentreLabel.textColor = COLOR_LABEL_DESCRIPTION;
    desCentreLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:desCentreLabel];
    [desCentreLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(line1.mas_bottom).offset(5);
        make.height.equalTo(@20);
    }];


//    //图形码输入框
    _invitationTextField = [[UITextField alloc] init];
    _invitationTextField.delegate = self;
    _invitationTextField.textColor = [UIColor lightGrayColor];
    _invitationTextField.backgroundColor = [UIColor whiteColor];
    _invitationTextField.borderStyle = UITextBorderStyleNone;
    _invitationTextField.keyboardType = UIKeyboardTypeASCIICapable;
    _invitationTextField.placeholder = NSLocalizedString(@"WeXCreatePassportViewController_invitationTextFieldPlacehoder", @"");
    [self.view addSubview:_invitationTextField];
    [_invitationTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(desCentreLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@50);
    }];


    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = COLOR_ALPHA_LINE;
    [self.view addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(_invitationTextField.mas_bottom);
        make.height.equalTo(@HEIGHT_LINE);
    }];

    UIButton *createBtn = [WeXCustomButton button];
    [createBtn setTitle:NSLocalizedString(@"WeXCreatePassportViewController_createButtonTitle", @"") forState:UIControlStateNormal];
    [createBtn addTarget:self action:@selector(createBtnClick) forControlEvents:UIControlEventTouchUpInside];
    createBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:createBtn];
    [createBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(line2.mas_bottom).offset(10);
        make.height.equalTo(@40);
    }];
    
    UILabel *desBottomLabel = [[UILabel alloc] init];
    desBottomLabel.text = NSLocalizedString(@"WeXCreatePassportViewController_description3", @"");
    desBottomLabel.font = [UIFont systemFontOfSize:15];
    desBottomLabel.textColor = COLOR_LABEL_DESCRIPTION;
    desBottomLabel.textAlignment = NSTextAlignmentLeft;
    desBottomLabel.numberOfLines = 0;
    [self.view addSubview:desBottomLabel];
    [desBottomLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(createBtn.mas_bottom).offset(5);
    }];
    
}



- (void)showBtnClick:(UIButton *)btn{
    btn.selected = !btn.selected;
    _passwordTextField.secureTextEntry = !btn.selected;
}


- (void)createBtnClick{
    
    
    if (_passwordTextField.text.length < 8) {
        [WeXPorgressHUD showText:@"密码长度应大于8位!" onView:self.view];
        return;
    }
    
    if (![WexCommonFunc checkoutString:_passwordTextField.text withRegularExpression:@"^[^\\u4e00-\\u9fa5]+$"]) {
        [WeXPorgressHUD showText:@"密码里面不能包含汉字!" onView:self.view];
        return;
    }
    
    
    [self.view endEditing:YES];
    
    [WeXPorgressHUD showLoadingAddedTo:self.view.window];
    
    [self createGetContractAddressRequest];
    
    
}


- (void)createRSA{
    if ([DDRSAWrapper generateRSAKeyPairWithKeySize:2048 publicKey:&_publicKey privateKey:&_privateKey]) {
        NSString * publicKeyBase64 = [DDRSAWrapper base64EncodedStringPublicKey:_publicKey];
        publicKeyBase64 = [WexCommonFunc stringRemoveSpaceWithString:publicKeyBase64];
        NSData *publicKeyData =  [[NSData alloc] initWithBase64EncodedString:publicKeyBase64 options:0];
        _rsaPublicKey = [WexCommonFunc hexStringWithData:publicKeyData];
        
        NSString * privateKeyBase64 = [DDRSAWrapper base64EncodedStringPrivateKey:_privateKey];
        privateKeyBase64 = [WexCommonFunc stringRemoveSpaceWithString:privateKeyBase64];
        NSData *privateKeyData =  [[NSData alloc] initWithBase64EncodedString:privateKeyBase64 options:0];
        _rsaPrivateKey = [WexCommonFunc hexStringWithData:privateKeyData];
        
    }
}


- (void)initPassHelper{
    
    [self createRSA];
    
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            return;
        }
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response) {
            [self cretePass];
        }];
    }];
    
}

- (void)cretePass{
    /** 创建钱包privateKey */
    [[WXPassHelper instance] createPrivateKeyBlock:^(id response) {
        if (response) {
            _walletPrivateKey = response;
            
            if ([_walletPrivateKey hasPrefix:@"0x"]) {
                _walletPrivateKey=[_walletPrivateKey substringFromIndex:2];
            }
        }
        // 合约定义说明
        NSString* abiJson=@"{'constant':false,'inputs':[{'name':'_publickey','type':'bytes'}],'name':'putKey','outputs':[{'name':'_result','type':'bool'}],'payable':false,'stateMutability':'nonpayable','type':'function'}";
        //        // 合约参数值
        NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_rsaPublicKey]; // 这里代表存放是自己的RSA公钥
        // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
        NSString* abiAddress=_contractAddress;
        // 以太坊私钥地址
        NSString* privateKey=_walletPrivateKey;
        [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:privateKey responseBlock:^(id response) {
            _rawTransaction = response;
            //上传本地pubkey
            [self createUploadPubKeyRequest];
            
        }];
        
    }];
}

#pragma -mark 发送请求
- (void)createGetTicketRequest{
    _getTicketAdapter = [[WeXGetTicketAdapter alloc] init];
    _getTicketAdapter.delegate = self;
    WeXGetTicketParamModal* paramModal = [[WeXGetTicketParamModal alloc] init];
    [_getTicketAdapter run:paramModal];
}

#pragma -mark 发送获取合约地址请求
- (void)createGetContractAddressRequest{
    _getContractAddressAdapter = [[WeXGetContractAddressAdapter alloc] init];
    _getContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    [_getContractAddressAdapter run:paramModal];
}

#pragma -mark 获取公钥请求
- (void)createGetPubKyeRequest{
    _getPubKeyAdapter = [[WeXGetPubKeyAdapter alloc] init];
    _getPubKeyAdapter.delegate = self;
    WeXGetPubKeyParamModal* paramModal = [[WeXGetPubKeyParamModal alloc] init];
    paramModal.address = _walletAddress;
    [_getPubKeyAdapter run:paramModal];
}

#pragma -mark 上传公钥请求
- (void)createUploadPubKeyRequest{
    _uoloadPubKeyAdapter = [[WeXUploadPubKeyAdapter alloc] init];
    _uoloadPubKeyAdapter.delegate = self;
    WeXUploadPubKeyParamModal* paramModal = [[WeXUploadPubKeyParamModal alloc] init];
    paramModal.ticket = _getTicketModel.ticket;
    paramModal.signMessage = _rawTransaction;
//    paramModal.code = _registerView.graphTextField.text;
    [_uoloadPubKeyAdapter run:paramModal];
    
}

#pragma -mark 查询上链结果请求
- (void)createReceiptResultRequest{
    _getReceiptAdapter = [[WeXGetReceiptResultAdapter alloc] init];
    _getReceiptAdapter.delegate = self;
    WeXGetReceiptResultParamModal* paramModal = [[WeXGetReceiptResultParamModal alloc] init];
    paramModal.txHash = _txHash;
    [_getReceiptAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _contractAddress = model.result;
            //合约地址请求成功 然后开始初始化passhelper
            [self initPassHelper];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
    }
    else if (adapter == _uoloadPubKeyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXUploadPubKeyResponseModal *model = (WeXUploadPubKeyResponseModal *)response;
            _txHash = model.result;
            [self createReceiptResultRequest];
        }
        else if([headModel.businessCode isEqualToString:@"CHALLENGE_FAILURE"])
        {
            [WeXPorgressHUD hideLoading];
            
            [WeXPorgressHUD showText:@"验证码校验失败!" onView:self.view];
        }
        else if([headModel.businessCode isEqualToString:@"TICKET_INVALID"])
        {
            [WeXPorgressHUD hideLoading];
            
            [WeXPorgressHUD showText:@"验证码超时!" onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            
            [WeXPorgressHUD showText:@"系统错误，请稍后再试" onView:self.view];
        }
    }
    else if (adapter == _getReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetReceiptResultResponseModal *model = (WeXGetReceiptResultResponseModal *)response;
            //上链成功
            if ([model.result isEqualToString:@"1"]) {
                /** 生成keystore json */
                [[WXPassHelper instance] keystoreCreateWithPrivateKey:_walletPrivateKey password:_passwordTextField.text responseBlock:^(id response) {
                    NSLog(@"keystore json=%@",response); // 返回keystore json
                    _keyStroe = response;
                    _walletAddress = [_keyStroe objectForKey:@"address"];
                    [self createGetPubKyeRequest];
                    
                }];
            }
            else
            {
                if (_requestCount > 4) {
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:@"创建口袋失败" onView:self.view];
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createReceiptResultRequest];
                    _requestCount++;
                });
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
        
        
    }
    else if (adapter == _getPubKeyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXGetPubKeyResponseModal *model = (WeXGetPubKeyResponseModal *)response;
            NSData *publicKeyData =  [[NSData alloc] initWithBase64EncodedString:model.result options:0];
            NSString *resultPublickKey  = [WexCommonFunc hexStringWithData:publicKeyData];
            //相等表示口袋创建成功
            if ([resultPublickKey isEqualToString:_rsaPublicKey]) {
                [self savePassport];
                
                //保存统一操作记录
                [WexCommonFunc saveManagerRecordWithTypeString:@"启用统一登录"];
                
                WeXRegisterSuccessViewController *ctrl = [[WeXRegisterSuccessViewController alloc] init];
                ctrl.type = WeXRegisterSuccessTypeCreate;
                ctrl.isFromAuthorize = self.isFromAuthorize;
                ctrl.url = self.url;
                [self.navigationController pushViewController:ctrl animated:YES];
            }
            else
            {
                [WeXPorgressHUD showText:@"创建口袋失败" onView:self.view];
            }
            
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
        
    }
}



#pragma mark -保存口袋信息
- (void)savePassport
{
    WeXPasswordCacheModal *model = [WeXPasswordCacheModal sharedInstance];
    model.rsaPublicKey = _rsaPublicKey;
    model.rasPrivateKey = _rsaPrivateKey;
    model.passportPassword = _passwordTextField.text;
    model.walletPrivateKey = _walletPrivateKey;
    model.keyStore = _keyStroe;
    model.isAllow = YES;
    [WexCommonFunc savePassport:model];
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
        
        if (textField == _passwordTextField) {
            if (comment.length > 20) {
                [WeXPorgressHUD showText:@"密码长度最多为20位" onView:self.view];
                return NO;
            }
        }
        else if (textField == _invitationTextField){
            if (comment.length > 4) {
                [WeXPorgressHUD showText:@"长度最多为4位" onView:self.view];
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





@end
