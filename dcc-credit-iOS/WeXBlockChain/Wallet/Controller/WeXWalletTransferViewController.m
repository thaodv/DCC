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
#import "WeXWalletDccTranstionDetailView.h"
#import "WeXScanViewController.h"

#import "WeXWalletDigitalAssetDetailController.h"
#import "WeXWalletTransferResultManager.h"
#import "WeXSaveAddressBookObject.h"
#import "WeXWalletAlertWithCancelButtonView.h"

#define kDccTransDetailViewHeight 350
#define kEthTransDetailViewHeight 450
#define ktransDetailViewHeight 450

#import "AFHTTPSessionManager.h"
#import "WeXAddressBookController.h"
#import "WeXPayCoinViewController.h"


@interface WeXWalletTransferViewController ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate,WeXPasswordManagerDelegate>
{
    UITableView *_tableView;
    UIView *_detailCoverView;//蒙版
    UIView *_coverView;//蒙版
    WeXWalletTranstionDetailView *_transDetailView;
    WeXWalletETHTranstionDetailView *_ethTransDetailView;
    WeXWalletAllTranstionDetailView *_allTransDetailView;
    WeXWalletDccTranstionDetailView *_dccTransDetailView;
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
    
    NSNumber *_nonce;
    NSString *_gasPrice;
}

@property (nonatomic,strong)WeXWalletInfuraGetGasPriceAdapter *gasPriceAdapter;

@end

@implementation WeXWalletTransferViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    BOOL isChinese = [[WeXLocalizedManager shareManager] isChinese];
    self.navigationItem.title = [NSString stringWithFormat:@"%@%@%@", self.tokenModel.symbol, isChinese ? @"" :@" ", WeXLocalizedString(@"转账") ];
    [self setNavigationNomalBackButtonType];
    [self setNavitem];
    [self commonInit];
    [self setupSubViews];
    if (self.transferType == WeXWalletTransferTypeNormal) {
       [self getGasPrice];
    }
    //2018.8.7 还款流程优化 (自动获取gasprice,gaslimit)
    if (self.useOriginalAmount) {
        [self getGasPrice];
//        [self getGasLimitRequest];
    }
}

- (void)commonInit{
    
    _urserModel = [WexCommonFunc getPassport];
}

- (void)getGasPrice{
        [[WXPassHelper instance] initPassHelperBlock:^(id response) {
            if(response!=nil) {
                NSError* error=response;
                NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
                return;
            }
            [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
                [[WXPassHelper instance] getGasPriceResponseBlock:^(id response) {
                    NSDecimalNumber *gasPrice = [WexCommonFunc stringWithOriginString:response dividString:NINE_ZERO];
                    _gasPriceTextField.text = [NSString stringWithFormat:@"%.0f",[gasPrice floatValue] < 1.0?1.0:[gasPrice floatValue]];
                }];
            }];
        }];
}

