//
//  WeXHomeSafetyView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/16.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXRegisterSuccessViewController;

typedef void(^SkipBtnBlock)(void);

@protocol WeXHomeSafetyViewDelegate<NSObject>

@optional
- (void)safetyViewDidSetPassword;

//点击取消获取跳过按钮
- (void)safetyViewDidSetSkip;

@end

@interface WeXHomeSafetyView : UIView

@property (nonatomic,strong)NSArray *dataArray;

@property (nonatomic,weak)id<WeXHomeSafetyViewDelegate> delegate;

@property (nonatomic,copy)SkipBtnBlock skipBtnBlock;

@property (nonatomic,copy)NSString *skipName;

- (void)dismiss;

@end
