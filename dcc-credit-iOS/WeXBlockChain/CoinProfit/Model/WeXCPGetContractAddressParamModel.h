//
//  WeXCPGetContractAddressParamModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXCPGetContractAddressParamModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *business;

@end

@interface WeXCPGetContractAddressResModel : WeXBaseNetModal

@property (nonatomic, copy) NSString  * result;


@end
