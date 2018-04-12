//
//  WeXRegisterView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/13.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^CancelBtnBlock)(void);

typedef void(^CreateBtnBlock)(void);

typedef void(^GraphBtnBlock)(void);

@interface WeXRegisterView : UIView

@property (nonatomic,strong)UITextField *passwordTextField;

@property (nonatomic,strong)UITextField *graphTextField;

@property (nonatomic,copy)CancelBtnBlock cancelBtnBlock;

@property (nonatomic,copy)CreateBtnBlock createBtnBlock;

@property (nonatomic,copy)GraphBtnBlock graphBtnBlock;

@property (nonatomic,strong)UIButton *graphBtn;

- (void)dismiss;

@end
