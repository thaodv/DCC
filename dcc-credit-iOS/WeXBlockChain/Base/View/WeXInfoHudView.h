//
//  WeXInfoHudView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/12/6.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

/** 手势密码的创建方式 */
typedef NS_ENUM(NSInteger,WeXInfoHudViewType) {
    WeXInfoHudViewNormal,      // 正常白色底
    WeXInfoHudViewOpaque,     // 透明底色
};

@interface WeXInfoHudView : UIView

@property (nonatomic,copy)NSString *text;

@property (nonatomic,strong)UIView *parentView;

@property (nonatomic,assign)WeXInfoHudViewType type;

- (instancetype)initWithType:(WeXInfoHudViewType)type;

- (void)dissmiss;

@end
