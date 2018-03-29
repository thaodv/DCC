//
//  WeXBaseNavigationController.m
//  WeXIco
//
//  Created by wcc on 2017/9/7.
//  Copyright © 2017年 Wex. All rights reserved.
//

#import "WeXBaseNavigationController.h"

@interface WeXBaseNavigationController ()

@end

@implementation WeXBaseNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationController.navigationBar.translucent = YES;
    
    [self.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
    
    [self.navigationBar setShadowImage:[UIImage new]];
    
    [self.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor blackColor],NSFontAttributeName:[UIFont systemFontOfSize:22]}];
    
}


@end
