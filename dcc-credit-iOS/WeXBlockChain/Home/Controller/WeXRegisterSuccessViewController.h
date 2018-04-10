//
//  WeXRegisterSuccessViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/14.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

typedef NS_ENUM(NSInteger,WeXRegisterSuccessType) {
    WeXRegisterSuccessTypeCreate,
    WeXRegisterSuccessTypeImport
};

@interface WeXRegisterSuccessViewController : WeXBaseViewController

@property (nonatomic,assign)WeXRegisterSuccessType type;

@property (nonatomic,assign)BOOL isFromAuthorize;//是否来自授权登录流程

@property (nonatomic,strong)NSURL *url;//授权登录传来的参数url


@end
