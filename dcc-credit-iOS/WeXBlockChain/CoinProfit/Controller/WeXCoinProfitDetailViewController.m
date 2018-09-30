//
//  WeXCoinProfitDetailViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/13.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCoinProfitDetailViewController.h"
#import "WeXCoinProfitTopProfitCell.h"
#import "WeXCPBuyFlowTableViewCell.h"
#import "WeXCoinProfitOnlyTextCell.h"
#import "WeXBuyInTableViewCell.h"
#import "WeXCPBuyAmoutViewController.h"
#import "WeXCPPotDetailViewController.h"
#import "WeXCPSaleInfoResModel.h"
#import "WeXCPHeader.h"
#import "WeXCPGetContractAddressAdapter.h"
#import "NSString+WexTool.h"
#import "WeXCPBuyInETHViewController.h"
#import "WeXHomePushService.h"
#import "WeXCPPotListViewController.h"
#import "WeXCPActivityMainResModel.h"

@interface WeXCoinProfitDetailViewController ()

@property (nonatomic, strong) NSArray *titles;
@property (nonatomic, strong) NSArray *subTitles;
@property (nonatomic, strong) WeXCPSaleInfoResModel *infoResModel;
@property (nonatomic, strong) WeXCPGetContractAddressAdapter *getContractAddress;
@property (nonatomic, strong) WeXCPGetContractAddressResModel *responseModel;
//总额度
@property (nonatomic, copy) NSString *cpTotalAmount;
//已售额度
@property (nonatomic, copy) NSString *cpHaveSaleAmout;
//剩余额度
@property (nonatomic, copy) NSString *cpRemainAmout;
//起购额度
@property (nonatomic, copy) NSString *cpMinBuyAmount;
//状态
@property (nonatomic, copy) NSString *cpStatus;
//区分是否是ETH
@property (nonatomic, assign) BOOL isETH;

@end

static NSString *const kCoinProfitTopProfitCellID = @"WeXCoinProfitTopProfitCellID";
static NSString *const kCPOnlyTextCellID = @"WeXCoinProfitOnlyTextCell";
static NSString *const kCPAutoHeightCellID = @"WeXCoinProfitAutoHeightTextCellID";
static NSString *const kCPBuyFlowCelID = @"WeXCPBuyFlowTableViewCellID";
static NSString *const kCPBuyInCellID = @"WeXBuyInTableViewCellID";


@implementation WeXCoinProfitDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    NSAssert(_productModel, @"productModel参数为空");
    _titles = @[@"认购币种",@"收益方式",@"产品额度",@"时间限制",@"剩余额度"];
    self.title = WeXLocalizedString(@"币生息");
    [self configureNavigaionBar];
    [self wex_autoRefresh];

}
- (void)wex_refreshContent {
    [self.tableView setContentOffset:CGPointZero animated:NO];
    [self wex_refreshAction];
}
- (void)wex_autoRefresh {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self p_configureContractAddressModel];
    [self setIsETH:[_productModel.assetCode isEqualToString:@"ETH"]];
    [self requestSaleInfo];
}
// MARK: - 配置合约地址Model
- (void)p_configureContractAddressModel {
    if ([self.responseModel.result length] < 1) {
        self.responseModel = [WeXCPGetContractAddressResModel new];
        self.responseModel.result = _productModel.contractAddress;
    }
}

- (void)wex_refreshAction {
    [self p_configureContractAddressModel];
    [self requestSaleInfo];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}
- (void)wex_endRefresh {
    [super wex_endRefresh];
    [WeXPorgressHUD hideLoading];
}

