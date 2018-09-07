//
//  WeXRegisterMemberModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface  WeXRegisterMemberParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *nonce;
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *loginName;
@property (nonatomic,copy)NSString *inviteCode;

@end

@interface  WeXRegisterMemberResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
