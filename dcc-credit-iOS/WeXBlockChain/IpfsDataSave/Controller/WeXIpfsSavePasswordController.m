
//
//  WeXIpfsSavePasswordController.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXIpfsSavePasswordController.h"
#import "WeXReSetIpfsPwdController.h"
#import "WeXMyIpfsSaveController.h"
//#import "NSData+WeXSAES256.h"
#import "WeXIpfsKeyPostAdapter.h"
#import "WeXIpfsKeyPostModel.h"
#import "WeXIpfsKeyGetAdapter.h"
#import "WeXIpfsKeyGetModel.h"
#import "WeXIpfsHashPostModel.h"
#import "WeXIpfsHashPostAdapter.h"
#import "AFNetworking.h"
#import "WeXIpfsContractHeader.h"
#import "WeXGetReceiptResult2Adapter.h"
#import "WeXIpfsHelper.h"

static NSString *const kAddressBookIdentifier = @"WeXAddressBookCellID";

@interface WeXIpfsSavePasswordController ()<UITextFieldDelegate,WeXPasswordManagerDelegate>

{
    WeXPasswordManager *_manager;
}

@property (nonatomic,strong)UIView *topView;
@property (nonatomic,strong)UIView *centralView;
@property (nonatomic,strong)UIView *bottomView;

@property (nonatomic,strong)UILabel *displayLabel;
@property (nonatomic,strong)UILabel *oneDefaultLabel;
@property (nonatomic,strong)UITextField *passwordsField;
@property (nonatomic,strong)UIButton *eyeBtn;

@property (nonatomic,strong)UIButton *readMoreBtn;
@property (nonatomic,strong)UIButton *surnBtn;

@property (nonatomic,strong)UIView *backView;
@property (nonatomic,strong)UIView *tipView;

@property (nonatomic,strong)WeXIpfsKeyPostAdapter *postIpfsKeyAdapter;
@property (nonatomic,strong)WeXIpfsKeyGetAdapter *getIpfsKeyAdapter;
@property (nonatomic,strong)WeXIpfsHashPostAdapter *postIpfsHashAdapter;
@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getReceiptAdapter;

@property (nonatomic,copy)NSString *rawTransaction;
@property (nonatomic,copy)NSString *contractAddress;

@property (nonatomic, assign) NSInteger requestCount;
@property (nonatomic,copy)NSString *txHash;
@property (nonatomic,copy)NSString *keyString;
@property (nonatomic,copy)NSString *twoKeyString;

@property (nonatomic,assign)BOOL isReSettingPwd;

@end

@implementation WeXIpfsSavePasswordController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
//    [self createGetIpfsKeyRequest];
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
    NSString *twopassWord = [user objectForKey:WEX_IPFS_MY_TWOCHECKKEY];
    if (!passWord && twopassWord) {
        _isReSettingPwd = YES;
        self.navigationItem.title = @"我的云存储";
        self.oneDefaultLabel.text = @"您的钱包已开启数据云存储功能,请输入云存储密码以便将云端存储的数据下载到手机本地。";
        _passwordsField .placeholder = @"输入8-20位云存储密码";
        [_readMoreBtn setTitle:@"忘记密码" forState:UIControlStateNormal];
    }else{
         _isReSettingPwd = NO;
        self.navigationItem.title = @"开启云存储";
        self.oneDefaultLabel.text = @"您需要设置云存储密码以开启数据备份功能。云存储密码丢失后无法找回,请妥善保管。";
        _passwordsField .placeholder = @"设置8-20位云存储密码";
        [_readMoreBtn setTitle:@"了解更多" forState:UIControlStateNormal];
    }
}

