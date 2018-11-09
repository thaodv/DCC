//
//  WeXSunRecordViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXSunRecordViewController.h"
#import "WeXHomeTableFooterView.h"
#import "WeXSunRecordDetailCell.h"
#import "WeXSunRecordTopCell.h"
#import "WeXSunBalanceRecordAdapter.h"
#import "WeXSunBalanceDetailAdapter.h"
#import "WeXDailyTaskDataModel.h"



@interface WeXSunRecordViewController ()
@property (nonatomic, strong) WeXSunBalanceRecordAdapter *recordAdapter;
@property (nonatomic, assign) NSInteger currentPage;
@property (nonatomic, strong) WeXSunBlanceResModel *resultModel;

@property (nonatomic, strong) WeXSunBalanceDetailAdapter *detailAdapter;
@property (nonatomic, strong) WeXDailyTaskDataModel *dataModel;

@end

static NSString * const kTopCellID    = @"WeXSunRecordTopCellID";
static NSString * const kRecordCellID = @"WeXSunRecordDetailCellID";
static NSString * const kSize = @"20";

@implementation WeXSunRecordViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self configureNavBar];
}

- (void)configureNavBar {
    self.title = WeXLocalizedString(@"阳光记录");
    [self setNavigationNomalBackButtonType];
    [self.tableView setBackgroundColor:WexSepratorLineColor];
}
- (void)wex_refreshAction {
    self.currentPage = 0;
    [self requestRecordList];
    [self requestSunValue];
}

- (void)wex_loadMoreData {
    self.currentPage ++;
    [self wex_refreshAction];
}

- (void)wex_autoRefreshAction {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self wex_refreshAction];
}

- (void)requestSunValue {
    if (!_dataModel) {
        __weak typeof(self) weakSelf     = self;
        _dataModel = [[WeXDailyTaskDataModel alloc] initWithRefreshSunValueBlock:^{
            [weakSelf setSunValue:[weakSelf.dataModel sunBalanceModel].balance];
            if (weakSelf.resultModel.items.count > 0) {
                [weakSelf.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationNone];
            }
        }];
    }
}

- (void)requestDetailAdapter {
    if (!_detailAdapter) {
        _detailAdapter = [WeXSunBalanceDetailAdapter new];
    }
    [_detailAdapter setDelegate:self];
    WeXSunBalanceDetailParamModel *params = [WeXSunBalanceDetailParamModel new];
    params.idNum = @"100128";
    [_detailAdapter run:params];
}


- (void)addNoMoreView {
    WeXHomeTableFooterView *footerView = [[WeXHomeTableFooterView alloc]
                                          initWithFrame:CGRectMake(0, 0, kScreenWidth, 45)];
    [footerView setLineHide];
    [footerView setBackgroundColor:WexSepratorLineColor];
    [footerView setTitle:@"没有更多记录了"];
    self.tableView.tableFooterView= footerView;
}

- (void)removeNoMoreView {
    [self.tableView.tableFooterView removeFromSuperview];
    self.tableView.tableFooterView = nil;
}


- (void)requestRecordList {
    if (!_recordAdapter) {
        _recordAdapter = [WeXSunBalanceRecordAdapter new];
    }
    _recordAdapter.delegate = self;
    WeXSunBlanceParamModel *paramsModel = [WeXSunBlanceParamModel new];
    paramsModel.size = kSize;
    paramsModel.number = [@(_currentPage) stringValue];
    [_recordAdapter run:paramsModel];
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXSunRecordTopCell class] forCellReuseIdentifier:kTopCellID];
    [tableView registerClass:[WeXSunRecordDetailCell class] forCellReuseIdentifier:kRecordCellID];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.resultModel ? 2 : 0;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return section == 0 ? 1 : [self.resultModel.items count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kTopCellID indexPath:indexPath];
    }
    return [self wexTableview:tableView cellForRowWithIdentifier:kRecordCellID indexPath:indexPath];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableView:tableView cellIdentifier:kTopCellID indexPath:indexPath];
    }
    return [self wexTableView:tableView cellIdentifier:kRecordCellID indexPath:indexPath];
}
- (void)wexTableView:(UITableView *)tableView configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        WeXSunRecordTopCell *cell = (WeXSunRecordTopCell *)currentCell;
        [cell setSunValue:self.sunValue];
    }
    else if (indexPath.section == 1) {
        WeXSunRecordDetailCell *recordCell = (WeXSunRecordDetailCell *)currentCell;
        WeXSunBlanceModel *itemModel = _resultModel.items[indexPath.row];
        [recordCell setSunBalanceListModel:itemModel];
        [recordCell setBottomLineHidden:indexPath.row == _resultModel.items.count - 1];
    }
}

- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response {
    if (adapter == _recordAdapter) {
        if ([headModel.systemCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            WeXSunBlanceResModel *resultModel = (WeXSunBlanceResModel *)response;
            self.resultModel = resultModel;
            if ([resultModel.totalPages integerValue] <= _currentPage + 1) {
                [self addNoMoreView];
                [self wex_uninstallRefreshFooter];
            }
            else {
                [self removeNoMoreView];
                [self wex_installRefreshFooter];
            }
            [self wex_endRefresh];
            [self.tableView reloadData];
        }
    }
}


@end
