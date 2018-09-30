//
//  WeXBorrowFeeSlideView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol WeXBorrowFeeSlideViewDelegate<NSObject>

@optional

- (void)borrowFeeSlideViewWithValue:(CGFloat)value;

@end


@interface WeXBorrowFeeSlideView : UIView

- (instancetype)initWithFrame:(CGRect )frame normalFee:(NSString *)normalFee fastFee:(NSString *)fastFee balance:(NSString *)balance;

- (instancetype)initWithNormalFee:(NSString *)normalFee fastFee:(NSString *)fastFee balance:(NSString *)balance;

@property (nonatomic,weak)id<WeXBorrowFeeSlideViewDelegate> delegate;



@end
