//
//  WeXUploadAuthenticationModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/2/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXUploadAuthenticationParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *ticket;

@property (nonatomic,copy)NSString *signMessage;

@property (nonatomic,copy)NSString *code;

@property (nonatomic,copy)NSString *business;


@end

@interface  WeXUploadAuthenticationResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;
@end
