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
#import "WeXCreditIDSuccessViewController.h"
#import "WeXCreditBankCardAuthenController.h"
#import "WeXCreditBankSuccessController.h"
#import "WeXCreditMobileOperatorAuthenController.h"
#import "WeXCreditMobileOperatorSuccessController.h"

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
    [self.tableView setBackgroundColor:WexSepratorLineColor];
    [WeXPorgressHUD showLoadingAddedTo:self.view];
}

- (void)wex_refreshAction {
    [self requestTaskListAdapter];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self requestTaskListAdapter];
}

- (void)requestTaskListAdapter {
    __weak typeof(self) weakSelf     = self;
    if (!_dataModel) {
        _dataModel = [[WeXDailyTaskDataModel alloc] initWithRefreshBlock:^(BOOL reloadAll, BOOL reloadSectionOne) {
            [weakSelf reloadDataIsAll:reloadAll isSectionOne:reloadSectionOne];
        }];
    }
    else {
        [_dataModel refreshAllDataBlock:^(BOOL reloadAll, BOOL reloadSectionOne) {
            [weakSelf reloadDataIsAll:reloadAll isSectionOne:reloadSectionOne];
        }];
    }
}

- (void)reloadDataIsAll:(BOOL)reloadAll isSectionOne:(BOOL)isSectionOne {
    if (reloadAll) {
        [self wex_endRefresh];
    }
    [self.tableView reloadData];
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
        else if ([itemModel.code isEqualToString:OPEN_CLOUD_STORE]) { //开通云存储
            [self handleIPFSEvent];
        }
        else if ([itemModel.code isEqualToString:ID]) { //实名认证
            [self pushToCredictViewController];
        }
        else if ([itemModel.code isEqualToString:BANK_CARD]) {
            [self pushToBankIDVerifyViewController]; //银行卡认证
        }
        else if ([itemModel.code isEqualToString:COMMUNICATION_LOG]) { //运营商认证
            [self pushToMobileOperatorViewController];
        }
        else if ([itemModel.code isEqualToString:TN_COMMUNICATION_LOG]) { //同牛认证
            [self pushToSameCowViewController];
        }
        else if ([itemModel.code isEqualToString:BACKUP_ID]) { //备份实名认证
            [self pushToBackUpNameIDViewController];
        }
        else if ([itemModel.code isEqualToString:BACKUP_BANK_CARD]) { //备份银行卡
            [self pushToBackUpNameIDViewController];
        }
        else if ([itemModel.code isEqualToString:BACKUP_COMMUNICATION_LOG]) { //备份运营商
            [self pushToBackUpNameIDViewController];
        }
        else if ([itemModel.code isEqualToString:BACKUP_TN_COMMUNICATION_LOG]) { //备份同牛
            [self pushToBackUpNameIDViewController];
        }
        else if ([itemModel.code isEqualToString:BACKUP_WALLET]) { //备份钱包
            
        }
    }
}

// MARK: - 前往实名认证页面
- (void)pushToCredictViewController {
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeSuccess || model.idAuthenStatus == WeXCreditIDAuthenStatusTypeInvalid) {
        WeXCreditIDSuccessViewController *ctrl = [[WeXCreditIDSuccessViewController alloc] init];
        ctrl.isFromDailyTask = true;
        [self.navigationController pushViewController:ctrl animated:YES];
    } else {
        WeXCreditAuthenProcessManager *processManager = [WeXCreditAuthenProcessManager shareManager];
        __weak typeof(self) weakSelf     = self;
        [_dataModel requestDataAdapterWithType:DailyTaskRequestAuthenNameID controller:self result:^(BOOL isSucc) {
            if (isSucc) {
                processManager.idAuthenProcessType = WeXIDAuthenProcessTypeMyCredit;
                WeXCreditIDAuthenViewController *ctrl = [[WeXCreditIDAuthenViewController alloc] init];
                [weakSelf.navigationController pushViewController:ctrl animated:YES];
            }
        }];
    }
}

