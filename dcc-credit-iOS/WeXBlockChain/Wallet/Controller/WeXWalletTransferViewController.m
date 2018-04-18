//
//  WeXWalletTransferViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletTransferViewController.h"
#import "WeXWalletTransferCell.h"
#import "WeXWalletInfuraGetGasPriceAdapter.h"

#import "WeXWalletTranstionDetailView.h"
#import "WeXWalletETHTranstionDetailView.h"
#import "WeXWalletAllTranstionDetailView.h"
#import "WeXScanViewController.h"

#import "WeXWalletDigitalAssetDetailController.h"
#import "WeXWalletTransferResultManager.h"


#define kEthTransDetailViewHeight 450
#define ktransDetailViewHeight 450

#import "AFHTTPSessionManager.h"




@interface WeXWalletTransferViewController ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate,WeXPasswordManagerDelegate>
{
    UITableView *_tableView;
    UIView *_detailCoverView;//蒙版
    UIView *_coverView;//蒙版
    WeXWalletTranstionDetailView *_transDetailView;
    WeXWalletETHTranstionDetailView *_ethTransDetailView;
    WeXWalletAllTranstionDetailView *_allTransDetailView;
    
    UITextField *_gasPriceTextField;
    UITextField *_toAddressTextField;
    UITextField *_valueTextField;
    UITextField *_gasLimitTextField;
    UILabel *_costLabel;
    CGFloat _cost;//最高矿工费
    WeXPasswordCacheModal *_urserModel;
    
    NSString *_gasLimit;
    NSString *_balace;
    NSString *_ethBalace;
    
    WeXPasswordCacheModal *_model;
    
    WeXPasswordManager *_manager;
}

@property (nonatomic,strong)WeXWalletInfuraGetGasPriceAdapter *gasPriceAdapter;

@end

@implementation WeXWalletTransferViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self commonInit];
    [self setupSubViews];
    [self getGasPrice];
   
    
  
}

- (void)createGetNonceRequest
{
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/html",nil];
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\',\'%@\']",[WexCommonFunc getFromAddress],@"pending"];
    NSLog(@"pararms=%@",pararms);
    NSArray *array = @[[WexCommonFunc getFromAddress],@"pending"];
    NSData *data = [NSJSONSerialization dataWithJSONObject:array options:0 error:nil];
    
    NSString *str1 = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
     NSLog(@"str1=%@",str1);

    NSString *newStr =  [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    
    NSLog(@"newStr=%@",newStr);
    NSDictionary *param = @{
                            @"params": str1,
                            @"token":@"eld3bpi3h5A1Jzbgyafd"
                            };
    [manager GET:@"https://api.infura.io/v1/jsonrpc/mainnet/eth_getTransactionCount" parameters:param progress:^(NSProgress * _Nonnull downloadProgress) {
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"responseObject=%@",responseObject);
        NSString *result = [responseObject objectForKey:@"result"];
         unsigned long red = strtoul([result UTF8String],0,16);
        NSLog(@"red=%lu",red);
        
//        NSString *nonce = [NSString stringWithFormat:@"%lu",red];
       NSNumber *nonce = [NSNumber numberWithLong:red];
        
        NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:@"1000000000000000000"] stringValue];
        NSString *gasprice = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:@"1000000000"] stringValue];
        [[WXPassHelper instance] sendETHTransactionWithContractToAddress:_toAddressTextField.text privateKey:_urserModel.walletPrivateKey value:value gasPrice:gasprice gasLimit:_gasLimitTextField.text nonce:nonce remark:@"" responseBlock:^(id response) {
            NSLog(@"bltx=%@",response);
            [WeXPorgressHUD hideLoading];
            //转账失败
            if([response isKindOfClass:[NSDictionary class]])
            {
                [WeXPorgressHUD hideLoading];
                [WeXPorgressHUD showText:@"转账失败!" onView:self.view];
                return;
            };
            
            if (response) {
                NSString *txHash = [NSString stringWithFormat:@"%@",response];
                WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
                [manager.dataDict setValue:_valueTextField.text forKey:txHash];
            }
            
            [WeXPorgressHUD showText:@"转账申请已提交，请耐心等待转账结果。" onView:self.view];
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                for (UIViewController *ctrl in self.navigationController.viewControllers) {
                    if ([ctrl isKindOfClass:[WeXWalletDigitalAssetDetailController class]]) {
                        [self.navigationController popToViewController:ctrl animated:YES];
                    }
                }
            });
            
        }];
        
        
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"Error: %@", error);
    }];
}


