//
//  WeXGestureSafetyView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/16.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

#define DrawPsw_Draw_Unlock_Pattern @"绘制解锁图案"
#define DrawPsw_Draw_Unlock_Pattern_Again @"请再次绘制解锁图案"
#define DrawPsw_Less_4points_Warning @"最少连接4个点，请重新绘制"
#define DrawPsw_Inconsistent_Lasttime @"与上次绘制不一致，请重新绘制"
#define DrawPsw_PswError_Count_Warning(x) ([NSString stringWithFormat:@"密码错误，还可以再输入%d次", x])
#define DrawPsw_PswError_5Times_Warning @"你已连续5次输错手势，手势解锁已关闭，请重新登录"
#define DrawPsw_ForgetPsw_Warning @"忘记手势密码，需重新登录"

#define Shake_Times 10


/** 手势密码的创建方式 */
typedef NS_ENUM(NSInteger,WeXGesturePswType) {
    WeXGesturePswTypeCreate,      // 创建手势密码
    WeXGesturePswTypeVerify,     // 验证手势密码
};

@protocol WeXGestureSafetyViewDelegate<NSObject>
@optional
//密码设置成功回调
- (void)gestureSafetyViewDidSetGesturePassword;
//验证成功回调
- (void)gestureSafetyViewVerifySuccess;

//点击取消按钮
- (void)gestureSafetyViewCancel;

@end

@interface WeXGestureSafetyView : UIView
//背景视图
@property (nonatomic,strong)UIView *coverView;

@property (nonatomic,assign)WeXGesturePswType type;

@property (nonatomic,weak)id<WeXGestureSafetyViewDelegate> delegate;

- (void)dismiss;

@end
