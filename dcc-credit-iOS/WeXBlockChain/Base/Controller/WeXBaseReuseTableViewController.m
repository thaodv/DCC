//
//  WeXBaseReuseTableViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/6/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseReuseTableViewController.h"
#import <Masonry.h>
#import <UITableView+FDTemplateLayoutCell.h>
#import <MJRefresh.h>

@interface WeXBaseReuseTableViewController ()

@end

@implementation WeXBaseReuseTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setUpSubViews];
    [self wex_loadView];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)setUpSubViews {
    _tableView = [[WeXBaseTableView alloc]  initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight) style:UITableViewStylePlain];
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.dataSource = self;
    _tableView.delegate   = self;
    _tableView.WexDelegate = self;
    _tableView.estimatedRowHeight = 100;
    _tableView.rowHeight  = UITableViewAutomaticDimension;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.showsVerticalScrollIndicator = NO;
    if (@available(iOS 11.0,*)) {
        _tableView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    } else {
        self.automaticallyAdjustsScrollViewInsets = false;
    }
}
- (void)wex_loadView {
    [self.view addSubview:self.tableView];
    [self wexRegisterTableViewCell:self.tableView];
    
    __weak typeof(self) weakSelf     = self;
    MJRefreshNormalHeader *refreshHeader = [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        __strong typeof(weakSelf) strongSelf = weakSelf;
        [strongSelf wex_refreshAction];
    }];
    [refreshHeader.lastUpdatedTimeLabel setHidden:true];
    [refreshHeader.stateLabel setHidden:true];
    self.tableView.mj_header = refreshHeader;
}

- (void)wex_refreshAction {
    
}
- (void)wex_unistallRefreshHeader {
    [self.tableView.mj_header removeFromSuperview];
    self.tableView.mj_header = nil;
}
- (void)wex_uninstallRefreshFooter {
    [self.tableView.mj_footer removeFromSuperview];
    self.tableView.mj_footer = nil;
}
- (void)wex_endRefresh {
    [self.tableView.mj_header endRefreshing];
    [WeXPorgressHUD hideLoading];
}

- (void)wex_tableviewTouchBegan:(WeXBaseTableView *)tableView {
    if (tableView == self.tableView) {
        [self.view endEditing:YES];
    }
}


#pragma mark --- UITableViewDataSorce----UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [self tableView:tableView heightForRowAtIndexPath:indexPath];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return [self numberOfSectionsInTableView:tableView];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [self tableView:tableView numberOfRowsInSection:section];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSAssert(false, @"子类必须重写");
    return nil;
}

#pragma mark --- WeXBaseReuseTableViewControllerProtocol
- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    
}

- (CGFloat)wexTableview:(UITableView *)tableView heightForHeaderFooterWithIdentifier:(NSString *)identifier inSection:(NSInteger)section {
    return [tableView fd_heightForHeaderFooterViewWithIdentifier:identifier configuration:^(id headerFooterView) {
        [self wexTableview:tableView configureHeaderFooterView:headerFooterView inSection:section];
    }];
}

- (UIView *)wexTableview:(UITableView *)tableView viewForHeaderFooterWithIdentifier:(NSString *)identifier inSection:(NSInteger)section {
    UITableViewHeaderFooterView *headerFooterView = [tableView dequeueReusableHeaderFooterViewWithIdentifier:identifier];
    [self wexTableview:tableView configureHeaderFooterView:headerFooterView inSection:section];
    return headerFooterView;
}

- (void)wexTableview:(UITableView *)tableView configureHeaderFooterView:(UITableViewHeaderFooterView *)headerFooterView inSection:(NSInteger)section {
    
}

- (CGFloat)wexTableView:(UITableView *)tableView cellIdentifier:(NSString *)identifier indexPath:(NSIndexPath *)indexPath {
    return [tableView fd_heightForCellWithIdentifier:identifier configuration:^(id cell) {
        [self wexTableView:tableView configureCell:cell indexPath:indexPath];
    }];
}

- (UITableViewCell*)wexTableview:(UITableView *)tableview cellForRowWithIdentifier:(NSString *)identifier indexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableview dequeueReusableCellWithIdentifier:identifier forIndexPath:indexPath];
    [self wexTableView:tableview configureCell:cell indexPath:indexPath];
    return cell;
}

- (void)wexTableView:(UITableView *)tableView configureCell:(UITableViewCell *)currentCell indexPath:(NSIndexPath *)indexPath {
    
}





@end