- (void)createGetBalaceRequest{
    
    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
        [[WXPassHelper instance] getETHBalance2WithContractAddress:[WexCommonFunc getFromAddress] type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
            NSString *balace = [WexCommonFunc formatterStringWithContractBalance:response decimals:18];
            _ethTransDetailView.balanceLabel.text = [NSString stringWithFormat:@"%@ETH",balace];
            _balace = balace;
        }];
    }
    else if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        NSString *abiJson = WEX_ERC20_ABI_BALANCE;
        //参数为需要查询的地址
        NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
        [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response)
         {
             [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getDCCContractAddress] data:response type:WEX_DCC_NODE_SERVER responseBlock:^(id response)
              {
                  NSDictionary *responseDict = response;
                  NSString * originBalance =[responseDict objectForKey:@"result"];
                  NSString * ethException =[responseDict objectForKey:@"ethException"];
                  if (![ethException isEqualToString:@"ethException"]) {
                      NSLog(@"balance=%@",originBalance);
                      originBalance = [NSString stringWithFormat:@"%@",originBalance];
                      NSString *balace = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                      _balace = balace;
                      _dccTransDetailView.balanceLabel.text = [NSString stringWithFormat:@"%@%@",balace,self.tokenModel.symbol];
                  }
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
    _transDetailView.userInteractionEnabled = NO;
    _ethTransDetailView.userInteractionEnabled = NO;
    _allTransDetailView.userInteractionEnabled = NO;
    _dccTransDetailView.userInteractionEnabled = NO;
    
    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
        {
            [[WXPassHelper instance] getETHNonceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
                _nonce = response;
                if (self.transferType == WeXWalletTransferTypeEdit) {
                    _nonce = [NSNumber numberWithInteger:[self.recordModel.nonce integerValue]];
                }
                NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
                NSString *gasprice = [[WexCommonFunc stringWithOriginString:_gasPriceTextField.text multiplyString:NINE_ZERO] stringValue];
                _gasPrice = gasprice;
                [[WXPassHelper instance] sendETHTransactionWithContractToAddress:_toAddressTextField.text privateKey:_urserModel.walletPrivateKey value:value gasPrice:gasprice gasLimit:_gasLimitTextField.text nonce:_nonce remark:@"" responseBlock:^(id response) {
                    [WeXPorgressHUD hideLoading];
                    if([response isKindOfClass:[NSDictionary class]])
                    {
                        [WeXPorgressHUD hideLoading];
                        [WeXPorgressHUD showText:WeXLocalizedString(@"提交失败") onView:self.view];
                        [self jumpToAssetDetailController];
                        _ethTransDetailView.userInteractionEnabled = YES;
                        return;
                    };
      
                    if (response) {
                        NSString *txHash = [NSString stringWithFormat:@"%@",response];
                        [self savePendingTransferModelWithTxhash:txHash];
                        [WeXSaveAddressBookObject saveLatelyTransactionAddress:_toAddressTextField.text];//存储交易地址
                    }
                    [WeXPorgressHUD showText:WeXLocalizedString(@"转账申请已提交，请耐心等待转账结果。") onView:self.view];
                    [self jumpToAssetDetailController];
                    
                }];
            }];
        }];
    }
    else if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        [[WXPassHelper instance] initProvider:WEX_DCC_NODE_SERVER responseBlock:^(id response)
         {
             //转账金额
             NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
             //abi方法
             NSString *abiJson = WEX_ERC20_ABI_TRANSFER;
             //参数为接收方地址和金额
             NSString *pararms = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressTextField.text,value];
             //私钥
             NSString *privateKey = _urserModel.walletPrivateKey;
             [[WXPassHelper instance] sendPrivateTransactionWithContractToAddress:[WexCommonFunc getDCCContractAddress] abiJson:abiJson privateKey:privateKey params:pararms responseBlock:^(id response) {
                 [WeXPorgressHUD hideLoading];
                 if([response isKindOfClass:[NSDictionary class]])
                 {
                     [WeXPorgressHUD hideLoading];
                     [WeXPorgressHUD showText:WeXLocalizedString(@"提交失败") onView:self.view];
                     [self jumpToAssetDetailController];
                     _dccTransDetailView.userInteractionEnabled = YES;
                     return;
                 };
                 if (response) {
                     NSString *txHash = [NSString stringWithFormat:@"%@",response];
                     [self savePendingTransferModelWithTxhash:txHash];
                     [WeXSaveAddressBookObject saveLatelyTransactionAddress:_toAddressTextField.text];//存储交易地址
                 }
                 [WeXPorgressHUD showText:WeXLocalizedString(@"转账申请已提交，请耐心等待转账结果。") onView:self.view];
                 [self jumpToAssetDetailController];
             }];
         }];
    }
    else
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             [[WXPassHelper instance] getETHNonceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
                 _nonce = response;
                 if (self.transferType == WeXWalletTransferTypeEdit) {
                     _nonce = [NSNumber numberWithInteger:[self.recordModel.nonce integerValue]];
                 }
                 NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
                 NSString *gasprice = [[WexCommonFunc stringWithOriginString:_gasPriceTextField.text multiplyString:NINE_ZERO] stringValue];
                 _gasPrice = gasprice;
                 NSString *abiJson = WEX_ERC20_ABI_TRANSFER;
                 NSString *pararms = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressTextField.text,value];
                 [[WXPassHelper instance] sendERC20TransactionWithContractToAddress:self.tokenModel.contractAddress abiJson:abiJson privateKey:_urserModel.walletPrivateKey params:pararms gasPrice:gasprice gasLimit:_gasLimitTextField.text nonce:_nonce responseBlock:^(id response) {
                     [WeXPorgressHUD hideLoading];
                     if([response isKindOfClass:[NSDictionary class]])
                     {
                         [WeXPorgressHUD hideLoading];
                         [WeXPorgressHUD showText:WeXLocalizedString(@"提交失败") onView:self.view];
                         [self jumpToAssetDetailController];
                
                         _transDetailView.userInteractionEnabled = YES;
                         return;
                     };
                     if (response) {
                         NSString *txHash = [NSString stringWithFormat:@"%@",response];
                         [self savePendingTransferModelWithTxhash:txHash];
                         [WeXSaveAddressBookObject saveLatelyTransactionAddress:_toAddressTextField.text];//存储交易地址
                     }
                     [WeXPorgressHUD showText:WeXLocalizedString(@"转账申请已提交，请耐心等待转账结果。") onView:self.view];
                     [self jumpToAssetDetailController];
                     
                 }];
                 
             }];
         }];
    }
    
}


