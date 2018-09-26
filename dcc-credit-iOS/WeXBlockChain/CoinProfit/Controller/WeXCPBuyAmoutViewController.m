//
//  WeXCPBuyAmoutViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPBuyAmoutViewController.h"
#import "WeXCPCompoundCell.h"
#import "WeXCoinProfitOnlyTextCell.h"
#import "WeXCPBuyBottomView.h"
#import "WeXCPGetContractAddressAdapter.h"
#import "WeXCPHeader.h"
#import "WeXWalletTransferResultManager.h"
#import "NSString+WexTool.h"
#import "WeXWalletDccTranstionDetailView.h"
#import "WeXCPPotDetailViewController.h"
#import "WeXTokenDccListViewController.h"
#import "WeXCPPushService.h"
#import "WeXDigitalAssetRLMModel.h"
#import "NSString+WexTool.h"
#import "WeXGetReceiptResult2Adapter.h"
#import "WeXCPActivityMainResModel.h"
#import "WeXHomePushService.h"
#import "WeXCPPotListViewController.h"

#define kDccTransDetailViewHeight 350


@interface WeXCPBuyAmoutViewController ()

@property (nonatomic, strong) WeXCPBuyBottomView *bottomView;
@property (nonatomic, strong) WeXCPGetContractAddressAdapter *getContractAddress;
@property (nonatomic, strong) WeXCPGetContractAddressResModel *responseModel;
@property (nonatomic, assign) BOOL isBuyEvent;
//购买数量
@property (nonatomic, copy) NSString *amount;
//钱包余额
@property (nonatomic, copy) NSString *walletBalance;
//起购额度
@property (nonatomic, copy) NSString *minBuyAmount;
//总额度
@property (nonatomic, copy) NSString *totalAmount;
//已认购额度
@property (nonatomic, copy) NSString *haveBuyAmount;
//剩余额度
@property (nonatomic, copy) NSString *remainAmount;

@property (nonatomic, weak) WeXWalletDccTranstionDetailView *dccTransDetailView;
@property (nonatomic, strong) UIView *detailCoverView;
@property (nonatomic, strong) WeXGetReceiptResult2Adapter *resultAdapter;
@property (nonatomic, assign) NSInteger requestCount;
@property (nonatomic, copy) NSString *txHash;

@end

static NSString *const kCPCompundCellID  = @"WeXCPCompoundCellID";
static NSString *const kCPOnlyTextCellID = @"WeXCoinProfitOnlyTextCellID";
static NSString *const kDefaultBalance   = @"--";

@implementation WeXCPBuyAmoutViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"认购额度");
    [self configureNavigaionBar];
    [self setUpBottomView];
    self.walletBalance = kDefaultBalance;
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self wex_unistallRefreshHeader];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self getCPContractAddress];
    [self getPrivateWalletBalance];
}

- (void)getCPContractAddress {
    if (!_getContractAddress) {
        _getContractAddress = [WeXCPGetContractAddressAdapter new];
    }
    _getContractAddress.delegate = self;
    [_getContractAddress run:nil];
}
- (void)configureNavigaionBar {
    [self setNavigationNomalBackButtonType];
    [self.tableView setBackgroundColor:ColorWithHex(0xF8F8FF)];
}
- (void)setUpBottomView {
    __weak typeof(self) weakSelf  = self;
    _bottomView = [WeXCPBuyBottomView createBuyBottomView:CGRectMake(0, 0, kScreenWidth, kScreenHeight - kNavgationBarHeight - 55 * 4 - 15 * 3) clickEvent:^(WeXCPBuyBottomViewType type) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (type == WeXCPBuyBottomViewTypeBuyIn) {
            if ([strongSelf checkBuyParams]) {
                [strongSelf appearBottomView];
            }
        } else {
            [strongSelf pushToTransferPublickToPtivateVC];
        }
        WEXNSLOG(@"%---ld",type);
    }];
    self.tableView.tableFooterView = _bottomView;
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCPCompoundCell class] forCellReuseIdentifier:kCPCompundCellID];
    [tableView registerClass:[WeXCoinProfitOnlyTextCell  class] forCellReuseIdentifier:kCPOnlyTextCellID];
}