- (void)commonInit{
    
    _urserModel = [WexCommonFunc getPassport];
     NSLog(@"from=%@",[_urserModel.keyStore objectForKey:@"address"]);
}

- (void)getGasPrice{
    
        // 初始化以太坊容器
        [[WXPassHelper instance] initPassHelperBlock:^(id response) {
            if(response!=nil)
            {
                NSError* error=response;
                NSLog(@"容器加载失败:%@",error);
                return;
            }
            /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
            [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {

                [[WXPassHelper instance] getGasPriceResponseBlock:^(id response) {
                    NSLog(@"gasprice:=%@",response);
                    NSDecimalNumber *gasPrice = [WexCommonFunc stringWithOriginString:response dividString:NINE_ZERO];
                    _gasPriceTextField.text = [NSString stringWithFormat:@"%.0f",[gasPrice floatValue]];
//                    _toAddressTextField.text = TEST_LU_ADDRESS;
                }];

            }];
        }];

    

}

- (void)createGetBalaceRequest{
    
    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             [[WXPassHelper instance] getETHBalanceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
                 NSString *balace = [WexCommonFunc formatterStringWithContractBalance:response decimals:18];
                 _ethTransDetailView.balanceLabel.text = [NSString stringWithFormat:@"%@ETH",balace];
                 _balace = balace;
             }];
         }];
    }
   else if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response)
         {
             //abi方法
             NSString *abiJson = WEX_ERC20_ABI_BALANCE;
             //参数为需要查询的地址
             NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
                 [[WXPassHelper instance] callContractAddress:[WexCommonFunc getFTCContractAddress] data:response responseBlock:^(id response) {
                     NSDictionary *responseDict = response;
                     NSString * originBalance =[responseDict objectForKey:@"result"];
                     NSString * ethException =[responseDict objectForKey:@"ethException"];
                     if (![ethException isEqualToString:@"ethException"]) {
                         NSLog(@"balance=%@",originBalance);
                         originBalance = [NSString stringWithFormat:@"%@",originBalance];
                         NSString *balace = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                         _balace = balace;
                         _allTransDetailView.balanceLabel.text = [NSString stringWithFormat:@"%@%@",balace,self.tokenModel.symbol];
                         CGFloat fee = [_valueTextField.text floatValue]*[self.feeRate floatValue];
                         _allTransDetailView.feeLabel.text = [NSString stringWithFormat:@"%.4f%@",fee,self.tokenModel.symbol];
                         _allTransDetailView.receiveLabel.text = [NSString stringWithFormat:@"%.4f%@",[_valueTextField.text floatValue]-fee,self.tokenModel.symbol];
                     }
                 }];
                 
             }];
         }];
    }
    else
    {
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             [[WXPassHelper instance] getETHBalanceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
                 NSString *balace = [WexCommonFunc formatterStringWithContractBalance:response decimals:18];
                 _ethBalace = [NSString stringWithFormat:@"%@ETH",balace];
                 _transDetailView.balanceEthLabel.text = [NSString stringWithFormat:@"%@ETH",balace];
             }];
             
         }];
        
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             NSString *abiJson = WEX_ERC20_ABI_BALANCE;
             NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
                 [[WXPassHelper instance] callContractAddress:self.tokenModel.contractAddress data:response responseBlock:^(id response) {
                     NSDictionary *responseDict = response;
                     NSString * originBalance =[responseDict objectForKey:@"result"];
                     NSString * ethException =[responseDict objectForKey:@"ethException"];
                     if (![ethException isEqualToString:@"ethException"]) {
                         NSString *balace = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                         _balace = balace;
                         _transDetailView.balanceLabel.text = [NSString stringWithFormat:@"%@%@",balace,self.tokenModel.symbol];
                     }
                 }];
                 
             }];
             
             
         }];
    }
    
}

