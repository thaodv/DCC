//
//  WeXBorrowLoginModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXBorrowLoginParamModal : WeXBaseNetModal
@property (nonatomic,copy)NSString *nonce;
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *sign;
@property (nonatomic,copy)NSString *username;
@property (nonatomic,copy)NSString *password;
@end


@interface  WeXBorrowLoginResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
