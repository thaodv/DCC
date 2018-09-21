//
//  WeXDeleteReceivieAddressToastView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXBorrowReceiveAddressModal;

typedef void(^DeleteButtonClickBlock)(void);

@protocol WeXDeleteReceivieAddressToastViewDelegate<NSObject>
@optional
//点击删除
- (void)deleteReceivieAddressToastViewDidDeletePassport;



@end

@interface WeXDeleteReceivieAddressToastView : UIView

@property (nonatomic,weak)id<WeXDeleteReceivieAddressToastViewDelegate> delegate;

@property (nonatomic,copy)DeleteButtonClickBlock deleteButtonClickBlock;

- (void)configWithModel:(WeXBorrowReceiveAddressModal *)model;

- (void)dismiss;

@end