- (void)getGasLimitRequest
{
    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
            [[WXPassHelper instance] getGasLimitWithToAddress:_toAddressTextField.text fromAddress:[WexCommonFunc getFromAddress] data:@"0x" responseBlock:^(id response) {
                NSLog(@"response=%@",response);
                if ([response isKindOfClass:[NSDictionary class]]) {
                    
                }
                else
                {
                    if (_gasLimitTextField.text.length ==0 ) {
                        _gasLimitTextField.text = [NSString stringWithFormat:@"%@",response];
                        [self configCostWithGasLimit:_gasLimitTextField.text];
                    }
                }
            }];
        }];
    }
    else
    {
        
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
            NSString *abiJson1 = WEX_ERC20_ABI_TRANSFER;
            NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
            NSString *pararms1 = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressTextField.text,value];
            
            [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson1 params:pararms1 responseBlock:^(id response){
                [[WXPassHelper instance] getGasLimitWithToAddress:self.tokenModel.contractAddress fromAddress:[WexCommonFunc getFromAddress] data:response responseBlock:^(id response) {
                    NSLog(@"response=%@",response);
                    if ([response isKindOfClass:[NSDictionary class]]) {
                        
                    }
                    else
                    {
                        if (_gasLimitTextField.text.length ==0 ) {
                            _gasLimitTextField.text = [NSString stringWithFormat:@"%@",response];
                            [self configCostWithGasLimit:_gasLimitTextField.text];
                        }
                    }
                }];
                
            }];
        }];
        
     
    }
    
}



