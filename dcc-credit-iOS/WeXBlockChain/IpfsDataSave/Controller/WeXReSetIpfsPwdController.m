//
//  WeXReSetIpfsPwdController.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXReSetIpfsPwdController.h"
#import "WeXMyIpfsSaveController.h"
#import "WeXIpfsContractHeader.h"
#import "WeXIpfsKeyGetAdapter.h"
#import "WeXIpfsKeyGetModel.h"
#import "WeXCardSettingViewController.h"
#import "WeXCreditDccGetContractAddressAdapter.h"
#import "WeXPasswordManager.h"
#import "WeXGetReceiptResult2Adapter.h"

@interface WeXReSetIpfsPwdController ()<WeXPasswordManagerDelegate>
{
    WeXPasswordManager *_manager;
    
}

@property (nonatomic,strong)WeXIpfsKeyGetAdapter *getIpfsKeyAdapter;
@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getReceiptAdapter;
@property (nonatomic,copy)NSString *contractAddress;

@property (nonatomic,copy)NSString *identifyContractAddress;
@property (nonatomic,copy)NSString *bankCardContractAddress;
@property (nonatomic,copy)NSString *phoneContractAddress;

@property (nonatomic,strong) WeXCreditDccGetContractAddressAdapter *getIDContractAddressAdapter;
@property (nonatomic,strong) WeXCreditDccGetContractAddressAdapter *getBankContractAddressAdapter;
@property (nonatomic,strong) WeXCreditDccGetContractAddressAdapter *getPhoneContractAddressAdapter;

@property (nonatomic,strong)dispatch_group_t getAllContractGroup;
@property (nonatomic,strong)dispatch_group_t getAllDeleteGroup;

@property (nonatomic, assign) NSInteger requestCount;
@property (nonatomic,copy)NSString *txHash;

@end

@implementation WeXReSetIpfsPwdController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    // Do any additional setup after loading the view.
}

#pragma mark -初始化控件布局
-(void)setupSubViews{
    self.navigationItem.title = @"重置密码";
    self.view.backgroundColor = [UIColor whiteColor];
 
    UILabel *oneDefaultLabel = [[UILabel alloc]init];
    oneDefaultLabel.font = [UIFont systemFontOfSize:15];
    //    self.oneDefaultLabel.backgroundColor = [UIColor greenColor];
    oneDefaultLabel.textColor = ColorWithHex(0x4A4A4A);
    oneDefaultLabel.text = @"如忘记密码或想更换密码,您可以重置旧的密码并设置新的密码。\n\n重置密码后存储在云端的数据会被全部删除,您需要设置新的密码以开启云存储功能,请谨慎操作。";
    oneDefaultLabel.numberOfLines = 0;//多行显示
    oneDefaultLabel.lineBreakMode = NSLineBreakByWordWrapping;
    
    [self.view addSubview:oneDefaultLabel];
    [oneDefaultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+20);
        make.left.equalTo(self.view).offset(15);
        make.right.equalTo(self.view).offset(-15);
    }];
    
    UIButton *surnBtn = [[UIButton alloc]init];
    [surnBtn setTitle:@"确定重置" forState:UIControlStateNormal];
    [surnBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [surnBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    surnBtn.layer.cornerRadius = 6;
    surnBtn.clipsToBounds = YES;
    [surnBtn addTarget:self action:@selector(surnBtnClick) forControlEvents: UIControlEventTouchUpInside];
    [self.view addSubview:surnBtn];
    [surnBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(oneDefaultLabel.mas_bottom).offset(40);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.mas_equalTo(40);
    }];
    
}

- (void)surnBtnClick{
    
  [self configLocalSafetyView];
 
}

- (void)deleteData{
    
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self createGetIpfsKeyRequest];

}

#pragma -mark 发送获取合约地址请求

