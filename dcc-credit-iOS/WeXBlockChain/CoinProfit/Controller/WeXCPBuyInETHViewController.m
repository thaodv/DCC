//
//  WeXCPBuyInETHViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPBuyInETHViewController.h"
#import "WeXCPBuyBottomView.h"
#import "WeXWalletETHTranstionDetailView.h"
#import "WeXGetReceiptResult2Adapter.h"
#import "WeXCoinProfitOnlyTextCell.h"
#import "WeXCPCompoundCell.h"
#import "WeXCPLeftAndRightLabelCell.h"
#import "WexCPHeader.h"
#import "WeXCPActivityMainResModel.h"
#import "WeXWalletTransferResultManager.h"
#import "WeXCPPotListViewController.h"
#import "NSString+WexTool.h"

#define kEthTransDetailViewHeight 450


@interface WeXCPBuyInETHViewController () <UITextFieldDelegate,WeXPasswordManagerDelegate>

@property (nonatomic, strong) WeXCPBuyBottomView *bottomView;
@property (nonatomic, assign) BOOL isBuyEvent;
//购买数量
@property (nonatomic, copy) NSString *amount;
//钱包余额
@property (nonatomic, copy) NSString *walletBalance;
//起购额度
@property (nonatomic, copy) NSString *minBuyAmount;
@property (nonatomic, copy) NSString *minOriginalBuyAmout;

//总额度
@property (nonatomic, copy) NSString *totalAmount;
//已认购额度
@property (nonatomic, copy) NSString *haveBuyAmount;
//剩余额度
@property (nonatomic, copy) NSString *remainAmount;

@property (nonatomic, strong) NSNumber *nonce;

@property (nonatomic, strong) WeXPasswordCacheModal *userModel;
//最高矿工费
@property (nonatomic, assign) double maxFee;

@property (nonatomic, weak) WeXWalletETHTranstionDetailView *dccTransDetailView;
@property (nonatomic, strong) UIView *detailCoverView;
@property (nonatomic, strong) WeXGetReceiptResult2Adapter *resultAdapter;
@property (nonatomic, assign) NSInteger requestCount;
@property (nonatomic, copy) NSString *txHash;
@property (nonatomic, strong) NSArray <NSString *> *sectionTitles;
@property (nonatomic, strong) NSArray <NSString *> *sectionSubtitles;
@property (nonatomic, strong) UITextField  * priceTextFiled;
@property (nonatomic, strong) UITextField  * limitTextField;

@property (nonatomic, strong) WeXWalletETHTranstionDetailView  *ethTransDetailView;

@property (nonatomic, strong) WeXPasswordManager *manager;


@end

static NSString * const kDefaultBalance   = @"--";
static NSString * const kCPCompundCellID  = @"WeXCPCompoundCellID";
static NSString * const kCPOnlyTextCellID = @"WeXCoinProfitOnlyTextCellID";
static NSString * const kCPLeftAndRightCellID = @"WeXCPLeftAndRightLabelCellID";

static NSTimeInterval const kTimerGap = 3.0;
static NSInteger const kMaxTimes = 5;



@implementation WeXCPBuyInETHViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"认购额度");
    [self configureNavigaionBar];
    [self setUpBottomView];
    _userModel = [WexCommonFunc getPassport];
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self wex_unistallRefreshHeader];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self getETHBalance];
    [self getSomeAmountInfoRequest];
    [self getGasPrice];
}

- (void)configureNavigaionBar {
    _sectionTitles = @[@"Gas Price",@"Gas Limit",@"最高矿工费"];
    _sectionSubtitles = @[@"Gwei",@"",@"ETH"];
    [self setNavigationNomalBackButtonType];
    [self.tableView setBackgroundColor:ColorWithHex(0xF8F8FF)];
    [self wex_unistallRefreshHeader];
}

- (void)setUpBottomView {
    __weak typeof(self) weakSelf  = self;
    _bottomView = [WeXCPBuyBottomView createBuyBottomView:CGRectMake(0, 0, kScreenWidth, 250) tipsType:WexCPBuyButtonTipsTypePublic clickEvent:^(WeXCPBuyBottomViewType type) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (type == WeXCPBuyBottomViewTypeBuyIn) {
            if ([strongSelf checkParams]) {
                [strongSelf createBottomView];
            }
            WEXNSLOG(@"这是立即认购事件");
        }
    }];
    self.tableView.tableFooterView = _bottomView;
}