- (void)configureNavigaionBar {
    [self setNavigationNomalBackButtonType];
    [self.tableView setBackgroundColor:ColorWithHex(0xF8F8FF)];
//    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"持仓" style:UIBarButtonItemStyleDone target:self action:@selector(positionEvent:)];
//    self.navigationItem.rightBarButtonItem = rightButton;
//    NSDictionary *attributes = @{NSFontAttributeName:WexFont(15),
//                                 NSForegroundColorAttributeName:ColorWithHex(0x5756B3)
//                                 };
//    [self.navigationItem.rightBarButtonItem setTitleTextAttributes:attributes forState:UIControlStateNormal];
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCoinProfitTopProfitCell class] forCellReuseIdentifier:kCoinProfitTopProfitCellID];
    [tableView registerClass:[WeXCoinProfitOnlyTextCell  class] forCellReuseIdentifier:kCPOnlyTextCellID];
    [tableView registerClass:[WeXCPBuyFlowTableViewCell  class] forCellReuseIdentifier:kCPBuyFlowCelID];
    [tableView registerClass:[WeXCoinProfitAutoHeightTextCell class] forCellReuseIdentifier:kCPAutoHeightCellID];
    [tableView registerClass:[WeXBuyInTableViewCell class] forCellReuseIdentifier:kCPBuyInCellID];
}
#pragma mark UITableViewDatasource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 5;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0:
            return 1;
            break;
        case 1:
            return _titles.count;
            break;
        case 2:
            return 2;
            break;
        case 3:
            return 2;
            break;
        case 4:
            return 2;
            break;
        default:
            return 0;
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section != 3) {
        return 15;
    }
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0:
            return [self wexTableView:tableView cellIdentifier:kCoinProfitTopProfitCellID indexPath:indexPath];
            break;
        case 1:
            return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            break;
        case 2:
            if (indexPath.row == 0) {
                return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            }
            return [self wexTableView:tableView cellIdentifier:kCPBuyFlowCelID indexPath:indexPath];
            break;
        case 3:
            if (indexPath.row == 0) {
                return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            }
            return [self wexTableView:tableView cellIdentifier:kCPAutoHeightCellID indexPath:indexPath];
            break;
        case 4:
            if (indexPath.row == 0) {
                return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            }
            return [self wexTableView:tableView cellIdentifier:kCPBuyInCellID indexPath:indexPath];
            break;
            
        default:
            return 0.01;
            break;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0:
            return [self wexTableview:tableView cellForRowWithIdentifier:kCoinProfitTopProfitCellID indexPath:indexPath];
            break;
        case 1:
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            break;
        case 2:
            if (indexPath.row == 0) {
                return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            }
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPBuyFlowCelID indexPath:indexPath];
            break;
        case 3:
            if (indexPath.row == 0) {
                return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            }
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPAutoHeightCellID indexPath:indexPath];
            break;
        case 4:
            if (indexPath.row == 0) {
                return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            }
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPBuyInCellID indexPath:indexPath];
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
            WeXCoinProfitTopProfitCell *cell = (WeXCoinProfitTopProfitCell *)currentCell;
//            [cell setNewCoinProfitDetailWithProductModel:_productModel];
            if (self.infoResModel) {
                [cell setSaleInfoModel:self.infoResModel];
            }
            if ([self.cpMinBuyAmount length] > 0) {
                [cell setMinBuyAmount:self.cpMinBuyAmount assetCode:_productModel.assetCode];
            }
        }
            break;
        case 1: {
            WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
            NSString *title = _titles[indexPath.row];
            NSString *subTitle = nil;
            if (indexPath.row == 0) {
                subTitle = _productModel.assetCode;
            } else if (indexPath.row == 1) {
                subTitle = self.infoResModel.profitMethod;
            } else if (indexPath.row == 2) {
                if ([self.cpTotalAmount length] > 0) {
                    subTitle = [NSString stringWithFormat:@"%@%@",self.cpTotalAmount,_productModel.assetCode];
                }
            } else if (indexPath.row == 3) {
                if ([self.infoResModel.endTime length] > 0) {
                    NSString *text = [NSString stringWithFormat:@"至%@",[self.infoResModel.closeTime monthAndDayTimeString]];
                    subTitle = [NSString stringWithFormat:@"%@,%@",text,@"或购完即止"];
                }
            } else {
                if ([self.cpRemainAmout length] > 0) {
                    NSString *rate = [NSString stringWithFormat:@"%.1f",[self.cpRemainAmout floatValue] * 100 / [self.cpTotalAmount floatValue]];
                    subTitle = [NSString stringWithFormat:@"%@ %@ (%@%@)",self.cpRemainAmout,_productModel.assetCode,rate,@"%"];
                }
            }
            if (indexPath.row == _titles.count - 1) {
                [cell setTitle:title subText:subTitle cellType:WeXCoinProfitOnlyTextCellTypeRedSubTextColor];
            } else {
                [cell setTitle:title subText:subTitle cellType:WeXCoinProfitOnlyTextCellTypeDefaultSubtextColor];
            }
        }
            break;
        case 2:
            if (indexPath.row == 0) {
                WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
                [cell setTitle:@"认购流程" subText:nil cellType:WeXCoinProfitOnlyTextCellTypeOnlyTitle];
            } else {
                WeXCPBuyFlowTableViewCell *cell = (WeXCPBuyFlowTableViewCell *)currentCell;
                if (self.infoResModel) {
                    [cell setSaleInfoModel:self.infoResModel];
                }
            }
            break;
        case 3:
            if (indexPath.row == 0) {
                WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
                [cell setTitle:@"产品简介" subText:nil cellType:WeXCoinProfitOnlyTextCellTypeOnlyTitle];
            } else {
                WeXCoinProfitAutoHeightTextCell *cell = (WeXCoinProfitAutoHeightTextCell *)currentCell;
                if (self.infoResModel) {
                    [cell setSaleInfoResModel:self.infoResModel];
                }
            }
            break;
        case 4: {
            if (indexPath.row == 0) {
                WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
                [cell setTitle:@"本服务由[Token Plus] 提供" subText:nil cellType:WeXCoinProfitOnlyTextCellTypeOnlyCenterTitle];
            } else {
                WeXBuyInTableViewCell *cell = (WeXBuyInTableViewCell *)currentCell;
                cell.BuyInEvent = ^{
                    [self buyInEvent];
                };
                //已售完
                if ([self.cpRemainAmout integerValue] < [self.cpMinBuyAmount integerValue]) {
                    [cell setBuyInCellType:WeXBuyInTableViewCellTypeSellOver];
                }
                //已结束
                if (![self.cpStatus isEqualToString:@"1"]) {
                    [cell setBuyInCellType:WeXBuyInTableViewCellTypeEnd];
                }
            }
        }
            break;
        default:
            break;
    }

}

