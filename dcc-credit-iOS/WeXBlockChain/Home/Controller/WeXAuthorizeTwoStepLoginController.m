//
//  WeXAuthorizeTwoStepLoginController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAuthorizeTwoStepLoginController.h"

#import "WeXGetPubKeyAdapter.h"
#import "WeXGetTicketAdapter.h"
#import "WeXGetContractAddressAdapter.h"
#import "WeXUploadPubKeyAdapter.h"
#import "WeXGetReceiptResultAdapter.h"

#import "WeXGraphView.h"

#import "WeXCardViewController.h"
#import "WeXBaseNavigationController.h"
#import "AppDelegate.h"
#import "AFNetworking.h"
#import "WeXAuthorizeLoginRecordRLMModel.h"

@interface WeXAuthorizeTwoStepLoginController ()<WeXPasswordManagerDelegate>
{
    WeXPasswordCacheModal *_model;
    
    WeXPasswordManager *_manager;
    
    RSA *_publicKey;
    RSA *_privateKey;
    NSString *_rsaPublicKey;
    NSString *_rsaPrivateKey;
    
    NSString *_rawTransaction;
    
    NSString *_txHash;
    
    NSString *_contractAddress;//合约地址
    
    NSInteger _requestCount;//查询上链结果请求的次数
    
    WeXGraphView *_graphView;//验证码试图
    
    NSString *_walletAddress;//钱包地址
    
    UILabel *_logoNamelabel;
    
    UIButton *_loginBtn;//登录按钮
}

@property (nonatomic,strong)WeXGetTicketAdapter *getTicketAdapter;
@property (nonatomic,strong)WeXGetContractAddressAdapter *getContractAddressAdapter;
@property (nonatomic,strong)WeXUploadPubKeyAdapter *uoloadPubKeyAdapter;
@property (nonatomic,strong)WeXGetReceiptResultAdapter *getReceiptAdapter;
@property (nonatomic,strong)WeXGetPubKeyAdapter *getPubKeyAdapter;
@property (nonatomic,strong)WeXGetTicketResponseModal *getTicketModel;

@end

@implementation WeXAuthorizeTwoStepLoginController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupNavigationBackButtonType];
    
    [self commonInit];
    [self setupSubViews];
    [self configProtocolRules];
}

//设置返回样式
- (void)setupNavigationBackButtonType
{
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"retreat2"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(backBtnClick)];
    self.navigationItem.leftBarButtonItem = item;
}

- (void)backBtnClick{
   
    //获取url中的参数，转化为字典
    NSDictionary *params = [self getParamsWithURL:self.url];
    //从字典中取出URL Schemes
    NSString *returnScheme = params[@"returnScheme"];
    NSString *signature = nil;
    NSString *address = nil;
    NSString *nonce = nil;
    NSString *urlSchemes = [NSString stringWithFormat:@"%@://params?address=%@&signature=%@&nonce=%@",returnScheme,address,signature,nonce];
    NSURL *backURL = [NSURL URLWithString:urlSchemes];
    //跳转回MyApp
    [[UIApplication sharedApplication] openURL:backURL options:@{} completionHandler:^(BOOL success) {
        NSLog(@"跳转结果%d",success);
        [self jumpToRootViewController];
    }];
    
}

- (void)jumpToRootViewController{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        WeXCardViewController *ctrl = [[WeXCardViewController alloc] init];
        WeXBaseNavigationController *navCtrl = [[WeXBaseNavigationController alloc] initWithRootViewController:ctrl];
        AppDelegate* appDelagete = (AppDelegate*)[UIApplication sharedApplication].delegate;
        appDelagete.window.rootViewController = navCtrl;
    });
}


