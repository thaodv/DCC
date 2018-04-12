//
//  WeXNumberSafetyView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/16.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>


/** 手势密码的创建方式 */
typedef NS_ENUM(NSInteger,WeXNumberPasswordType) {
    WeXNumberPasswordTypeCreate,      // 创建手势密码
    WeXNumbePasswordTypeVerify,     // 验证手势密码
};

@protocol WeXNumberSafetyViewDelegate<NSObject>
@optional

- (void)numberSafetyViewDidSetNumberPassword;

//验证成功回调
- (void)numberSafetyViewVerifySuccess;

//点击取消按钮
- (void)numberSafetyViewCancel;

@end

@interface WeXNumberSafetyView : UIView

//背景视图
@property (nonatomic,strong)UIView *coverView;

@property (nonatomic,assign)WeXNumberPasswordType type;

@property (nonatomic,weak)id<WeXNumberSafetyViewDelegate> delegate;

- (void)dismiss;

@end