//资产详情页
- (void)jumpToAssetDetailController
{
    if (self.useOriginalAmount) {
        !self.DidTransferAmount ? : self.DidTransferAmount();
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        for (UIViewController *ctrl in self.navigationController.viewControllers) {
            if ([ctrl isKindOfClass:[WeXWalletDigitalAssetDetailController class]]) {
                [self.navigationController popToViewController:ctrl animated:YES];
            } else if ([ctrl isKindOfClass:[WeXPayCoinViewController class]]) {
                [self.navigationController popToViewController:ctrl animated:YES];
            }
        }
    });
}

- (void)savePendingTransferModelWithTxhash:(NSString *)txhash
{
    WeXWalletTransferPendingModel *model = [[WeXWalletTransferPendingModel alloc] init];
    model.from = [WexCommonFunc getFromAddress];
    model.to = _toAddressTextField.text;
    NSDate *nowTime = [NSDate date];
    NSTimeInterval timeStamp = nowTime.timeIntervalSince1970;
    model.timeStamp = [NSString stringWithFormat:@"%f",timeStamp];
    model.value =_valueTextField.text?[[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue]:nil;
    model.txhash = txhash;
    model.nonce = [_nonce stringValue];
    model.gasPrice = _gasPriceTextField.text?[[WexCommonFunc stringWithOriginString:_gasPriceTextField.text multiplyString:NINE_ZERO] stringValue]:nil;
    model.gasLimit = _gasLimitTextField.text;
    
    WeXWalletTransferResultManager *manager = [[WeXWalletTransferResultManager alloc] init];
    [manager savePendingModel:model symbol:self.isPrivateChain?[NSString stringWithFormat:@"%@_private",self.tokenModel.symbol]:self.tokenModel.symbol];
}


//初始化滚动视图
-(void)setupSubViews{
    
    _tableView = [[UITableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 50;
    _tableView.scrollEnabled = NO;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
    if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        
        UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, 100)];
        _tableView.tableFooterView = footerView;
        
        UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, 44)];
        label1.text = WeXLocalizedString(@"DCC业务链");
        label1.textAlignment = NSTextAlignmentLeft;
        label1.font = [UIFont systemFontOfSize:14];
        label1.textColor = COLOR_LABEL_DESCRIPTION;
        [footerView addSubview:label1];
        [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(footerView).offset(10);
            make.leading.equalTo(footerView).offset(15);
            make.trailing.equalTo(footerView).offset(-15);
        }];
        UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, 44)];
        label2.text = WeXLocalizedString(@"WalletDccDescriptionToastBottomText");
        label2.textAlignment = NSTextAlignmentLeft;
        label2.font = [UIFont systemFontOfSize:14];
        label2.textColor = [UIColor redColor];
        label2.numberOfLines = 0;
        [footerView addSubview:label2];
        [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(label1.mas_bottom).offset(10);
            make.leading.equalTo(footerView).offset(15);
            make.trailing.equalTo(footerView).offset(-15);
        }];
    }
    else
    {
        if (self.transferType == WeXWalletTransferTypeEdit) {
            UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, 100)];
            _tableView.tableFooterView = footerView;
            
            UILabel *label1 = [[UILabel alloc] init];
            label1.text = WeXLocalizedString(@"温馨提示：\n用区块链技术实现编辑交易的实际业务逻辑为：用一笔新的交易（Gas Price大于原交易的Gas Price）覆盖之前的转账交易。提交成功后，原交易状态为【已撤销】，系统不显示，只显示当前提交的交易记录。");
            label1.textAlignment = NSTextAlignmentLeft;
            label1.font = [UIFont systemFontOfSize:14];
            label1.textColor = COLOR_LABEL_DESCRIPTION;
            label1.numberOfLines = 0;
            [footerView addSubview:label1];
            [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(footerView).offset(10);
                make.leading.equalTo(footerView).offset(15);
                make.trailing.equalTo(footerView).offset(-15);
            }];
        }
    }
    
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapClick)];
    [_tableView addGestureRecognizer:tap];
    
    WeXCustomButton *nextBtn = [WeXCustomButton button];
    [nextBtn setTitle:WeXLocalizedString(@"下一步") forState:UIControlStateNormal];
    [nextBtn addTarget:self action:@selector(nextBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:nextBtn];
    [nextBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@50);
    }];
    
    
}