- (void)createTransferRequest{
    
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    
//    [self createGetNonceRequest];
//
//
//    return;
    
    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
        {
            //发送上链请求
            [[WXPassHelper instance] getETHNonceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
                NSLog(@"Nonce=%@",response);
                NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:@"1000000000000000000"] stringValue];
                NSString *gasprice = [[WexCommonFunc stringWithOriginString:_gasPriceTextField.text multiplyString:@"1000000000"] stringValue];
                [[WXPassHelper instance] sendETHTransactionWithContractToAddress:_toAddressTextField.text privateKey:_urserModel.walletPrivateKey value:value gasPrice:gasprice gasLimit:_gasLimitTextField.text nonce:response remark:@"" responseBlock:^(id response) {
                    NSLog(@"bltx=%@",response);
                    [WeXPorgressHUD hideLoading];
                    //转账失败
                    if([response isKindOfClass:[NSDictionary class]])
                    {
                        [WeXPorgressHUD hideLoading];
                        [WeXPorgressHUD showText:@"转账失败!" onView:self.view];
                        return;
                    };
      
                    if (response) {
                        NSString *txHash = [NSString stringWithFormat:@"%@",response];
                        WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
                        NSMutableArray *datasArray = [manager.dataDict objectForKey:self.tokenModel.symbol];
                        if (!datasArray) {
                            datasArray = [NSMutableArray array];
                        }
                        WeXWalletTransferResultModel *model = [[WeXWalletTransferResultModel alloc] init];
                        model.value = _valueTextField.text;
                        model.txhash = txHash;
                        [datasArray addObject:model];
                        [manager.dataDict setValue:datasArray forKey:self.tokenModel.symbol];
                    }
                    
                    [WeXPorgressHUD showText:@"转账申请已提交，请耐心等待转账结果。" onView:self.view];
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        for (UIViewController *ctrl in self.navigationController.viewControllers) {
                            if ([ctrl isKindOfClass:[WeXWalletDigitalAssetDetailController class]]) {
                                [self.navigationController popToViewController:ctrl animated:YES];
                            }
                        }
                    });
                    
                }];
            }];
        }];
    }
    else if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
       
         [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response)
          {
              //转账金额
               NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
              //abi方法
              NSString *abiJson = WEX_ERC20_ABI_TRANSFER;
              //参数为接收方地址和金额
              NSString *pararms = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressTextField.text,value];
              //私钥
              NSString *privateKey = _urserModel.walletPrivateKey;
              [[WXPassHelper instance] sendPrivateTransactionWithContractToAddress:[WexCommonFunc getFTCContractAddress] abiJson:abiJson privateKey:privateKey params:pararms responseBlock:^(id response) {
                  NSLog(@"txhash=%@",response);
                  [WeXPorgressHUD hideLoading];
                  //转账失败
                  if([response isKindOfClass:[NSDictionary class]])
                  {
                      [WeXPorgressHUD hideLoading];
                      [WeXPorgressHUD showText:@"转账失败!" onView:self.view];
                      return;
                  };
                  if (response) {
                      NSString *txHash = [NSString stringWithFormat:@"%@",response];
                      WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
                      NSMutableArray *datasArray = [manager.dataDict objectForKey:self.tokenModel.symbol];
                      if (!datasArray) {
                          datasArray = [NSMutableArray array];
                      }
                      WeXWalletTransferResultModel *model = [[WeXWalletTransferResultModel alloc] init];
                      model.value = _valueTextField.text;
                      model.txhash = txHash;
                      [datasArray addObject:model];
                      [manager.dataDict setValue:datasArray forKey:self.tokenModel.symbol];
                  }
                  
                  [WeXPorgressHUD showText:@"转账申请已提交，请耐心等待转账结果。" onView:self.view];
                  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                      for (UIViewController *ctrl in self.navigationController.viewControllers) {
                          if ([ctrl isKindOfClass:[WeXWalletDigitalAssetDetailController class]]) {
                              [self.navigationController popToViewController:ctrl animated:YES];
                          }
                      }
                  });
              }];
          }];
      
    }
    else
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             [[WXPassHelper instance] getETHNonceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
//                 NSLog(@"Nonce=%@",response);
//                 NSString *nonce = [NSString stringWithFormat:@"%@",response];
                 NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:@"1000000000000000000"] stringValue];
                 NSString *gasprice = [[WexCommonFunc stringWithOriginString:_gasPriceTextField.text multiplyString:@"1000000000"] stringValue];
                 NSString *abiJson = WEX_ERC20_ABI_TRANSFER;
                 NSString *pararms = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressTextField.text,value];
                 [[WXPassHelper instance] sendERC20TransactionWithContractToAddress:self.tokenModel.contractAddress abiJson:abiJson privateKey:_urserModel.walletPrivateKey params:pararms gasPrice:gasprice gasLimit:_gasLimitTextField.text nonce:response responseBlock:^(id response) {
                     NSLog(@"response=%@",response);
                     [WeXPorgressHUD hideLoading];
                     //转账失败
                     if([response isKindOfClass:[NSDictionary class]])
                     {
                         [WeXPorgressHUD hideLoading];
                         [WeXPorgressHUD showText:@"转账失败!" onView:self.view];
                         return;
                     };
                     if (response) {
                         NSString *txHash = [NSString stringWithFormat:@"%@",response];
                         WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
                         NSMutableArray *datasArray = [manager.dataDict objectForKey:self.tokenModel.symbol];
                         if (!datasArray) {
                             datasArray = [NSMutableArray array];
                         }
                         WeXWalletTransferResultModel *model = [[WeXWalletTransferResultModel alloc] init];
                         model.value = _valueTextField.text;
                         model.txhash = txHash;
                         [datasArray addObject:model];
                         [manager.dataDict setValue:datasArray forKey:self.tokenModel.symbol];
                     }
                     
                     [WeXPorgressHUD showText:@"转账申请已提交，请耐心等待转账结果。" onView:self.view];
                     dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                         for (UIViewController *ctrl in self.navigationController.viewControllers) {
                             if ([ctrl isKindOfClass:[WeXWalletDigitalAssetDetailController class]]) {
                                 [self.navigationController popToViewController:ctrl animated:YES];
                             }
                         }
                     });
                     
                 }];
                 
             }];
         }];
    }
    
}


