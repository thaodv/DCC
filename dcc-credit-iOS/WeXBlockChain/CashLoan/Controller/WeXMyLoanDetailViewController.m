//
//  WeXMyLoanDetailViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyLoanDetailViewController.h"
#import "WeXMyLoanDetailModel.h"
#import "WeXRepayViewController.h"
#import "WeXCashLoanInfoViewController.h"

#import "WeXOrderProcessAlertView.h"


@interface WeXMyLoanDetailViewController ()
@property (nonatomic, strong) WeXCashLoanDetailView *detailView;

@end

@implementation WeXMyLoanDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"借款详情");
    [self p_configureNav];
    [self p_initUI];
}

- (void)p_configureNav {
    [self.view setBackgroundColor:[UIColor whiteColor]];
    [self setNavigationNomalBackButtonType];
}

- (void)p_initUI {
    __weak typeof(self) weakSelf     = self;
    _detailView = [WeXCashLoanDetailView createLoanDetailWithStatus:self.detailStatus detailModel:nil eventType:^(WeXCashLoanDetailEventType type) {
        [weakSelf hanleCashLoanDetailEventType:type];
    }];
    _detailView.frame = CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight - kNavgationBarHeight);
    [self.view addSubview:_detailView];

}

- (void)hanleCashLoanDetailEventType:(WeXCashLoanDetailEventType)type {
    switch (type) {
        case WeXCashLoanDetailEventService: {
            [self showAlertView];
        }
            WEXNSLOG(@"查看服务");
            break;
        case WeXCashLoanDetailEventDeposit: {
            WeXCashLoanInfoViewController *LoanInfoVC = [WeXCashLoanInfoViewController new];
            [WeXHomePushService pushFromVC:self toVC:LoanInfoVC];
        }
            WEXNSLOG(@"提现");
            break;
        case WeXCashLoanDetailEventAdvanceRepay: {
            WeXRepayViewController *repayVC = [WeXRepayViewController new];
            repayVC.detailModel = nil;
            repayVC.status = type;
            [WeXHomePushService pushFromVC:self toVC:repayVC];
        }
            WEXNSLOG(@"提前还款");
            break;
        case WeXCashLoanDetailEventLoanAgain:
            WEXNSLOG(@"再借一笔");
            break;
        case WeXCashLoanDetailEventRepayment: {
            WeXRepayViewController *repayVC = [WeXRepayViewController new];
            repayVC.detailModel = nil;
            repayVC.status = type;
            [WeXHomePushService pushFromVC:self toVC:repayVC];
            WEXNSLOG(@"还款");
        }
            break;
        default:
            break;
    }
}

- (void)showAlertView {
    [[WeXOrderProcessAlertView createWithTitle:@"当前已有一笔订单在进行中，无法再次进行借款。" okEvent:^{
        WEXNSLOG(@"我知道了");
    }] show];
}

@end
