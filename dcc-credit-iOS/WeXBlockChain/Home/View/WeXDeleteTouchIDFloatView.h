//
//  WeXDeleteTouchIDFloatView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/12/13.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol WeXDeleteTouchIDFloatViewDelegate<NSObject>
@optional
//点击开启
- (void)touchIDFloatViewDidClickOpenButtoon;
//点击取消
- (void)touchIDFloatViewDidClickCancelButton;


@end

@interface WeXDeleteTouchIDFloatView : UIView

@property (nonatomic,weak)id<WeXDeleteTouchIDFloatViewDelegate> delegate;

- (void)dismiss;

@end
