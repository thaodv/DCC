//
//  WeXUploadPubKeyModal.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/17.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXUploadPubKeyParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *ticket;

@property (nonatomic,copy)NSString *signMessage;

@property (nonatomic,copy)NSString *code;

@end

@interface  WeXUploadPubKeyResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