#pragma mark -初始化控件布局
-(void)setupSubViews{
    
    self.view.backgroundColor = [UIColor whiteColor];
    //头部布局
    _topView = [[UIView alloc]init];
    _topView.backgroundColor = ColorWithHex(0xF8F8FF);
    [self.view addSubview:_topView];
    [_topView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.left.right.mas_equalTo(0);
        make.height.mas_offset(64);
    }];
    self.oneDefaultLabel = [[UILabel alloc]init];
    self.oneDefaultLabel.font = [UIFont systemFontOfSize:14];
    self.oneDefaultLabel.textColor = ColorWithHex(0x4A4A4A);

    self.oneDefaultLabel.numberOfLines = 2;//多行显示
    self.oneDefaultLabel.lineBreakMode = NSLineBreakByWordWrapping;
    [self.topView addSubview:self.oneDefaultLabel];
    [_oneDefaultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.topView).offset(15);
        make.left.equalTo(self.topView).offset(15);
        make.right.equalTo(self.topView).offset(-15);
    }];
 
    _centralView = [[UIView alloc]init];
    [self.view addSubview:_centralView];
    [_centralView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.topView.mas_bottom).offset(0);
        make.left.right.mas_equalTo(0);
        make.height.mas_offset(86);
    }];
    
    UILabel *twoDefultLabel = [[UILabel alloc]init];
    twoDefultLabel.font = [UIFont systemFontOfSize:15];
    twoDefultLabel.text = WeXLocalizedString(@"云存储密码");
    twoDefultLabel.textColor = ColorWithHex(0x333333);
    [self.centralView addSubview:twoDefultLabel];
    [twoDefultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(10);
        make.left.mas_equalTo(10);
        make.width.mas_equalTo(100);
        make.height.mas_equalTo(30);
    }];
    
    _eyeBtn = [[UIButton alloc]init];
    [_eyeBtn setImage:[UIImage imageNamed:@"eye2"] forState:UIControlStateNormal];
    [_eyeBtn setImage:[UIImage imageNamed:@"eye1"] forState:UIControlStateSelected];
    [_eyeBtn addTarget:self action:@selector(eyesBtnClick) forControlEvents: UIControlEventTouchUpInside];
    [self.centralView addSubview:_eyeBtn];
    [_eyeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(twoDefultLabel);
        make.right.mas_equalTo(-10);
        make.width.mas_equalTo(30);
        make.height.mas_equalTo(20);
    }];
    
    _passwordsField = [[UITextField alloc]init];
    _passwordsField .textColor = ColorWithHex(0x4A4A4A);
    _passwordsField .font = [UIFont systemFontOfSize:14.0f];
//    _passwordsField .placeholder = @"设置8-20位云存储密码";
    _passwordsField.secureTextEntry = YES;
    [self.centralView addSubview:_passwordsField];
    _passwordsField.delegate = self;
    [_passwordsField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(twoDefultLabel);
        make.leading.equalTo(twoDefultLabel.mas_trailing).offset(5);
        make.trailing.equalTo(self.eyeBtn.mas_leading).offset(-15);
        make.height.mas_equalTo(30);
    }];
    UIView *oneLineView = [[UIView alloc]init];
    oneLineView.backgroundColor = ColorWithHex(0xEFEFEF);
    [self.centralView addSubview:oneLineView];
    [oneLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.passwordsField.mas_bottom).offset(10);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
        make.height.mas_equalTo(1);
    }];
    
    _readMoreBtn = [[UIButton alloc]init];
    [_readMoreBtn setTitleColor:ColorWithHex(0x6766CC) forState:UIControlStateNormal];
    [_readMoreBtn addTarget:self action:@selector(judgeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    _readMoreBtn.titleLabel.font = [UIFont systemFontOfSize:14.0f];
    _readMoreBtn.titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.centralView addSubview:_readMoreBtn];
    [_readMoreBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(oneLineView.mas_bottom).offset(10);
        make.left.mas_equalTo(15);
        make.width.mas_equalTo(60);
        make.height.mas_equalTo(30);
    }];
    
    _surnBtn = [[UIButton alloc]init];
    [_surnBtn setTitle:@"确定" forState:UIControlStateNormal];
    [_surnBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_surnBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    _surnBtn.layer.cornerRadius = 6;
    _surnBtn.clipsToBounds = YES;
    [_surnBtn addTarget:self action:@selector(surnBtnClick) forControlEvents: UIControlEventTouchUpInside];
    [self.view addSubview:_surnBtn];
    [_surnBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.centralView.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.mas_equalTo(40);
    }];

}