//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = [NSString stringWithFormat:@"%@转账",self.tokenModel.symbol];
    titleLabel.font = [UIFont systemFontOfSize:20];
    titleLabel.textColor = ColorWithLabelDescritionBlack;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@15);
    }];
    
    
    _tableView = [[UITableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 50;
    _tableView.scrollEnabled = NO;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(20);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
    if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        UILabel *noMoreLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, 44)];
        noMoreLabel.text = [NSString stringWithFormat:@"  FTC转账手续费%.0f%@",[self.feeRate floatValue]*1000,@"‰"];
        noMoreLabel.textAlignment = NSTextAlignmentLeft;
        noMoreLabel.font = [UIFont systemFontOfSize:14];
        noMoreLabel.textColor = ColorWithButtonRed;
        _tableView.tableFooterView = noMoreLabel;
    }
    
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapClick)];
    [_tableView addGestureRecognizer:tap];
    
    WeXCustomButton *nextBtn = [WeXCustomButton button];
    [nextBtn setTitle:@"下一步" forState:UIControlStateNormal];
    [nextBtn addTarget:self action:@selector(nextBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:nextBtn];
    [nextBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@170);
        make.height.equalTo(@50);
    }];
    
    
}


- (void)nextBtnClick{
    
     [self.view endEditing:YES];
    
    if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        if (_toAddressTextField.text.length == 0||[_toAddressTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:@"收款人地址不能为空!" onView:self.view];
            return;
        }
        if (_valueTextField.text.length == 0||[_valueTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:@"转账金额不能为空!" onView:self.view];
            return;
        }
    }
    else
    {
        if (_toAddressTextField.text.length == 0||[_toAddressTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:@"收款人地址不能为空!" onView:self.view];
            return;
        }
        if (_valueTextField.text.length == 0||[_valueTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:@"转账金额不能为空!" onView:self.view];
            return;
        }
        if (_gasPriceTextField.text.length == 0||[_gasPriceTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:@"GasPrice不能为空!" onView:self.view];
            return;
        }
        if (_gasLimitTextField.text.length == 0||[_gasLimitTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:@"GasLimit不能为空!" onView:self.view];
            return;
        }
    }
    
   
    
   
    
    //校验gaslimit
    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
            [[WXPassHelper instance] getGasLimitWithToAddress:_toAddressTextField.text fromAddress:[_urserModel.keyStore objectForKey:@"address"] data:@"0x" responseBlock:^(id response) {
                NSLog(@"response=%@",response);
                //异常情况
                if ([response isKindOfClass:[NSDictionary class]]) {
                    [WeXPorgressHUD showText:@"服务器异常!" onView:self.view];
                }
                else
                {
                    NSString *gasLimit1 = _gasLimitTextField.text;
                    NSString *gasLimit2 = [NSString stringWithFormat:@"%@",response];
                    //用户输入的小于网络获取的
                    if ([gasLimit1 floatValue] < [gasLimit2 floatValue]) {
                        UIAlertAction *alert = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                        }];
                        UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:@"提示" message:@"Gas Limit不足请重新设置" preferredStyle:UIAlertControllerStyleAlert];
                        [alertCtrl addAction:alert];
                        [self presentViewController:alertCtrl animated:YES completion:nil];
                    }
                    else
                    {
                        [self createTransferDetailView];
                    }
                }
               
            }];
        }];
    }
    else if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        [self createTransferDetailView];

    }
    else
    {
        
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
            NSString *abiJson1 = WEX_ERC20_ABI_TRANSFER;
            NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
            NSString *pararms1 = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressTextField.text,value];
            
            [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson1 params:pararms1 responseBlock:^(id response){
                [[WXPassHelper instance] getGasLimitWithToAddress:self.tokenModel.contractAddress fromAddress:[WexCommonFunc getFromAddress] data:response responseBlock:^(id response) {
                    NSLog(@"response=%@",response);
                    //异常情况
                    if ([response isKindOfClass:[NSDictionary class]]) {
                        [WeXPorgressHUD showText:@"服务器异常!" onView:self.view];
                    }
                    else
                    {
                        NSString *gasLimit1 = _gasLimitTextField.text;
                        NSString *gasLimit2 = [NSString stringWithFormat:@"%@",response];
                        //用户输入的小于网络获取的
                        if ([gasLimit1 floatValue] < [gasLimit2 floatValue]) {
                            UIAlertAction *alert = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                            }];
                            UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:@"提示" message:@"Gas Limit不足请重新设置" preferredStyle:UIAlertControllerStyleAlert];
                            [alertCtrl addAction:alert];
                            [self presentViewController:alertCtrl animated:YES completion:nil];
                        }
                        else
                        {
                            [self createTransferDetailView];
                        }
                    }
                   
                }];
                
            }];
        }];
        
    }
   
}

