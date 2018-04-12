//
//  WeXPasswordModifySuccessFloatView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/29.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol WeXPasswordModifySuccessFloatViewDelegate<NSObject>
@optional

//点击备份
- (void)passwordModifySuccessFloatViewDidBackup;


@end

@interface WeXPasswordModifySuccessFloatView : UIView

@property (nonatomic,weak)id<WeXPasswordModifySuccessFloatViewDelegate> delegate;


@end
