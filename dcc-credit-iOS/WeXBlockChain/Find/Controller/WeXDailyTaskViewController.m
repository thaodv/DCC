//
//  WeXDailyTaskViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/1.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDailyTaskViewController.h"
#import "WeXDailyTaskHeadSignCell.h"
#import "WeXDailyTaskDataModel.h"
#import "WeXDailyTaskItemCell.h"
#import "WeXDailyTaskHeadView.h"
#import "WeXCheckRSAPublickKeyManager.h"
#import "WeXCreditIDAuthenViewController.h"
#import "WeXSunRecordViewController.h"
#import "WeXWXManager.h"

#import "WeXMyIpfsSaveController.h"
#import "WeXIpfsSavePasswordController.h"

@interface WeXDailyTaskViewController ()

@property (nonatomic, strong) WeXDailyTaskDataModel * dataModel;
@property (nonatomic, strong) WeXPasswordCacheModal *cacheModel;

@end

static NSString * const kHeadSignCellID = @"WeXDailyTaskHeadSignCell";
static NSString * const kHeaderViewID   = @"WeXDailyTaskHeadViewID";
static NSString * const kItemCellID     = @"WeXDailyTaskItemCellID";

@implementation WeXDailyTaskViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self configureNavBar];
}
- (void)configureNavBar {
    self.title = WeXLocalizedString(@"日常任务");
    [self setNavigationNomalBackButtonType];
    [self wex_unistallRefreshHeader];
    [self.tableView setBackgroundColor:WexSepratorLineColor];
    [self requestTaskListAdapter];
}
- (void)requestTaskListAdapter {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    __weak typeof(self) weakSelf     = self;
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _dataModel = [[WeXDailyTaskDataModel alloc] initWithRefreshBlock:^(BOOL reloadAll, BOOL reloadSectionOne) {
        if (reloadAll) {
            [WeXPorgressHUD hideLoading];
            [weakSelf.tableView reloadData];
        }
        else if (reloadSectionOne) {
            [weakSelf.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationNone];
        }
    }];
}


- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXDailyTaskHeadSignCell class] forCellReuseIdentifier:kHeadSignCellID];
    [tableView registerClass:[WeXDailyTaskItemCell class] forCellReuseIdentifier:kItemCellID];
    [tableView registerClass:[WeXDailyTaskHeadView class] forHeaderFooterViewReuseIdentifier:kHeaderViewID];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return [_dataModel numberOfSections] + 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return [_dataModel sectionOneModel] ? 1 : 0;
    }
    return [_dataModel numOfRowsInSection:section - 1];
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableView:tableView cellIdentifier:kHeadSignCellID indexPath:indexPath];
    }
    return [self wexTableView:tableView cellIdentifier:kItemCellID indexPath:indexPath];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kHeadSignCellID indexPath:indexPath];
    }
    return [self wexTableview:tableView cellForRowWithIdentifier:kItemCellID indexPath:indexPath];
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return 0.01;
    }
    return 40;
}
- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 10;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return [UIView new];
    }
    return [self wexTableview:tableView viewForHeaderFooterWithIdentifier:kHeaderViewID inSection:section];
}
- (void)wexTableView:(UITableView *)tableView configureCell:(UITableViewCell *)currentCell indexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        WeXTaskSignResModel *resModel = [_dataModel sectionOneModel];
        WeXDailyTaskHeadSignCell *signCell = (WeXDailyTaskHeadSignCell *)currentCell;
        signCell.SignEvent = ^{
            [self beginSignRequest];
        };
        signCell.ClickSunBalance = ^{
            WeXSunRecordViewController *sunRecordVC = [WeXSunRecordViewController new];
            sunRecordVC.sunValue = [[_dataModel sunBalanceModel]balance];
            [WeXHomePushService pushFromVC:self toVC:sunRecordVC];
        };
        NSInteger status = 0;
        if ([_dataModel todaySignStatusModel]) {
            status = 1;
        }
        [signCell setTaskSignResModel:resModel balanceModel:[_dataModel sunBalanceModel]  todayStatus:status];
    }
    else {
        NSIndexPath *newIndexPath = [NSIndexPath indexPathForRow:indexPath.row inSection:indexPath.section - 1];
        WeXDailyTaskItemCell  *itemCell = (WeXDailyTaskItemCell *)currentCell;
        WeXTaskListItemModel *itemModel = [_dataModel rowModelOfIndexPath:newIndexPath];
        [itemCell setItemSectionModel:[_dataModel sectionModelInSection:newIndexPath.section] rowModel:itemModel];
        [itemCell setBottomLineHidden:indexPath.row == [_dataModel numOfRowsInSection:indexPath.section - 1] - 1];
    }
}
- (void)wexTableview:(UITableView *)tableView configureHeaderFooterView:(UITableViewHeaderFooterView *)headerFooterView inSection:(NSInteger)section {
    if (section != 0) {
        WeXDailyTaskHeadView *headerView = (WeXDailyTaskHeadView *)headerFooterView;
        [headerView.contentView setBackgroundColor:[UIColor whiteColor]];
        WeXTaskListItemListModel *sectionModel = [_dataModel sectionModelInSection:section - 1];
        [headerView setTitle:sectionModel.name subTitle:sectionModel.descripe];
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section > 0) {
        NSIndexPath *newIndexPath = [NSIndexPath indexPathForRow:indexPath.row inSection:indexPath.section - 1];
        WeXTaskListItemModel *itemModel = [_dataModel rowModelOfIndexPath:newIndexPath];
//      任务已经完成点击无交互
        if ([itemModel.status isEqualToString:FULFILLED]) {
            return;
        }
        if ([itemModel.code isEqualToString:INVITE_FRIEND]) { //邀请好友助力
            [self inviteFriendHelp];
        }
        else if ([itemModel.code isEqualToString:BACKUP_ID]) {
            [self pushToBackUPNameController];
        }
        else if ([itemModel.code isEqualToString:OPEN_CLOUD_STORE]) { //开通云存储
            [self handleIPFSEvent];
        }
    }
}


