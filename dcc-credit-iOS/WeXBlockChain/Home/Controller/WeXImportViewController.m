//
//  WeXImportViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/13.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXImportViewController.h"
#import "WeXRegisterSuccessViewController.h"
#import "WeXGetTicketAdapter.h"
#import "WeXGetContractAddressAdapter.h"
#import "YYKeyboardManager.h"
#import "WeXScanViewController.h"
#import "WeXUploadPubKeyAdapter.h"
#import "WeXGetReceiptResultAdapter.h"
#import "WeXGetPubKeyAdapter.h"

#import "WeXGetMemberIdAdapter.h"
#import "WeXBorrowGetNonceAdapter.h"
#import "WeXInviteCodeViewController.h"

#define kTitleButtonHeight 50
typedef NS_ENUM(NSInteger,WeXImportPassportType) {
    WeXImportPassportTypeKeyStore,
    WeXImportPassportTypePrivateKey
};

@interface WeXImportViewController ()<YYKeyboardObserver,UITextViewDelegate,UITextFieldDelegate>
{
    UITextView *_contentTextView;
    
    RSA *_publicKey;
    RSA *_privateKey;
    
    NSString *_rsaPublicKey;
    NSString *_rsaPrivateKey;
    NSString *_walletAddress;//钱包地址
    NSString *_walletPrivateKey;//钱包私钥
    NSDictionary *_keyStroe;//钱包
    
    NSString *_rawTransaction;
    
    NSString *_txHash;
    
    NSString *_contractAddress;//合约地址
        
    NSInteger _requestCount;//查询上链结果请求的次数
    
    UILabel *_centreDescriptionLabel;
    UILabel *_titleDescriptionLabel;
    
    UIButton *_showBtn;
    UIImageView *_backImageView;
    
    NSString *_nonce;
}

@property (nonatomic,assign)WeXImportPassportType importType;

@property (nonatomic,strong)UIButton *keyStroeBtn;

@property (nonatomic,strong)UIButton *privateKeyBtn;

@property (strong, nonatomic) UIView *moveLine;//tab下划线

@property (nonatomic,strong)UITextField *passwordTextField;

@property (nonatomic,strong)UITextField *invitationTextField;

@property (nonatomic,strong)WeXGetTicketAdapter *getTicketAdapter;
@property (nonatomic,strong)WeXGetContractAddressAdapter *getContractAddressAdapter;
@property (nonatomic,strong)WeXUploadPubKeyAdapter *uoloadPubKeyAdapter;
@property (nonatomic,strong)WeXGetReceiptResultAdapter *getReceiptAdapter;
@property (nonatomic,strong)WeXGetPubKeyAdapter *getPubKeyAdapter;
@property (nonatomic,strong)WeXGetTicketResponseModal *getTicketModel;

@property (nonatomic,strong)WeXBorrowGetNonceAdapter *getNonceAdapter;

@property (nonatomic,strong)WeXGetMemberIdAdapter *getMemberAdapter;


//图形码按钮
@property (nonatomic,strong)UIButton *graphBtn;

@property (nonatomic,strong) UILabel *descriptionLabel;//粘贴框中提示label;

@property (nonatomic,strong) UILabel *passwordLeftLabel;//密码输入框左侧label;

@end

@implementation WeXImportViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"导入已有钱包");
    [[YYKeyboardManager defaultManager] addObserver:self];
    self.importType = WeXImportPassportTypeKeyStore;
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}

- (void)initPassHelper{
    //创建RSA
    [self createRSA];
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response) {
            NSLog(@"nitProvide=%@",response);
            [self cretePass];
        }];
    }];
    
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

