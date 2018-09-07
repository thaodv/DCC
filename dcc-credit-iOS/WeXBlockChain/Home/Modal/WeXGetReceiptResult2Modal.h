//
//  WeXGetReceiptResult2Modal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXGetReceiptResult2ParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *txHash;

@end

@interface  WeXGetReceiptResult2ResponseModal : WeXBaseNetModal

@property (nonatomic,assign)BOOL hasReceipt;
@property (nonatomic,assign)BOOL approximatelySuccess;

@end
