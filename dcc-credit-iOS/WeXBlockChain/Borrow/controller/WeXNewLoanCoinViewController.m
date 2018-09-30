//
//  WeXNewLoanCoinViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXNewLoanCoinViewController.h"
#import "WeXLoanRecentOrderCell.h"
#import "WeXQueryProductByLenderCodeModal.h"
#import "WeXHomeIndicatorCell.h"
#import "WeXLoanHotCoinsCell.h"
#import "WeXMyBorrowListViewController.h"
#import "WeXHomePushService.h"
#import "WeXLoanQueryOrdersAdapter.h"
#import "WeXLoanGetOrderDetailAdapter.h"
#import "WeXBorrowProductDetailController.h"
#import "WeXMyBorrowListViewController.h"

@interface WeXNewLoanCoinViewController ()
@property (nonatomic, strong) WeXLoanQueryOrdersAdapter *getOrdersAdapter;
@property (nonatomic, strong) NSMutableArray  <WeXLoanQueryOrdersResponseModal_item *>*ordeListArray;
@property (nonatomic, strong) WeXLoanGetOrderDetailAdapter *getOrderDetailAdapter;
@property (nonatomic, strong) WeXLoanGetOrderDetailResponseModal *orderDetailModel;

@end

@implementation WeXNewLoanCoinViewController

static NSString * const kRecentCellID = @"WeXLoanRecentOrderCellID";
static NSString * const kHotTitleCellID = @"WeXHomeIndicatorCellID";
static NSString * const kHotCellID = @"WeXLoanHotCoinsCellID";


- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"信用借币");
    [self configureNav];
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self requestLastOrder];
}


- (void)configureNav {
    [self wex_unistallRefreshHeader];
    [self setNavigationNomalBackButtonType];
    [self.tableView setBackgroundColor:WexSepratorLineColor];
    _ordeListArray = [NSMutableArray new];
}

- (void)requestLastOrder {
    _getOrdersAdapter = [[WeXLoanQueryOrdersAdapter alloc] init];
    _getOrdersAdapter.delegate = self;
    _getOrdersAdapter.number = 0;
    WeXLoanQueryOrdersParamModal *paramModel = [[WeXLoanQueryOrdersParamModal alloc] init];
    paramModel.number = @"0";
    paramModel.size   = @"5";
    [_getOrdersAdapter run:paramModel];
}

- (void)createGetOrderDetailRequestWithOrderID:(NSString *)orderID{
    _getOrderDetailAdapter = [[WeXLoanGetOrderDetailAdapter alloc] init];
    _getOrderDetailAdapter.delegate = self;
    WeXLoanGetOrderDetailParamModal* paramModal = [[WeXLoanGetOrderDetailParamModal alloc] init];
    paramModal.chainOrderId = orderID;
    [_getOrderDetailAdapter run:paramModal];
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXLoanRecentOrderCell class] forCellReuseIdentifier:kRecentCellID];
    [tableView registerClass:[WeXHomeIndicatorCell   class] forCellReuseIdentifier:kHotTitleCellID];
    [tableView registerClass:[WeXLoanHotCoinsCell    class] forCellReuseIdentifier:kHotCellID];
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return _ordeListArray.count > 0 ? 1 : 0;
    }
    return _hotCoins.count + 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableView:tableView cellIdentifier:kRecentCellID indexPath:indexPath];
    } else {
        if (indexPath.row == 0) {
            return [WeXHomeIndicatorCell cellHeight];
        }
        return [self wexTableView:tableView cellIdentifier:kHotCellID indexPath:indexPath];
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kRecentCellID indexPath:indexPath];
    } else {
        if (indexPath.row == 0) {
            return [self wexTableview:tableView cellForRowWithIdentifier:kHotTitleCellID indexPath:indexPath];
        }
        return [self wexTableview:tableView cellForRowWithIdentifier:kHotCellID indexPath:indexPath];
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if(indexPath.section == 1 && indexPath.row > 0) {
        [self pushToBorrowCoinControllerWithCoinModel:_hotCoins[indexPath.row - 1]];
    }
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        WeXLoanRecentOrderCell *cell = (WeXLoanRecentOrderCell *)currentCell;
        [cell setRecentOrderData:_orderDetailModel];
        cell.DidClickMoreOrder = ^{
            [WeXHomePushService pushFromVC:self toVC:[WeXMyBorrowListViewController new]];
        };
    } else {
        if (indexPath.row == 0) {
            WeXHomeIndicatorCell *cell = (WeXHomeIndicatorCell *)currentCell;
            [cell setLeftTitle:@"热门币种" rightTitle:nil];
            [cell.contentView setBackgroundColor:WexSepratorLineColor];
        } else {
            WeXLoanHotCoinsCell *cell = (WeXLoanHotCoinsCell *)currentCell;
            [cell setLoanHotCoinsModel:_hotCoins[indexPath.row - 1]];
        }
    }
}

- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getOrdersAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [_ordeListArray removeAllObjects];
            WeXLoanQueryOrdersResponseModal *model = (WeXLoanQueryOrdersResponseModal *)response;
            [_ordeListArray addObjectsFromArray:model.items];
            [self createGetOrderDetailRequestWithOrderID:_ordeListArray.firstObject.orderId];
            WEXNSLOG(@"model=%@",model);
        } else {
            [WeXPorgressHUD hideLoading];
        }
    } else if (adapter == _getOrderDetailAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            self.orderDetailModel = (WeXLoanGetOrderDetailResponseModal *)response;
            [WeXPorgressHUD hideLoading];
            [self.tableView reloadData];
        } else {
            [WeXPorgressHUD hideLoading];
        }
    }
}


// MARK: - 信用借币
- (void)pushToBorrowCoinControllerWithCoinModel:(WeXQueryProductByLenderCodeResponseModal_item *)model {
    WeXBorrowProductDetailController *borrowCoinVC = [WeXBorrowProductDetailController new];
    borrowCoinVC.productModel = model;
    [WeXHomePushService pushFromVC:self toVC:borrowCoinVC];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}



@end
