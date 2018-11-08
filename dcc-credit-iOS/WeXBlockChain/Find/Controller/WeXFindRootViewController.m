//
//  WeXFindRootViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/29.
//  Copyright © 2018年 WeX. All rights reserved.

#import "WeXFindRootViewController.h"

#import "WeXFindVariousTaskCell.h"
#import "WeXFindHeadAvatarCell.h"
#import "WeXHomeIndicatorCell.h"
#import "WeXHomeIconAndTextCell.h"
#import "WeXHomeTableFooterView.h"
#import "WeXHomeSectionHeaderView.h"
#import "WeXBindWeChatAlertView.h"
#import "WeXInviteFriendViewController.h"
#import "WeXDailyTaskViewController.h"
#import "WeXSunRecordViewController.h"
#import "WeXFindIListItemCell.h"
#import "WeXMisteryGardenViewController.h"
#import "WeXWXManager.h"
#import "AppDelegate.h"
#import "WeXDailyTaskDataModel.h"


@interface WeXFindRootViewController ()

@property (nonatomic, strong) WeXBindWeChatAlertView *alertView;
@property (nonatomic, strong) WeXDailyTaskDataModel *dataModel;

@end

static NSString * const kHeadAvatarCellID  = @"WeXFindHeadAvatarCellID";
static NSString * const kTaskCellID        = @"WeXFindVariousTaskCellID";
static NSString * const kSectionHeaderID   = @"WeXHomeSectionHeaderViewID";
static NSString * const KListItemCellID    = @"WeXFindIListItemCellID";

@implementation WeXFindRootViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self configureNavBar];
    [self initDataModel];
}

- (void)configureNavBar {
    self.title = WeXLocalizedString(@"发现");
    [self wex_unistallRefreshHeader];
    [self wex_addTopSepratorLine:10 text:nil];
    [self.tableView setBackgroundColor:WexSepratorLineColor];
    WeXHomeTableFooterView *footerView = [[WeXHomeTableFooterView alloc]
                                          initWithFrame:CGRectMake(0, 0, kScreenWidth, 45)];
    [footerView setLineHide];
    [footerView setBackgroundColor:WexSepratorLineColor];
    [footerView setTitle:@"—— 更多发现，敬请期待 ——"];
    self.tableView.tableFooterView= footerView;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (![self isBindWeChat]) {
        [self configureAlertView];
    }
    if (self.dataModel) {
        __weak typeof(self) weakSelf  = self;
        [self.dataModel requestSunValueWithResult:^{
           [weakSelf reloadSectionOneData];
        }];
    }
}

- (void)initDataModel {
    if (!_dataModel) {
        __weak typeof(self) weakSelf  = self;
        _dataModel = [[WeXDailyTaskDataModel alloc] initWithRefreshSunValueBlock:^{
            [weakSelf reloadSectionOneData];
        }];
    }
}

- (void)reloadSectionOneData {
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationNone];
}


- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXFindHeadAvatarCell  class] forCellReuseIdentifier:kHeadAvatarCellID];
    [tableView registerClass:[WeXFindVariousTaskCell class] forCellReuseIdentifier:kTaskCellID];
    [tableView registerClass:[WeXFindIListItemCell   class] forCellReuseIdentifier:KListItemCellID];
    [tableView registerClass:[WeXHomeSectionHeaderView class] forHeaderFooterViewReuseIdentifier:kSectionHeaderID];
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return section == 0 ? 2 : 1;
}
- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 10;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    WeXHomeSectionHeaderView *headerView = [tableView dequeueReusableHeaderFooterViewWithIdentifier:kSectionHeaderID];
    [headerView.contentView setBackgroundColor:WexSepratorLineColor];
    return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            return [self wexTableView:tableView cellIdentifier:kHeadAvatarCellID indexPath:indexPath];
        }
        return [self wexTableView:tableView cellIdentifier:kTaskCellID indexPath:indexPath];
    }
    else {
        return [self wexTableView:tableView cellIdentifier:KListItemCellID indexPath:indexPath];
    }
    return 0.01;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            return [self wexTableview:tableView cellForRowWithIdentifier:kHeadAvatarCellID indexPath:indexPath];
        }
        return [self wexTableview:tableView cellForRowWithIdentifier:kTaskCellID indexPath:indexPath];
    }
    else {
        return [self wexTableview:tableView cellForRowWithIdentifier:KListItemCellID indexPath:indexPath];
    }
    return nil;
}