- (BOOL)checkParams {
    if ([self.amount floatValue] < [self.minBuyAmount floatValue]) {
        [WeXPorgressHUD showText:@"请输入正确的认购额度" onView:self.view];
        return NO;
    }
    if ([self.priceTextFiled.text floatValue] <=0) {
        [WeXPorgressHUD showText:@"请输入Gas Price" onView:self.view];
        return NO;
    }
    if ([self.limitTextField.text floatValue] <=0) {
        [WeXPorgressHUD showText:@"请输入Gas Limit" onView:self.view];
        return NO;
    }
    return true;
}
- (void)createBottomView {
    //创建注册试图
    WeXWalletETHTranstionDetailView  *ethTransDetailView = [[WeXWalletETHTranstionDetailView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, kEthTransDetailViewHeight)];
    ethTransDetailView.fromLabel.text = [_userModel.keyStore objectForKey:@"address"];
    ethTransDetailView.toLabel.text = _productModel.contractAddress;
    ethTransDetailView.valueLabel.text = [NSString stringWithFormat:@"%@%@",_amount,_productModel.assetCode];
    ethTransDetailView.costLabel.text = [NSString stringWithFormat:@"%.5f",_maxFee];
    ethTransDetailView.highPayLabel.text = [NSString stringWithFormat:@"%@%.4f%@",WeXLocalizedString(@"最高支付金额"),[_amount floatValue]+_maxFee,_productModel.assetCode];
    ethTransDetailView.balanceLabel.text = [_walletBalance stringByAppendingString:_productModel.assetCode];
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
        if ([_amount floatValue]+_maxFee  > [_walletBalance floatValue]) {
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
- (void)configLocalSafetyView{
    //没有密码
    _userModel = [WexCommonFunc getPassport];
    if (_userModel.passwordType == WeXPasswordTypeNone) {
        [self beginBuyEvent];
    } else {
        WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
        manager.delegate = self;
        [manager loadPassword];
        _manager = manager;
    }
}

#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerVerifySuccess{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self beginBuyEvent];
    });
}

// MARK: - 购买
- (void)beginBuyEvent {
    NSString *value    = [[WexCommonFunc stringWithOriginString:self.amount multiplyString:EIGHTEEN_ZERO] stringValue];
    NSString *gasprice = [[WexCommonFunc stringWithOriginString:_priceTextFiled.text multiplyString:NINE_ZERO] stringValue];
    NSString *gasLimit = _limitTextField.text;
    
    NSString *params = @"[]";
    NSString *abiJson = WEXCP_ETH_Invest_ABI;
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [[WXPassHelper instance] getETHNonceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
        _nonce = response;
        [[WXPassHelper instance] sendETHInvestTransactionWithContractToAddress:_productModel.contractAddress abiJson:abiJson privateKey:_userModel.walletPrivateKey params:params gasPrice:gasprice gasLimit:gasLimit nonce:_nonce value:value responseBlock:^(id response) {
            //转账失败
            if([response isKindOfClass:[NSDictionary class]]) {
                [WeXPorgressHUD hideLoading];
                NSString *message = [response objectForKey:@"message"];
                [WeXPorgressHUD showText:message onView:self.view];
                return;
            };
            if (response) {
                NSString *txHash = [NSString stringWithFormat:@"%@",response];
                [self savePendingTransferModelWithTxhash:txHash];
                [self getResultRequestWithTXHash:txHash complete:^(BOOL result) {
                    if (result) {
                        [WeXPorgressHUD hideLoading];
                        [WeXPorgressHUD showText:@"购买成功" onView:self.view];
                        [self buySuccessEvent];
                        WEXNSLOG(@"购买成功");
                    } else {
                        WEXNSLOG(@"购买失败");
                    }
                }];
            }
        }];
    }];
}