// MARK: - 持仓
- (void)positionEvent:(UIBarButtonItem *)buttonItem {
    WEXNSLOG(@"持仓");
//    WeXCPPotDetailViewController *potDetailVC = [WeXCPPotDetailViewController new];
//    [self.navigationController pushViewController:potDetailVC animated:YES];
    [WeXHomePushService pushFromVC:self toVC:[WeXCPPotListViewController new]];
}

// MARK: - 认购
- (void)buyInEvent {
    [WeXPorgressHUD hideLoading];
    if ([_productModel.assetCode isEqualToString:@"DCC"]) {
        WeXCPBuyAmoutViewController *buyDCCVC = [WeXCPBuyAmoutViewController new];
        buyDCCVC.productModel = _productModel;
        [WeXHomePushService pushFromVC:self toVC:buyDCCVC];
    } else {
        WeXCPBuyInETHViewController *buyETHVC = [WeXCPBuyInETHViewController new];
        buyETHVC.productModel = _productModel;
        [WeXHomePushService pushFromVC:self toVC:buyETHVC];
    }
//    WeXCPBuyAmoutViewController *buyAmountVC = [WeXCPBuyAmoutViewController new];
//
//    [self.navigationController pushViewController:buyAmountVC animated:YES];
    WEXNSLOG(@"呵呵呵哒");
}

