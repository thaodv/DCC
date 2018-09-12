//
//  WeXWalletDigitalRecordDetailController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalRecordDetailController.h"
#import "WeXWalletRecordHashWebController.h"
#import "WeXWalletEtherscanGetRecordDetailAdapter.h"
#import "WeXWalletTransferResultManager.h"
#import "WeXAddAddressBookController.h"
#import "WeXSaveAddressBookObject.h"
#import "WeXWalletTransferViewController.h"
#import "WeXWalletAlertWithTwoButtonView.h"
#import "WeXWalletDigitalAssetDetailController.h"
#import "WeXAddressWebViewController.h"

#define CANCEL_GAS_LIMIT_STRING @"200000"

@interface WeXWalletDigitalRecordDetailController ()
{
    UILabel *_costLabel;
    UILabel *_statusLabel;
    UILabel *_heightLabel;
    
    NSString *_gasUsed;
    NSString *_gasPrice;
    NSString *_blockNumber;
    
    NSString *_cancelGasPrice;
    NSString *_cancelGasLimit;
    
}

@property (nonatomic,strong)WeXWalletEtherscanGetRecordDetailAdapter *getRecordDetailAdapter;
@property (nonatomic,strong)WeXCustomButton *addAddressBtn;
@end

@implementation WeXWalletDigitalRecordDetailController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupNavgationType];
    [self setupSubViews];
    [self createBottomActions];
   
    if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain) {
        return;
    }
    
    if (!([self.tokenModel.symbol isEqualToString:@"ETH"])&&!self.recordModel.isPending) {
         [self creteRequest];
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    NSString *address = [WexCommonFunc getFromAddress];
    if ([address isEqualToString:self.recordModel.to] && ![address isEqualToString:self.recordModel.from]) {
        if ([WeXSaveAddressBookObject judgeAddressIsExist:self.recordModel.from]) {
            _addAddressBtn.hidden = YES;
        }else{
            _addAddressBtn.hidden = NO;
        }
    }
    if (![address isEqualToString:self.recordModel.to] && [address isEqualToString:self.recordModel.from]) {
        if ([WeXSaveAddressBookObject judgeAddressIsExist:self.recordModel.to]) {
            _addAddressBtn.hidden = YES;
        }else{
            _addAddressBtn.hidden = NO;
        }
    }
}

- (void)createBottomActions
{
    if (self.recordModel.isPending) {
        WeXCustomButton *editBtn = [WeXCustomButton button];
        [editBtn setTitle:WeXLocalizedString(@"编辑") forState:UIControlStateNormal];
        [editBtn addTarget:self action:@selector(editBtnClick) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:editBtn];
        [editBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(self.view).offset(-30);
            make.leading.equalTo(self.view).offset(20);
            make.height.equalTo(@50);
        }];
        
        WeXCustomButton *cancelBtn = [WeXCustomButton button];
        [cancelBtn setTitle:WeXLocalizedString(@"撤销") forState:UIControlStateNormal];
        [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:cancelBtn];
        [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(editBtn);
            make.trailing.equalTo(self.view).offset(-20);
            make.width.equalTo(editBtn);
            make.leading.equalTo(editBtn.mas_trailing).offset(10);
            make.height.equalTo(editBtn);
        }];
        
    }
    else
    {
        NSString *address = [WexCommonFunc getFromAddress];
        NSLog(@"address = %@",address);
        
        if ([address isEqualToString:self.recordModel.to] && ![address isEqualToString:self.recordModel.from]) {
           
            if ([WeXSaveAddressBookObject judgeAddressIsExist:self.recordModel.from]) {
            }else{
                _addAddressBtn = [WeXCustomButton button];
                [_addAddressBtn setTitle:WeXLocalizedString(@"添加地址") forState:UIControlStateNormal];
                [_addAddressBtn addTarget:self action:@selector(addAddressBtnClick) forControlEvents:UIControlEventTouchUpInside];
                [self.view addSubview:_addAddressBtn];
                [_addAddressBtn mas_makeConstraints:^(MASConstraintMaker *make) {
                    make.bottom.equalTo(self.view).offset(-30);
                    make.leading.equalTo(self.view).offset(15);
                    make.trailing.equalTo(self.view).offset(-15);
                    make.height.equalTo(@50);
                }];
            }
          }
        
        if (![address isEqualToString:self.recordModel.to] && [address isEqualToString:self.recordModel.from]) {
            
            if ([WeXSaveAddressBookObject judgeAddressIsExist:self.recordModel.to]) {
            }else{
                _addAddressBtn = [WeXCustomButton button];
                [_addAddressBtn setTitle:WeXLocalizedString(@"添加地址") forState:UIControlStateNormal];
                [_addAddressBtn addTarget:self action:@selector(addAddressBtnClick) forControlEvents:UIControlEventTouchUpInside];
                [self.view addSubview:_addAddressBtn];
                [_addAddressBtn mas_makeConstraints:^(MASConstraintMaker *make) {
                    make.bottom.equalTo(self.view).offset(-30);
                    make.leading.equalTo(self.view).offset(15);
                    make.trailing.equalTo(self.view).offset(-15);
                    make.height.equalTo(@50);
                }];
            }
            
        }
       
    }
}