- (void)nextBtnClick{
    
     [self.view endEditing:YES];
    
    if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        if (_toAddressTextField.text.length == 0||[_toAddressTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"收款人地址不能为空!") onView:self.view];
            return;
        }
        if (_valueTextField.text.length == 0||[_valueTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"转账金额不能为空!") onView:self.view];
            return;
        }
    }
    else
    {
        if (_toAddressTextField.text.length == 0||[_toAddressTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"收款人地址不能为空!") onView:self.view];
            return;
        }
        if (_valueTextField.text.length == 0||[_valueTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"转账金额不能为空!") onView:self.view];
            return;
        }
        if (_gasPriceTextField.text.length == 0||[_gasPriceTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"GasPrice不能为空!") onView:self.view];
            return;
        }
        if (_gasLimitTextField.text.length == 0||[_gasLimitTextField.text isEqualToString:@""]) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"GasLimit不能为空!") onView:self.view];
            return;
        }
    }
    
   
    if (self.transferType == WeXWalletTransferTypeEdit) {
         NSDecimalNumber *gasPrice = [WexCommonFunc stringWithOriginString:self.recordModel.gasPrice dividString:NINE_ZERO];
        if ([_gasPriceTextField.text floatValue] <= [gasPrice floatValue]) {
            WeXWalletAlertWithCancelButtonView *alertView = [[WeXWalletAlertWithCancelButtonView alloc] initWithFrame:self.view.bounds];
            NSString *content = [NSString stringWithFormat:WeXLocalizedString(@"Gas Price>%@Gwei(原交易的Gas Price)，才可以提交编辑的交易。"),gasPrice];
            alertView.contentLabel.text = content;
            [self.view addSubview:alertView];
            return;
        }
        
    }
    
    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