- (void)createTransferDetailView{
    //创建蒙版
    _detailCoverView = [[UIView alloc] initWithFrame:self.view.bounds];
    _detailCoverView.backgroundColor = [UIColor blackColor];
    _detailCoverView.alpha = COVER_VIEW_ALPHA;
    [self.view addSubview:_detailCoverView];
    
    if([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
        //创建注册试图
        WeXWalletETHTranstionDetailView  *ethTransDetailView = [[WeXWalletETHTranstionDetailView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, kEthTransDetailViewHeight)];
        ethTransDetailView.fromLabel.text = [_urserModel.keyStore objectForKey:@"address"];
        ethTransDetailView.toLabel.text = _toAddressTextField.text;
        ethTransDetailView.valueLabel.text = [NSString stringWithFormat:@"%@%@",_valueTextField.text,self.tokenModel.symbol];
        ethTransDetailView.costLabel.text = _costLabel.text;
        ethTransDetailView.highPayLabel.text = [NSString stringWithFormat:@"最高支付金额  %.4f%@",[_valueTextField.text floatValue]+_cost,self.tokenModel.symbol];
        ethTransDetailView.backgroundColor = [UIColor whiteColor];
        [self.view addSubview:ethTransDetailView];
        _ethTransDetailView = ethTransDetailView;
        //动画
        [UIView animateWithDuration:0.5 animations:^{
            ethTransDetailView.frame = CGRectMake(0, kScreenHeight-kEthTransDetailViewHeight, kScreenWidth, kEthTransDetailViewHeight);
        }];
        
        __weak typeof(ethTransDetailView) weakTransDetailView = ethTransDetailView;
        //点击取消按钮
        ethTransDetailView.cancelBtnBlock = ^{
            [UIView animateWithDuration:0.5 animations:^{
                weakTransDetailView.frame = CGRectMake(0, kScreenHeight, kScreenWidth, kEthTransDetailViewHeight);
            }];
            //删除蒙版
            [_detailCoverView removeFromSuperview];
            [weakTransDetailView dismiss];
        };
        //点击创建按钮
        ethTransDetailView.confirmBtnBlock  = ^{
            
            if ([_valueTextField.text floatValue]+_cost  > [_balace floatValue]) {
                UIAlertAction *alert = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                }];
                UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:@"提示" message:@"持有量不足,请核对后重新提交!" preferredStyle:UIAlertControllerStyleAlert];
                [alertCtrl addAction:alert];
                [self presentViewController:alertCtrl animated:YES completion:nil];
//                [WeXPorgressHUD showText:@"持有量不足,请核对后重新提交!" onView:self.view];
                return;
            }
            
            [self configLocalSafetyView];
            
          
            
        };
    }
    else if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        
        //创建注册试图
        WeXWalletAllTranstionDetailView  *transDetailView = [[WeXWalletAllTranstionDetailView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, ktransDetailViewHeight-30)];
        transDetailView.fromLabel.text = [WexCommonFunc getFromAddress];
        transDetailView.toLabel.text = _toAddressTextField.text;
        transDetailView.valueLabel.text = [NSString stringWithFormat:@"%.4f%@",[_valueTextField.text floatValue],self.tokenModel.symbol];
        transDetailView.backgroundColor = [UIColor whiteColor];
        [self.view addSubview:transDetailView];
        _allTransDetailView = transDetailView;
        
        __weak typeof(transDetailView) weakTransDetailView = transDetailView;
        
        //动画
        [UIView animateWithDuration:0.5 animations:^{
            transDetailView.frame = CGRectMake(0, kScreenHeight-ktransDetailViewHeight, kScreenWidth, ktransDetailViewHeight);
        }];
        
        //点击取消按钮
        transDetailView.cancelBtnBlock = ^{
            [UIView animateWithDuration:0.5 animations:^{
                weakTransDetailView.frame = CGRectMake(0, kScreenHeight, kScreenWidth, ktransDetailViewHeight);
            }];
            //删除蒙版
            [_detailCoverView removeFromSuperview];
            [weakTransDetailView dismiss];
        };
        
        //点击创建按钮
        transDetailView.confirmBtnBlock  = ^{
            if ([_valueTextField.text floatValue] > [_balace floatValue]) {
                UIAlertAction *alert = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                }];
                UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:@"提示" message:@"持有量不足,请核对后重新提交!" preferredStyle:UIAlertControllerStyleAlert];
                [alertCtrl addAction:alert];
                [self presentViewController:alertCtrl animated:YES completion:nil];
                return;
            }
            
            [self configLocalSafetyView];
            
            
        };
    }
    else
    {
        //创建注册试图
        WeXWalletTranstionDetailView  *transDetailView = [[WeXWalletTranstionDetailView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, ktransDetailViewHeight)];
        transDetailView.fromLabel.text = [WexCommonFunc getFromAddress];
        transDetailView.toLabel.text = _toAddressTextField.text;
        transDetailView.valueLabel.text = [NSString stringWithFormat:@"%@%@",_valueTextField.text,self.tokenModel.symbol];
        transDetailView.costLabel.text = _costLabel.text;
        transDetailView.backgroundColor = [UIColor whiteColor];
        [self.view addSubview:transDetailView];
        _transDetailView = transDetailView;
        
        __weak typeof(transDetailView) weakTransDetailView = transDetailView;
        
        //动画
        [UIView animateWithDuration:0.5 animations:^{
            _transDetailView.frame = CGRectMake(0, kScreenHeight-ktransDetailViewHeight, kScreenWidth, ktransDetailViewHeight);
        }];
        
        //点击取消按钮
        transDetailView.cancelBtnBlock = ^{
            [UIView animateWithDuration:0.5 animations:^{
                weakTransDetailView.frame = CGRectMake(0, kScreenHeight, kScreenWidth, ktransDetailViewHeight);
            }];
            //删除蒙版
            [_detailCoverView removeFromSuperview];
            [weakTransDetailView dismiss];
        };
        
        //点击创建按钮
        transDetailView.confirmBtnBlock  = ^{
            if ([_valueTextField.text floatValue] > [_balace floatValue]|| _cost > [_ethBalace floatValue]) {
                UIAlertAction *alert = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                }];
                UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:@"提示" message:@"持有量不足,请核对后重新提交!" preferredStyle:UIAlertControllerStyleAlert];
                [alertCtrl addAction:alert];
                [self presentViewController:alertCtrl animated:YES completion:nil];
                return;
            }
            
            [self configLocalSafetyView];
            
            
        };
    }
    
    [self createGetBalaceRequest];
    
}



- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        return 2;
    }
    return 5;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXWalletTransferCell * cell = nil;
    if (indexPath.row == 0) {
        WeXWalletTransferWithButtonCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:2];
        cell.contentTextField.placeholder = @"收款人钱包地址";
        [cell.scanButton addTarget:self action:@selector(scanButtonClick) forControlEvents:UIControlEventTouchUpInside];
        _toAddressTextField = cell.contentTextField;
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        [_toAddressTextField setValue:ColorWithLabelDescritionBlack forKeyPath:@"_placeholderLabel.textColor"];
        cell.contentTextField.textColor = ColorWithLabelDescritionBlack;

        return cell;
    }
    else if (indexPath.row == 1)
    {
        WeXWalletTransferNormalCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:1];
        cell.contentTextField.placeholder = @"转账金额";
        [cell.contentTextField setValue:ColorWithLabelDescritionBlack forKeyPath:@"_placeholderLabel.textColor"];
        cell.contentTextField.textColor = ColorWithLabelDescritionBlack;
        cell.backgroundColor = [UIColor clearColor];
        _valueTextField = cell.contentTextField;
        _valueTextField.delegate = self;
        _valueTextField.keyboardType = UIKeyboardTypeDecimalPad;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        return cell;
    }
    else if (indexPath.row == 2)
    {
        WeXWalletTransferWithLabelCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:3];
        cell.contentTextField.placeholder = @"自定义Gas Price";
        [cell.contentTextField setValue:ColorWithLabelDescritionBlack forKeyPath:@"_placeholderLabel.textColor"];
        cell.contentTextField.textColor = ColorWithLabelDescritionBlack;

        cell.backgroundColor = [UIColor clearColor];
        _gasPriceTextField = cell.contentTextField;
        _gasPriceTextField.delegate = self;
        _gasPriceTextField.keyboardType = UIKeyboardTypeDecimalPad;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;

        return cell;
    }
    else if (indexPath.row == 3)
    {
        WeXWalletTransferNormalCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:1];
        _gasLimitTextField = cell.contentTextField;
        _gasLimitTextField.placeholder = @"自定义Gas Limit";
        [cell.contentTextField setValue:ColorWithLabelDescritionBlack forKeyPath:@"_placeholderLabel.textColor"];
        cell.contentTextField.textColor = ColorWithLabelDescritionBlack;

        _gasLimitTextField.delegate = self;
        _gasLimitTextField.keyboardType = UIKeyboardTypeDecimalPad;
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;

        return cell;
    }
    else if (indexPath.row == 4)
    {
     WeXWalletTransferWithTwoLabelCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:4];
        cell.backgroundColor = [UIColor clearColor];
        _costLabel = cell.costLabel;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.costLabel.textColor = ColorWithLabelDescritionBlack;
        return cell;
    }
    return cell;
    
}