- (void)editBtnClick
{
    
    WeXWalletAlertWithTwoButtonView *alertView = [[WeXWalletAlertWithTwoButtonView alloc] initWithFrame:self.view.bounds];
    NSString *content = [NSString stringWithFormat:@"%@nonce=%@%@",WeXLocalizedString(@"编辑后的交易记录将覆盖原交易记录"),self.recordModel.nonce,@"确认编辑此交易吗?"];
    if (![[WeXLocalizedManager shareManager] isChinese]) {
        content = [NSString stringWithFormat:@"%@ nonce is %@",WeXLocalizedString(@"撤销后的交易记录将覆盖原交易记录,确认撤销此交易吗?"),self.recordModel.nonce];
    }
    alertView.contentLabel.text = content;
    alertView.confirmButtonBlock = ^{
        WeXWalletTransferViewController *ctrl = [[WeXWalletTransferViewController alloc]init];
        ctrl.tokenModel = self.tokenModel;
        ctrl.isPrivateChain = self.isPrivateChain;
        ctrl.transferType = WeXWalletTransferTypeEdit;
        ctrl.recordModel = self.recordModel;
        [self.navigationController pushViewController:ctrl animated:YES];
    };
    [self.view addSubview:alertView];
    
    
}

- (void)cancelBtnClick
{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self getGasPrice];
    
  
}

