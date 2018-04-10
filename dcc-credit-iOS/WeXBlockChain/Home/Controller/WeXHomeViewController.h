//
//  WeXHomeViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/13.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

@interface WeXHomeViewController : WeXBaseViewController

@property (nonatomic,assign)BOOL isFromAuthorize;//是否来自授权登录流程

@property (nonatomic,strong)NSURL *url;//授权登录传来的参数url

@end