- (void)wexTableView:(UITableView *)tableView configureCell:(UITableViewCell *)currentCell indexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            WeXFindHeadAvatarCell *cell = (WeXFindHeadAvatarCell *)currentCell;
            UIImage* face=[IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE];
            if(!face) {
                face=[UIImage imageNamed:@"Fill 9"];
            }
            [cell setAvatarImage:face nickName:[WexDefaultConfig instance].nickName ?
             [WexDefaultConfig instance].nickName : @""];
        }
        else {
            WeXFindVariousTaskCell *cell = (WeXFindVariousTaskCell *)currentCell;
            [cell setSunValue:[_dataModel sunBalanceModel].balance];
            cell.ClickTaskCell = ^(WexFindTaskCellClickType type) {
                [self hanleTaskCellClick:type];
                WEXNSLOG(@"---%@",@(type));
            };
        }
    }
    else if (indexPath.section == 1) {
        WeXFindIListItemCell *cell = (WeXFindIListItemCell *)currentCell;
        [cell setTitle:@"神秘花园" imageName:@"huayuan" subTitle:@"赚阳光 领代币！" subDes:@"阳光越多，可收取的代币奖励越多~~" notiTitle:@"有奖励未领取哦" notiHighText:nil notiTime:@"2分钟之前"];
    }
    else if (indexPath.section == 2) {
        WeXFindIListItemCell *cell = (WeXFindIListItemCell *)currentCell;
        [cell setTitle:@"知识问题PK" imageName:@"zhishi" subTitle:@"参与PK赚阳光 领取奖励" subDes:@"10000小伙伴正在PK对战中～" notiTitle:@"昵称获胜  获得+5阳光" notiHighText:@"获得+5阳光" notiTime:@"1分钟之前"];
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1) {
        [self pushToMisteryGarden];
    }
    else if (indexPath.section == 2) {
        [self pushToPK];
    }
}
// MARK: - 神秘花园
- (void)pushToMisteryGarden {
    WEXNSLOG(@"神秘花园");
    [WeXHomePushService pushFromVC:self toVC:[WeXMisteryGardenViewController new]];
}

// MARK: - 知识问题PK
- (void)pushToPK {
    WEXNSLOG(@"知识问答PK");
}


- (void)hanleTaskCellClick:(WexFindTaskCellClickType)type {
    switch (type) {
        case WexFindTaskCellClickGarden: {
            [self pushToMisteryGarden];
        }
            break;
        case WexFindTaskCellClickTask: {
            [WeXHomePushService pushFromVC:self toVC:[WeXDailyTaskViewController new]];
        }
            break;
        case WexFindTaskCellClickInvite: {
            [WeXHomePushService pushFromVC:self toVC:[WeXInviteFriendViewController new]];
        }
            break;
        case WexFindTaskCellClickSunshine:  {
            WeXSunRecordViewController *recordVC = [WeXSunRecordViewController new];
            recordVC.sunValue = [[_dataModel sunBalanceModel] balance];
            [WeXHomePushService pushFromVC:self toVC:recordVC];
        }
            break;
            
        default:
            break;
    }
}

- (void)configureAlertView {
    if (_alertView) {return;}
    __weak typeof(self) weakSelf     = self;
    _alertView = [WeXBindWeChatAlertView createAlertViewWithComplteEvent:^(BOOL isOk, BOOL isCancel) {
        [weakSelf setAlertView:nil];
        if (isOk) {
            [weakSelf hanleBindWeChatEvent];
        }
        if (isCancel) {
            [weakSelf hanleCancelBindEvent];
        }
    }];
    [_alertView show];
}

- (void)hanleBindWeChatEvent {
    WeXWXManager *manager = [WeXWXManager wxManager];
    [manager sendAuthenRequestInView:self.view
                            complete:^(BOOL isSucc, NSString *playerID) {
                                if (isSucc) {
                                    [self saveBindWeChatResultWithPlayerID:playerID];
                                }
                                else {
                                    [WeXPorgressHUD showText:WeXLocalizedString(@"绑定失败,请稍后重试") onView:self.view];
                                }
                            }];
}

- (void)hanleCancelBindEvent {
    AppDelegate *delegate = (AppDelegate *)[[UIApplication sharedApplication]delegate];
    UITabBarController *rootVC = (UITabBarController *) [delegate.window rootViewController];
    [rootVC setSelectedIndex:0];
}


- (void)saveBindWeChatResultWithPlayerID:(NSString *)playerID {
    WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    cacheModel.playerID = playerID;
    cacheModel.isBindWeChat = true;
    [WexCommonFunc savePassport:cacheModel];
    [WeXPorgressHUD showText:WeXLocalizedString(@"绑定成功") onView:self.view];
}

- (BOOL)isBindWeChat {
    WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    return cacheModel.isBindWeChat;
}



@end