//        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
//            [[WXPassHelper instance] getGasLimitWithToAddress:_toAddressTextField.text fromAddress:[_urserModel.keyStore objectForKey:@"address"] data:@"0x" responseBlock:^(id response) {
//                NSLog(@"response=%@",response);
//                //异常情况
//                if ([response isKindOfClass:[NSDictionary class]]) {
//                    [WeXPorgressHUD showText:WeXLocalizedString(@"服务器异常!") onView:self.view];
//                }
//                else
//                {
//                    NSString *gasLimit1 = _gasLimitTextField.text;
//                    NSString *gasLimit2 = [NSString stringWithFormat:@"%@",response];
//                    //用户输入的小于网络获取的
//                    if ([gasLimit1 floatValue] < [gasLimit2 floatValue]) {
//                        UIAlertAction *alert = [UIAlertAction actionWithTitle:WeXLocalizedString(@"确定") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
//                        }];
//                        UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:WeXLocalizedString(@"提示") message:WeXLocalizedString(@"Gas Limit不足请重新设置") preferredStyle:UIAlertControllerStyleAlert];
//                        [alertCtrl addAction:alert];
//                        [self presentViewController:alertCtrl animated:YES completion:nil];
//                    }
//                    else
//                    {
//                        [self createTransferDetailView];
//                    }
//                }
//
//            }];
//        }];
            [self createTransferDetailView];

    }
    else if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        [self createTransferDetailView];
    }
    else
    {
        
        [self createTransferDetailView];
        
//        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
//            NSString *abiJson1 = WEX_ERC20_ABI_TRANSFER;
//            NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
//            NSString *pararms1 = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressTextField.text,value];
//            
//            [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson1 params:pararms1 responseBlock:^(id response){
//                [[WXPassHelper instance] getGasLimitWithToAddress:self.tokenModel.contractAddress fromAddress:[WexCommonFunc getFromAddress] data:response responseBlock:^(id response) {
//                    NSLog(@"response=%@",response);
//                    //异常情况
//                    if ([response isKindOfClass:[NSDictionary class]]) {
//                        [WeXPorgressHUD showText:WeXLocalizedString(@"服务器异常!") onView:self.view];
//                    }
//                    else
//                    {
//                        NSString *gasLimit1 = _gasLimitTextField.text;
//                        NSString *gasLimit2 = [NSString stringWithFormat:@"%@",response];
//                        //用户输入的小于网络获取的
//                        if ([gasLimit1 floatValue] < [gasLimit2 floatValue]) {
//                            UIAlertAction *alert = [UIAlertAction actionWithTitle:WeXLocalizedString(@"确定") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
//                            }];
//                            UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:WeXLocalizedString(@"提示") message:WeXLocalizedString(@"Gas Limit不足请重新设置") preferredStyle:UIAlertControllerStyleAlert];
//                            [alertCtrl addAction:alert];
//                            [self presentViewController:alertCtrl animated:YES completion:nil];
//                        }
//                        else
//                        {
//                            [self createTransferDetailView];
//                        }
//                    }
//                   
//                }];
//                
//            }];
//        }];
//        
    }
   
}

// MARK: - 转账2018.7.24 (弹出底部页面)
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
        ethTransDetailView.highPayLabel.text = [NSString stringWithFormat:@"%@%.4f%@",WeXLocalizedString(@"最高支付金额"),[_valueTextField.text floatValue]+_cost,self.tokenModel.symbol];
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
                UIAlertAction *alert = [UIAlertAction actionWithTitle:WeXLocalizedString(@"确定") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                }];
                UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:WeXLocalizedString(@"提示") message:WeXLocalizedString(@"持有量不足,请核对后重新提交!") preferredStyle:UIAlertControllerStyleAlert];
                [alertCtrl addAction:alert];
                [self presentViewController:alertCtrl animated:YES completion:nil];
