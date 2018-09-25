//
//  WeXCPPotListViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPPotListViewController.h"
#import "WeXCPMarketTopAmountCell.h"
#import "WeXCPPotInfoCell.h"
#import "WeXCPPotListAdapter.h"
#import "WeXCPNoRecordCell.h"

@interface WeXCPPotListViewController ()
@property (nonatomic, strong) WeXCPPotListAdapter *potListAdapter;
@property (nonatomic, strong) NSMutableArray <WeXCPPotListResultModel *> *postList;


@end

static NSString * const kTopAmountCellID   = @"WeXCPMarketTopAmountCellID";
static NSString * const kPotInfoCellID     = @"WeXCPProductInfoCellID";
static NSString * const kNoRecordCellID    = @"WeXCPNoRecordCellID";


@implementation WeXCPPotListViewController


- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"币生息持仓");
    [self configureData];
}

- (void)wex_autoRefreshAction {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self requestPostList];
}
- (void)requestPostList {
    if (!_potListAdapter) {
        _potListAdapter = [WeXCPPotListAdapter new];
    }
    _potListAdapter.delegate = self;
    WeXCPPotListParamModel *param = [WeXCPPotListParamModel new];
    param.userAddress = [WexCommonFunc getFromAddress];
    [_potListAdapter run:param];
}


- (NSMutableArray *)postList{
    if(!_postList){
        _postList = [NSMutableArray new];
    }
    return _postList;
}

- (void)configureData {
    [self wex_unistallRefreshHeader];
    [self.tableView setBackgroundColor:WexSepratorLineColor];
    [self setNavigationNomalBackButtonType];
}

- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response {
    if ([headModel.businessCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
        WeXCPPotListMainModel *resModel = (WeXCPPotListMainModel *)response;
        [self.postList removeAllObjects];
        [self.postList addObjectsFromArray:resModel.result];
        [self.tableView reloadData];
        [WeXPorgressHUD hideLoading];
    } else {
        [WeXPorgressHUD hideLoading];
        [WeXPorgressHUD showText:@"系统错误" onView:self.view];
    }
    WEXNSLOG(@"%@---%@",headModel,response);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCPMarketTopAmountCell class] forCellReuseIdentifier:kTopAmountCellID];
    [tableView registerClass:[WeXCPPotInfoCell class] forCellReuseIdentifier:kPotInfoCellID];
    [tableView registerClass:[WeXCPNoRecordCell class] forCellReuseIdentifier:kNoRecordCellID];
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 1;
    } else {
        if (_postList.count > 0) {
            return _postList.count;
        }
        return 1;
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
        if (_postList.count > 0) {
            return [self wexTableView:tableView cellIdentifier:kPotInfoCellID indexPath:indexPath];
        }
        return 300;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kTopAmountCellID indexPath:indexPath];
    } else {
        if (_postList.count > 0) {
            return [self wexTableview:tableView cellForRowWithIdentifier:kPotInfoCellID indexPath:indexPath];
        } else {
            return [self wexTableview:tableView cellForRowWithIdentifier:kNoRecordCellID indexPath:indexPath];
        }
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        WeXCPMarketTopAmountCell *cell = (WeXCPMarketTopAmountCell *)currentCell;
        [cell setPrincipal:@"2000.89" profit:@"100.08"];
    } else {
        if (_postList.count > 0) {
            WeXCPPotInfoCell *cell = (WeXCPPotInfoCell *)currentCell;
            [cell setPotListModel:_postList[indexPath.row]];
        } else {
            WeXCPNoRecordCell *cell = (WeXCPNoRecordCell *)currentCell;
            [cell setImageName:@"Wex_Coin_NoRecord" subText:@"暂无记录"];
        }
    }
    
}

@end
