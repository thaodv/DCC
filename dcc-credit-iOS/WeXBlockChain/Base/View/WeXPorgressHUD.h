//
//  WeXPorgressHUD.h
//  WeXIco
//
//  Created by wcc on 2017/10/16.
//  Copyright © 2017年 Wex. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MBProgressHUD.h"
#import "WeXBaseLoadingView.h"

@interface WeXPorgressHUD : NSObject

+ (void)showHUDAddedTo:(UIView *)view animated:(BOOL)animated;

+ (void)hideHUDForView:(UIView *)view animated:(BOOL)animated;

+ (void)showText:(NSString *)text onView:(UIView *)view;

+ (void)showOpaqueText:(NSString *)text onView:(UIView *)view;

+ (void)showLoadingAddedTo:(UIView *)view;

+ (void)hideLoading;



@end