- (void)savePendingTransferModelWithTxhash:(NSString *)txhash {
    WeXWalletTransferPendingModel *model = [[WeXWalletTransferPendingModel alloc] init];
    model.from = [WexCommonFunc getFromAddress];
    model.to = _productModel.contractAddress;
    NSDate *nowTime = [NSDate date];
    NSTimeInterval timeStamp = nowTime.timeIntervalSince1970;
    model.timeStamp = [NSString stringWithFormat:@"%f",timeStamp];
    model.value =_amount?[[WexCommonFunc stringWithOriginString:_amount multiplyString:EIGHTEEN_ZERO] stringValue]:nil;
    model.txhash = txhash;
    model.nonce = [_nonce stringValue];
    model.gasPrice = _priceTextFiled.text?[[WexCommonFunc stringWithOriginString:_priceTextFiled.text multiplyString:NINE_ZERO] stringValue]:nil;
    model.gasLimit = _limitTextField.text;
    WeXWalletTransferResultManager *manager = [[WeXWalletTransferResultManager alloc] init];
    [manager savePendingModel:model symbol:_productModel.assetCode];
}


- (void)getResultRequestWithTXHash:(NSString *)txHash complete:(void(^)(BOOL))complte {
    WEXNSLOG(@"查询次数%ld",_requestCount);
    _requestCount ++ ;
    [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        [[WXPassHelper instance] queryTransactionReceipt:txHash responseBlock:^(id response) {
            NSLog(@"%@",response);
            if ([response isKindOfClass:[NSDictionary class]]) {
                NSString *status = [response objectForKey:@"status"];
                if ([status isEqualToString:@"0x1"]) {
                    !complte ? : complte (true);
                    return ;
                } else {
                    if (_requestCount < kMaxTimes) {
                        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(kTimerGap * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                            [self getResultRequestWithTXHash:txHash complete:complte];
                        });
                    } else {
                        _requestCount = 0;
                        !complte ? : complte (false);
                        return ;
                    }
                }
            }
            if (response) {
                if (_requestCount < kMaxTimes) {
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(kTimerGap * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [self getResultRequestWithTXHash:txHash complete:complte];
                    });
                } else {
                    _requestCount = 0;
                    !complte ? : complte (false);
                    return ;
                }
            }
        }];
    }];
}

- (void)getETHBalance {
    [[WXPassHelper instance] getETHBalance2WithContractAddress:[WexCommonFunc getFromAddress] type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        if ([response isKindOfClass:[NSDictionary class]]) {
            self.walletBalance = @"--";
        }
        else {
            self.walletBalance = [WexCommonFunc formatterStringWithContractBalance:response decimals:18];
        }
        [WeXPorgressHUD hideLoading];
        [self.tableView reloadData];
    }];
}

- (void)getSomeAmountInfoRequest {
    NSString *params = @"[]";
    //总额度
    NSString *totalAmountJson = WEXCP_ETH_InvestCeilAmount_ABI;
    //产品起购额度
    NSString *minAmountJson   = WEXCP_ETH_MinAmountPerHand_ABI;
    //已认购额度
    NSString *haveBuyAmountJson = WEXCP_ETH_InvestedTotalAmount_ABI;
    
    [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        //产品起购额度
        [[WXPassHelper instance] encodeFunCallAbiInterface:minAmountJson params:params responseBlock:^(id response) {
            [[WXPassHelper instance] callContractAddress:_productModel.contractAddress data:response responseBlock:^(id response) {
                NSDictionary *responseDict = response;
                NSString * originBalance =[responseDict objectForKey:@"result"];
                NSString * ethException =[responseDict objectForKey:@"ethException"];
                if (![ethException isEqualToString:@"ethException"]) {
                    self.minOriginalBuyAmout = originBalance;
                    NSString *balace = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
                    self.minBuyAmount = balace;
                    [self.tableView reloadData];
                }
                [WeXPorgressHUD hideLoading];
                WEXNSLOG(@"r1-----esponse:%@",response);
            }];
        }];
        
        //总额度
        [[WXPassHelper instance] encodeFunCallAbiInterface:totalAmountJson params:params responseBlock:^(id response) {
            [[WXPassHelper instance] callContractAddress:_productModel.contractAddress data:response responseBlock:^(id response) {
                NSDictionary *responseDict = response;
                NSString * originBalance =[responseDict objectForKey:@"result"];
                NSString * ethException =[responseDict objectForKey:@"ethException"];
                if (![ethException isEqualToString:@"ethException"]) {
                    NSString *balace = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
                    self.totalAmount = balace;
                    [self refreshRemainAmout];
                }
                [WeXPorgressHUD hideLoading];
                WEXNSLOG(@"r2-----esponse:%@",response);
            }];
        }];
        
        //已认购额度
        [[WXPassHelper instance] encodeFunCallAbiInterface:haveBuyAmountJson params:params responseBlock:^(id response) {
            [[WXPassHelper instance] callContractAddress:_productModel.contractAddress data:response responseBlock:^(id response) {
                NSDictionary *responseDict = response;
                NSString * originBalance   = [responseDict objectForKey:@"result"];
                NSString * ethException    = [responseDict objectForKey:@"ethException"];
                if (![ethException isEqualToString:@"ethException"]) {
                    NSString *balace = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
                    self.haveBuyAmount = balace;
                    [self refreshRemainAmout];
                }
                [WeXPorgressHUD hideLoading];
                WEXNSLOG(@"r3-----esponse:%@",response);
            }];
        }];
    }];
}

