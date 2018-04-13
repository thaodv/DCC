//
//  WeXDeletePubKeyModal.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/27.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXDeletePubKeyParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *ticket;

@property (nonatomic,copy)NSString *signMessage;

@property (nonatomic,copy)NSString *code;

@end

@interface  WeXDeletePubKeyResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;
@end