- (void)cretePass{
    // 合约定义说明
    NSString* abiJson=@"{'constant':false,'inputs':[{'name':'_publickey','type':'bytes'}],'name':'putKey','outputs':[{'name':'_result','type':'bool'}],'payable':false,'stateMutability':'nonpayable','type':'function'}";
    //合约参数值 RSA公钥
    NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_rsaPublicKey];
    // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
    NSString* abiAddress=_contractAddress;
    //导入类型为keystore
    if (self.importType == WeXImportPassportTypeKeyStore) {
        NSString *keyStore = _contentTextView.text;
        /** 恢复钱包私钥 */
        [[WXPassHelper instance] restoreAccountFromKeyStore:keyStore password:_passwordTextField.text responseBlock:^(id response) {
             NSLog(@"response=%@",response);
            //导入失败
            if([response isKindOfClass:[NSDictionary class]])
            {
                [WeXPorgressHUD hideLoading];
                [WeXPorgressHUD showText:WeXLocalizedString(@"KeyStore信息或钱包密码错误") onView:self.view];
                return;
            };
            //导入成功
            _walletPrivateKey = response;
            [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:_walletPrivateKey responseBlock:^(id response) {
                _rawTransaction = response;
                //上传本地pubkey
                [self createUploadPubKeyRequest];
            }];
        }];
    }
    //导入类型为PrivateKey
    else if (self.importType == WeXImportPassportTypePrivateKey){
        /** 判错误处理 */
        [[WXPassHelper instance] keystoreCreateWithPrivateKey:_contentTextView.text password:_passwordTextField.text responseBlock:^(id response) {
            NSLog(@"response=%@",response);
            //导入失败
            NSString *hasError = [response objectForKey:@"ethException"];
            if(hasError.length > 0)
            {
                [WeXPorgressHUD hideLoading];
                [WeXPorgressHUD showText:WeXLocalizedString(@"私钥明文格式不对!") onView:self.view];
                return;
            };
        }];
        _walletPrivateKey = _contentTextView.text;
        [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:_walletPrivateKey responseBlock:^(id response) {
            _rawTransaction = response;
            //上传本地pubkey
            [self createUploadPubKeyRequest];
        }];
    }
 
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
    paramModal.code = _invitationTextField.text;
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

#pragma -mark 发送请求
- (void)createGetNonceRequest{
    _getNonceAdapter = [[WeXBorrowGetNonceAdapter alloc] init];
    _getNonceAdapter.delegate = self;
    WeXBorrowGetNonceParamModal* paramModal = [[WeXBorrowGetNonceParamModal alloc] init];
    [_getNonceAdapter run:paramModal];
}

