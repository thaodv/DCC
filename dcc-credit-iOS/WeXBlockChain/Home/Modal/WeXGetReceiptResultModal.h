//
//  WeXGetReceiptResultModal.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXGetReceiptResultParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *txHash;

@end

@interface  WeXGetReceiptResultResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