// MARK: - 刷新剩余额度
- (void)refreshRemainAmout {
    if ([self.totalAmount length] > 0 && [self.haveBuyAmount length] > 0) {
        float remainAmout = [self.totalAmount floatValue] - [self.haveBuyAmount floatValue];
        self.remainAmount = [@(remainAmout) stringValue];
        [self.tableView reloadData];
    }
}

// MARK: - 购买成功
- (void)buySuccessEvent {
    [WeXPorgressHUD showText:@"认购成功" onView:self.view];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.7 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.ethTransDetailView removeFromSuperview];
        [self.ethTransDetailView    removeFromSuperview];
        [WeXHomePushService pushFromVC:self toVC:[WeXCPPotListViewController new]];
    });
}


- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCPCompoundCell          class] forCellReuseIdentifier:kCPCompundCellID];
    [tableView registerClass:[WeXCoinProfitOnlyTextCell  class] forCellReuseIdentifier:kCPOnlyTextCellID];
    [tableView registerClass:[WeXCPLeftAndRightLabelCell class] forCellReuseIdentifier:kCPLeftAndRightCellID];
}

#pragma mark UITableViewDatasource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0:
            return 1;
            break;
        case 1:
            return 3;
            break;
        case 2:
            return 3;
            break;
        default:
            return 0;
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 15;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == 1) {
        return 15;
    }
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0:
            return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            break;
        case 1:
            if (indexPath.row == 0) {
                return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            } else {
                return [self wexTableView:tableView cellIdentifier:kCPCompundCellID indexPath:indexPath];
            }
            break;
        case 2:
            return [self wexTableView:tableView cellIdentifier:kCPLeftAndRightCellID indexPath:indexPath];
            break;
            
        default:
            return 0.01;
            break;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0:
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            break;
        case 1:
            if (indexPath.row == 0) {
                return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            }
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPCompundCellID indexPath:indexPath];
            break;
        case 2:
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPLeftAndRightCellID indexPath:indexPath];
            break;
        default:
            return nil;
            break;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0: {
            WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
            [cell setBottomLineHidden:true];
            [cell setTitle:[NSString stringWithFormat:@"%@%@",_productModel.saleInfo.name,@"(剩余额度 --)"] highText:@"--"];
            if ([self.remainAmount length]> 0) {
                NSString *highText = [NSString stringWithFormat:@"%@ %@",self.remainAmount,_productModel.assetCode];
                
                NSString *text = [NSString stringWithFormat:@"%@(剩余额度 %@)",_productModel.saleInfo.name,highText];
                [cell setTitle:text highText:highText];
            }
        }
            break;
        case 1: {
            if (indexPath.row == 0) {
                WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
                [cell setBottomLineHidden:true];
                [cell setTitle:@"认购额度" subText:nil cellType:WeXCoinProfitOnlyTextCellTypeOnlyBoldTitle];
            }
            else if (indexPath.row == 1) {
                WeXCPCompoundCell *cell = (WeXCPCompoundCell *)currentCell;
                [cell setKeyBoardIsWithDot:true maxDotNum:2];
                if (self.minBuyAmount) {
                    NSString *placeHolder = [NSString stringWithFormat:@"%@%@%@",@"最小认购额度",self.minBuyAmount,_productModel.assetCode];
                    [cell setLeftTitle:nil rightText:_productModel.assetCode placeHolder:placeHolder type:WeXCPCompoundTypeTextFiledAndLabel];
                }
                if ([self.amount length] > 0) {
                    [cell setLeftTextFiled:self.amount];
                }
                cell.DidInputText = ^(NSString *text) {
                    self.amount = text;
                    WEXNSLOG(@"%@",text);
                };
            } else {
                WeXCPCompoundCell *cell = (WeXCPCompoundCell *)currentCell;
                NSString *balance= [NSString stringWithFormat:@"可用额度: %@%@",self.walletBalance,_productModel.assetCode];
                if ([self.walletBalance isEqualToString:kDefaultBalance]) {
                    balance = @"可用额度: --";
                }
                [cell setLeftTitle:balance rightText:@"全部" placeHolder:nil type:WeXCPCompoundTypeLabelAndButton];
                cell.DidClickAllButton = ^{
                    [self.view endEditing:YES];
                    //2018.8.16 由于获取不到余额,所以写的假数据
                    double amount = [self.walletBalance floatValue];
                    self.amount   = [NSString stringWithFormat:@"%.0f",floor(amount)];
                    [self reloadCellWithSection:indexPath.section row:indexPath.row - 1];
                    WEXNSLOG(@"我点击了全部啊----%f",floor(amount));
                };
                [cell setBottomLineHidden:true];
            }
        }
            break;
        case 2: {
            WeXCPLeftAndRightLabelCell *cell = (WeXCPLeftAndRightLabelCell *)currentCell;
            [cell setLeftTitle:_sectionTitles[indexPath.row] rightTitle:_sectionSubtitles[indexPath.row]];
            if (indexPath.row == 0) {
                cell.textField.userInteractionEnabled = true;
                self.priceTextFiled = cell.textField;
                cell.textField.delegate = self;
            } else if (indexPath.row == 1) {
                cell.textField.userInteractionEnabled = true;
                self.limitTextField = cell.textField;
                cell.textField.delegate = self;
            } else {
                cell.textField.userInteractionEnabled = NO;
                if (self.maxFee) {
                    NSString *maxFee = [NSString stringWithFormat:@"%.8f",[_limitTextField.text floatValue]*[_priceTextFiled.text floatValue]*powf(10, -9)];
                    [cell.textField setText:maxFee];
                }
            }
        }
            break;
            
            break;
        default:
            break;
    }
}