#pragma -mark 获取数据发送请求
- (void)createGetMemberRequest{
    _getMemberAdapter = [[WeXGetMemberIdAdapter alloc] init];
    _getMemberAdapter.delegate = self;
    WeXGetMemberIdParamModal *modal = [[WeXGetMemberIdParamModal alloc] init];
    modal.nonce = _nonce;
    modal.address = [WexCommonFunc getFromAddress];
    [_getMemberAdapter run:modal];
    
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getTicketAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            _getTicketModel = (WeXGetTicketResponseModal *)response;
            [self initPassHelper];

        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _contractAddress = model.result;
            //合约地址请求成功 然后开始初始化passhelper
            [self createGetTicketRequest];

        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
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
            [WeXPorgressHUD showText:WeXLocalizedString(@"验证码不正确!") onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetReceiptResultResponseModal *model = (WeXGetReceiptResultResponseModal *)response;
            //上链成功
            if ([model.result isEqualToString:@"1"]) {
                //导入类型为keystore
                if (self.importType == WeXImportPassportTypeKeyStore) {
                    NSString *keyStore = _contentTextView.text;
                    NSData *data = [keyStore dataUsingEncoding:NSUTF8StringEncoding];
                    _keyStroe  = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
                    _walletAddress = [_keyStroe objectForKey:@"address"];
                    [self createGetPubKyeRequest];
                }
                //导入类型为PrivateKey
                else if (self.importType == WeXImportPassportTypePrivateKey){
                    /** 生成keystore json */
                    [[WXPassHelper instance] keystoreCreateWithPrivateKey:_walletPrivateKey password:_passwordTextField.text responseBlock:^(id response) {
                        NSLog(@"keystore json=%@",response); // 返回keystore json
                        _keyStroe = response;
                        _walletAddress = [_keyStroe objectForKey:@"address"];
                        [self createGetPubKyeRequest];
                    }];
                }
            }
            else
            {
                //超过四次查询没有成功，调到钱包页面。设置状态为不可用
                if (_requestCount > 4) {
                    [WeXPorgressHUD hideLoading];
                    [self savePassport:NO];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"导入失败!") onView:self.view];
                }
                else{
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [self createReceiptResultRequest];
                        _requestCount++;
                    });
                }
                
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
        
        
    }
    else if (adapter == _getPubKeyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetPubKeyResponseModal *model = (WeXGetPubKeyResponseModal *)response;
            NSData *publicKeyData =  [[NSData alloc] initWithBase64EncodedString:model.result options:0];
            NSString *resultPublickKey  = [WexCommonFunc hexStringWithData:publicKeyData];
            //相等表示钱包创建成功
            if ([resultPublickKey isEqualToString:_rsaPublicKey]) {
                [self savePassport:YES];
                //保存统一操作记录
                [WexCommonFunc saveManagerRecordWithTypeString:WeXLocalizedString(@"启用统一登录")];
            }
            else
            {
                [self savePassport:NO];
            }
            
            [self createGetNonceRequest];
          
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
   else if (adapter == _getNonceAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXBorrowGetNonceResponseModal *model = (WeXBorrowGetNonceResponseModal *)response;
            NSLog(@"model=%@",model);
            _nonce = model.result;
            if (_nonce) {
                [self createGetMemberRequest];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:[UIApplication sharedApplication].keyWindow];
        }
    }
    else if (adapter == _getMemberAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXGetMemberIdResponseModal *model = (WeXGetMemberIdResponseModal *)response;
            WEXNSLOG(@"%@",model);
            if (model.memberId&&model.memberId.length > 0) {
                WeXRegisterSuccessViewController *ctrl = [[WeXRegisterSuccessViewController alloc] init];
                ctrl.type = WeXRegisterSuccessTypeImport;
                [self.navigationController pushViewController:ctrl animated:YES];
                
                WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
                model.hasMemberId = YES;
                [WexCommonFunc savePassport:model];
            }
            else
            {
                WeXInviteCodeViewController *ctrl = [[WeXInviteCodeViewController alloc] init];
                ctrl.type = WeXRegisterSuccessTypeImport;
                [self.navigationController pushViewController:ctrl animated:YES];
            }
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

#pragma mark -保存钱包信息
- (void)savePassport:(BOOL)isAllow
{
    WeXPasswordCacheModal *model = [WeXPasswordCacheModal sharedInstance];
    model.rsaPublicKey = _rsaPublicKey;
    model.rasPrivateKey = _rsaPrivateKey;
    model.passportPassword = _passwordTextField.text;
    model.walletPrivateKey = _walletPrivateKey;
    model.keyStore = _keyStroe;
    model.isAllow = isAllow;
    [WexCommonFunc savePassport:model];
}

//初始化滚动视图
-(void)setupSubViews{
    //KEYSTORE按钮
    _keyStroeBtn =[UIButton buttonWithType:UIButtonTypeCustom];
    _keyStroeBtn.frame = CGRectMake(0, kNavgationBarHeight, kScreenWidth/2, kTitleButtonHeight);
    _keyStroeBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [_keyStroeBtn setTitle:@"KeyStore" forState:UIControlStateNormal];
    [_keyStroeBtn setTitleColor:COLOR_THEME_ALL forState:UIControlStateSelected];
    [_keyStroeBtn setTitleColor:COLOR_LABEL_DESCRIPTION forState:UIControlStateNormal];
    _keyStroeBtn.backgroundColor = [UIColor clearColor];
    [_keyStroeBtn addTarget:self action:@selector(keyStroeBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_keyStroeBtn];
    //私钥明文按钮
    _privateKeyBtn =[UIButton buttonWithType:UIButtonTypeCustom];
    _privateKeyBtn.frame = CGRectMake(kScreenWidth/2, kNavgationBarHeight, kScreenWidth/2, kTitleButtonHeight);
    _privateKeyBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [_privateKeyBtn setTitle:WeXLocalizedString(@"私钥明文") forState:UIControlStateNormal];
    [_privateKeyBtn setTitleColor:COLOR_THEME_ALL forState:UIControlStateSelected];
    [_privateKeyBtn setTitleColor:COLOR_LABEL_DESCRIPTION forState:UIControlStateNormal];
    _privateKeyBtn.backgroundColor = [UIColor clearColor];
    [_privateKeyBtn addTarget:self action:@selector(privateKeyBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_privateKeyBtn];
 
    
    //创建移动下划线
    UIView *line = [[UIView alloc] initWithFrame:CGRectMake(0,CGRectGetMaxY(_keyStroeBtn.frame)-1, kScreenWidth/2*0.6, 1)];
    line.backgroundColor = COLOR_THEME_ALL;
    line.WeX_centerX = _keyStroeBtn.WeX_centerX;
    [self.view addSubview:line];
    self.moveLine = line;
    //粘贴背景框
    UIImageView *backImageView = [[UIImageView alloc] init];
//    backImageView.image = [UIImage imageNamed:@"copyFrame"];
    backImageView.layer.cornerRadius = 12;
    backImageView.layer.masksToBounds = YES;
    backImageView.layer.borderWidth = 1;
    backImageView.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_keyStroeBtn.mas_bottom).offset(0);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@140);
    }];
    _backImageView = backImageView;
    
    
    //输入文本框
    UITextView *contentTextView = [[UITextView alloc] init];
    contentTextView.delegate = self;
    contentTextView.font = [UIFont systemFontOfSize:15];
    contentTextView.backgroundColor = [UIColor clearColor];
    contentTextView.textColor = COLOR_LABEL_DESCRIPTION;
    [self.view addSubview:contentTextView];
    [contentTextView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView).offset(10);
        make.leading.equalTo(backImageView).offset(10);
        make.trailing.equalTo(backImageView).offset(-5);
        make.bottom.equalTo(backImageView).offset(-30);
    }];
    
    _contentTextView = contentTextView;
    
    //粘贴框提示文字
    _descriptionLabel = [[UILabel alloc] init];
    _descriptionLabel.text = WeXLocalizedString(@"请粘贴KeyStore信息");
    _descriptionLabel.font = [UIFont systemFontOfSize:19];
    _descriptionLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _descriptionLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:_descriptionLabel];
    [_descriptionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.equalTo(backImageView);
        make.width.equalTo(backImageView);
        make.height.equalTo(@20);
    }];
    //扫一扫按钮
    UIButton *scanBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [scanBtn setImage:[UIImage imageNamed:@"scan"] forState:UIControlStateNormal];
    [scanBtn addTarget:self action:@selector(scanBtnClick) forControlEvents:UIControlEventTouchUpInside];
    scanBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:scanBtn];
    [scanBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(backImageView).offset(-8);
        make.trailing.equalTo(backImageView).offset(-13);
        make.width.equalTo(@30);
        make.height.equalTo(@30);
    }];
    
    
    UILabel *desTitleLabel = [[UILabel alloc] init];
    desTitleLabel.hidden = YES;
    desTitleLabel.text = WeXLocalizedString(@"WeXCreatePassportViewController_description1");
    desTitleLabel.font = [UIFont systemFontOfSize:15];
    desTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    desTitleLabel.backgroundColor = COLOR_LABEL_DES_BACKGROUND;
    desTitleLabel.textAlignment = NSTextAlignmentLeft;
    desTitleLabel.numberOfLines = 2;
    desTitleLabel.adjustsFontSizeToFitWidth = YES;
    [self.view addSubview:desTitleLabel];
    [desTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(backImageView.mas_bottom).offset(5);
        make.height.equalTo(@70);
    }];
    _titleDescriptionLabel = desTitleLabel;
    
    //显示密码按钮
    UIButton *showBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [showBtn setImage:[UIImage imageNamed:@"eye2"] forState:UIControlStateNormal];
    [showBtn setImage:[UIImage imageNamed:@"eye1"] forState:UIControlStateSelected];
    [showBtn setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
    [showBtn addTarget:self action:@selector(showBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    showBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.view addSubview:showBtn];
    [showBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView.mas_bottom).offset(15);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@30);
        make.height.equalTo(@45);
    }];
    _showBtn = showBtn;
    
    
  
    
    //密码输入框
    _passwordTextField = [[UITextField alloc] init];
    _passwordTextField.delegate = self;
    _passwordTextField.borderStyle = UITextBorderStyleNone;
    _passwordTextField.textColor = [UIColor lightGrayColor];
    _passwordTextField.secureTextEntry = YES;
    _passwordLeftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0,150,45)];
    _passwordLeftLabel.text = WeXLocalizedString(@"验证数字钱包密码:");
    _passwordLeftLabel.font = [UIFont systemFontOfSize:17];
    _passwordLeftLabel.textColor = [UIColor lightGrayColor];
    _passwordLeftLabel.backgroundColor = [UIColor clearColor];
    _passwordTextField.leftViewMode = UITextFieldViewModeAlways;
    _passwordTextField.font = [UIFont systemFontOfSize:17];
    _passwordTextField.leftView = _passwordLeftLabel;
    [self.view addSubview:_passwordTextField];
    [_passwordTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(showBtn).offset(0);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(showBtn.mas_leading);
        make.height.equalTo(@45);
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
    
    
    UILabel *descriptionLabel = [[UILabel alloc] init];
    descriptionLabel.hidden = YES;
    descriptionLabel.text = WeXLocalizedString(@"不少于8位字符，建议混合大小写字母、数字、特殊字符");
    descriptionLabel.adjustsFontSizeToFitWidth = YES;
    descriptionLabel.font = [UIFont systemFontOfSize:15];
    descriptionLabel.textColor = COLOR_LABEL_DESCRIPTION;
    descriptionLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:descriptionLabel];
    [descriptionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    _centreDescriptionLabel = descriptionLabel;
    

//    _invitationTextField = [[UITextField alloc] init];
//    _invitationTextField.borderStyle = UITextBorderStyleNone;
//    _invitationTextField.delegate = self;
//    _invitationTextField.textColor = COLOR_LABEL_DESCRIPTION;
//    UILabel *leftLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(0,0,125,45)];
//    leftLabel2.text = WeXLocalizedString(@"邀请码(可为空)");
//    leftLabel2.font = [UIFont systemFontOfSize:17];
//    leftLabel2.textColor = [UIColor lightGrayColor];
//    leftLabel2.backgroundColor = [UIColor clearColor];
//    _invitationTextField.leftViewMode = UITextFieldViewModeAlways;
//    _invitationTextField.font = [UIFont systemFontOfSize:17];
//    _invitationTextField.leftView = leftLabel2;;
//    [self.view addSubview:_invitationTextField];
//    [_invitationTextField mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(_passwordTextField.mas_bottom).offset(10);
//        make.leading.equalTo(self.view).offset(15);
//        make.trailing.equalTo(self.view).offset(-15);
//        make.height.equalTo(@45);
//    }];
//
//    UIView *line2 = [[UIView alloc] init];
//    line2.backgroundColor = COLOR_ALPHA_LINE;
//    [self.view addSubview:line2];
//    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.leading.equalTo(self.view).offset(15);
//        make.trailing.equalTo(self.view).offset(-15);
//        make.top.equalTo(_invitationTextField.mas_bottom);
//        make.height.equalTo(@HEIGHT_LINE);
//    }];

    UIButton *loginBtn = [WeXCustomButton button];
    [loginBtn setTitle:WeXLocalizedString(@"导入已有钱包") forState:UIControlStateNormal];
    [loginBtn addTarget:self action:@selector(loginBtnClick) forControlEvents:UIControlEventTouchUpInside];
    loginBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:loginBtn];
    [loginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-40);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@40);
    }];