- (void)commonInit{
    _model = [WexCommonFunc getPassport];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UIImageView *logoBackImageView = [[UIImageView alloc] init];
    logoBackImageView.image = [UIImage imageNamed:@"copyFrame"];
    [self.view addSubview:logoBackImageView];
    [logoBackImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+40);
        make.leading.equalTo(self.view).offset(40);
        make.trailing.equalTo(self.view).offset(-40);
        make.height.mas_equalTo(logoBackImageView.mas_width).multipliedBy(1/2.5);
        
    }];
    
    UIImageView *logoImageView = [[UIImageView alloc] init];
    logoImageView.image = [UIImage imageNamed:@"wexLogo1"];
    [self.view addSubview:logoImageView];
    [logoImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(logoBackImageView);
        make.trailing.equalTo(logoBackImageView.mas_centerX).offset(-20);
        make.width.equalTo(@70);
        make.height.equalTo(@70);
    }];
    
    _logoNamelabel = [[UILabel alloc] init];
    _logoNamelabel.text = @"口袋地址:";
    _logoNamelabel.font = [UIFont systemFontOfSize:18];
    _logoNamelabel.textColor = [UIColor whiteColor];
    _logoNamelabel.numberOfLines = 0;
    _logoNamelabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:_logoNamelabel];
    [_logoNamelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(logoImageView);
        make.leading.equalTo(logoBackImageView.mas_centerX).offset(10);
        make.trailing.equalTo(logoBackImageView).offset(-20);
        make.bottom.equalTo(logoImageView);
    }];
    
    //获取url中的参数，转化为字典
    NSDictionary *params = [self getParamsWithURL:self.url];
    NSString *appName = params[@"appName"];
    _logoNamelabel.text = appName;
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"使用两步验证";
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(logoBackImageView.mas_bottom).offset(20);
        make.leading.trailing.equalTo(self.view);
        make.height.equalTo(@20);
    }];
    
    
    NSString *address = [_model.keyStore objectForKey:@"address"];
    
    UIImageView *backImageView = [[UIImageView alloc] init];
    backImageView.image = [UIImage imageNamed:@"copyFrame"];
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(40);
        make.trailing.equalTo(self.view).offset(-40);
        make.height.mas_equalTo(backImageView.mas_width).multipliedBy(1/2.5);
        
    }];
    
    UIImageView *QRImageView = [[UIImageView alloc] init];
    QRImageView.layer.magnificationFilter = kCAFilterNearest;
    QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:address imageViewWidth:70];
    QRImageView.backgroundColor = [UIColor greenColor];
    [self.view addSubview:QRImageView];
    [QRImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(backImageView);
        make.trailing.equalTo(backImageView.mas_centerX).offset(-20);
        make.width.equalTo(@70);
        make.height.equalTo(@70);
    }];
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = @"口袋地址:";
    label1.font = [UIFont systemFontOfSize:12];
    label1.textColor = [UIColor whiteColor];
    label1.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(QRImageView);
        make.leading.equalTo(backImageView.mas_centerX).offset(20);
        make.height.equalTo(@15);
    }];
    
    UILabel *label2 = [[UILabel alloc] init];
    label2.text = [NSString stringWithFormat:@"***%@",[address substringWithRange:NSMakeRange(address.length-6, 6)]];
    label2.font = [UIFont systemFontOfSize:12];
    label2.textColor = [UIColor whiteColor];
    label2.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(QRImageView);
        make.leading.equalTo(backImageView.mas_centerX).offset(20);
        make.height.equalTo(@15);
    }];
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = [UIColor lightGrayColor];
    line.alpha = 0.3;
    [self.view addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(label2);
        make.trailing.equalTo(label2);
        make.top.equalTo(label2.mas_bottom);
        make.height.equalTo(@1);
    }];
    
    UILabel *label3= [[UILabel alloc] init];
    label3.text = @"WEXPASS";
    label3.font = [UIFont systemFontOfSize:12];
    label3.textColor = [UIColor whiteColor];
    label3.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label3];
    [label3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(QRImageView);
        make.leading.equalTo(backImageView.mas_centerX).offset(20);
        make.height.equalTo(@15);
    }];
    
    UILabel *stateLabel = [[UILabel alloc] init];
    if (_model.isAllow) {
        stateLabel.text = @"两步验证状态：可用";
        stateLabel.backgroundColor = ColorWithRGB(178, 225, 135);
        
    }
    else
    {
        stateLabel.text = @"两步验证状态：不可用";
        stateLabel.backgroundColor = ColorWithRGB(191, 192,193);
    }
    
    stateLabel.font = [UIFont boldSystemFontOfSize:14];
    stateLabel.textColor = [UIColor whiteColor];
    stateLabel.textAlignment = NSTextAlignmentCenter;
    stateLabel.layer.cornerRadius = 20;
    stateLabel.layer.masksToBounds = YES;
    [self.view addSubview:stateLabel];
    [stateLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@220);
        make.height.equalTo(@40);
    }];
    
    
    UIButton *loginBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    if (_model.isAllow) {
        
        [loginBtn setTitle:@"确认" forState:UIControlStateNormal];
    }
    else
    {
        [loginBtn setTitle:@"启动并使用两步验证" forState:UIControlStateNormal];
        
    }
    [loginBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [loginBtn setBackgroundImage:[UIImage imageNamed:@"button1"] forState:UIControlStateNormal];
    [loginBtn addTarget:self action:@selector(loginBtnClick) forControlEvents:UIControlEventTouchUpInside];
    loginBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:loginBtn];
    [loginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-40);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@50);
    }];
    
    _loginBtn = loginBtn;
    
    
    
}
#pragma -mark 校验格式
- (void)configProtocolRules{
        //获取url中的参数，转化为字典
        NSDictionary *params = [self getParamsWithURL:self.url];
        NSLog(@"dict=====\n%@",params);
        
        if (params == nil) {
            [WeXPorgressHUD showText:@"格式不正确!" onView:self.view];
            _loginBtn.enabled = NO;
            return;
        }
        
        NSString *protocol = [params objectForKey:@"protocol"];
        if (![protocol isEqualToString:@"wtx.wexchain.io"]) {
            [WeXPorgressHUD showText:@"格式不正确!" onView:self.view];
            _loginBtn.enabled = NO;
            return;
        }
        
        NSNumber *version = [params objectForKey:@"version"];
        if ([version integerValue] > [[WexCommonFunc getVersion] integerValue]) {
            [WeXPorgressHUD showText:@"格式不正确!" onView:self.view];
            _loginBtn.enabled = NO;
            return;
        }
}