// MARK: - 前往银行卡认证界面
- (void)pushToBankIDVerifyViewController {
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    if (![self isAuthenID:model]) {return;}
    WeXCreditAuthenProcessManager *processManager = [WeXCreditAuthenProcessManager shareManager];
    __weak typeof(self) weakSelf     = self;
    if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeNone) {
        [_dataModel requestDataAdapterWithType:DailyTaskRequestAuthenBankID controller:self result:^(BOOL isSucc) {
            if (isSucc) {
                processManager.bankAuthenProcessType = WeXBankAuthenProcessTypeMyCredit;
                WeXCreditBankCardAuthenController *ctrl = [[WeXCreditBankCardAuthenController alloc] init];
                [weakSelf.navigationController pushViewController:ctrl animated:YES];
            }
        }];
    }
    else if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeSuccess ||model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeInvalid) {
        WeXCreditBankSuccessController *ctrl = [[WeXCreditBankSuccessController alloc] init];
        ctrl.isFromDailyTask = true;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}

// MARK: - 前往运营商认证界面
- (void)pushToMobileOperatorViewController {
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    if (![self isAuthenID:model]) {return;}
    WeXCreditAuthenProcessManager *processManager = [WeXCreditAuthenProcessManager shareManager];
    __weak typeof(self) weakSelf     = self;
    if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeNone) {
        [_dataModel requestDataAdapterWithType:DailyTaskRequestAuthenCarrier controller:self result:^(BOOL isSucc) {
            if (isSucc) {
                processManager.mobileAuthenProcessType = WeXMobileAuthenProcessTypeMyCredit;
                WeXCreditMobileOperatorAuthenController *ctrl = [[WeXCreditMobileOperatorAuthenController alloc] init];
                ctrl.creditMobileType =  WeXCreditMobileOperatorTypeCloud;
                [weakSelf.navigationController pushViewController:ctrl animated:YES];
            }
        }];
    }
    // MARK: - 手机运营商认证结果页 (成功或者过期)
    else if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeSuccess || model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeInvalid) {
        WeXCreditMobileOperatorSuccessController *ctrl = [[WeXCreditMobileOperatorSuccessController alloc] init];
        ctrl.creditMobileType = WeXCreditMobileOperatorSuccessTypeCloud;
        ctrl.isFromDailyTask = true;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}

// MARK: - 前往同牛认证界面
- (void)pushToSameCowViewController {
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    if (![self isAuthenID:model]) {return;}
    WeXCreditAuthenProcessManager *processManager = [WeXCreditAuthenProcessManager shareManager];
    __weak typeof(self) weakSelf     = self;
    if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeNone) {
        [_dataModel requestDataAdapterWithType:DailyTaskRequestAuthenCarrier controller:self result:^(BOOL isSucc) {
            if (isSucc) {
                processManager.mobileAuthenProcessType = WeXMobileAuthenProcessTypeMyCredit;
                WeXCreditMobileOperatorAuthenController *ctrl = [[WeXCreditMobileOperatorAuthenController alloc] init];
                ctrl.creditMobileType = WeXCreditMobileOperatorTypeSameCow;
                [weakSelf.navigationController pushViewController:ctrl animated:YES];
            }
        }];
    }
    // MARK: - 同牛手机运营商认证结果页 (成功或者过期)
    else if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeSuccess || model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeInvalid) {
        WeXCreditMobileOperatorSuccessController *ctrl = [[WeXCreditMobileOperatorSuccessController alloc] init];
        ctrl.creditMobileType = WeXCreditMobileOperatorTypeSameCow;
        ctrl.isFromDailyTask = true;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}


// MARK: - 是否进行实名认证
- (BOOL)isAuthenID:(WeXPasswordCacheModal *)model {
    if (model.idAuthenStatus != WeXCreditIDAuthenStatusTypeSuccess) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"请先完成实名认证") onView:self.view];
        return NO;
    }
    return true;
}

// MARK: - 备份实名认证
- (void)pushToBackUpNameIDViewController {
    [self handleIPFSEvent];
}

// MARK: - 处理点击IPFS项
- (void)handleIPFSEvent {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    __weak typeof(self) weakSelf  = self;
    [_dataModel requestDataAdapterWithType:DailyTaskRequestIPFS controller:self  result:^(BOOL isSucc) {
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
        ctrl.isFromDailyTask = true;
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
    [[WeXWXManager wxManager] sendMinProgramTitle:@"小程序分享" description:@"快来帮帮我吧" playerID:cacheModel.playerID webURL:nil complete:^(BOOL isSucc) {
        
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
