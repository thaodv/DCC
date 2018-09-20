//
//  WeXLoginRecoredViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoginRecoredViewController.h"
#import "WeXNewPassportManagerRecordCell.h"
#import "WeXPassportManagerRLMModel.h"
#import "WeXLoginManagerMoreDataCell.h"

@interface WeXLoginRecoredViewController ()

@end

@implementation WeXLoginRecoredViewController

static NSString * const kRecoredCellID    = @"WeXNewPassportManagerRecordCell";
static NSString * const kNoMoreDataCellID = @"WeXLoginManagerMoreDataCell";


- (void)viewDidLoad {
    [super viewDidLoad];
    if (self.type == WeXLoginRecoredTypeLogin) {
        self.title = WeXLocalizedString(@"登录记录");
    } else {
        self.title = WeXLocalizedString(@"变更记录");
    }
    [self configureNav];
}

- (void)configureNav {
    [self setNavigationNomalBackButtonType];
    [self wex_unistallRefreshHeader];
    [self.tableView reloadData];
}
- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXNewPassportManagerRecordCell class] forCellReuseIdentifier:kRecoredCellID];
    [tableView registerClass:[WeXLoginManagerMoreDataCell class] forCellReuseIdentifier:kNoMoreDataCellID];
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _dataArray.count > 0 ? _dataArray.count : 1;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return  _dataArray.count > 0 ? 60 : 300;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (_dataArray.count > 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kRecoredCellID indexPath:indexPath];
    }
    return [self wexTableview:tableView cellForRowWithIdentifier:kNoMoreDataCellID indexPath:indexPath];
}
- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    if (_dataArray.count > 0) {
        WeXNewPassportManagerRecordCell *cell = (WeXNewPassportManagerRecordCell *)currentCell;
        WeXPassportManagerRLMModel *model     = _dataArray[indexPath.row];
        [cell setManagerModel:model];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    } else {
        WeXLoginManagerMoreDataCell *cell = (WeXLoginManagerMoreDataCell *)currentCell;
        [cell setTitle:@"暂无更多记录~"];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}



@end