//
    
}

- (void)keyStroeBtnClick:(UIButton *)btn{
    btn.selected = YES;
    _privateKeyBtn.selected = NO;
    [UIView animateWithDuration:0.3 animations:^{
        self.moveLine.WeX_centerX = _keyStroeBtn.WeX_centerX;
    }];
    
    self.importType = WeXImportPassportTypeKeyStore;
    _descriptionLabel.text = WeXLocalizedString(@"请粘贴KEYSTORE信息");
    _passwordLeftLabel.text = WeXLocalizedString(@"验证数字钱包密码:");
    _centreDescriptionLabel.hidden = YES;
    _titleDescriptionLabel.hidden = YES;
    [_showBtn mas_updateConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_backImageView.mas_bottom).offset(15);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@30);
        make.height.equalTo(@45);
    }];
    [self.view layoutIfNeeded];
}

- (void)privateKeyBtnClick:(UIButton *)btn{
    btn.selected = YES;
    _keyStroeBtn.selected = NO;
    [UIView animateWithDuration:0.3 animations:^{
        self.moveLine.WeX_centerX = _privateKeyBtn.WeX_centerX;
    }];
    
    self.importType = WeXImportPassportTypePrivateKey;
    _descriptionLabel.text = WeXLocalizedString(@"请粘贴私钥明文");
    _passwordLeftLabel.text = WeXLocalizedString(@"设置数字钱包密码:");
    _centreDescriptionLabel.hidden = NO;
    _titleDescriptionLabel.hidden = NO;
    [_showBtn mas_updateConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_backImageView.mas_bottom).offset(80);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@30);
        make.height.equalTo(@45);
    }];
    [self.view layoutIfNeeded];
}
#pragma mark -点击登陆按钮
- (void)loginBtnClick{
    
    if([_contentTextView.text isEqualToString: @""]||_contentTextView.text == nil){
        [WeXPorgressHUD showText:WeXLocalizedString(@"请核对后重新输入!") onView:self.view];
        return;
    }
    
    if (self.importType == WeXImportPassportTypeKeyStore) {
        NSString *keyStore = _contentTextView.text;
        NSData *data = [keyStore dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *keyStoreDict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
        if (!keyStoreDict) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"KeyStore格式不对!") onView:self.view];
            return;
        }
    }
    
    if (self.importType == WeXImportPassportTypePrivateKey) {
        if (_passwordTextField.text.length < 8) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"请核对后重新输入!") onView:self.view];
            return;
        }
    }
    
    [self.view endEditing:YES];
    
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    
    [self createGetContractAddressRequest];
    
   
}
#pragma mark -点击扫一扫按钮
- (void)scanBtnClick{
    WeXScanViewController *ctrl = [[WeXScanViewController alloc] init];
    ctrl.handleType = WeXScannerHandleTypeImport;
    ctrl.responseBlock = ^(NSString *content) {
        if (content) {
            _contentTextView.text = content;
            [_contentTextView resignFirstResponder];
            _descriptionLabel.hidden = YES;
        }
    };
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)showBtnClick:(UIButton *)btn{
    btn.selected = !btn.selected;
    _passwordTextField.secureTextEntry = !btn.selected;
}