- (void)addAddressBtnClick
{
    WeXAddAddressBookController *vc = [[WeXAddAddressBookController alloc]init];
    vc.alertAddressType = WeXAlertAddressTypeAdd;
    NSString *address = [WexCommonFunc getFromAddress];
    if ([address isEqualToString:self.recordModel.to] && ![address isEqualToString:self.recordModel.from]) {
      vc.addAddressBookStr = self.recordModel.from;
    }
    if (![address isEqualToString:self.recordModel.to] && [address isEqualToString:self.recordModel.from]) {
        vc.addAddressBookStr = self.recordModel.to;
    }
    vc.addNavNameStr = @"WeXWalletDigitalRecordDetailController";
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)getGasPrice{
     NSLog(@"recordModel=%@",self.recordModel);
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
            [[WXPassHelper instance] getGasPriceResponseBlock:^(id response) {
                NSLog(@"gasprice:=%@",response);
                [WeXPorgressHUD hideLoading];
                NSDecimalNumber *gasPrice = [WexCommonFunc stringWithOriginString:response dividString:NINE_ZERO];
                if (self.recordModel.gasPrice) {
                    NSDecimalNumber *cachePrice = [WexCommonFunc stringWithOriginString:self.recordModel.gasPrice dividString:NINE_ZERO];
                    _cancelGasPrice = [NSString stringWithFormat:@"%.2f",[gasPrice floatValue] > [cachePrice floatValue]+0.5?[gasPrice floatValue]:[cachePrice floatValue]+0.5];
                    
                    WeXWalletAlertWithTwoButtonView *alertView = [[WeXWalletAlertWithTwoButtonView alloc] initWithFrame:self.view.bounds];
                    NSDecimalNumber *newPrice = [WexCommonFunc stringWithOriginString:_cancelGasPrice dividString:NINE_ZERO];
                    NSString *content;
                    if ([[WeXLocalizedManager shareManager] isChinese]) {
                        content = [NSString stringWithFormat:@"%@nonce=%@%@%.4fETH%@", WeXLocalizedString(@"撤销后的交易记录将覆盖原交易记录"),self.recordModel.nonce,WeXLocalizedString(@"撤销交易最大需消耗"), [newPrice floatValue]*[CANCEL_GAS_LIMIT_STRING floatValue],WeXLocalizedString(@"确认撤销此交易吗?")];
                    } else {
                        content = [NSString stringWithFormat:@"%@ nonce is %@ and fee is %.4fETH",WeXLocalizedString(@"撤销后的交易记录将覆盖原交易记录,确认撤销此交易吗?"),
                                   self.recordModel.nonce,[newPrice floatValue]*[CANCEL_GAS_LIMIT_STRING floatValue]];
                    }
                    alertView.contentLabel.text = content;
                    alertView.confirmButtonBlock = ^{
                        [WeXPorgressHUD showLoadingAddedTo:self.view];
                        [self createTransferRequest];
                    };
                    [self.view addSubview:alertView];
                }
            }];
            
        }];
    }];
}

//- (void)getGasLimitRequest
//{
//    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
//    {
//        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
//            [[WXPassHelper instance] getGasLimitWithToAddress:[WexCommonFunc getFromAddress] fromAddress:[WexCommonFunc getFromAddress] data:@"0x" responseBlock:^(id response) {
//                NSLog(@"response=%@",response);
//                if ([response isKindOfClass:[NSDictionary class]]) {
//                    _cancelGasLimit = @"1000000";
//                }
//                else
//                {
//                    _cancelGasLimit = response;
//                }
//
//                [self createTransferRequest];
//            }];
//        }];
//    }
//    else
//    {
//
//        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
//            NSString *abiJson1 = WEX_ERC20_ABI_TRANSFER;
//            NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
//            NSString *pararms1 = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressTextField.text,value];
//
//            [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson1 params:pararms1 responseBlock:^(id response){
//                [[WXPassHelper instance] getGasLimitWithToAddress:self.tokenModel.contractAddress fromAddress:[WexCommonFunc getFromAddress] data:response responseBlock:^(id response) {
//                    NSLog(@"response=%@",response);
//                    if ([response isKindOfClass:[NSDictionary class]]) {
//
//                    }
//                    else
//                    {
//                        if (_gasLimitTextField.text.length ==0 ) {
//                            _gasLimitTextField.text = [NSString stringWithFormat:@"%@",response];
//                            [self configCostWithGasLimit:_gasLimitTextField.text];
//                        }
//                    }
//                }];
//
//            }];
//        }];
////
////
//    }
//
//}

- (void)jumpToAssetDetailController
{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        for (UIViewController *ctrl in self.navigationController.viewControllers) {
            if ([ctrl isKindOfClass:[WeXWalletDigitalAssetDetailController class]]) {
                [self.navigationController popToViewController:ctrl animated:YES];
            }
        }
    });
}


