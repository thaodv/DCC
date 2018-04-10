//
//  WeXPassportViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

@interface WeXPassportViewController : WeXBaseViewController

@property (nonatomic,strong)UITableView *cardTabelView;

@property (nonatomic,strong)UITableView *tokenTabelView;

@property (nonatomic, strong) NSIndexPath *currentIndexPath;

//是否显示密码设置成功后的提示
@property (nonatomic,assign)BOOL isShowDescription;

@end