- (void)reloadCellWithSection:(NSInteger)section row:(NSInteger)row  {
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:row inSection:section];
    [self.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
}

// MARK: - UITextFiledDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    if (textField == _limitTextField) {
        if (self.amount.length>0) {
            [self getGasLimitRequest];
        }
    }
}

- (BOOL)textField:(UITextField *)textField
shouldChangeCharactersInRange:(NSRange)range
replacementString:(NSString *)string {
    if (textField == _limitTextField) {
        [self configCostWithGasLimit:textField.text];
    }
    return true;
}
- (BOOL)textFieldShouldEndEditing:(UITextField *)textField {
    if (textField == _limitTextField) {
        [self configCostWithGasLimit:textField.text];
    }
    return true;
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
                _priceTextFiled.text = [NSString stringWithFormat:@"%.0f",[gasPrice floatValue] < 1.0?1.0:[gasPrice floatValue]];
            }];
        }];
    }];
}

- (void)getGasLimitRequest {
    //    @"0xb63f7de8e81daba914263771371941ee44029caa";
    NSString *testAddress = _productModel.contractAddress;
    NSString *params = @"[]";
    [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        [[WXPassHelper instance] encodeFunCallAbiInterface:WEXCP_ETH_Invest_ABI params:params responseBlock:^(id response) {
            [[WXPassHelper instance]
             getCPETHGasLimitWithToAddress:testAddress
             fromAddress:[WexCommonFunc getFromAddress]
             data:response
             value:[self.minOriginalBuyAmout transferTenToSixteenScale]
             responseBlock:^(id response) {
                 if (![response isKindOfClass:[NSDictionary class]]) {
                     if (_limitTextField.text.length ==0 ) {
                         _limitTextField.text = [NSString stringWithFormat:@"%@",response];
                         [self configCostWithGasLimit:_limitTextField.text];
                     }
                 }
             }];
        }];
    }];
}

- (void)configCostWithGasLimit:(NSString *)gasLimitString {
    _maxFee = [gasLimitString floatValue]*[_priceTextFiled.text floatValue]*powf(10, -9);
    [self reloadCellWithSection:2 row:2];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


@end