- (void)eyesBtnClick{
    _passwordsField.secureTextEntry = !_passwordsField.secureTextEntry;
    _eyeBtn.selected = !_eyeBtn.selected;
}

- (void)judgeBtnClick{
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
    NSString *twoPassWord = [user objectForKey:WEX_IPFS_MY_TWOCHECKKEY];
    if (!passWord && twoPassWord) {
        WeXReSetIpfsPwdController *vc = [[WeXReSetIpfsPwdController alloc]init];
        vc.fromVc = self.fromVc;
        [self.navigationController pushViewController:vc animated:YES];
    }else{
          [self addTipsView];
    }

}

- (void)addTipsView{
    
    if (_backView) {
        return;
    }
    _backView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
    _backView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    [self.view addSubview:_backView];
    if (_tipView) {
        return;
    }
    _tipView = [[UIView alloc]initWithFrame:CGRectMake(kScreenWidth*0.15, kScreenHeight*0.2, kScreenWidth*0.7, kScreenHeight*0.6)];
    _tipView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:_tipView];
    _tipView.layer.cornerRadius = 6;
    _tipView.clipsToBounds = YES;
    
    UILabel *defultLabel = [[UILabel alloc]init];
    defultLabel.text = @"数据云存储";
    defultLabel.font = [UIFont systemFontOfSize:15.0];
    defultLabel.textColor = ColorWithHex(0x4A4A4A);
    defultLabel.textAlignment = NSTextAlignmentCenter;
    [self.tipView addSubview:defultLabel];
    [defultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.tipView).offset(10);
        make.centerX.equalTo(self.tipView);
        make.width.mas_offset(100);
        make.height.mas_offset(40);
    }];
    
    UIButton *deleteBtn = [[UIButton alloc]init];
    [deleteBtn setImage:[UIImage imageNamed:@"dcc_cha"] forState:UIControlStateNormal];
    [deleteBtn addTarget:self action:@selector(removeBackViewClick) forControlEvents:UIControlEventTouchUpInside];
    [self.tipView addSubview:deleteBtn];
    [deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.tipView).offset(8);
        make.right.equalTo(self.tipView).offset(-15);
        make.width.mas_offset(30);
        make.height.mas_offset(30);
    }];
    
    UITextView *displayView = [[UITextView alloc]init];
    displayView.textColor = ColorWithHex(0x4A4A4A);
    displayView.font = [UIFont systemFontOfSize:14.0];
    displayView.editable = NO;
    displayView.text = @"1.开始云存储功能有什么好处?\n开启后,可将您手机本地的已认证信息加密后传到云空间,删除钱包重新导入或更换设备时,可以同步到手机无需再次认证。\n\n 2.数据会存储在哪里?\nIPFS,一个分布式存储和共享文件的网络传输协议系统,数据将以中心化方式存储,比传统方式更加稳定高效。\n\n 3.使用云存储数据安全吗?\n非常安全,数据会以AES-一种对称加密算法加密后存在IPFS,仅有您自己通过密码可以解密。 \n\n 4.云存储密码有什么用? \n 云存储密码用于解开加密后的个人数据,该密码系统并不存储,一旦丢失无法找回,请妥善保管。";
    
    [self.tipView addSubview:displayView];
    [displayView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(defultLabel.mas_bottom).offset(1);
        make.right.equalTo(self.tipView).offset(-10);
        make.left.equalTo(self.tipView).offset(10);
        make.height.mas_equalTo(kScreenHeight *0.6 -60);
    }];
}

- (void)removeBackViewClick{
    [self.backView removeFromSuperview];
    self.backView = nil;
    [self.tipView removeFromSuperview];
    self.tipView = nil;
}

- (void)surnBtnClick{
    
    //密码长度错误
    if (self.passwordsField.text.length<8|| self.passwordsField.text.length>20) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"密码长度为8-20位!") onView:self.view];
        return;
    }
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
    NSString *twoPassWord = [user objectForKey:WEX_IPFS_MY_TWOCHECKKEY];
    
    if (!passWord && twoPassWord) {
       
        [self createGetIpfsKeyRequest];

    }else{
    
        [self configLocalSafetyView];
     
    }
}