#pragma -mark 获取公钥请求
- (void)createGetPubKyeRequest{
    _getPubKeyAdapter = [[WeXGetPubKeyAdapter alloc] init];
    _getPubKeyAdapter.delegate = self;
    WeXGetPubKeyParamModal* paramModal = [[WeXGetPubKeyParamModal alloc] init];
    paramModal.address = [_model.keyStore objectForKey:@"address"];
    [_getPubKeyAdapter run:paramModal];
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

#pragma -mark 上传公钥请求
- (void)createUploadPubKeyRequest{
    _uoloadPubKeyAdapter = [[WeXUploadPubKeyAdapter alloc] init];
    _uoloadPubKeyAdapter.delegate = self;
    WeXUploadPubKeyParamModal* paramModal = [[WeXUploadPubKeyParamModal alloc] init];
    paramModal.ticket = _getTicketModel.ticket;
    paramModal.signMessage = _rawTransaction;
    paramModal.code = _graphView.graphTextField.text;
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
    if (adapter == _getTicketAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            _getTicketModel = (WeXGetTicketResponseModal *)response;
            [_graphView.graphBtn setImage:[WexCommonFunc imageWihtBase64String:_getTicketModel.image] forState:UIControlStateNormal];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
    }
    else if (adapter == _getContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _contractAddress = model.result;
            //合约地址请求成功 然后开始初始化passhelper
            [self updatePubKeyPassport];
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
            
            [WeXPorgressHUD showText:@"验证码不正确!" onView:self.view];
        }
        else if([headModel.businessCode isEqualToString:@"TICKET_INVALID"])
        {
            [WeXPorgressHUD hideLoading];
            
            [WeXPorgressHUD showText:@"验证码超时!" onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            
            [WeXPorgressHUD showText:@"系统繁忙!" onView:self.view];
        }
    }
    else if (adapter == _getReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetReceiptResultResponseModal *model = (WeXGetReceiptResultResponseModal *)response;
            //上链成功
            if ([model.result isEqualToString:@"1"]) {
                _walletAddress = [_model.keyStore objectForKey:@"address"];
                [self createGetPubKyeRequest];
            }
            else
            {
                if (_requestCount > 4) {
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:@"系统繁忙,请稍后再试!" onView:self.view];
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
            WeXGetPubKeyResponseModal *model = (WeXGetPubKeyResponseModal *)response;
            NSData *publicKeyData =  [[NSData alloc] initWithBase64EncodedString:model.result options:0];
            NSString *resultPublickKey  = [WexCommonFunc hexStringWithData:publicKeyData];
            //相等表示口袋创建成功
            if ([resultPublickKey isEqualToString:_rsaPublicKey]) {
                //更新秘钥
                _model.rsaPublicKey = _rsaPublicKey;
                _model.rasPrivateKey = _rsaPrivateKey;
                _model.isAllow = YES;
                [WexCommonFunc savePassport:_model];
                NSLog(@"启用成功");
                
                //保存统一操作记录
                [WexCommonFunc saveManagerRecordWithTypeString:@"启用统一登录"];
                
                [self configReturnProcess];
            }
            else
            {
                [WeXPorgressHUD hideLoading];
                [WeXPorgressHUD showText:@"系统繁忙,请稍后再试!" onView:self.view];
            }
            
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
        
        
    }
    
    
}

- (void)loginBtnClick{
    
    [self configLocalSafetyView];
    
}
- (void)configReturnProcess{
        //获取url中的参数，转化为字典
        NSDictionary *params = [self getParamsWithURL:self.url];
        NSLog(@"dict=====\n%@",params);
        //从字典中取出URL Schemes
        NSString *returnScheme = params[@"returnScheme"];
        NSString *address = [_model.keyStore objectForKey:@"address"];
        NSString *nonce = params[@"nonce"];
        NSString *originSign = [NSString stringWithFormat:@"%@,%@",address,nonce];
        NSData *nonceData = [WexCommonFunc rsaSHA256SignPlainSring:originSign withHexPrivateKey:_model.rasPrivateKey];
        NSString *signature = [WexCommonFunc hexStringNo0xWithData:nonceData];
        NSString *urlSchemes = [NSString stringWithFormat:@"%@://params?address=%@&signature=%@&nonce=%@",returnScheme,address,signature,nonce];
        
        [WeXPorgressHUD hideLoading];
        
        NSURL *backURL = [NSURL URLWithString:urlSchemes];
        
        NSLog(@"backURL=====\n%@",backURL);
        //跳转回MyApp
        //        [[UIApplication sharedApplication] openURL:backURL];
        [[UIApplication sharedApplication] openURL:backURL options:@{} completionHandler:^(BOOL success) {
            NSLog(@"跳转结果%d",success);
            NSString *appId = params[@"appId"];
            NSString *appName = params[@"appName"];
            [self saveLoginRecordWithAppId:appId appName:appName];
            
            [self jumpToRootViewController];
        }];
    
}

- (void)configLoginProcess{
    //授权登录可用时候
    if (_model.isAllow) {
        
        [WeXPorgressHUD showLoadingAddedTo:self.view.window];
        [self configReturnProcess];
    }
    else //授权登录不可用时候
    {
        _graphView = [[WeXGraphView alloc] initWithFrame:self.view.bounds];
        [self.view addSubview:_graphView];
        [self createGetTicketRequest];
        
        __weak typeof(self) weakSelf = self;
        
        _graphView.comfirmBtnBlock = ^{
            [WeXPorgressHUD showLoadingAddedTo:weakSelf.view];
            [weakSelf createGetContractAddressRequest];
        };
        
        _graphView.graphBtnBlock = ^{
            [weakSelf createGetTicketRequest];
        };
    }
}

- (void)updatePubKeyPassport{
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
            NSLog(@"nitProvide=%@",response);
            // 合约定义说明
            NSString* abiJson=@"{'constant':false,'inputs':[{'name':'_publickey','type':'bytes'}],'name':'putKey','outputs':[{'name':'_result','type':'bool'}],'payable':false,'stateMutability':'nonpayable','type':'function'}";
            //            // 合约参数值
            NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_rsaPublicKey]; //存放是自己的RSA公钥
            // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
            NSString* abiAddress = _contractAddress;
            NSString* privateKey=_model.walletPrivateKey;
            [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:privateKey responseBlock:^(id response) {
                NSLog(@"_rawTransaction=%@",response);
                _rawTransaction = response;
                //上传本地pubkey
                [self createUploadPubKeyRequest];
            }];
        }];
    }];
    
}