- (void)requestSaleInfo {
    NSString *params = @"[]";
    //总额度
    NSString *totalAmountJson    = _isETH ? WEXCP_ETH_InvestCeilAmount_ABI : WEXCP_InvestCeilAmount_ABI_BALANCE;
    //已认购额度
    NSString *haveBuyAmountJson  = _isETH ? WEXCP_ETH_InvestedTotalAmount_ABI: WEXCP_InvestedTotalAmount_ABI_BALANCE;
    //产品起购额度
    NSString *minAmountJson      = _isETH ? WEXCP_ETH_MinAmountPerHand_ABI : WEXCP_MinAmountPerHand_ABI_BALANCE;
    //活动状态
    NSString *statusJson         = _isETH ? WEXCP_ETH_Status_ABI : WEXCP_STATUS_ABI_BALANCE;
    //规则信息
    NSString *salInfoJson = _isETH ? WEXCP_ETH_SaleInfo_ABI : WEXCP_SaleInfo_ABI_BALANCE;
    //根据不同期数来获取对应的URL
    NSString *DCCURL = [WEXCP_INVEST_V_URL stringByAppendingString:[_productModel.name formatInputString]];
    NSString *webURL = _isETH ? YTF_DEVELOP_INFURA_SERVER : DCCURL;
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        [[WXPassHelper instance] initProvider:webURL responseBlock:^(id response) {
            //产品起购额度
            [[WXPassHelper instance] encodeFunCallAbiInterface:minAmountJson params:params responseBlock:^(id response) {
                [[WXPassHelper instance] callContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
                    NSDictionary *responseDict = response;
                    NSString * originBalance =[responseDict objectForKey:@"result"];
                    NSString * ethException =[responseDict objectForKey:@"ethException"];
                    if (![ethException isEqualToString:@"ethException"]) {
                        NSString *balace = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
                        self.cpMinBuyAmount = balace;
                    } else {
                        [self wex_endRefresh];
                    }
                    WEXNSLOG(@"r1-----esponse:%@",response);
                }];
            }];
            
            //总额度
            [[WXPassHelper instance] encodeFunCallAbiInterface:totalAmountJson params:params responseBlock:^(id response) {
                [[WXPassHelper instance] callContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
                    NSDictionary *responseDict = response;
                    NSString * originBalance   = [responseDict objectForKey:@"result"];
                    NSString * ethException    = [responseDict objectForKey:@"ethException"];
                    if (![ethException isEqualToString:@"ethException"]) {
                        NSString *balace = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
                        self.cpTotalAmount = balace;
                        [self refreshRemainAmount];
                    } else {
                        [self wex_endRefresh];
                    }
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
                        self.cpHaveSaleAmout = balace;
                        [self refreshRemainAmount];
                    } else {
                        [self wex_endRefresh];
                    }
                    WEXNSLOG(@"r3-----esponse:%@",response);
                }];
            }];
            //规则信息
            [[WXPassHelper instance] encodeFunCallAbiInterface:salInfoJson params:params responseBlock:^(id response) {
                [[WXPassHelper instance] call3ContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
                    NSDictionary *responseDict = response;
                    NSString * originBalance   = [responseDict objectForKey:@"result"];
                    NSString * ethException    = [responseDict objectForKey:@"ethException"];
                    if (![ethException isEqualToString:@"ethException"]) {
                        NSDictionary *jsonDic = [originBalance transferHexStringDataToNSDictionary];
                        WeXCPSaleInfoResModel *infoResModel = [[WeXCPSaleInfoResModel alloc] initWithDictionary:jsonDic error:nil];
                        self.infoResModel = infoResModel;
                        self.title = infoResModel.name;
                        [self wex_endRefresh];
                        [self.tableView reloadData];
                        WEXNSLOG(@"jsonDic:---%@----%@",originBalance,jsonDic);
                    } else {
                        [WeXPorgressHUD showText:@"服务异常,请检查网络后重试" onView:self.view];
                        [self wex_endRefresh];
                    }
                }];
            }];
            
            //活动状态
            [[WXPassHelper instance] encodeFunCallAbiInterface:statusJson params:params responseBlock:^(id response) {
                [[WXPassHelper instance] callContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
                    NSDictionary *responseDict = response;
                    NSString * originBalance   = [responseDict objectForKey:@"result"];
                    NSString * ethException    = [responseDict objectForKey:@"ethException"];
                    if (![ethException isEqualToString:@"ethException"]) {
                        self.cpStatus = originBalance;
                        [self.tableView reloadData];
                    }
                }];
            }];
        }];
    }];
}

- (void)refreshRemainAmount {
    if ([self.cpTotalAmount length] > 0 && [self.cpHaveSaleAmout length] > 0) {
        if ([_productModel.assetCode isEqualToString:@"DCC"]) {
            self.cpRemainAmout = [@([self.cpTotalAmount integerValue] - [self.cpHaveSaleAmout integerValue]) stringValue];
        } else {
            self.cpRemainAmout = [@([self.cpTotalAmount floatValue] - [self.cpHaveSaleAmout floatValue]) stringValue];
        }
//        [self reloadCellWithRow:4 section:1];
        [self.tableView reloadData];
    }
    [WeXPorgressHUD hideLoading];
}


- (void)reloadCellWithRow:(NSInteger)row section:(NSInteger)section {
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:row inSection:section];
    [self.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
}

- (void)getCPContractAddress {
    if (!_getContractAddress) {
        _getContractAddress = [WeXCPGetContractAddressAdapter new];
    }
    _getContractAddress.delegate = self;
    [_getContractAddress run:nil];
}
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response {
    if (adapter == _getContractAddress) {
        if ([headModel.businessCode isEqualToString:WexSuccess] && [headModel.systemCode isEqualToString:WexSuccess]) {
            self.responseModel = (WeXCPGetContractAddressResModel *)response;
            [self requestSaleInfo];
        } else {
            [self wex_endRefresh];
            [WeXPorgressHUD showText:@"系统错误" onView:self.view];
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


@end
