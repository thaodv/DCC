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
#import "WeXHomeTableFooterView.h"
#import "WeXCPUserPotAssetAdapter.h"

@interface WeXCPMarketViewController ()

@property (nonatomic, strong) WeXCPMarketActivityListAdapter *productListAdapter;
@property (nonatomic, strong) NSMutableArray  <WeXCPActivityListModel *>*dataListArray;
@property (nonatomic, strong) WeXCPUserPotAssetAdapter   * potAssetAdapter;
@property (nonatomic, strong) WeXCPUserPotAssetResModel  * potAssetModel;

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
//    [self wex_unistallRefreshHeader];
    [self.tableView setBackgroundColor:WexSepratorLineColor];
    [self setNavigationNomalBackButtonType];
    WeXHomeTableFooterView *footerView = [[WeXHomeTableFooterView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 45)];
    [footerView setLineHide];
    [footerView setBackgroundColor:WexSepratorLineColor];
    [footerView setTitle:@"更多产品敬请期待~"];
    self.tableView.tableFooterView= footerView;
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self requestUserPotAsset];
}
- (void)wex_refreshAction {
    [self requestProductList];
    [self requestUserPotAsset];
}

- (void)wex_autoRefreshAction {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self requestProductList];
}

- (void)requestUserPotAsset {
    if (!_potAssetAdapter) {
        _potAssetAdapter = [WeXCPUserPotAssetAdapter new];
    }
    WeXCPUserPotAssetParamModel *param = [WeXCPUserPotAssetParamModel new];
    param.userAddress = [WexCommonFunc getFromAddress];
    _potAssetAdapter.delegate = self;
    [_potAssetAdapter run:param];
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
    if (adapter == _productListAdapter) {
        if ([headModel.businessCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            WeXCPActivityMainResModel *resModel = (WeXCPActivityMainResModel *)response;
            [self.dataListArray removeAllObjects];
            [self.dataListArray addObjectsFromArray:resModel.result];
            [self.tableView reloadData];
            [self wex_endRefresh];
        } else {
            [self wex_endRefresh];
            [WeXPorgressHUD showText:@"系统错误" onView:self.view];
        }
    }
    else if (adapter == _potAssetAdapter) {
        if ([headModel.businessCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            WeXCPUserPotAssetResModel *resModel = (WeXCPUserPotAssetResModel *)response;
            self.potAssetModel = resModel;
            [self.tableView reloadData];
        } else {
            [WeXPorgressHUD showText:@"系统错误" onView:self.view];
        }
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
        WeXCPPotListViewController *potListVC = [WeXCPPotListViewController new];
        potListVC.userPotAssetModel = _potAssetModel;
        [WeXHomePushService pushFromVC:self toVC:potListVC];
    } else {
//      这是跳转到币生息详情的
        WeXCoinProfitDetailViewController *detailVC = [WeXCoinProfitDetailViewController new];
        detailVC.title        = _dataListArray[indexPath.row - 1].saleInfo.name;
        detailVC.productModel = _dataListArray[indexPath.row - 1];
        [WeXHomePushService pushFromVC:self toVC:detailVC];
    }
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        WeXCPMarketTopAmountCell *cell = (WeXCPMarketTopAmountCell *)currentCell;
        NSString *principal = [NSString stringWithFormat:@"%.2f",_potAssetModel.corpus];
        NSString *benefit   = [NSString stringWithFormat:@"%.2f",_potAssetModel.profit];
        [cell setPrincipal:principal profit:benefit];
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
