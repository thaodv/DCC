//
//  WeXChooseReceiveAddressViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

typedef void(^ChooseAddressBlock)(NSString *addressName,NSString *addressContent);


@interface WeXChooseReceiveAddressViewController : WeXBaseViewController

@property (nonatomic,copy)ChooseAddressBlock chooseAddressBlock;

@end