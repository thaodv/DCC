//
//  WeXCPPotDetailViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPPotDetailViewController.h"
#import "WeXCoinProfitTopProfitCell.h"
#import "WeXCPCompoundCell.h"
#import "WeXCoinProfitOnlyTextCell.h"
#import "WeXCPNoRecordCell.h"
#import "WeXCPHeader.h"
#import "WeXCPGetContractAddressAdapter.h"
#import "WeXCPSaleInfoResModel.h"
#import "NSString+WexTool.h"
#import "WeXCoinProfitDetailViewController.h"

@interface WeXCPPotDetailViewController ()

@property (nonatomic, strong) NSArray *titles;
@property (nonatomic, strong) NSArray *subTitles;
@property (nonatomic, strong) WeXCPGetContractAddressAdapter *getContractAddress;
@property (nonatomic, strong) WeXCPSaleInfoResModel *infoResModel;
@property (nonatomic, strong) WeXCPGetContractAddressResModel *responseModel;
//个人投资总额
@property (nonatomic, copy) NSString *cpInvestTotalAmount;
//活动状态
@property (nonatomic, copy) NSString *cpStatus;


@end

static NSString *const kCPTopProfitCellID = @"WeXCoinProfitTopProfitCell";
static NSString *const kCPArrowCellID     = @"WeXCPCompoundCell";
static NSString *const kCPOnlyTextCellID  = @"WeXCoinProfitOnlyTextCellID";
static NSString *const kCPNoRecordCellID  = @"WeXCPNoRecordCellID";


@implementation WeXCPPotDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"币生息详情");
    [self configureNavigaionBar];
    [self wex_autoRefresh];
}
- (void)configureNavigaionBar {
    [self setNavigationNomalBackButtonType];
    _titles = @[@"DCC分期2018060728",@"预期年化收益",@"管理期限",@"收益方式",@"起息日期",@"到期日期"];
    _subTitles = @[@"10.5%",@"28天",@"到期还本付息",@"2018-04-26",@"2018-04-26"];
}
- (void)wex_autoRefresh {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self getCPContractAddress];
}
- (void)wex_refreshAction {
    if ([self.responseModel.result length] > 0) {
        [self requestSaleInfo];
    } else {
        [self getCPContractAddress];
    }
}
- (void)backItemClick {
    if (self.popToCoinProfitDetailVC) {
        [self.navigationController.viewControllers enumerateObjectsUsingBlock:^(UIViewController * obj, NSUInteger idx, BOOL *  stop) {
            if ([obj isKindOfClass:[WeXCoinProfitDetailViewController class]]) {
                WeXCoinProfitDetailViewController *coinVC = (WeXCoinProfitDetailViewController *)obj;
                [coinVC wex_refreshContent];
                [self.navigationController popToViewController:coinVC animated:true];
                *stop = true;
            }
        }];
    } else {
        [self.navigationController popViewControllerAnimated:true];
    }
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCPCompoundCell class] forCellReuseIdentifier:kCPArrowCellID];
    [tableView registerClass:[WeXCoinProfitOnlyTextCell  class] forCellReuseIdentifier:kCPOnlyTextCellID];
    [tableView registerClass:[WeXCoinProfitTopProfitCell class] forCellReuseIdentifier:kCPTopProfitCellID];
    [tableView registerClass:[WeXCPNoRecordCell class] forCellReuseIdentifier:kCPNoRecordCellID];
}