- (void)judgeCheckPwdClick:(NSString *)orginStr{
    
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    NSString *addressStr = [WexCommonFunc getFromAddress];
    //要求先用钱包地址和输入的密码做sha256(校验密码)
    NSString *walletPwd = [addressStr stringByAppendingString:_passwordsField.text];
    NSLog(@"walletPwd = %@",walletPwd);
    NSData *ipfsPwdData = [walletPwd  dataUsingEncoding:NSUTF8StringEncoding];
    NSString *idHashStr = [WexCommonFunc stringSHA256WithData:ipfsPwdData];
    NSLog(@"idHashStr = %@",idHashStr);
    //要求先用钱包私钥和上面的值做sha256(加密密码)
    NSString *walletPrivateKeyStr = [WeXIpfsHelper toLower:model.walletPrivateKey];
    WEXNSLOG(@"walletPrivateKeyStr = %@",walletPrivateKeyStr);
    NSString *twoWalletPwd = [walletPrivateKeyStr stringByAppendingString:_passwordsField.text];
    NSLog(@"twoWalletPwd = %@",twoWalletPwd);
    NSData *twoIpfsPwdData = [twoWalletPwd  dataUsingEncoding:NSUTF8StringEncoding];
    NSString *twoHashStr = [WexCommonFunc stringSHA256WithData:twoIpfsPwdData];
    NSLog(@"twoHashStr = %@",twoHashStr);
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    
    WEXNSLOG(@"orginStr = %@",orginStr);
    [WeXPorgressHUD hideLoading];
    if ([orginStr isEqualToString: idHashStr]) {
        [user setObject:twoHashStr forKey:WEX_IPFS_MY_KEY];
        [user setObject:idHashStr forKey:WEX_IPFS_MY_CHECKKEY];
        WeXMyIpfsSaveController *vc = [[WeXMyIpfsSaveController alloc]init];
        vc.iSFromSettingVc = YES;
        vc.fromVc = self.fromVc;
        [self.navigationController pushViewController:vc animated:YES];
    }else{
        
        [WeXPorgressHUD showText:@"您输入的云存储密码不正确" onView:self.view];
    }
    
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [_passwordsField resignFirstResponder];
    return YES;
}

#pragma -mark 查询上链结果请求
- (void)createReceiptResultRequest:(NSString *)txHash{
    _getReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getReceiptAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = txHash;
    [_getReceiptAdapter run:paramModal];
}

- (void)createGetIpfsKeyRequest{
    _getIpfsKeyAdapter = [[WeXIpfsKeyGetAdapter alloc] init];
    //    _getAgentAdapter.currentyName = self.tokenModel.symbol;
    _getIpfsKeyAdapter.delegate = self;
    WeXIpfsKeyGetModel *paramModal = [[WeXIpfsKeyGetModel alloc] init];
//    paramModal.getIpfsKey = @"";
    [_getIpfsKeyAdapter run:paramModal];
}

- (void)createPostIpfsHashRequest:(NSString *)getAddressStr{
    _postIpfsHashAdapter = [[WeXIpfsHashPostAdapter alloc] init];
    //    _getAgentAdapter.currentyName = self.tokenModel.symbol;
    _postIpfsHashAdapter.delegate = self;
    WeXIpfsHashPostModel *paramModal = [[WeXIpfsHashPostModel alloc] init];
    paramModal.getIpfsToken = getAddressStr;
    [_postIpfsHashAdapter run:paramModal];
}

