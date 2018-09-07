//
//  WeXSelectedNodeViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXSelectedNodeViewController.h"
#import "WeXSelectNodeTableViewCell.h"
//#import "WeXNetworkCheckManager.h"
#import "WeXLocalCacheManager.h"

@interface WeXSelectedNodeViewController ()

@property (nonatomic, strong) NSMutableArray <WeXNetworkCheckModel *> *dataArray;


@end

static NSString *const kNodeCellID = @"WeXSelectNodeTableViewCellID";


@implementation WeXSelectedNodeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"选择节点");
    [self setNavigationNomalBackButtonType];
    _dataArray = [NSMutableArray new];
    [self wex_unistallRefreshHeader];
    [self autoRefreshData];
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXSelectNodeTableViewCell class] forCellReuseIdentifier:kNodeCellID];
}

- (void)autoRefreshData {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [[WeXNetworkCheckManager shareManager] startAllNodeNetworkDelay:^(NSArray<WeXNetworkCheckModel *> *results) {
        [self.dataArray removeAllObjects];
        [self.dataArray addObjectsFromArray:results];
        [self.tableView reloadData];
        [WeXPorgressHUD hideLoading];
    }];
}


#pragma mark UITableViewDatasource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [self wexTableView:tableView cellIdentifier:kNodeCellID indexPath:indexPath];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [self wexTableview:tableView cellForRowWithIdentifier:kNodeCellID indexPath:indexPath];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    WeXNetworkCheckModel * checkModel = self.dataArray[indexPath.row];
    if (checkModel.nodeCheckState == WexNetworkCheckStateChecking) {
        [WeXPorgressHUD showText:@"该节点网络正在检测中,请稍后再试" onView:self.view];
        return;
    }
    [WeXLocalCacheManager updateLocalCacheValue:[@(indexPath.row) stringValue] ID:WexLocalNodeIndex];
    [self.tableView reloadData];
    [WeXPorgressHUD showText:@"节点切换成功" onView:self.view];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.8 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        !self.DidSelectedNode ? : self.DidSelectedNode(_dataArray[indexPath.row]);
        [self.navigationController popViewControllerAnimated:YES];
    });
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    NSInteger selectedIndex = [[WeXLocalCacheManager getLocalCacheWithID:WexLocalNodeIndex] integerValue];
    WeXSelectNodeTableViewCell *cell = (WeXSelectNodeTableViewCell *)currentCell;
    [cell setDelayModel:_dataArray[indexPath.row] isSelected:selectedIndex == indexPath.row];
}

- (void)dealloc {
    NSLog(@"acdd");
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}



@end