#pragma mark UITableViewDatasource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return [self.cpInvestTotalAmount integerValue] <= 0 ? 1 : 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if ([self.cpInvestTotalAmount integerValue] <= 0) {
        return 1;
    }
    switch (section) {
        case 0:
            return 1;
            break;
        case 1:
            return _titles.count;
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
    if (section == 1) {
        return 15;
    }
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([self.cpInvestTotalAmount integerValue] <= 0) {
        return 55 * 6 + 50;
    }
    switch (indexPath.section) {
        case 0:
            return [self wexTableView:tableView cellIdentifier:kCPTopProfitCellID indexPath:indexPath];
            break;
        case 1:
            if (indexPath.row == 0) {
                return [self wexTableView:tableView cellIdentifier:kCPArrowCellID indexPath:indexPath];
            } else {
                return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
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
    if ([self.cpInvestTotalAmount integerValue] <= 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kCPNoRecordCellID indexPath:indexPath];
    }
    switch (indexPath.section) {
        case 0:
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPTopProfitCellID indexPath:indexPath];
            break;
        case 1:
            if (indexPath.row == 0) {
                return [self wexTableview:tableView cellForRowWithIdentifier:kCPArrowCellID indexPath:indexPath];
            }
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
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
    if (indexPath.section == 1 && indexPath.row == 0) {
        WEXNSLOG(@"呵呵呵哒");
    }
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    if ([self.cpInvestTotalAmount integerValue] <= 0) {
        WeXCPNoRecordCell *cell = (WeXCPNoRecordCell *)currentCell;
        [cell setImageName:@"Wex_Coin_NoRecord" subText:@"暂无记录"];
    } else {
        switch (indexPath.section) {
            case 0: {
                WeXCoinProfitTopProfitCell *cell = (WeXCoinProfitTopProfitCell *)currentCell;
                if ([self.cpInvestTotalAmount length] > 0 && [self.cpStatus length] > 0) {
                    [cell setSaleInfoModel:self.infoResModel statusString:self.cpStatus totalAmount:self.cpInvestTotalAmount];
                }
            }
                break;
            case 1: {
                if (indexPath.row == 0) {
                    WeXCPCompoundCell *cell = (WeXCPCompoundCell *)currentCell;
                    [cell setLeftTitle:self.infoResModel.name rightButtonImage:nil];
                }
                else{
                    WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
                    NSString *subTitle = nil;
                    if (indexPath.row == 0) {
                        subTitle = self.infoResModel.name;
                    } else if (indexPath.row == 1) {
                        subTitle = [NSString stringWithFormat:@"%@%@",self.infoResModel.annualRate,@"%"];
                    } else if (indexPath.row == 2) {
                        subTitle = [self.infoResModel.period stringByAppendingString:@"天"];
                    } else if (indexPath.row == 3) {
                        subTitle = self.infoResModel.profitMethod;
                    } else if (indexPath.row == 4) {
                        subTitle = [self.infoResModel.incomeTime
                                    yearToDayTimeString];
                    } else {
                        subTitle = [self.infoResModel.endTime yearToDayTimeString];
                    }
                    [cell setTitle:_titles[indexPath.row] subText:subTitle cellType:WeXCoinProfitOnlyTextCellTypeDefaultSubtextColor];
                }
            }
                break;
                
            default:
                break;
        }
    }
}

- (void)refreshTopHeadCell {
    if ([self.cpInvestTotalAmount integerValue] <= 0) {
        [self.tableView reloadData];
    }
    if ([self.cpInvestTotalAmount length] > 0 && [self.cpStatus length] > 0) {
        [self.tableView reloadData];
    }
}
- (void)reloadCellWithRow:(NSInteger)row section:(NSInteger)section {
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:row inSection:section];
    [self.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
}

- (void)requestSaleInfo {
    NSString *params   = @"[]";
    NSString *p_params = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    //投资总额度
    NSString *totalAmountJson = WEXCP_PInvestedTotalAmount_ABI_BALANCE;
    //活动状态
    NSString *statusJson = WEXCP_STATUS_ABI_BALANCE;
    
    [[WXPassHelper instance] initProvider:WEXCP_INVEST_URL responseBlock:^(id response) {
        //活动状态
        [[WXPassHelper instance] encodeFunCallAbiInterface:statusJson params:params responseBlock:^(id response) {
            [[WXPassHelper instance] callContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
                NSDictionary *responseDict = response;
                NSString * originBalance   = [responseDict objectForKey:@"result"];
                NSString * ethException    = [responseDict objectForKey:@"ethException"];
                if (![ethException isEqualToString:@"ethException"]) {
                    self.cpStatus = originBalance;
                    [self refreshTopHeadCell];
                } else {
                    [self wex_endRefresh];
                }
            }];
        }];

        //规则信息
        [[WXPassHelper instance] encodeFunCallAbiInterface:WEXCP_SaleInfo_ABI_BALANCE params:params responseBlock:^(id response) {
            [[WXPassHelper instance] call3ContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
                NSDictionary *responseDict = response;
                NSString * originBalance   = [responseDict objectForKey:@"result"];
                NSString * ethException    = [responseDict objectForKey:@"ethException"];
                if (![ethException isEqualToString:@"ethException"]) {
                    NSDictionary *jsonDic = [originBalance transferHexStringDataToNSDictionary];
                    WeXCPSaleInfoResModel *infoResModel = [[WeXCPSaleInfoResModel alloc] initWithDictionary:jsonDic error:nil];
                    self.infoResModel = infoResModel;
                    [self.tableView reloadData];
                    WEXNSLOG(@"jsonDic:---%@----%@",originBalance,jsonDic);
                } else {
                    [WeXPorgressHUD showText:@"服务异常,请检查网络后重试" onView:self.view];
                }
            }];
        }];
        
        //投资总额
        [[WXPassHelper instance] encodeFunCallAbiInterface:totalAmountJson params:p_params responseBlock:^(id response) {
            [[WXPassHelper instance] callContractAddress:self.responseModel.result data:response responseBlock:^(id response) {
                NSDictionary *responseDict = response;
                NSString * originBalance   = [responseDict objectForKey:@"result"];
                NSString * ethException    = [responseDict objectForKey:@"ethException"];
                if (![ethException isEqualToString:@"ethException"]) {
                    originBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
                    self.cpInvestTotalAmount = originBalance;
                    [self wex_endRefresh];
                    [self refreshTopHeadCell];
                } else {
                    [self wex_endRefresh];
                }
            }];
        }];
    }];
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
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误" onView:self.view];
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


@end