#pragma mark UITableViewDatasource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
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
            return 1;
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
            return 0.01;
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
            return nil;
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
            [cell setTitle:@"私链DCC币生息1期(剩余额度 --)" highText:@"--"];
            if ([self.remainAmount length]> 0) {
                NSString *highText = [NSString stringWithFormat:@"%@ DCC",self.remainAmount];
                NSString *text = [NSString stringWithFormat:@"私链DCC币生息1期(剩余额度 %@)",highText];
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
                if (self.minBuyAmount) {
                    NSString *placeHolder = [NSString stringWithFormat:@"%@%@DCC",@"最小认购额度",self.minBuyAmount];
                    [cell setLeftTitle:nil rightText:@"DCC" placeHolder:placeHolder type:WeXCPCompoundTypeTextFiledAndLabel];
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
                NSString *balance= [NSString stringWithFormat:@"可用额度: %@DCC",self.walletBalance];
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
        case 2:
            break;
            
            break;
        default:
            break;
    }
}

- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response {
    if (adapter == _getContractAddress) {
        if ([headModel.businessCode isEqualToString:WexSuccess] && [headModel.systemCode isEqualToString:WexSuccess]) {
            self.responseModel = (WeXCPGetContractAddressResModel *)response;
            if (self.isBuyEvent) {
                [self buyInEvent];
            }
            [self getSomeAmountInfoRequest];
        } else {
            if (self.isBuyEvent) {
                [WeXPorgressHUD hideLoading];
                [WeXPorgressHUD showText:@"系统错误" onView:self.view];
            }
        }
    }
    else if (adapter == _resultAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            if (model.hasReceipt && model.approximatelySuccess) {
                //上链成功
                [WeXPorgressHUD hideLoading];
                [self buySuccessEvent];
                _requestCount = 0;
            } else {
                if (_requestCount > 5) {
                    _requestCount = 0;
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"购买失败,请稍后重试") onView:self.view];
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self getResultRequestWithTXHash:self.txHash];
                    _requestCount++;
                });
            }
        } else {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"购买失败,请稍后重试") onView:self.view];
        }
    }
}

// MARK: - 立即认购
- (void)buyInEvent {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    //转换为乘以10的18次方
    NSString *value = [[WexCommonFunc stringWithOriginString:self.amount multiplyString:EIGHTEEN_ZERO] stringValue];
    NSString* abiParamsValues = [NSString stringWithFormat:@"[\'%@\']",value]; 
    // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
    NSString* contractAddress = self.responseModel.result;
    // 以太坊私钥地址
    NSString* privateKey = cacheModel.walletPrivateKey;
    
    //根据不同期数来获取对应的URL
    NSString *DCCURL = [WEXCP_INVEST_V_URL stringByAppendingString:[_productModel.name formatInputString]];
    if ([contractAddress length] > 0) {
        [[WXPassHelper instance] initProvider:DCCURL responseBlock:^(id response) {
            [[WXPassHelper instance] signTransactionWithContractAddress:contractAddress abiInterface:WEXCP_INVEST_ABI_BALANCE params:abiParamsValues privateKey:privateKey responseBlock:^(id response) {
                [[WXPassHelper instance] sendRawTransaction:response responseBlock:^(id response) {
                    self.isBuyEvent = NO;
                    if([response isKindOfClass:[NSDictionary class]]) {
                        [WeXPorgressHUD hideLoading];
                        [WeXPorgressHUD showText:WeXLocalizedString(@"认购失败,请稍后再试") onView:self.view];
                        return;
                    };
                    if (response) {
                        NSString *txHash = [NSString stringWithFormat:@"%@",response];
                        [self savePendingTransferModelWithTxhash:txHash];
                        self.txHash = txHash;
                        [self getResultRequestWithTXHash:txHash];
                    }
                    WEXNSLOG(@"-----%@",response);
                }];
            }];
        }];
    } else {
        [self getCPContractAddress];
    }
}

