//
//  WeXMyLoanViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/12.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyLoanViewController.h"
#import "WeXCPNoRecordCell.h"
#import "WeXMyLoanTableViewCell.h"
#import "WeXMyLoanModel.h"
#import "WeXMyLoanDetailViewController.h"

@interface WeXMyLoanViewController ()

@property (nonatomic, strong) NSMutableArray *dataArray;


@end

@implementation WeXMyLoanViewController

static NSString * const kNorecodCellID   = @"WeXCPNoRecordCellID";
static NSString * const kListOrderCellID = @"WeXMyLoanTableViewCellID";


- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"我的借款");
    [self.tableView setBackgroundColor:WexSepratorLineColor];
    [self setNavigationNomalBackButtonType];
    [self p_configureData];
}
- (void)wex_refreshAction {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self wex_endRefresh];
        [self.dataArray removeAllObjects];
        [self.tableView reloadData];
    });
}

- (void)p_configureData {
    _dataArray = [NSMutableArray new];
    for (int i =0; i < 12; i ++) {
        WeXMyLoanModel *LoanModel = [WeXMyLoanModel new];
        LoanModel.status =  [@ (i  % 7) stringValue];
        [_dataArray addObject:LoanModel];
    }
    [self.tableView reloadData];
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCPNoRecordCell class] forCellReuseIdentifier:kNorecodCellID];
    [tableView registerClass:[WeXMyLoanTableViewCell class] forCellReuseIdentifier:kListOrderCellID];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _dataArray.count > 0 ? _dataArray.count : 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (_dataArray.count > 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kListOrderCellID indexPath:indexPath];
    }
    return [self wexTableview:tableView cellForRowWithIdentifier:kNorecodCellID indexPath:indexPath];
}

- (void)wexTableView:(UITableView *)tableView configureCell:(UITableViewCell *)currentCell indexPath:(NSIndexPath *)indexPath {
    if (_dataArray.count > 0) {
        WeXMyLoanTableViewCell *cell = (WeXMyLoanTableViewCell *)currentCell;
        [cell setMyLoanModel:_dataArray[indexPath.row]];
    } else {
        WeXCPNoRecordCell *cell = (WeXCPNoRecordCell *)currentCell;
        [cell setImageName:@"MyLoanNoRecord" subText:@"暂无借款记录"];
    }
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (_dataArray.count > 0) {
        return [self wexTableView:tableView cellIdentifier:kListOrderCellID indexPath:indexPath];
    }
    return [self wexTableView:tableView cellIdentifier:kNorecodCellID indexPath:indexPath];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    WeXMyLoanModel *loanModel = _dataArray[indexPath.row];
    if (![loanModel.status isEqualToString:@"0"]) {
        WeXMyLoanDetailViewController *loanDetailVC = [WeXMyLoanDetailViewController new];
        loanDetailVC.detailStatus = [loanModel.status integerValue] - 1;
        [WeXHomePushService pushFromVC:self toVC:loanDetailVC];
    }

}


@end
