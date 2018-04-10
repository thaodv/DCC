//
//  WeXAuthorizeLoginViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/30.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"


typedef NS_ENUM(NSInteger, WeXAuthorizeLoginType) {
    WeXAuthorizeLoginTypeApp,  //来自app的授权登录
    WeXAuthorizeLoginTypeWebpage//来自网页授权登录
};

@interface WeXAuthorizeLoginViewController : WeXBaseViewController

@property (nonatomic,strong)NSURL *url;

@property (nonatomic,assign)WeXAuthorizeLoginType type;

@property (nonatomic,strong)NSDictionary *paramsDict;//网页登录时候传过来的参数

@end
