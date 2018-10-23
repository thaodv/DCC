//
//  WeXCashLoanInfoViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCashLoanInfoViewController.h"
#import "WeXLoanInfoTextCell.h"
#import "WeXLoanInfoSwitchCell.h"
#import "WeXLoanInfoSubmitCell.h"
#import "WeXBindBankIDViewController.h"


@interface WeXCashLoanInfoViewController ()

@property (nonatomic, assign) BOOL isRead;
@property (nonatomic, copy) NSString *walletBalance;


@end

static NSString * const kTextCellID   = @"WeXLoanInfoTextCellID";
static NSString * const kSwitchCellID = @"WeXLoanInfoSwitchCellID";
static NSString * const kSubmitCellID = @"WeXLoanInfoSubmitCellID";


@implementation WeXCashLoanInfoViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self p_initDataUI];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self getPrivateWalletBalance];
}

- (void)p_initDataUI {
    self.title = WeXLocalizedString(@"借款信息");
    [self setNavigationNomalBackButtonType];
    [self wex_unistallRefreshHeader];
    [self wex_addTopSepratorLine];
    [self.tableView setBackgroundColor:WexSepratorLineColor];
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXLoanInfoTextCell class] forCellReuseIdentifier:kTextCellID];
    [tableView registerClass:[WeXLoanInfoSwitchCell class] forCellReuseIdentifier:kSwitchCellID];
    [tableView registerClass:[WeXLoanInfoSubmitCell class] forCellReuseIdentifier:kSubmitCellID];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 4;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 6;
    }
    else if (section == 1) {
        return 1;
    }
    else if (section == 2) {
        return 2;
    }
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0 ) {
        return [self wexTableView:tableView cellIdentifier:kTextCellID indexPath:indexPath];
    }
    else if (indexPath.section == 1) {
        return [self wexTableView:tableView cellIdentifier:kSwitchCellID indexPath:indexPath];
    }
    else if (indexPath.section == 2) {
        return [self wexTableView:tableView cellIdentifier:kTextCellID indexPath:indexPath];
    }
    return [self wexTableView:tableView cellIdentifier:kSubmitCellID indexPath:indexPath];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kTextCellID indexPath:indexPath];
    }
    else if (indexPath.section == 1) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kSwitchCellID indexPath:indexPath];
    }
    else if (indexPath.section == 2) {
        return [self wexTableview:tableView cellForRowWithIdentifier:kTextCellID indexPath:indexPath];
    }
    return [self wexTableview:tableView cellForRowWithIdentifier:kSubmitCellID indexPath:indexPath];
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        WeXLoanInfoTextCell *textCell = (WeXLoanInfoTextCell *)currentCell;
        [textCell setBottomLineHidden:true];
        switch (indexPath.row) {
            case 0: {
                [textCell setTitle:@"借款金额" subTitle:@"2000元" cellType:WeXLoanInfoTextCellPurple];
            }
                break;
            case 1: {
                [textCell setTitle:@"借款期限" subTitle:@"14天" cellType:WeXLoanInfoTextCellRed];
            }
                break;
            case 2: {
                [textCell setTitle:@"借款周期" subTitle:@"2018.10.01-2018.10.14" cellType:WeXLoanInfoTextCellDefault];
            }
                break;
            case 3: {
                [textCell setTitle:@"借款利息" subTitle:@"36%" cellType:WeXLoanInfoTextCellDefault];
            }
                break;
            case 4: {
                [textCell setTitle:@"到账金额" subTitle:@"2000元" cellType:WeXLoanInfoTextCellDefault];
            }
                break;
            default: {
                [textCell setTitle:@"借款利息" subTitle:@"400元" cellType:WeXLoanInfoTextCellDefault];
            }
                break;
        }
    }
    else if (indexPath.section == 1) {
        WeXLoanInfoSwitchCell *switchCell = (WeXLoanInfoSwitchCell *)currentCell;
        NSString *subText = nil;
        if ([self.walletBalance length] > 0) {
            subText = [NSString stringWithFormat:@"%@%@DCC",@"DCC持有量：",self.walletBalance];
            if ([self.walletBalance integerValue] > 0) {
                [switchCell setSwitcherEnable:true];
            }
        }
        [switchCell setTitle:@"扣款3200DCC抵扣400元手续费" subTitle:subText priceTitle:@"1DCC=0.05rmb"];
    }
    else if (indexPath.section == 2) {
        WeXLoanInfoTextCell *textCell = (WeXLoanInfoTextCell *)currentCell;
        if (indexPath.row == 0) {
            [textCell setTitle:@"还款总额" subTitle:@"2050元" cellType:WeXLoanInfoTextCellHighDefault];
        } else {
            [textCell setTitle:@"收款银行卡" subTitle:@"尾号0102" cellType:WeXLoanInfoTextCellHighArrow];
            [textCell setBottomLineHidden:true];
        }
    }
    else if (indexPath.section == 3) {
        WeXLoanInfoSubmitCell *cell = (WeXLoanInfoSubmitCell *)currentCell;
        cell.DidReadProtocol = ^(BOOL isRead) {
            [self setIsRead:isRead];
            [self refreshSubmitCell];
            WEXNSLOG(@"%@",isRead ? @"已读协议" : @"未读协议");
        };
        cell.DidClickProtocol = ^{
            WEXNSLOG(@"跳转到服务协议");
        };
        cell.DidClickSubmit = ^{
            WEXNSLOG(@"提交数据");
        };
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 2 && indexPath.row == 1) {
        [WeXHomePushService pushFromVC:self toVC:[WeXBindBankIDViewController new]];
    }
}

// MARK: - 刷新b底部提交按钮状态
- (void)refreshSubmitCell {
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0 inSection:3];
    WeXLoanInfoSubmitCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
    if (cell && [cell isKindOfClass:[WeXLoanInfoSubmitCell class]]) {
        [cell setSubmitButtonEnable:self.isRead];
        [cell setIsRead:self.isRead];
    }
}

- (void)getPrivateWalletBalance {
    //abi方法
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    //参数为需要查询的地址
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
        //私链
        [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getDCCContractAddress] data:response type:WEX_DCC_NODE_SERVER responseBlock:^(id response) {
            WEXNSLOG(@"dcc私链balance=%@",response);
            NSDictionary *responseDict = response;
            NSString * originBalance = [responseDict objectForKey:@"result"];
            NSString * ethException  = [responseDict objectForKey:@"ethException"];
            //针对查询钱包余额失败的情况
            if ([ethException isEqualToString:@"ethException"]) {
                self.walletBalance = @"--";
            } else {
                self.walletBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
            }
            [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:1] withRowAnimation:UITableViewRowAnimationNone];
        }];
    }];
}


@end
