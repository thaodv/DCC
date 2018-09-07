//
//  WeXAddInviteCodeView.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^InviteConfirmBtnBlock)(NSString *code);

@interface WeXAddInviteCodeView : UIView

@property (nonatomic,copy)InviteConfirmBtnBlock inviteConfirmBtnBlock;

- (void)dismiss;

@end
