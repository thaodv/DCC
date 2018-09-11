//
//  WeXMyIpfsSaveController.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

@interface WeXMyIpfsSaveController : WeXBaseViewController
//ipfs云端入口过多,处理特殊情况的BOOL(用于设置密码界面进去同步界面返回时的按钮判断)
@property (nonatomic,assign)BOOL iSFromSettingVc;
//ipfs云端入口过多,处理特殊情况的记录fromVC(用于同步界面和重置密码界面返回时的按钮判断)
@property(nonatomic,strong)UIViewController *fromVc;

@end