- (void)createTransferRequest{
    
    WeXPasswordCacheModal *urserModel = [WexCommonFunc getPassport];
    if ([self.tokenModel.symbol isEqualToString:@"ETH"])
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             NSString *gasprice = [[WexCommonFunc stringWithOriginString:_cancelGasPrice multiplyString:NINE_ZERO] stringValue];
             NSNumber *nonce = [NSNumber numberWithInteger:[self.recordModel.nonce integerValue]];
             [[WXPassHelper instance] sendETHTransactionWithContractToAddress:[WexCommonFunc getFromAddress] privateKey:urserModel.walletPrivateKey value:@"0" gasPrice:gasprice gasLimit:CANCEL_GAS_LIMIT_STRING nonce:nonce remark:@"0x0" responseBlock:^(id response) {
                 NSLog(@"bltx=%@",response);
                 [WeXPorgressHUD hideLoading];
                 //转账失败
                 if([response isKindOfClass:[NSDictionary class]])
                 {
                     [WeXPorgressHUD hideLoading];
                     [WeXPorgressHUD showText:WeXLocalizedString(@"撤销失败!") onView:self.view];
                     [self jumpToAssetDetailController];
                     return;
                 };
                 
                 if (response) {
                     NSString *txHash = [NSString stringWithFormat:@"%@",response];
                     [self savePendingTransferModelWithTxhash:txHash];
                     
                 }
                 
                 [WeXPorgressHUD showText:WeXLocalizedString(@"撤销申请已提交，请耐心等待转账结果。") onView:self.view];
                 dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                     [self.navigationController popViewControllerAnimated:YES];
                 });
                 
             }];
         }];
    }
    else
    {
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             NSString *gasprice = [[WexCommonFunc stringWithOriginString:_cancelGasPrice multiplyString:NINE_ZERO] stringValue];
             NSNumber *nonce = [NSNumber numberWithInteger:[self.recordModel.nonce integerValue]];
             NSString *abiJson = WEX_ERC20_ABI_TRANSFER;
             NSString *pararms = [NSString stringWithFormat:@"[\'%@\',\'%@\']",[WexCommonFunc getFromAddress],@"0"];
             [[WXPassHelper instance] sendERC20TransactionWithContractToAddress:self.tokenModel.contractAddress abiJson:abiJson privateKey:urserModel.walletPrivateKey params:pararms gasPrice:gasprice gasLimit:CANCEL_GAS_LIMIT_STRING nonce:nonce responseBlock:^(id response) {
                 NSLog(@"bltx=%@",response);
                 [WeXPorgressHUD hideLoading];
                 //转账失败
                 if([response isKindOfClass:[NSDictionary class]])
                 {
                     [WeXPorgressHUD hideLoading];
                     [WeXPorgressHUD showText:WeXLocalizedString(@"撤销失败!") onView:self.view];
                     [self jumpToAssetDetailController];
                     return;
                 };
                 if (response) {
                     NSString *txHash = [NSString stringWithFormat:@"%@",response];
                     [self savePendingTransferModelWithTxhash:txHash];
                 }

                 [WeXPorgressHUD showText:WeXLocalizedString(@"撤销申请已提交，请耐心等待转账结果。") onView:self.view];
                 dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                     [self.navigationController popViewControllerAnimated:YES];
                  });
             }];

         }];
    }
    
}

- (void)savePendingTransferModelWithTxhash:(NSString *)txhash
{
    WeXWalletTransferPendingModel *model = [[WeXWalletTransferPendingModel alloc] init];
    model.from = [WexCommonFunc getFromAddress];
    model.to = [WexCommonFunc getFromAddress];
    NSDate *nowTime = [NSDate date];
    NSTimeInterval timeStamp = nowTime.timeIntervalSince1970;
    model.timeStamp = [NSString stringWithFormat:@"%f",timeStamp];
    model.value = [[WexCommonFunc stringWithOriginString:@"0" multiplyString:EIGHTEEN_ZERO] stringValue];
    model.txhash = txhash;
    model.nonce = self.recordModel.nonce;
    model.gasPrice = [[WexCommonFunc stringWithOriginString:_cancelGasPrice multiplyString:NINE_ZERO] stringValue];
    
    WeXWalletTransferResultManager *manager = [[WeXWalletTransferResultManager alloc] init];
    [manager savePendingModel:model symbol:self.isPrivateChain?[NSString stringWithFormat:@"%@_private",self.tokenModel.symbol]:self.tokenModel.symbol];
}

