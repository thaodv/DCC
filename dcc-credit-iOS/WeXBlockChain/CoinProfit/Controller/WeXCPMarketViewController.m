//
//  WeXCPMarketViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPMarketViewController.h"
#import "WeXCPMarketTopAmountCell.h"
#import "WeXCPProductInfoCell.h"
#import "WeXHomeIndicatorCell.h"
#import "WeXCPPotListViewController.h"
#import "WeXHomePushService.h"
#import "WeXCPBuyAmoutViewController.h"
#import "WeXCPBuyInETHViewController.h"
#import "WeXCPMarketActivityListAdapter.h"
#import "WeXCoinProfitDetailViewController.h"

@interface WeXCPMarketViewController ()

@property (nonatomic, strong) WeXCPMarketActivityListAdapter *productListAdapter;
@property (nonatomic, strong) NSMutableArray  <WeXCPActivityListModel *>*dataListArray;

@end

static NSString * const kTopAmountCellID   = @"WeXCPMarketTopAmountCellID";
static NSString * const kIndicatorCellID   = @"WeXHomeIndicatorCellID";
static NSString * const kProductInfoCellID = @"WeXCPProductInfoCellID";


@implementation WeXCPMarketViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"币生息");
    [self configureData];
}
- (void)configureData {
    [self wex_unistallRefreshHeader];
    [self.tableView setBackgroundColor:WexSepratorLineColor];
    [self setNavigationNomalBackButtonType];
}
- (void)wex_autoRefreshAction {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self requestProductList];
}

- (void)requestProductList {
    if (!_productListAdapter) {
        _productListAdapter = [WeXCPMarketActivityListAdapter new];
    }
    _productListAdapter.delegate = self;
    [_productListAdapter run:nil];
}


- (NSMutableArray *)dataListArray{
    if(!_dataListArray){
        _dataListArray = [NSMutableArray new];
    }
    return _dataListArray;
}


- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response {
    if ([headModel.businessCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
        WeXCPActivityMainResModel *resModel = (WeXCPActivityMainResModel *)response;
        [self.dataListArray removeAllObjects];
        [self.dataListArray addObjectsFromArray:resModel.result];
        [self.tableView reloadData];
        [WeXPorgressHUD hideLoading];
    } else {
        [WeXPorgressHUD hideLoading];
        [WeXPorgressHUD showText:@"系统错误" onView:self.view];
    }
    WEXNSLOG(@"%@---%@",headModel,response);
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCPMarketTopAmountCell class] forCellReuseIdentifier:kTopAmountCellID];
    [tableView registerClass:[WeXHomeIndicatorCell class] forCellReuseIdentifier:kIndicatorCellID];
    [tableView registerClass:[WeXCPProductInfoCell class] forCellReuseIdentifier:kProductInfoCellID];
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 1;
    } else {
        if (_dataListArray.count > 0) {
            return _dataListArray.count + 1;
        }
        return 0;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableView:tableView cellIdentifier:kTopAmountCellID indexPath:indexPath];
    } else {
        if (indexPath.row == 0) {
            return [WeXHomeIndicatorCell cellHeight];
        }
        return [self wexTableView:tableView cellIdentifier:kProductInfoCellID indexPath:indexPath];
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kTopAmountCellID indexPath:indexPath];
    } else {
        if (indexPath.row == 0) {
            return [self wexTableview:tableView cellForRowWithIdentifier:kIndicatorCellID indexPath:indexPath];
        }
        return [self wexTableview:tableView cellForRowWithIdentifier:kProductInfoCellID indexPath:indexPath];
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        [WeXHomePushService pushFromVC:self toVC:[WeXCPPotListViewController new]];
    } else {
        WeXCPActivityListModel *product = _dataListArray[indexPath.row - 1];
//      这是跳转到币生息详情的
        WeXCoinProfitDetailViewController *detailVC = [WeXCoinProfitDetailViewController new];
        detailVC.title = product.saleInfo.name;
        detailVC.assetCode = product.assetCode;
        [WeXHomePushService pushFromVC:self toVC:[WeXCoinProfitDetailViewController new]];
//        if ([product.assetCode isEqualToString:@"DCC"]) {
//            [WeXHomePushService pushFromVC:self toVC:[WeXCPBuyAmoutViewController new]];
//        } else {
//            [WeXHomePushService pushFromVC:self toVC:[WeXCPBuyInETHViewController new]];
//        }
    }
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        WeXCPMarketTopAmountCell *cell = (WeXCPMarketTopAmountCell *)currentCell;
        [cell setPrincipal:@"2000.89" profit:@"100.08"];
    } else {
        if (indexPath.row == 0) {
            WeXHomeIndicatorCell *cell = (WeXHomeIndicatorCell *)currentCell;
            [cell.contentView setBackgroundColor:WexSepratorLineColor];
            [cell setLeftTitle:@"热门推荐" rightTitle:nil];
        } else {
            WeXCPProductInfoCell *cell = (WeXCPProductInfoCell *)currentCell;
            [cell setMarkeetProductModel:_dataListArray[indexPath.row - 1]];
        }
    }
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


@end
