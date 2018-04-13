//
//  WeXGraphView.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/27.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^ComfirmBtnBlock)(void);

typedef void(^GraphBtnBlock)(void);

@interface WeXGraphView : UIView<UITextFieldDelegate>

@property (nonatomic,strong)UITextField *graphTextField;

@property (nonatomic,strong)UIButton *graphBtn;

@property (nonatomic,copy)ComfirmBtnBlock comfirmBtnBlock;

@property (nonatomic,copy)GraphBtnBlock graphBtnBlock;

@end