- (void)getRawTranstion
{
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
         NSString *addressStr = [WexCommonFunc getFromAddress];
          //要求用钱包地址和输入的密码做sha256(用于密码校验)
         NSString *walletPwd = [addressStr stringByAppendingString:_passwordsField.text];
         WEXNSLOG(@"walletPwd = %@",walletPwd);
         NSData *ipfsPwdData = [walletPwd  dataUsingEncoding:NSUTF8StringEncoding];
         NSString *idHashStr = [WexCommonFunc stringSHA256WithData:ipfsPwdData];
         WEXNSLOG(@"idHashStr = %@",idHashStr);
         NSString *oneIdHashStr = [NSString stringWithFormat:@"0x%@",idHashStr];
         WEXNSLOG(@"oneIdHashStr = %@",oneIdHashStr);
         _keyString = idHashStr;
        //要求用钱包私钥和输入的密码做sha256(用于数据加密)
         NSString *walletPrivateKeyStr = [WeXIpfsHelper toLower:model.walletPrivateKey];
         WEXNSLOG(@"walletPrivateKeyStr = %@",walletPrivateKeyStr);
         NSString *twoWalletPwd = [walletPrivateKeyStr stringByAppendingString:_passwordsField.text];
    
         WEXNSLOG(@"twoWalletPwd = %@",twoWalletPwd);
         NSData *twoIpfsPwdData = [twoWalletPwd  dataUsingEncoding:NSUTF8StringEncoding];
         NSString *twoHashStr = [WexCommonFunc stringSHA256WithData:twoIpfsPwdData];
         WEXNSLOG(@"twoHashStr = %@",twoHashStr);
//         NSString *twoIdHashStr = [NSString stringWithFormat:@"0x%@",twoHashStr];
//         WEXNSLOG(@"idHashStr = %@",idHashStr);
         _twoKeyString = twoHashStr;
         // 合约定义说明
         NSString* abiJson = WEX_IPFS_ABI_POST_KEY;
         NSString* abiParamsValues = [NSString stringWithFormat:@"[\'%@\']",oneIdHashStr];
         WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
         // 合约地址
         NSString* abiAddress = _contractAddress;
         WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
         [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:cacheModel.walletPrivateKey responseBlock:^(id response)
          {
              NSLog(@"rawTransaction=%@",response);
              _rawTransaction = response;
      
              [[WXPassHelper instance] initProvider:WEX_IPFS_KEYHASH_URL responseBlock:^(id response) {
                  [[WXPassHelper instance] sendRawTransaction:_rawTransaction responseBlock:^(id response) {
                      NSLog(@ "reponse = %@",response);
                      NSString *txHash = response;
                      _txHash = txHash;
                      [self createReceiptResultRequest:txHash];
                      
//                      [WeXPorgressHUD hideLoading];

//                      NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
//                      [user setObject:twoHashStr forKey:WEX_IPFS_MY_KEY];
//                      [user setObject:idHashStr forKey:WEX_IPFS_MY_CHECKKEY];
//
//                      WeXMyIpfsSaveController *vc = [[WeXMyIpfsSaveController alloc]init];
//                      vc.iSFromSettingVc = YES;
//                      vc.fromVc = self.fromVc;
//                      [self.navigationController pushViewController:vc animated:YES];
                      
                  }];
              }];
          }];
     }];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
  
    if (adapter == _getIpfsKeyAdapter) {
        NSLog(@"response = %@",response);
        WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
        NSLog(@"model.result = %@",model.result);
        _contractAddress = model.result;
        if (_isReSettingPwd) {
            [self getRawTranstion:_contractAddress];//校验密码
        }else{
             [self getRawTranstion];//写入密码
        }
    }
    else if (adapter == _getReceiptAdapter){
        
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            //上链成功
            WEXNSLOG(@"%@",model);
//            WEXNSLOG(@"上链成功");
            WEXNSLOG(@"0.0");
            if (model.hasReceipt && model.approximatelySuccess) {
                //上链成功
                [WeXPorgressHUD hideLoading];
                WEXNSLOG(@"上链成功");
                WEXNSLOG(@"0.0");
                [WeXPorgressHUD hideLoading];
                NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                [user setObject:_twoKeyString forKey:WEX_IPFS_MY_KEY];
                [user setObject:_keyString forKey:WEX_IPFS_MY_CHECKKEY];
                
                WeXMyIpfsSaveController *vc = [[WeXMyIpfsSaveController alloc]init];
                vc.iSFromSettingVc = YES;
                vc.fromVc = self.fromVc;
                [self.navigationController pushViewController:vc animated:YES];
            } else
            {
                if (_requestCount > 10) {
                    _requestCount = 0;
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"系统失败,请稍后重试") onView:self.view];
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createReceiptResultRequest:_txHash];
                    _requestCount++;
                });
            }
        }else{
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }

}

