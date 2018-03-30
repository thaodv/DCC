//
//  WeXBaseViewController.h
//  WeXIco
//
//  Created by wcc on 2017/9/7.
//  Copyright © 2017年 Wex. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WexBaseNetAdapter.h"


@class WeXBaseViewController;
typedef NS_ENUM(NSInteger,WeXBaseViewBackgroundType) {
    WeXBaseViewBackgroundTypeNormal,
    WeXBaseViewBackgroundTypeNone
};

@protocol WexBaseViewControllerProtocol
-(void)MyViewController:(WeXBaseViewController*)controller onRightButton:(id)sender;
@end

@interface WeXBaseViewController : UIViewController<WexBaseNetAdapterDelegate>

@property (nonatomic,assign)WeXBaseViewBackgroundType backgroundType;

@property (nonatomic,weak)id<WexBaseViewControllerProtocol> delegate;

//留给子类调用设置导航栏返回默认样式方法
- (void)setNavigationNomalBackButtonType;


//留给子类调用设置导航栏右边按钮样式方法
- (void)configNavigitonBarWithImage:(NSString *)image;

-(void)showRightButtonTitleIos7Above:(NSString *)title tintColor:(UIColor*)tintColor;


@end
