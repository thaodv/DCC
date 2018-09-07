//
//  WeXGetMemberIdModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface  WeXGetMemberIdParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *nonce;
@property (nonatomic,copy)NSString *address;

@end

@interface  WeXGetMemberIdResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *memberId;
@property (nonatomic,copy)NSString *inviteCode;
@end

