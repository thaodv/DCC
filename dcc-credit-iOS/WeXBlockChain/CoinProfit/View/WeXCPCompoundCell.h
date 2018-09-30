//
//  WeXCPCompoundCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

typedef NS_ENUM(NSInteger,WeXCPCompoundType) {
    WeXCPCompoundTypeTextFiledAndLabel = 0, //左边输入右边Label
    WeXCPCompoundTypeLabelAndButton = 1, //左边Label右边Button
};

@interface WeXCPCompoundCell : WeXBaseTableViewCell

@property (nonatomic,copy) void (^DidClickAllButton)(void);
@property (nonatomic,copy) void (^DidInputText)(NSString *);


- (void)setLeftTitle:(NSString *)leftTitle
           rightText:(NSString *)rightText
         placeHolder:(NSString *)placeHolder
                type:(WeXCPCompoundType)cellType;

- (void)setLeftTitle:(NSString *)leftTitle
    rightButtonImage:(NSString *)image;

- (void)setLeftTextFiled:(NSString *)text;



/**
 设置键盘是否带有小数 以及最多输入几位小数

 @param aDot 是否需要小数
 @param maxDotNum 最多几位小数
 */
- (void)setKeyBoardIsWithDot:(BOOL)aDot maxDotNum:(NSInteger)maxDotNum;


@end
