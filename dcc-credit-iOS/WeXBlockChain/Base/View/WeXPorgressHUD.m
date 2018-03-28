//
//  WeXPorgressHUD.m
//  WeXIco
//
//  Created by wcc on 2017/10/16.
//  Copyright © 2017年 Wex. All rights reserved.
//

#import "WeXPorgressHUD.h"
#import "WeXInfoHudView.h"

static WeXBaseLoadingView* _loadView = nil;

@implementation WeXPorgressHUD

+ (void)showHUDAddedTo:(UIView *)view animated:(BOOL)animated{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:animated];
    
    hud.bezelView.backgroundColor = [UIColor grayColor];
    
    hud.contentColor = [UIColor whiteColor];

}


+ (void)hideHUDForView:(UIView *)view animated:(BOOL)animated{
    
    dispatch_async(dispatch_get_global_queue( DISPATCH_QUEUE_PRIORITY_LOW, 0), ^{
        // Do something...
        dispatch_async(dispatch_get_main_queue(), ^{
            [MBProgressHUD hideHUDForView:view animated:YES];
        });
    });
   
}

+ (void)showText:(NSString *)text onView:(UIView *)view
{
//    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
//
//    hud.bezelView.backgroundColor = [UIColor clearColor];
//
//    hud.backgroundColor = [UIColor clearColor];
//
//    hud.backgroundView.backgroundColor = [UIColor clearColor];
//
//    hud.mode = MBProgressHUDModeText;
//
//    hud.label.text = text;
//
//    hud.label.textColor = [UIColor whiteColor];
//
//    hud.label.numberOfLines = 0;
//
//    hud.offset = CGPointMake(0.f, 0);
//
//    [hud hideAnimated:YES afterDelay:122.f];
    
    
    WeXInfoHudView *infoView = [[WeXInfoHudView alloc] initWithType:WeXInfoHudViewOpaque];
    infoView.text = text;
    infoView.parentView = view;
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [infoView dissmiss];
    });
    
}

+ (void)showOpaqueText:(NSString *)text onView:(UIView *)view{
    WeXInfoHudView *infoView = [[WeXInfoHudView alloc] initWithType:WeXInfoHudViewOpaque];
    infoView.text = text;
    infoView.parentView = view;
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [infoView dissmiss];
    });
}

+ (void)showLoadingAddedTo:(UIView *)view{
    _loadView = [[WeXBaseLoadingView alloc] initWithFrame:view.bounds];
    [view addSubview:_loadView];
  
}

+ (void)hideLoading{
    
    [_loadView dissMiss];
    _loadView = nil;
    
}

@end
