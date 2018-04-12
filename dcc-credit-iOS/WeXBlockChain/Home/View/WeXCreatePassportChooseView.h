//
//  WeXCreatePassportChooseView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/3/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol WeXCreatePassportChooseViewDelegate<NSObject>
@optional

- (void)clickCreatePassportButton;
//点击备份
- (void)clickImportPassportButton;


@end

@interface WeXCreatePassportChooseView : UIView

@property (nonatomic,weak)id<WeXCreatePassportChooseViewDelegate> delegate;

- (void)dismiss;

@end

