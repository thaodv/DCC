//
//  WeXWalletEtherscanGetRecordDetailModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXWalletEtherscanGetRecordDetailParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *module;
@property (nonatomic,copy)NSString *action;
@property (nonatomic,copy)NSString *txhash;
@end

@interface  WeXWalletEtherscanGetRecordDetailResponseModal : WeXBaseNetModal

@end

