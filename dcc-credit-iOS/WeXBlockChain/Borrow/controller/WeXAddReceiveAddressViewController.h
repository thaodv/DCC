//
//  WeXAddReceiveAddressViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

typedef NS_ENUM(NSInteger,WeXReceiveAddressType) {
    WeXReceiveAddressTypeAdd,
    WeXReceiveAddressTypeModify
};

@interface WeXAddReceiveAddressViewController : WeXBaseViewController

@property (nonatomic,assign)WeXReceiveAddressType type;

@property (nonatomic,assign)NSInteger index;



@end
