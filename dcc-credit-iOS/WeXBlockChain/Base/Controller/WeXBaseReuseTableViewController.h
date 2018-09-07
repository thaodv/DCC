//
//  WeXBaseReuseTableViewController.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/6/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"
#import "WeXBaseTableView.h"

@protocol WeXBaseReuseTableViewControllerProtocol <NSObject>

- (CGFloat)wexTableView:(UITableView *)tableView cellIdentifier:(NSString *)identifier indexPath:(NSIndexPath *)indexPath;

- (void)wexTableView:(UITableView *)tableView configureCell:(UITableViewCell *)currentCell indexPath:(NSIndexPath *)indexPath;

- (UITableViewCell *)wexTableview:(UITableView *)tableview cellForRowWithIdentifier:(NSString *)identifier indexPath:(NSIndexPath *)indexPath;


/**
 注册TableViewCell /Header(FooterView)

 @param tableView 被注册的TableView
 */
- (void)wexRegisterTableViewCell:(UITableView *)tableView;

- (CGFloat)wexTableview:(UITableView *)tableView heightForHeaderFooterWithIdentifier:(NSString *)identifier inSection:(NSInteger)section;

- (UIView *)wexTableview:(UITableView *)tableView viewForHeaderFooterWithIdentifier:(NSString *)identifier inSection:(NSInteger)section;

- (void)wexTableview:(UITableView *)tableView configureHeaderFooterView:(UITableViewHeaderFooterView *)headerFooterView inSection:(NSInteger)section;


@end


@interface WeXBaseReuseTableViewController : WeXBaseViewController <UITableViewDelegate,UITableViewDataSource,WeXBaseReuseTableViewControllerProtocol,WeXBaseTableViewDelegate>

@property (nonatomic, strong) WeXBaseTableView *tableView;

- (void)wex_refreshAction;

- (void)wex_uninstallRefreshFooter;

- (void)wex_endRefresh;

- (void)wex_unistallRefreshHeader;


@end
