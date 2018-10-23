//
//  WeXCashLoanViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/12.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCashLoanViewController.h"
#import "WeXCashLoanView.h"

#import "WeXCashAmountApplicationController.h"

#import "WeXCashLoanTestView.h"
#import "WeXMyLoanViewController.h"
#import "WeXCashLoanRealInfoController.h"


@interface WeXCashLoanViewController ()

@property (nonatomic, strong) WeXCashLoanView *loanView;
@property (nonatomic, strong) WeXCashLoanTestView *testView;

@end

@implementation WeXCashLoanViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"法币借贷");
    [self.view setBackgroundColor:[UIColor whiteColor]];
    [self setNavigationNomalBackButtonType];
    [self p_initUI];
    [self p_initConstraints];
    [self p_initTestLoanView];
}

- (void)p_initUI {
    UIBarButtonItem *rightButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"我的借款" style:UIBarButtonItemStylePlain target:self action:@selector(myLoanEvent:)];
    [rightButtonItem setTitleTextAttributes:@{NSFontAttributeName:WexFont(16),NSForegroundColorAttributeName:ColorWithHex(0xC009FF)} forState:UIControlStateNormal];
    self.navigationItem.rightBarButtonItem = rightButtonItem;
    
    if (!self.isDefault) {
        _loanView = [WeXCashLoanView createCashLoanViewWithType:_viewType model:nil event:^(WeXCashLoanViewEventType eventType) {
            WEXNSLOG(@"---%ld",eventType);
        }];
        [self.view addSubview:_loanView];
    } else {
        _loanView = [WeXCashLoanView createCashLoanViewWithType:WeXCashLoanViewDefault model:nil event:^(WeXCashLoanViewEventType eventType) {
            if (eventType == WeXCashLoanViewEventApplyAmount) {
                WeXCashAmountApplicationController *ctrl = [[WeXCashAmountApplicationController alloc] init];
                [WeXHomePushService pushFromVC:self toVC:ctrl];
            }
        }];
        [self.view addSubview:_loanView];
    }
    
}
/*
 WeXCashLoanViewDefault         = 0, // 默认额度申请界面
 WeXCashLoanViewAmountAndPeriod = 1, // 额度以及期限
 WeXCashLoanViewApplayReach     = 2, //借款金额已到账
 WeXCashLoanViewApplyOverdue    = 3, //借款金额已经逾期
 WeXCashLoanViewIsChecking      = 4, //资料正在审核
 WeXCashLoanViewCheckFail       = 5, //资料审核失败
 WeXCashLoanViewCheckExpried    = 6, //认证已过期
 WeXCashLoanViewApplying        = 7, //有笔借款正在进行中
 */

- (void)p_initTestLoanView {
    NSArray <NSString *> *items = @[@"额度申请界面",@"额度以及期限",@"借款金额已到账",@"借款金额已经逾期",@"资料正在审核",
                                    @"资料审核失败",@"认证已过期"];
    if (self.isDefault) {
        _testView = [WeXCashLoanTestView createTestViewItems:items clickIndex:^(NSInteger index) {
            WeXCashLoanViewController *cashLoanViewController = [WeXCashLoanViewController new];
            cashLoanViewController.viewType = index;
            [WeXHomePushService pushFromVC:self toVC:cashLoanViewController];
        }];
        [self.view addSubview:_testView];
        [_testView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.mas_equalTo(0);
            make.top.mas_equalTo(kNavgationBarHeight + 20);
            make.bottom.mas_equalTo(-10);
        }];
    }
}

- (void)p_initConstraints {
    [self.loanView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(kNavgationBarHeight);
        make.left.right.bottom.mas_equalTo(0);
    }];
}

- (void)myLoanEvent:(UIBarButtonItem *)sender {
    [WeXHomePushService pushFromVC:self toVC:[WeXMyLoanViewController new]];
    WEXNSLOG(@"我的借款");
}



@end
