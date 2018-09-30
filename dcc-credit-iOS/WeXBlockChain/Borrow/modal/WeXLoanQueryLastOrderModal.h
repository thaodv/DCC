//
//  WeXLoanQueryLastOrderModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface  WeXLoanQueryLastOrderParamModal : WeXBaseNetModal

@end

@interface  WeXLoanQueryLastOrderResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *orderId;

@property (nonatomic,copy)NSString *version;
@property (nonatomic,copy)NSString *borrower;
@property (nonatomic,copy)NSString *idHash;
@property (nonatomic,copy)NSString *status;
@property (nonatomic,copy)NSString *fee;
@property (nonatomic,copy)NSString *applicationDigest;

@end