// MARK: - 查询单个交易记录是否成功/失败
- (void)creteRequest{
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        
        //2018.8.8 针对单个交易记录去查询结果成功/失败
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             [[WXPassHelper instance] queryTransactionReceipt:self.recordModel.hashStr responseBlock:^(id response) {
                 NSLog(@"%@",response);
                  if ([response isKindOfClass:[NSDictionary class]]) {
                      NSString *status = [response objectForKey:@"status"];
                      if ([status isEqualToString:@"0x1"]) {
                          _statusLabel.text = WeXLocalizedString(@"成功");
                      }
                      else
                      {
                          _statusLabel.text = WeXLocalizedString(@"失败");
                      }
                      _gasUsed = [response objectForKey:@"gasUsed"];
                      [self configResult];
                  }
             }];
             [[WXPassHelper instance] queryTransactionReceiptWithPending:self.recordModel.hashStr responseBlock:^(id response) {
                 NSLog(@"%@",response);
                 if ([response isKindOfClass:[NSDictionary class]]) {
                     NSDictionary *rep = [response objectForKey:@"rep"];
                     if ([rep isKindOfClass:[NSDictionary class]]) {
                         _gasPrice = [rep objectForKey:@"gasPrice"];
                         _blockNumber = [rep objectForKey:@"blockNumber"];
                         _heightLabel.text = [NSString stringWithFormat:@"%@",_blockNumber];
                         [self configResult];
                     }
                 }
             }];
         }];
    }];
}

- (void)configResult
{
    if (_gasPrice&&_gasUsed) {
        NSString *gasPrice = [NSString stringWithFormat:@"%@",_gasPrice];
        NSString *gasUsed = [NSString stringWithFormat:@"%@",_gasUsed];
        NSDecimalNumber * decimal1 = [WexCommonFunc stringWithOriginString:gasUsed multiplyString:gasPrice];
        NSDecimalNumber * decimal2 = [WexCommonFunc stringWithOriginString:[decimal1 stringValue] dividString:EIGHTEEN_ZERO];
        _costLabel.text = [decimal2 stringValue];
    }
    
}

#pragma -mark 获取交易记录请求
- (void)createGetRecordDetailRequest{
    _getRecordDetailAdapter = [[WeXWalletEtherscanGetRecordDetailAdapter alloc] init];
    _getRecordDetailAdapter.delegate = self;
    WeXWalletEtherscanGetRecordDetailParamModal* paramModal = [[WeXWalletEtherscanGetRecordDetailParamModal alloc] init];
    paramModal.module = @"transaction";
    paramModal.action = @"getstatus";
    paramModal.txhash = self.recordModel.hashStr;
    [_getRecordDetailAdapter run:paramModal];
    
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getRecordDetailAdapter) {
        
    }
    
}
    
- (void)setupNavgationType{
//    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"digital_share"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
//    self.navigationItem.rightBarButtonItem = rihgtItem;
        
}

- (void)rightItemClick{
    
}
    
    //初始化滚动视图