// MARK: - 前往实名认证页面
- (void)pushToCredictViewController {
    WeXCreditAuthenProcessManager *processManager = [WeXCreditAuthenProcessManager shareManager];
    WeXCheckRSAPublickKeyManager *checkManager = [WeXCheckRSAPublickKeyManager shareManager];
    [checkManager createCheckPublickKeyRequestWithParentController:self responseBlock:^(BOOL result) {
        if (result) {
            processManager.idAuthenProcessType = WeXIDAuthenProcessTypeMyCredit;
            WeXCreditIDAuthenViewController *ctrl = [[WeXCreditIDAuthenViewController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }];
}

// MARK: - 前往备份实名认证
- (void)pushToBackUPNameController {
    [[WeXGradenTaskManager manager] sendFinishTaskNotiToServerWithTaskType:WexFinishTaskBackName];
}


// MARK: - 处理点击IPFS项
- (void)handleIPFSEvent {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    __weak typeof(self) weakSelf  = self;
    [_dataModel requestIPFSKeyDataAdapterWithResult:^(BOOL isSucc) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        [WeXPorgressHUD hideLoading];
        if (isSucc) {
            [strongSelf pushToIPFSVC];
        } else {
            [WeXPorgressHUD showText:@"网络请求超时,请稍后重试" onView:strongSelf.view];
        }
    }];
}

// MARK: - 前往IPFS
- (void)pushToIPFSVC {
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    NSString *passWord = model.ipfsCheckKey;
    if (passWord) {
        WeXMyIpfsSaveController *ctrl = [[WeXMyIpfsSaveController alloc] init];
        ctrl.fromVc = self;
        [self.navigationController pushViewController:ctrl animated:YES];
    } else{
        WeXIpfsSavePasswordController *ctrl = [[WeXIpfsSavePasswordController alloc] init];
        ctrl.fromVc = self;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}


// MARK: - 邀请好友助力
- (void)inviteFriendHelp {
    WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    [[WeXWXManager wxManager] sendMinProgramTitle:@"小程序分享" description:@"快来帮办我吧" playerID:cacheModel.playerID webURL:nil complete:^(BOOL isSucc) {
        
    }];
}

// MARK: - 请求签到行为
- (void)beginSignRequest {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    __weak typeof(self) weakSelf     = self;
    _dataModel.SignResult = ^{
        [WeXPorgressHUD hideLoading];
        if ([weakSelf.dataModel requestSignResultModel]) {
            [WeXPorgressHUD showText:@"签到成功" onView:weakSelf.view];
        }
        else {
            [WeXPorgressHUD showText:@"签到失败,请稍后重试" onView:weakSelf.view];
        }
        [weakSelf.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationNone];
    };
    [_dataModel requestSignRequestAdapter];

}



@end
