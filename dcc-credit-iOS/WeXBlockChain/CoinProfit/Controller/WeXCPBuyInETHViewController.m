//
//  WeXCPBuyInETHViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPBuyInETHViewController.h"
#import "WeXCPBuyBottomView.h"
#import "WeXWalletETHTranstionDetailView.h"
#import "WeXGetReceiptResult2Adapter.h"
#import "WeXCoinProfitOnlyTextCell.h"
#import "WeXCPCompoundCell.h"
#import "WeXCPLeftAndRightLabelCell.h"

@interface WeXCPBuyInETHViewController () <UITextFieldDelegate>

@property (nonatomic, strong) WeXCPBuyBottomView *bottomView;
//@property (nonatomic, strong) WeXCPGetContractAddressAdapter *getContractAddress;
//@property (nonatomic, strong) WeXCPGetContractAddressResModel *responseModel;
@property (nonatomic, assign) BOOL isBuyEvent;
//购买数量
@property (nonatomic, copy) NSString *amount;
//钱包余额
@property (nonatomic, copy) NSString *walletBalance;
//起购额度
@property (nonatomic, copy) NSString *minBuyAmount;
//总额度
@property (nonatomic, copy) NSString *totalAmount;
//已认购额度
@property (nonatomic, copy) NSString *haveBuyAmount;
//剩余额度
@property (nonatomic, copy) NSString *remainAmount;

@property (nonatomic, weak) WeXWalletETHTranstionDetailView *dccTransDetailView;
@property (nonatomic, strong) UIView *detailCoverView;
@property (nonatomic, strong) WeXGetReceiptResult2Adapter *resultAdapter;
@property (nonatomic, assign) NSInteger requestCount;
@property (nonatomic, copy) NSString *txHash;
@property (nonatomic, strong) NSArray <NSString *> *sectionTitles;
@property (nonatomic, strong) NSArray <NSString *> *sectionSubtitles;
@property (nonatomic, strong) UITextField  * priceTextFiled;
@property (nonatomic, strong) UITextField  * limitTextField;





@end

static NSString * const kDefaultBalance   = @"--";
static NSString * const kCPCompundCellID  = @"WeXCPCompoundCellID";
static NSString * const kCPOnlyTextCellID = @"WeXCoinProfitOnlyTextCellID";
static NSString * const kCPLeftAndRightCellID = @"WeXCPLeftAndRightLabelCellID";


@implementation WeXCPBuyInETHViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"认购额度");
    [self configureNavigaionBar];
    [self setUpBottomView];
}

- (void)configureNavigaionBar {
    _sectionTitles = @[@"Gas Price",@"Gas Limit",@"最高矿工费"];
    _sectionSubtitles = @[@"Gwei",@"",@"ETH"];
    [self setNavigationNomalBackButtonType];
    [self.tableView setBackgroundColor:ColorWithHex(0xF8F8FF)];
    [self wex_unistallRefreshHeader];
}

- (void)setUpBottomView {
    __weak typeof(self) weakSelf  = self;
    _bottomView = [WeXCPBuyBottomView createBuyBottomView:CGRectMake(0, 0, kScreenWidth, 250) tipsType:WexCPBuyButtonTipsTypePublic clickEvent:^(WeXCPBuyBottomViewType type) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (type == WeXCPBuyBottomViewTypeBuyIn) {
            WEXNSLOG(@"这是立即认购事件");
        }
    }];
    self.tableView.tableFooterView = _bottomView;
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCPCompoundCell          class] forCellReuseIdentifier:kCPCompundCellID];
    [tableView registerClass:[WeXCoinProfitOnlyTextCell  class] forCellReuseIdentifier:kCPOnlyTextCellID];
    [tableView registerClass:[WeXCPLeftAndRightLabelCell class] forCellReuseIdentifier:kCPLeftAndRightCellID];
}

