//
//  WeXBaseNavigationController.m
//  WeXIco
//
//  Created by wcc on 2017/9/7.
//  Copyright © 2017年 Wex. All rights reserved.
//

#import "WeXBaseNavigationController.h"

@interface WeXBaseNavigationController ()<UIGestureRecognizerDelegate, UINavigationControllerDelegate>

@end

@implementation WeXBaseNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationController.navigationBar.translucent = YES;

    [self.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
    
    [self.navigationBar setBarTintColor:[UIColor whiteColor]];
    
    [self.navigationBar setShadowImage:[UIImage new]];
    
    [self.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName:COLOR_LABEL_TITLE,NSFontAttributeName:[UIFont systemFontOfSize:22]}];
    
    [self.navigationBar setBarStyle:UIBarStyleDefault];
    
  
//    __weak WeXBaseNavigationController *weakSelf = self;
//
//    if ([self respondsToSelector:@selector(interactivePopGestureRecognizer)])
//    {
//        self.interactivePopGestureRecognizer.delegate = weakSelf;
//
//        self.delegate = weakSelf;
//    }
    
}

//- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
//{
//    if (gestureRecognizer == self.interactivePopGestureRecognizer)
//    {
//        if ([self.visibleViewController isKindOfClass:[WexBaseViewController class]])
//        {
//            WexBaseViewController* controller = (WexBaseViewController*)self.visibleViewController;
//            return [controller isEnablePopGesture];
//        }
//        else
//        {
//            return NO;
//        }
//    }
//
//    return YES;
//}

// 通常的做法，本App中不采用

// - (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
// {
//     if (gestureRecognizer == self.interactivePopGestureRecognizer)
//     {
//         NSLog(@"_isTransitioning=%d",[[self valueForKey:@"_isTransitioning"] boolValue]);
//         if (self.viewControllers.count < 2 || self.visibleViewController == [self.viewControllers objectAtIndex:0])
//         {
//             return NO;
//         }
//     }
//
//     return YES;
// }


#pragma mark UINavigationControllerDelegate

//- (void)navigationController:(UINavigationController *)navigationController
//       didShowViewController:(UIViewController *)viewController
//                    animated:(BOOL)animate
//{
//    if ([self respondsToSelector:@selector(interactivePopGestureRecognizer)])
//    {
//        self.interactivePopGestureRecognizer.enabled = YES;
//    }
//}



@end