- (void)graphBtnClick{
    [self createGetTicketRequest];
}
-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

#pragma mark -YYKeyboardDelegate
- (void)keyboardChangedWithTransition:(YYKeyboardTransition)transition {
    [UIView animateWithDuration:transition.animationDuration delay:0 options:transition.animationOption animations:^{
        ///用此方法获取键盘的rect
        CGRect kbFrame = [[YYKeyboardManager defaultManager] convertRect:transition.toFrame toView:self.view];
        ///从新计算view的位置并赋值
        CGRect graphFrame = _invitationTextField.frame;
        CGRect viewFrame = self.view.frame;
        
        if (kbFrame.origin.y < CGRectGetMaxY(graphFrame)+50) {
            viewFrame.origin.y = kbFrame.origin.y-CGRectGetMaxY(graphFrame)-50;
        }
        else
        {
            viewFrame.origin.y = 0;
        }
            
        self.view.frame = viewFrame;
    } completion:^(BOOL finished) {
        
    }];
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
                [WeXPorgressHUD showText:WeXLocalizedString(@"密码长度最多为20位") onView:self.view
                 ];
                return NO;
            }
        }
        else if (textField == _invitationTextField){
            if (comment.length > 4) {
                [WeXPorgressHUD showText:WeXLocalizedString(@"长度最多为4位") onView:self.view];
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
#pragma mark -textViewDelegate

-(void)textViewDidBeginEditing:(UITextView *)textView
{
    NSLog(WeXLocalizedString(@"开始编辑"));
    _descriptionLabel.hidden = YES;
}

-(void)textViewDidEndEditing:(UITextView *)textView
{
    NSLog(WeXLocalizedString(@"停止编辑"));
    if ([textView.text isEqualToString: @""]||textView.text == nil) {
        _descriptionLabel.hidden = NO;
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
    if ([text isEqualToString:@"\n"]){ //判断输入的字是否是回车，即按下return
        //在这里做你响应return键的代码
        [self.view endEditing:YES];
        return YES;
    }
    
    return YES;
}



//记得释放通知

-(void)dealloc
{
    
    [[YYKeyboardManager defaultManager] removeObserver:self];
    
}



@end