#pragma mark -密码
- (void)configLocalSafetyView{
    //   没有密码
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    if (model.passwordType == WeXPasswordTypeNone) {
         [self createGetIpfsKeyRequest];
    } else{
        WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
        manager.delegate = self;
        [manager loadPassword];
        _manager = manager;
    }
}

#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerVerifySuccess{
     [self createGetIpfsKeyRequest];
}


- (void)getRawTranstion:(NSString *)contractAddress
{
    
    // 合约定义说明
    NSString* abiJson = WEX_IPFS_ABI_GET_KEY;
    // 合约参数值 第一个参数为version 暂时为1
    NSString* abiParamsValues = @"[]";
    WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
    // 合约地址
    NSString* abiAddress = contractAddress;
    NSString *addressStr = [WexCommonFunc getFromAddress];
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         [[WXPassHelper instance] initIpfsProvider:WEX_IPFS_KEYHASH_URL responseBlock:^(id response) {
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:abiParamsValues responseBlock:^(id response) {
                 [[WXPassHelper instance] call4IpfsContractAddress:abiAddress data:response walletAddress:addressStr responseBlock:^(id response) {
                     NSDictionary *responseDict = response;
                     NSString *str = response[@"result"];
                     if (str.length > 0) {
                         
                         if([str isEqualToString:@"0x"]){
                             //密码为空
                             NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                             [user removeObjectForKey:WEX_IPFS_MY_CHECKKEY];
                             [user removeObjectForKey:WEX_IPFS_MY_TWOCHECKKEY];
                             [WeXPorgressHUD showText:@"您的密码已经被重置,请重新设置密码" onView:self.view];
                             [self.navigationController popViewControllerAnimated:YES];
                             return ;
                         }
                         
                         [[WXPassHelper instance]getIpfsEncodeWithKeyHashString:str ResponseBlock:^(id response) {
                             WEXNSLOG(@"response = %@",response);
                             NSDictionary *dict = response;
                             WEXNSLOG(@"dict = %@",dict);
                             if (!dict) {
                                 return;
                             }
                             NSString *keyHashStr = dict[@"1"];
                             NSArray *array = [keyHashStr componentsSeparatedByString:@"0x"]; //字符串按照分隔成数组
                             
                             WEXNSLOG(@"array=%@",array); //结果是
                             
                             if (array.count>1) {
                                 NSString *pwdStr = array[1];
                                 WEXNSLOG(@"pwdStr = %@",pwdStr);
                                 if ([pwdStr isEqualToString:@"0000000000000000000000000000000000000000000000000000000000000000"] || [str isEqualToString:@"0x"]) {
                                     
                                     //处理另一个设备重置密码以后没有输入了新密码,这台设备密码也要删除重置
                                     NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                                     //                        NSString *passWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
                                     //                                 if (passWord.length>0) {
                                     [user removeObjectForKey:WEX_IPFS_MY_CHECKKEY];
                                     [user removeObjectForKey:WEX_IPFS_MY_TWOCHECKKEY];
                                   
                                     [WeXPorgressHUD showText:@"您的密码已经被重置,请重新设置密码" onView:self.view];
                                     [self.navigationController popViewControllerAnimated:YES];
                                     //                                                                          }else{}
                                 }else{
                                     
                                     NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                                     NSString *passWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
                                     //处理另一个设备重置密码以后并且输入了新密码,这台设备密码也要删除重置
                                     if (passWord.length>0 && ![passWord isEqualToString:pwdStr]) {
                                         [user removeObjectForKey: WEX_IPFS_MY_CHECKKEY];
                                         [self judgeCheckPwdClick:pwdStr];
                                         return ;
                                     }else{}
                                     [user setObject:pwdStr forKey:WEX_IPFS_MY_TWOCHECKKEY];
                                     [self judgeCheckPwdClick:pwdStr];
                                     
                                 }
                             }
                         }];
                         
                     }else{}
                     NSLog(@"responseDict = %@",responseDict);
                 }];
             }];
         }];
     }];
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