#pragma mark UITableViewDatasource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0:
            return 1;
            break;
        case 1:
            return 3;
            break;
        case 2:
            return 3;
            break;
        default:
            return 0;
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 15;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == 1) {
        return 15;
    }
    return 0.01;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0:
            return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            break;
        case 1:
            if (indexPath.row == 0) {
                return [self wexTableView:tableView cellIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            } else {
                return [self wexTableView:tableView cellIdentifier:kCPCompundCellID indexPath:indexPath];
            }
            break;
        case 2:
            return [self wexTableView:tableView cellIdentifier:kCPLeftAndRightCellID indexPath:indexPath];
            break;
            
        default:
            return 0.01;
            break;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0:
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            break;
        case 1:
            if (indexPath.row == 0) {
                return [self wexTableview:tableView cellForRowWithIdentifier:kCPOnlyTextCellID indexPath:indexPath];
            }
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPCompundCellID indexPath:indexPath];
            break;
        case 2:
            return [self wexTableview:tableView cellForRowWithIdentifier:kCPLeftAndRightCellID indexPath:indexPath];
            break;
        default:
            return nil;
            break;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
}

- (void)wexTableView:(UITableView *)tableView
       configureCell:(UITableViewCell *)currentCell
           indexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0: {
            WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
            [cell setBottomLineHidden:true];
            [cell setTitle:@"私链DCC币生息1期(剩余额度 --)" highText:@"--"];
            if ([self.remainAmount length]> 0) {
                NSString *highText = [NSString stringWithFormat:@"%@ DCC",self.remainAmount];
                NSString *text = [NSString stringWithFormat:@"私链DCC币生息1期(剩余额度 %@)",highText];
                [cell setTitle:text highText:highText];
            }
        }
            break;
        case 1: {
            if (indexPath.row == 0) {
                WeXCoinProfitOnlyTextCell *cell = (WeXCoinProfitOnlyTextCell *)currentCell;
                [cell setBottomLineHidden:true];
                [cell setTitle:@"认购额度" subText:nil cellType:WeXCoinProfitOnlyTextCellTypeOnlyBoldTitle];
            }
            else if (indexPath.row == 1) {
                WeXCPCompoundCell *cell = (WeXCPCompoundCell *)currentCell;
                if (self.minBuyAmount) {
                    NSString *placeHolder = [NSString stringWithFormat:@"%@%@DCC",@"最小认购额度",self.minBuyAmount];
                    [cell setLeftTitle:nil rightText:@"DCC" placeHolder:placeHolder type:WeXCPCompoundTypeTextFiledAndLabel];
                }
                if ([self.amount length] > 0) {
                    [cell setLeftTextFiled:self.amount];
                }
                cell.DidInputText = ^(NSString *text) {
                    self.amount = text;
                    WEXNSLOG(@"%@",text);
                };
            } else {
                WeXCPCompoundCell *cell = (WeXCPCompoundCell *)currentCell;
                NSString *balance= [NSString stringWithFormat:@"可用额度: %@DCC",self.walletBalance];
                if ([self.walletBalance isEqualToString:kDefaultBalance]) {
                    balance = @"可用额度: --";
                }
                [cell setLeftTitle:balance rightText:@"全部" placeHolder:nil type:WeXCPCompoundTypeLabelAndButton];
                cell.DidClickAllButton = ^{
                    [self.view endEditing:YES];
                    //2018.8.16 由于获取不到余额,所以写的假数据
                    double amount = [self.walletBalance floatValue];
                    self.amount   = [NSString stringWithFormat:@"%.0f",floor(amount)];
                    [self reloadCellWithSection:indexPath.section row:indexPath.row - 1];
                    WEXNSLOG(@"我点击了全部啊----%f",floor(amount));
                };
                [cell setBottomLineHidden:true];
            }
        }
            break;
        case 2: {
            WeXCPLeftAndRightLabelCell *cell = (WeXCPLeftAndRightLabelCell *)currentCell;
            [cell setLeftTitle:_sectionTitles[indexPath.row] rightTitle:_sectionSubtitles[indexPath.row]];
            if (indexPath.row == 0) {
                cell.textField.userInteractionEnabled = true;
                self.priceTextFiled = cell.textField;
                cell.textField.delegate = self;
            } else if (indexPath.row == 1) {
                cell.textField.userInteractionEnabled = true;
                self.limitTextField = cell.textField;
                cell.textField.delegate = self;
            } else {
                cell.textField.userInteractionEnabled = NO;
            }
        }
            break;
            
            break;
        default:
            break;
    }
}

- (void)reloadCellWithSection:(NSInteger)section row:(NSInteger)row  {
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:row inSection:section];
    [self.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
}

// MARK: - UITextFiledDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


@end
