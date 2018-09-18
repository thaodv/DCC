//
//  WeXMainTabViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMainTabViewController.h"
#import "UIImage+Color.h"
#import "WeXPassportViewController.h"
#import "WeXBaseNavigationController.h"
#import "WeXActivityHomeViewController.h"
#import "WeXCreatePassportChooseView.h"
#import "WeXHomePushService.h"
#import "WeXCreatePassportViewController.h"
#import "WeXImportViewController.h"
#import "AppDelegate.h"
#import "WeXNewMyController.h"

@interface WeXMainTabViewController ()
<UITabBarControllerDelegate,WeXCreatePassportChooseViewDelegate>

@end

@implementation WeXMainTabViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self p_loadChildViewControllers];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)p_loadChildViewControllers {
    WeXBaseNavigationController *homeNav = [[WeXBaseNavigationController alloc] initWithRootViewController:[WeXPassportViewController new]];
    [self setupItemWithController:homeNav norImageName:@"ISHome-dark" selImageName:@"ISHome-light" title:@"服务"];
    
    WeXBaseNavigationController *detectNav = [[WeXBaseNavigationController alloc] initWithRootViewController:[WeXActivityHomeViewController new]];
    [self setupItemWithController:detectNav norImageName:@"ISWallet-dark" selImageName:@"ISWallet-light" title:@"发现"];
    
    WeXBaseNavigationController *myNav = [[WeXBaseNavigationController alloc] initWithRootViewController:[WeXNewMyController new]];
    [self setupItemWithController:myNav norImageName:@"ISMy-dark" selImageName:@"ISMy-light" title:@"我的"];

    [self setViewControllers:@[homeNav,detectNav,myNav]];
    self.delegate = self;
}
- (void)setupItemWithController:(UIViewController *)vc
                   norImageName:(NSString *)norImageName
                   selImageName:(NSString *)selImageName
                          title:(NSString *)title {
    vc.tabBarItem.title = title;
    vc.tabBarItem.image = [UIImage imageNamed:norImageName];
    vc.tabBarItem.selectedImage = [UIImage imageWithOriginalName:selImageName];
    NSMutableDictionary *attNor = [NSMutableDictionary dictionary];
    attNor[NSForegroundColorAttributeName] = ColorWithHex(0xad6dfd);
    [vc.tabBarItem setTitleTextAttributes:attNor forState:UIControlStateSelected];
    NSMutableDictionary *attSel = [NSMutableDictionary dictionary];
    attSel[NSForegroundColorAttributeName] = ColorWithHex(0x8e8e8e);
    [vc.tabBarItem setTitleTextAttributes:attSel forState:UIControlStateNormal];
}


// MARK: - UITabBarControllerDelegate
- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController {
    if (![viewController isKindOfClass:[WeXPassportViewController class]] && ![self hasPassport]) {
        [self createPassportChooseView];
    }
    WEXNSLOG(@"selected viewcontroller is :%@",viewController);
}

- (BOOL)tabBarController:(UITabBarController *)tabBarController shouldSelectViewController:(UIViewController *)viewController {
    if (![viewController isKindOfClass:[WeXPassportViewController class]] && ![self hasPassport]) {
        [self createPassportChooseView];
        return NO;
    }
    return YES;
}

- (void)createPassportChooseView {
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
    WeXCreatePassportChooseView *chooseView = [[WeXCreatePassportChooseView alloc] initWithFrame:delegate.window.bounds];
    chooseView.delegate = self;
    [delegate.window addSubview:chooseView];
}
// MARK: - 创建
-(void)clickCreatePassportButton {
    [WeXHomePushService pushFromVC:self toVC:[WeXCreatePassportViewController new]];
}
// MARK: - 导入
- (void)clickImportPassportButton {
    [WeXHomePushService pushFromVC:self toVC:[WeXImportViewController new]];
}

- (BOOL)hasPassport {
    NSString *address = [WexCommonFunc getFromAddress];
    if (!address) {
        return NO;
    }
    return YES;
}



@end
