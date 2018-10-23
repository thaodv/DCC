//
//  WeXRepayViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXRepayViewController.h"
#import "WeXRepayCashView.h"
#import "WeXRepayCashAlertView.h"

@interface WeXRepayViewController ()
@property (nonatomic, strong) WeXRepayCashView *repayCashView;
@property (nonatomic, strong) WeXRepayCashAlertView *alertView;

@end

@implementation WeXRepayViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self p_initUI];
    [self p_initAlertView];
}

- (void)p_initUI {
    self.title = WeXLocalizedString(@"还款");
    [self setNavigationNomalBackButtonType];
    __weak typeof(self) weakSelf     = self;
    _repayCashView = [WeXRepayCashView createRepayCashViewWithStatus:self.status detailModel:self.detailModel clickEvent:^(WeXRepayCashEventType type) {
        [weakSelf.alertView show];
    }];
    [self.view addSubview:_repayCashView];
    [_repayCashView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.mas_equalTo(0);
        make.top.mas_equalTo(kNavgationBarHeight);
        make.bottom.mas_equalTo(0);
    }];
}

- (void)p_initAlertView {
    if (!_alertView) {
        _alertView = [WeXRepayCashAlertView createAlertViewWithLoalDataModel:nil parentView:self.view submitEvent:^(NSString *phoneCode) {
            WEXNSLOG(@"phonecode is %@",phoneCode);
        }];
        [_alertView setBackgroundColor:ColorWithRGBA(74, 74, 74, 0.5)];
    }
    _alertView.frame = CGRectMake(0, 0, kScreenWidth, kScreenHeight);
}


@end
