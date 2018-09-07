//
//  WeXGetRedPacketView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol WeXGetRedPacketViewDelegate<NSObject>
@optional

- (void)redPacketViewClickJumpButton;

@end

typedef void(^ConfirmBtnBlock)(void);

typedef void(^JumpBtnBlock)(void);

@interface WeXGetRedPacketView : UIView

@property (nonatomic,strong)UILabel *amountLabel;

@property (nonatomic,copy)ConfirmBtnBlock confirmBtnBlock;

@property (nonatomic,copy)JumpBtnBlock jumpBtnBlock;

@property (nonatomic,weak)id<WeXGetRedPacketViewDelegate> delegate;

- (void)removeFrontRedPacketView;

- (void)dismiss;

@end
