//
//  WeXLoanGetOrderIdModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXLoanGetOrderIdParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *txHash;


@end

@protocol WeXLoanGetOrderIdResponseModal_item;
@interface  WeXLoanGetOrderIdResponseModal_item : WeXBaseNetModal

@property (nonatomic,copy)NSString *orderId;

@end

@interface  WeXLoanGetOrderIdResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSMutableArray<WeXLoanGetOrderIdResponseModal_item> *resultList;

@end