- (void)scanButtonClick{
    WeXScanViewController *ctrl = [[WeXScanViewController alloc]  init];
    ctrl.handleType = WeXScannerHandleTypeToAddress;
    ctrl.responseBlock = ^(NSString *content) {
        if (content) {
            _toAddressTextField.text = content;
        }
    };
    [self.navigationController pushViewController:ctrl animated:YES];
}


-(void)textFieldDidBeginEditing:(UITextField *)textField
{
    if (textField == _gasLimitTextField) {
        if (_toAddressTextField.text.length>0 &&_valueTextField.text.length>0) {
            [self getGasLimitRequest];
        }
    }
}

-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if(textField == _gasLimitTextField){
        NSString *comment;
        if(range.length == 0)
        {
            comment = [NSString stringWithFormat:@"%@%@",textField.text, string];
        }
        else
        {
            comment = [textField.text substringToIndex:textField.text.length -range.length];
        }
        [self configCostWithGasLimit:comment];
       
        
    }
    else if (textField == _gasPriceTextField)
    {
        NSString *comment;
        if(range.length == 0)
        {
            comment = [NSString stringWithFormat:@"%@%@",textField.text, string];
        }
        else
        {
            comment = [textField.text substringToIndex:textField.text.length -range.length];
        }
        [self configCostWithGasPrice:comment];
    }
    return YES;
}

- (void)configCostWithGasPrice:(NSString *)gasPriceString {
    _cost = [gasPriceString floatValue]*[_gasLimitTextField.text floatValue]*powf(10, -9);
    _costLabel.text = [NSString stringWithFormat:@"%.4fETH",[gasPriceString floatValue]*[_gasLimitTextField.text floatValue]*powf(10, -9)];
}


- (void)configCostWithGasLimit:(NSString *)gasLimitString {
    _cost = [gasLimitString floatValue]*[_gasPriceTextField.text floatValue]*powf(10, -9);
    _costLabel.text = [NSString stringWithFormat:@"%.4fETH",[gasLimitString floatValue]*[_gasPriceTextField.text floatValue]*powf(10, -9)];
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

- (void)tapClick{
     [self.view endEditing:YES];
}

#pragma mark -密码

- (void)configLocalSafetyView{
    //没有密码
    _model = [WexCommonFunc getPassport];
    if (_model.passwordType == WeXPasswordTypeNone) {
        [self createTransferRequest];
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
        [self createTransferRequest];
        
    });
    
}



@end
