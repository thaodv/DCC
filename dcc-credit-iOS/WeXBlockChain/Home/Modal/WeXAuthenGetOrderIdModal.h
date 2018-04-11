//
//  WeXAuthenGetOrderIdModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/2/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXAuthenGetOrderIdParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *txHash;

@property (nonatomic,copy)NSString *business;

@end

@interface  WeXAuthenGetOrderIdResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *orderId;

@property (nonatomic,copy)NSString *status;

@end

