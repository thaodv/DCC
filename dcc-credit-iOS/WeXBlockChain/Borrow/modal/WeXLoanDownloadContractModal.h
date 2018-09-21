//
//  WeXLoanDownloadContractModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface  WeXLoanDownloadContractParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *chainOrderId;

@end

@interface  WeXLoanDownloadContractResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
