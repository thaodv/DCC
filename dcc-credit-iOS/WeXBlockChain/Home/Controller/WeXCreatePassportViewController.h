//
//  WeXCreatePassportViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/3/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

@interface WeXCreatePassportViewController : WeXBaseViewController

@property (nonatomic,assign)BOOL isFromAuthorize;//是否来自授权登录流程

@property (nonatomic,strong)NSURL *url;//授权登录传来的参数url

@end