#pragma mark - 创建RSA
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

//将url里面的参数转换成字典
- (NSDictionary *)getParamsWithURL:(NSURL *)url {
    
    //query是？后面的参数，在这个demo中，指的是title=hello&content=helloworld&urlschemes=shixueqian
    NSString *query = url.query;
    
    //进行字符串的拆分，通过&来拆分，把每个参数分开
    NSArray *subArray = [query componentsSeparatedByString:@"&"];
    //把subArray转换为字典
    //tempDic中存放一个URL中转换的键值对
    NSMutableDictionary *tempDic = [NSMutableDictionary dictionary];
    
    for (int i = 0 ; i < subArray.count ; i++) {
        //通过“=”拆分键和值
        NSArray *dicArray = [subArray[i] componentsSeparatedByString:@"="]
        ;
        //给字典加入元素,=前面为key，后面为value
        [tempDic setObject:dicArray[1] forKey:dicArray[0]];
    }
    //返回转换后的字典
    return tempDic ;
}


- (void)configLocalSafetyView{
    _model = [WexCommonFunc getPassport];
    if (_model.passwordType == WeXPasswordTypeNone) {
        [self configLoginProcess];
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
        [self configLoginProcess];
    });
    
}



- (void)saveLoginRecordWithAppId:(NSString *)AppId appName:(NSString *)appName{
    WeXAuthorizeLoginRecordRLMModel *model = [[WeXAuthorizeLoginRecordRLMModel alloc] init];
    model.appId = AppId;
    
    model.appName = appName;
    
    model.date = [NSDate date];
    RLMRealm *realm = [RLMRealm defaultRealm];
    [realm beginWriteTransaction];
    [realm addObject:model];
    [realm commitWriteTransaction];
    
}



@end