- (void)getSomeAmountInfoRequest {
    NSString *params = @"[]";
    //总额度
    NSString *totalAmountJson = WEXCP_InvestCeilAmount_ABI_BALANCE;
    //产品起购额度
    NSString *minAmountJson   = WEXCP_MinAmountPerHand_ABI_BALANCE;
    //已认购额度
    NSString *haveBuyAmountJson = WEXCP_InvestedTotalAmount_ABI_BALANCE;
    
    //根据不同期数来获取对应的URL
    NSString *DCCURL = [WEXCP_INVEST_V_URL stringByAppendingString:[_productModel.name formatInputString]];

    [[WXPassHelper instance] initProvider: DCCURL responseBlock:^(id response) {
        //产品起购额度
        [[WXPassHelper instance] encodeFunCallAbiInterface:minAmountJson params:params responseBlock:^(id response) {
            [[WXPassHelper instance] callContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
                NSDictionary *responseDict = response;
                NSString * originBalance = [responseDict objectForKey:@"result"];
                NSString * ethException  = [responseDict objectForKey:@"ethException"];
                if (![ethException isEqualToString:@"ethException"]) {
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
            [[WXPassHelper instance] callContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
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
            [[WXPassHelper instance] callContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
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

- (void)getPrivateWalletBalance {
    //abi方法
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    //参数为需要查询的地址
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
        //私链
        [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getDCCContractAddress] data:response type:WEX_DCC_NODE_SERVER responseBlock:^(id response) {
            WEXNSLOG(@"dcc私链balance=%@",response);
            NSDictionary *responseDict = response;
            NSString * originBalance =[responseDict objectForKey:@"result"];
            NSString * ethException =[responseDict objectForKey:@"ethException"];
            //针对查询钱包余额失败的情况
            if ([ethException isEqualToString:@"ethException"]) {
                self.walletBalance = @"--";
            } else {
                self.walletBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
            }
            [WeXPorgressHUD hideLoading];
            [self.tableView reloadData];
        }];
    }];
}
// MARK: - 购买成功
- (void)buySuccessEvent {
    [WeXPorgressHUD showText:@"认购成功" onView:self.view];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.7 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        WeXCPPotDetailViewController *potDetailVC = [WeXCPPotDetailViewController new];
//        [self.dccTransDetailView removeFromSuperview];
//        [self.detailCoverView    removeFromSuperview];
//        potDetailVC.popToCoinProfitDetailVC = true;
//        potDetailVC.buyProductModel = _productModel;
//        [WeXHomePushService pushFromVC:self toVC:potDetailVC];
        
        [self.dccTransDetailView removeFromSuperview];
        [self.detailCoverView    removeFromSuperview];
        [WeXHomePushService pushFromVC:self toVC:[WeXCPPotListViewController new]];
    });
}
- (void)appearBottomView {
    //创建蒙版
    _detailCoverView = [[UIView alloc] initWithFrame:self.view.bounds];
    _detailCoverView.backgroundColor = [UIColor blackColor];
    _detailCoverView.alpha = COVER_VIEW_ALPHA;
    [self.view addSubview:_detailCoverView];
    
    WeXWalletDccTranstionDetailView  *transDetailView = [[WeXWalletDccTranstionDetailView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, kDccTransDetailViewHeight)];
    [transDetailView setTranstionViewType:WeXWalletTranstionViewTypeCPBuy];
    transDetailView.fromLabel.text  = [WexCommonFunc getFromAddress];
    transDetailView.toLabel.text    = self.responseModel.result;

    transDetailView.valueLabel.text = [NSString stringWithFormat:@"%.4f%@",[self.amount floatValue],@"DCC"];
    transDetailView.backgroundColor   = [UIColor whiteColor];
    transDetailView.balanceLabel.text = [NSString stringWithFormat:@"%@DCC",self.walletBalance];
    [self.view addSubview:transDetailView];
    self.dccTransDetailView = transDetailView;
    
    __weak typeof(transDetailView) weakTransDetailView = transDetailView;
    __weak typeof(self) weakSelf  = self;
    //动画
    [UIView animateWithDuration:0.5 animations:^{
        transDetailView.frame = CGRectMake(0, kScreenHeight-kDccTransDetailViewHeight, kScreenWidth, kDccTransDetailViewHeight);
    }];
    
    //点击取消按钮
    transDetailView.cancelBtnBlock = ^{
        [UIView animateWithDuration:0.5 animations:^{
            weakTransDetailView.frame = CGRectMake(0, kScreenHeight, kScreenWidth, kDccTransDetailViewHeight);
        }];
        [self.detailCoverView removeFromSuperview];
        [weakTransDetailView dismiss];
    };
    transDetailView.confirmBtnBlock  = ^{
        if ([weakSelf.amount floatValue] > [weakSelf.walletBalance floatValue]) {
            UIAlertAction *alert = [UIAlertAction actionWithTitle:WeXLocalizedString(@"确定") style:UIAlertActionStyleCancel handler:^(UIAlertAction * action) {
            }];
            UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:WeXLocalizedString(@"提示") message:WeXLocalizedString(@"持有量不足,请核对后重新提交!") preferredStyle:UIAlertControllerStyleAlert];
            [alertCtrl addAction:alert];
            [self presentViewController:alertCtrl animated:YES completion:nil];
            return;
        }
        __strong typeof(weakSelf) strongSelf = weakSelf;
        //发起购买请求
        [strongSelf buyInEvent];
    };
}

- (BOOL)checkBuyParams {
    if ([self.amount integerValue] < [self.minBuyAmount integerValue]) {
        NSString *text = [NSString stringWithFormat:@"最低买入%@DCC",self.minBuyAmount];
        [WeXPorgressHUD showText:text onView:self.view];
        return NO;
    }
    if ([self.amount integerValue] > [self.totalAmount integerValue] - [self.haveBuyAmount integerValue]) {
        NSString *text = [NSString stringWithFormat:@"最多买入%ld DCC",[self.totalAmount integerValue] - [self.haveBuyAmount integerValue]];
        [WeXPorgressHUD showText:text onView:self.view];
        return NO;
    }
    if (![self.amount isPureInt]) {
        [WeXPorgressHUD showText:@"认购额度只能是整数" onView:self.view];
        return NO;
    }
    return YES;
}
// MARK: - 公链转私链
- (void)pushToTransferPublickToPtivateVC {
    RLMResults <WeXDigitalAssetRLMModel *>*results = [WeXDigitalAssetRLMModel allObjects];
    WeXDigitalAssetRLMModel *rlmModel = results.firstObject;
    WeXWalletDigitalGetTokenResponseModal_item *model =  [[WeXWalletDigitalGetTokenResponseModal_item alloc] init];
    model.symbol =rlmModel.symbol;
    model.name = rlmModel.name;
    model.iconUrl = rlmModel.iconUrl;
    model.decimals = rlmModel.decimals;
    model.contractAddress = rlmModel.contractAddress;
    WeXTokenDccListViewController *dccListVC = [WeXTokenDccListViewController new];
    dccListVC.tokenModel = model;
    [WeXCPPushService pushFrom:self to:dccListVC];
}

- (void)savePendingTransferModelWithTxhash:(NSString *)txhash {
    WeXWalletTransferPendingModel *model = [[WeXWalletTransferPendingModel alloc] init];
    model.from = [WexCommonFunc getFromAddress];
    //收款地址,智能合约地址
    model.to   = self.responseModel.result;
    NSDate *nowTime = [NSDate date];
    NSTimeInterval timeStamp = nowTime.timeIntervalSince1970;
    model.timeStamp = [NSString stringWithFormat:@"%f",timeStamp];
    model.value = self.amount ? [[WexCommonFunc stringWithOriginString:self.amount multiplyString:EIGHTEEN_ZERO] stringValue]:nil;
    model.txhash   = txhash;
    model.nonce    = nil;
    model.gasPrice = nil;
    model.gasLimit = nil;
    WeXWalletTransferResultManager *manager = [[WeXWalletTransferResultManager alloc] init];
    [manager savePendingModel:model symbol:[NSString stringWithFormat:@"%@_private",@"DCC"]];
}

- (void)getResultRequestWithTXHash:(NSString *)txHash {
    if (!_resultAdapter) {
        _resultAdapter = [WeXGetReceiptResult2Adapter new];
    }
    _resultAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = txHash;
    [_resultAdapter run:paramModal];
}

// MARK: - 刷新剩余额度
- (void)refreshRemainAmout {
    if ([self.totalAmount length] > 0 && [self.haveBuyAmount length] > 0) {
        NSInteger remainAmout = [self.totalAmount integerValue] - [self.haveBuyAmount integerValue];
        self.remainAmount = [@(remainAmout) stringValue];
        [self.tableView reloadData];
    }
}

- (void)reloadCellWithSection:(NSInteger)section row:(NSInteger)row  {
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:row inSection:section];
    [self.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


@end
