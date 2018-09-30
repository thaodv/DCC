//
//  WeXLoginViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoginViewController.h"
#import "WeXLaunchView.h"
#import "WeXImportViewController.h"
#import "WeXCreatePassportViewController.h"
#import "WeXHomePushService.h"

@interface WeXLoginViewController () <WeXLaunchViewDelegate>

@property (nonatomic, strong) WeXLaunchView *launchView;

@end

@implementation WeXLoginViewController

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [UIApplication sharedApplication].statusBarHidden = true;
    [self.navigationController setNavigationBarHidden:true animated:NO];
}
- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [UIApplication sharedApplication].statusBarHidden = false;
    [self.navigationController setNavigationBarHidden:false animated:NO];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)initUI {
    _launchView = [[WeXLaunchView alloc] initWithFrame:[UIScreen mainScreen].bounds ];
    _launchView.delegate = self;
    [self.view addSubview:_launchView];
}


// MARK: - WeXLaunchViewDelegate
- (void)launchViewDidClicImportWallet:(WeXLaunchView *)launchView {
    [WeXHomePushService pushFromVC:self toVC:[WeXImportViewController new]];
}

- (void)launchViewDidClickCreateWallet:(WeXLaunchView *)launchView {
    [WeXHomePushService pushFromVC:self toVC:[WeXCreatePassportViewController new]];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

@end
