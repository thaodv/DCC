//
//  WeXLoanCancelModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface  WeXLoanCancelParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *chainOrderId;

@end

@interface  WeXLoanCancelResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