- (void)createGetIpfsKeyRequest{
    _getIpfsKeyAdapter = [[WeXIpfsKeyGetAdapter alloc] init];
    //    _getAgentAdapter.currentyName = self.tokenModel.symbol;
    _getIpfsKeyAdapter.delegate = self;
    WeXIpfsKeyGetModel *paramModal = [[WeXIpfsKeyGetModel alloc] init];
    //    paramModal.getIpfsKey = @"";
    [_getIpfsKeyAdapter run:paramModal];
}

#pragma -mark 查询上链结果请求
- (void)createReceiptResultRequest:(NSString *)txHash{
    _getReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getReceiptAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = txHash;
    [_getReceiptAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    
    if (adapter == _getIpfsKeyAdapter) {
        NSLog(@"response = %@",response);
        WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
        NSLog(@"model.result = %@",model.result);
        _contractAddress = model.result;
        if (_contractAddress.length>0) {
            [self getRawTranstion];
        }
    }

    else if (adapter == _getReceiptAdapter){
        
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            //上链成功
            WEXNSLOG(@"%@",model);

            if (model.hasReceipt && model.approximatelySuccess) {
                //上链成功
                [WeXPorgressHUD hideLoading];
                WEXNSLOG(@"上链成功");
                WEXNSLOG(@"0.0");
                NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                [user removeObjectForKey:WEX_IPFS_MY_KEY];
                [user removeObjectForKey:WEX_IPFS_MY_CHECKKEY];
                [user removeObjectForKey:WEX_IPFS_MY_TWOCHECKKEY];
                [user removeObjectForKey:WEX_IPFS_IDENTIFY_HASH];
                [user removeObjectForKey:WEX_IPFS_BankCard_HASH];
                [user removeObjectForKey:WEX_IPFS_PhoneOperator_HASH];
                //IPFS新增节点
                [user removeObjectForKey:WEX_IPFS_DEFAULT_NONEURL];
                [user removeObjectForKey:WEX_IPFS_CUSTOM_NONEURL];
                [user removeObjectForKey:WEX_IPFS_MAIN_NONEURL];
                [user removeObjectForKey:WEX_IPFS_CUSTOM_PUBLICADDRESS];
                [user removeObjectForKey:WEX_IPFS_CUSTOM_PORTNUM];
                
                [WeXPorgressHUD hideLoading];
                
                for (UIViewController *controller in self.navigationController.viewControllers) {
                    if ([controller isKindOfClass:[self.fromVc class]]) {
                        [self.navigationController popToViewController:self.fromVc animated:YES];
                    }
                }
                
                
//              if (_getAllDeleteGroup) {
//                dispatch_group_leave(_getAllDeleteGroup);
//              }else{}
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
                //            [WeXPorgressHUD hideLoading];
                //            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
         }

        }
    }
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
         
         // 合约定义说明
         NSString* abiJson = WEX_IPFS_ABI_DELETE_KEY ;
         NSString* abiParamsValues = @"[]";
         WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
         // 合约地址
         NSString* abiAddress = _contractAddress;
         WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
         [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:cacheModel.walletPrivateKey responseBlock:^(id response)
          {
              WEXNSLOG(@"rawTransaction=%@",response);
               NSString *rawTransaction = response;
              [[WXPassHelper instance] initProvider:WEX_IPFS_KEYHASH_URL responseBlock:^(id response) {
                  [[WXPassHelper instance] sendRawTransaction:rawTransaction responseBlock:^(id response) {
                      WEXNSLOG(@"reponse = %@",response);
                      NSString *txHash = response;
                      _txHash = txHash;
                      [self createReceiptResultRequest:txHash];
                      
                  }];
              }];
          }];
     }];
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -密码
- (void)configLocalSafetyView{
//    没有密码
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    if (model.passwordType == WeXPasswordTypeNone) {
      [self deleteData];
    } else{
     WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
     manager.delegate = self;
     [manager loadPassword];
     _manager = manager;
    }
    
}

#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerVerifySuccess{
     [self deleteData];
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
