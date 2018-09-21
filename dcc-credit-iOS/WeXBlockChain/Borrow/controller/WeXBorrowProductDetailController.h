//
//  WeXBorrowProductDetailController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

@class WeXQueryProductByLenderCodeResponseModal_item;

@interface WeXBorrowProductDetailController : WeXBaseViewController

@property (nonatomic,strong)WeXQueryProductByLenderCodeResponseModal_item *productModel;

@end