-(void)setupSubViews{
    UILabel *recordlabel = [[UILabel alloc] init];
    recordlabel.text = WeXLocalizedString(@"交易记录详情");
    recordlabel.font = [UIFont systemFontOfSize:20];
    recordlabel.textColor = COLOR_LABEL_DESCRIPTION;
    recordlabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:recordlabel];
    [recordlabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *valueLabel = [[UILabel alloc] init];
    NSString *valueStr = [WexCommonFunc formatterStringWithContractBalance:self.recordModel.value decimals:[self.tokenModel.decimals integerValue]];
    
    if ([[WexCommonFunc getFromAddress] isEqualToString: self.recordModel.from]) {
        valueLabel.text = [NSString stringWithFormat:@"-%@%@",valueStr,self.tokenModel.symbol];
    }
    else 
    {
        valueLabel.text = [NSString stringWithFormat:@"+%@%@",valueStr,self.tokenModel.symbol];
    }
    valueLabel.font = [UIFont systemFontOfSize:20];
    valueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    valueLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:valueLabel];
    [valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(recordlabel.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.height.equalTo(@20);
    }];
    
    UILabel *toTitleLabel = [[UILabel alloc] init];
    toTitleLabel.text = WeXLocalizedString(@"接收地址:");
    toTitleLabel.font = [UIFont systemFontOfSize:18];
    toTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    toTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:toTitleLabel];
    [toTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueLabel.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    
    UILabel *toLabel = [[UILabel alloc] init];
    NSDictionary *attribtDic = @{NSUnderlineStyleAttributeName: [NSNumber numberWithInteger:NSUnderlineStyleSingle]};
    NSMutableAttributedString *attribtStr = [[NSMutableAttributedString alloc]initWithString:self.recordModel.to attributes:attribtDic];
    toLabel.attributedText = attribtStr;
//    toLabel.text = self.recordModel.to;
    toLabel.font = [UIFont systemFontOfSize:14];
    toLabel.textColor = COLOR_THEME_ALL;
    toLabel.textAlignment = NSTextAlignmentLeft;
    toLabel.numberOfLines = 2;
    [self.view addSubview:toLabel];
    [toLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
//        make.height.equalTo(@40);
    }];
    toLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *twoTapGes = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(twoTapGesClick)];
    [toLabel addGestureRecognizer:twoTapGes];
    
    UILabel *fromTitleLabel = [[UILabel alloc] init];
    fromTitleLabel.text = WeXLocalizedString(@"付款地址:");
    fromTitleLabel.font = [UIFont systemFontOfSize:18];
    fromTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    fromTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:fromTitleLabel];
    [fromTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel.mas_bottom).offset(30);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel);
    }];
    
    UILabel *fromLabel = [[UILabel alloc] init];
    NSMutableAttributedString *twoAttribtStr = [[NSMutableAttributedString alloc]initWithString:self.recordModel.from attributes:attribtDic];
    fromLabel.attributedText = twoAttribtStr;