//                [WeXPorgressHUD showText:WeXLocalizedString(@"持有量不足,请核对后重新提交!") onView:self.view];
                return;
            }
            
            [self configLocalSafetyView];
            
          
            
        };
    }
    else if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        WeXWalletDccTranstionDetailView  *transDetailView = [[WeXWalletDccTranstionDetailView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, kDccTransDetailViewHeight)];
        transDetailView.fromLabel.text = [WexCommonFunc getFromAddress];
        transDetailView.toLabel.text = _toAddressTextField.text;
        transDetailView.valueLabel.text = [NSString stringWithFormat:@"%.4f%@",[_valueTextField.text floatValue],self.tokenModel.symbol];
        transDetailView.backgroundColor = [UIColor whiteColor];
        [self.view addSubview:transDetailView];
        _dccTransDetailView = transDetailView;
        
        __weak typeof(transDetailView) weakTransDetailView = transDetailView;
        
        //动画
        [UIView animateWithDuration:0.5 animations:^{
            transDetailView.frame = CGRectMake(0, kScreenHeight-kDccTransDetailViewHeight, kScreenWidth, kDccTransDetailViewHeight);
        }];
        
        //点击取消按钮
        transDetailView.cancelBtnBlock = ^{
            [UIView animateWithDuration:0.5 animations:^{
                weakTransDetailView.frame = CGRectMake(0, kScreenHeight, kScreenWidth, kDccTransDetailViewHeight);
            }];
            [_detailCoverView removeFromSuperview];
            [weakTransDetailView dismiss];
        };
        transDetailView.confirmBtnBlock  = ^{
            if ([_valueTextField.text floatValue] > [_balace floatValue]) {
                UIAlertAction *alert = [UIAlertAction actionWithTitle:WeXLocalizedString(@"确定") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                }];
                UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:WeXLocalizedString(@"提示") message:WeXLocalizedString(@"持有量不足,请核对后重新提交!") preferredStyle:UIAlertControllerStyleAlert];
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
                UIAlertAction *alert = [UIAlertAction actionWithTitle:WeXLocalizedString(@"确定") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                }];
                UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:WeXLocalizedString(@"提示") message:WeXLocalizedString(@"持有量不足,请核对后重新提交!") preferredStyle:UIAlertControllerStyleAlert];
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
    if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
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
        cell.contentTextField.placeholder = WeXLocalizedString(@"收款人钱包地址");
        if (self.transferType == WeXWalletTransferTypeEdit) {
            cell.contentTextField.text = self.recordModel.to;
        }
        else
        {
            if(_addressStr.length>0){
                cell.contentTextField.text = _addressStr;
            }
        }
        
        [cell.scanButton addTarget:self action:@selector(goChooseAddressClick) forControlEvents:UIControlEventTouchUpInside];
        _toAddressTextField = cell.contentTextField;
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        [_toAddressTextField setValue:COLOR_LABEL_DESCRIPTION forKeyPath:@"_placeholderLabel.textColor"];
        cell.contentTextField.textColor = COLOR_LABEL_DESCRIPTION;
        
        return cell;
    }
    else if (indexPath.row == 1)
    {
        WeXWalletTransferNormalCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:1];
        cell.contentTextField.placeholder = WeXLocalizedString(@"转账金额");
        UILabel *leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,0,120,50)];
        leftLabel.text = WeXLocalizedString(@"金额");
        leftLabel.font = [UIFont systemFontOfSize:17];
        leftLabel.textColor = COLOR_LABEL_DESCRIPTION;
        cell.contentTextField.leftViewMode = UITextFieldViewModeAlways;
        cell.contentTextField.leftView = leftLabel;
        [cell.contentTextField setValue:COLOR_LABEL_DESCRIPTION forKeyPath:@"_placeholderLabel.textColor"];
        cell.contentTextField.textColor = COLOR_LABEL_DESCRIPTION;
        cell.backgroundColor = [UIColor clearColor];
        _valueTextField = cell.contentTextField;
        _valueTextField.delegate = self;
        _valueTextField.keyboardType = UIKeyboardTypeDecimalPad;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        if (self.transferType == WeXWalletTransferTypeEdit) {
            NSString *valueStr = [WexCommonFunc formatterStringWithContractBalance:self.recordModel.value decimals:18];
            cell.contentTextField.text = valueStr;
        } else {
            //2018.8.7 还款流程修改
            if (self.useOriginalAmount) {
                cell.contentTextField.text = self.recordModel.value;
            }
        }
        
        return cell;
    }
    else if (indexPath.row == 2)
    {
        WeXWalletTransferWithLabelCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:3];
        cell.contentTextField.placeholder = WeXLocalizedString(@"自定义Gas Price");
        UILabel *leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,0,120,50)];
        leftLabel.text = @"Gas Price";
        leftLabel.font = [UIFont systemFontOfSize:17];
        leftLabel.textColor = COLOR_LABEL_DESCRIPTION;
        cell.contentTextField.leftViewMode = UITextFieldViewModeAlways;
        cell.contentTextField.leftView = leftLabel;
        [cell.contentTextField setValue:COLOR_LABEL_DESCRIPTION forKeyPath:@"_placeholderLabel.textColor"];
        cell.contentTextField.textColor = COLOR_LABEL_DESCRIPTION;
        
        cell.backgroundColor = [UIColor clearColor];
        _gasPriceTextField = cell.contentTextField;
        _gasPriceTextField.delegate = self;
        _gasPriceTextField.keyboardType = UIKeyboardTypeDecimalPad;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        if (self.transferType == WeXWalletTransferTypeEdit) {
            NSDecimalNumber *gasPrice = [WexCommonFunc stringWithOriginString:self.recordModel.gasPrice dividString:NINE_ZERO];
            cell.contentTextField.text = [gasPrice stringValue];
        }
        
        return cell;
    }
    else if (indexPath.row == 3)
    {
        WeXWalletTransferNormalCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:1];
        _gasLimitTextField = cell.contentTextField;
        _gasLimitTextField.placeholder = WeXLocalizedString(@"自定义Gas Limit");
        UILabel *leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,0,120,50)];
        leftLabel.text = @"Gas Limit";
        leftLabel.font = [UIFont systemFontOfSize:17];
        leftLabel.textColor = COLOR_LABEL_DESCRIPTION;
        cell.contentTextField.leftViewMode = UITextFieldViewModeAlways;
        cell.contentTextField.leftView = leftLabel;
        [cell.contentTextField setValue:COLOR_LABEL_DESCRIPTION forKeyPath:@"_placeholderLabel.textColor"];
        cell.contentTextField.textColor = COLOR_LABEL_DESCRIPTION;
        
        _gasLimitTextField.delegate = self;
        _gasLimitTextField.keyboardType = UIKeyboardTypeDecimalPad;
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        if (self.transferType == WeXWalletTransferTypeEdit) {
            
            cell.contentTextField.text = self.recordModel.gasLimit;
        }
        
        return cell;
    }
    else if (indexPath.row == 4)
    {
        WeXWalletTransferWithTwoLabelCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransferCell" owner:self options:nil] objectAtIndex:4];
        cell.backgroundColor = [UIColor clearColor];
        _costLabel = cell.costLabel;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.costLabel.textColor = COLOR_LABEL_DESCRIPTION;
        cell.titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        cell.titleLabel.font = [UIFont systemFontOfSize:17];
        
        if (self.transferType == WeXWalletTransferTypeEdit) {
            _cost = [_gasPriceTextField.text floatValue]*[_gasLimitTextField.text floatValue]*powf(10, -9);
            _costLabel.text = [NSString stringWithFormat:@"%.4fETH",[_gasPriceTextField.text floatValue]*[_gasLimitTextField.text floatValue]*powf(10, -9)];
        }
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
//    }else if (textField == _toAddressTextField)
//    {
//        if(_addressStr.length>0){
//            _addressStr = _toAddressTextField.text;
//        }
    }
    
    
    return YES;
}

