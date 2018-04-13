//
//  WeXGetContractAddressModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXGetContractAddressParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *business;

@end

@interface  WeXGetContractAddressResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