//    fromLabel.text = self.recordModel.from;;
    fromLabel.font = [UIFont systemFontOfSize:14];
    fromLabel.textColor = COLOR_THEME_ALL;
    fromLabel.textAlignment = NSTextAlignmentLeft;
    fromLabel.numberOfLines = 2;
    [self.view addSubview:fromLabel];
    [fromLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromTitleLabel);
        make.leading.equalTo(fromTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
//        make.height.equalTo(@40);
    }];
    
    fromLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *threeTapGes = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(threeTapGesClick)];
    [fromLabel addGestureRecognizer:threeTapGes];
    
    if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        UILabel *transactionTitleLabel = [[UILabel alloc] init];
        transactionTitleLabel.text = WeXLocalizedString(@"交  易  号:");
        transactionTitleLabel.font = [UIFont systemFontOfSize:18];
        transactionTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        transactionTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:transactionTitleLabel];
        [transactionTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(fromLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *transactionLabel = [[UILabel alloc] init];
//        transactionLabel.text = [WexCommonFunc formatterStringWithContractAddress:self.recordModel.hashStr];
        NSLog(@"self.recordModel.hashStr = %@",self.recordModel.hashStr);
        transactionLabel.text = self.recordModel.hashStr;
        transactionLabel.font = [UIFont systemFontOfSize:14];
        transactionLabel.textColor = COLOR_LABEL_DESCRIPTION;
        transactionLabel.textAlignment = NSTextAlignmentLeft;
        transactionLabel.numberOfLines = 0;
        transactionLabel.lineBreakMode = NSLineBreakByWordWrapping;
        [self.view addSubview:transactionLabel];
        [transactionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(transactionTitleLabel);
            make.leading.equalTo(transactionTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
//            make.height.equalTo(@60);
        }];
        
        UILabel *heightTitleLabel = [[UILabel alloc] init];
        heightTitleLabel.text = WeXLocalizedString(@"区块高度:");
        heightTitleLabel.font = [UIFont systemFontOfSize:18];
        heightTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        heightTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:heightTitleLabel];
        [heightTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(transactionTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *heightLabel = [[UILabel alloc] init];
        if (self.recordModel.blockNumber) {
            heightLabel.text = self.recordModel.blockNumber;
        }
        
        heightLabel.font = [UIFont systemFontOfSize:14];
        heightLabel.textColor = COLOR_LABEL_DESCRIPTION;
        heightLabel.textAlignment = NSTextAlignmentLeft;
        heightLabel.numberOfLines = 2;
        [self.view addSubview:heightLabel];
        [heightLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(heightTitleLabel);
            make.leading.equalTo(heightTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        _heightLabel = heightLabel;
        
        UILabel *timeTitleLabel = [[UILabel alloc] init];
        timeTitleLabel.text = WeXLocalizedString(@"交易时间:");
        timeTitleLabel.font = [UIFont systemFontOfSize:18];
        timeTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        timeTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:timeTitleLabel];
        [timeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(heightTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *timeLabel = [[UILabel alloc] init];
        timeLabel.text = [WexCommonFunc formatterTimeStringWithTimeStamp:self.recordModel.timeStamp];
        timeLabel.font = [UIFont systemFontOfSize:14];
        timeLabel.textColor = COLOR_LABEL_DESCRIPTION;
        timeLabel.textAlignment = NSTextAlignmentLeft;
        timeLabel.numberOfLines = 2;
        [self.view addSubview:timeLabel];
        [timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(timeTitleLabel);
            make.leading.equalTo(timeTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];

    }
    else
    {
        UILabel *costTitleLabel = [[UILabel alloc] init];
        costTitleLabel.text = WeXLocalizedString(@"矿  工  费:");
        costTitleLabel.font = [UIFont systemFontOfSize:18];
        costTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        costTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:costTitleLabel];
        [costTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(fromTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *costLabel = [[UILabel alloc] init];
        if (self.recordModel.gasUsed&&self.recordModel.gasPrice) {
            NSDecimalNumber * decimal1 = [WexCommonFunc stringWithOriginString:self.recordModel.gasUsed multiplyString:self.recordModel.gasPrice];
            NSDecimalNumber * decimal2 = [WexCommonFunc stringWithOriginString:[decimal1 stringValue] dividString:EIGHTEEN_ZERO];
            costLabel.text = [decimal2 stringValue];
        }
        costLabel.font = [UIFont systemFontOfSize:14];
        costLabel.textColor = COLOR_LABEL_DESCRIPTION;
        costLabel.textAlignment = NSTextAlignmentLeft;
        costLabel.numberOfLines = 2;
        [self.view addSubview:costLabel];
        [costLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(costTitleLabel);
            make.leading.equalTo(costTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        _costLabel = costLabel;
        
        UILabel *transactionTitleLabel = [[UILabel alloc] init];
        transactionTitleLabel.text = WeXLocalizedString(@"交  易  号:");
        transactionTitleLabel.font = [UIFont systemFontOfSize:18];
        transactionTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        transactionTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:transactionTitleLabel];
        [transactionTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(costTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *transactionLabel = [[UILabel alloc] init];
//        transactionLabel.text = [WexCommonFunc formatterStringWithContractAddress:self.recordModel.hashStr];
//        NSLog(@"self.recordModel.hashStr = %@",self.recordModel.hashStr);
        NSMutableAttributedString *threeAttribtStr = [[NSMutableAttributedString alloc]initWithString:self.recordModel.hashStr attributes:attribtDic];
        transactionLabel.attributedText = threeAttribtStr;
//        transactionLabel.text = self.recordModel.hashStr;
        transactionLabel.font = [UIFont systemFontOfSize:14];
        transactionLabel.textColor = COLOR_THEME_ALL;
        transactionLabel.textAlignment = NSTextAlignmentLeft;
        transactionLabel.numberOfLines = 0;
        transactionLabel.lineBreakMode = NSLineBreakByWordWrapping;
        [self.view addSubview:transactionLabel];
        [transactionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(transactionTitleLabel);
            make.leading.equalTo(transactionTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
//            make.height.equalTo(@60);
        }];
        transactionLabel.userInteractionEnabled = YES;
        UITapGestureRecognizer *tapGes = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapGesClick)];
        [transactionLabel addGestureRecognizer:tapGes];
        
        UILabel *heightTitleLabel = [[UILabel alloc] init];
        heightTitleLabel.text = WeXLocalizedString(@"区块高度:");
        heightTitleLabel.font = [UIFont systemFontOfSize:18];
        heightTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        heightTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:heightTitleLabel];
        [heightTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(transactionTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *heightLabel = [[UILabel alloc] init];
        if (self.recordModel.blockNumber) {
            heightLabel.text = self.recordModel.blockNumber;
        }
        
        heightLabel.font = [UIFont systemFontOfSize:14];
        heightLabel.textColor = COLOR_LABEL_DESCRIPTION;
        heightLabel.textAlignment = NSTextAlignmentLeft;
        heightLabel.numberOfLines = 2;
        [self.view addSubview:heightLabel];
        [heightLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(heightTitleLabel);
            make.leading.equalTo(heightTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        _heightLabel = heightLabel;
        
        UILabel *timeTitleLabel = [[UILabel alloc] init];
        timeTitleLabel.text = WeXLocalizedString(@"交易时间:");
        timeTitleLabel.font = [UIFont systemFontOfSize:18];
        timeTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        timeTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:timeTitleLabel];
        [timeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(heightTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *timeLabel = [[UILabel alloc] init];
        timeLabel.text = [WexCommonFunc formatterTimeStringWithTimeStamp:self.recordModel.timeStamp];
        timeLabel.font = [UIFont systemFontOfSize:14];
        timeLabel.textColor = COLOR_LABEL_DESCRIPTION;
        timeLabel.textAlignment = NSTextAlignmentLeft;
        timeLabel.numberOfLines = 2;
        [self.view addSubview:timeLabel];
        [timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(timeTitleLabel);
            make.leading.equalTo(timeTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        
        
        UILabel *statusTitleLabel = [[UILabel alloc] init];
        statusTitleLabel.text = WeXLocalizedString(@"交易状态:");
        statusTitleLabel.font = [UIFont systemFontOfSize:18];
        statusTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        statusTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:statusTitleLabel];
        [statusTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(timeTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *statusLabel = [[UILabel alloc] init];
        if (self.recordModel.isError) {
            statusLabel.text = [self.recordModel.isError isEqualToString: @"1"]?WeXLocalizedString(@"失败"):WeXLocalizedString(@"成功");
        }
        
        statusLabel.font = [UIFont systemFontOfSize:14];
        statusLabel.textColor = COLOR_LABEL_DESCRIPTION;
        statusLabel.textAlignment = NSTextAlignmentLeft;
        statusLabel.numberOfLines = 2;
        [self.view addSubview:statusLabel];
        [statusLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(statusTitleLabel);
            make.leading.equalTo(statusTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        _statusLabel = statusLabel;
    }
}

- (void)tapGesClick{
    
    WeXWalletRecordHashWebController *ctrl = [[WeXWalletRecordHashWebController alloc] init];
    ctrl.txHash = self.recordModel.hashStr;
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)twoTapGesClick{
    
    WeXAddressWebViewController *vc = [[WeXAddressWebViewController alloc]init];
    vc.addressStr = self.recordModel.to;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)threeTapGesClick{
    
    WeXAddressWebViewController *vc = [[WeXAddressWebViewController alloc]init];
    vc.addressStr = self.recordModel.from;
    [self.navigationController pushViewController:vc animated:YES];
}

@end