- (void)configCostWithGasPrice:(NSString *)gasPriceString {
    _cost = [gasPriceString floatValue]*[_gasLimitTextField.text floatValue]*powf(10, -9);
    _costLabel.text = [NSString stringWithFormat:@"%.8fETH",[gasPriceString floatValue]*[_gasLimitTextField.text floatValue]*powf(10, -9)];
}

- (void)configCostWithGasLimit:(NSString *)gasLimitString {
    _cost = [gasLimitString floatValue]*[_gasPriceTextField.text floatValue]*powf(10, -9);
    _costLabel.text = [NSString stringWithFormat:@"%.8fETH",[gasLimitString floatValue]*[_gasPriceTextField.text floatValue]*powf(10, -9)];
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

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    if (_addressStr.length>0) {
        NSIndexPath *indexPath=[NSIndexPath indexPathForRow:0 inSection:0];
        WeXWalletTransferWithButtonCell *cell = [_tableView cellForRowAtIndexPath:indexPath];
        cell.contentTextField.text = _addressStr;
        NSLog(@"_toAddressTextField.text%@",_toAddressTextField.text);
//        [_tableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath,nil] withRowAnimation:UITableViewRowAnimationNone];
    }
//    [_tableView reloadSections:0 withRowAnimation:UITableViewRowAnimationNone];
}

#pragma mark -- 设置导航栏
-(void)setNavitem{
    
    UIButton *scanClickBtn = [[UIButton alloc]initWithFrame:CGRectMake(0, 0, 40, 30)];
    //    [scanClickBtn setBackgroundColor:[UIColor redColor]];
    [scanClickBtn setImage:[UIImage imageNamed:@"Fill 1"] forState:UIControlStateNormal];
    [scanClickBtn setImage:[UIImage imageNamed:@"Fill 1"] forState:UIControlStateSelected];
    [scanClickBtn addTarget:self action:@selector(scanButtonClick) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithCustomView:scanClickBtn];
    self.navigationItem.rightBarButtonItem = rightButton;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    if(indexPath.row == 0){
        return 96;
    }else{
        return 44;
    }
}

- (void)goChooseAddressClick{
    WeXAddressBookController *vc = [[WeXAddressBookController alloc]init];
    vc.addressBookType = WeXMainAddressBookTypeChooseTwo;
    [self.navigationController pushViewController:vc animated:YES];
}

@end
