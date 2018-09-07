//
//  WeXDeletePassportView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/29.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>


@protocol WeXDeletePassportViewDelegate<NSObject>
@optional
//点击删除钱包
- (void)deletePassportViewDidDeletePassport;
//点击备份
- (void)deletePassportViewDidBackup;


@end


@interface WeXDeletePassportView : UIView

@property (nonatomic,weak)id<WeXDeletePassportViewDelegate> delegate;

- (void)dismiss;

@end
