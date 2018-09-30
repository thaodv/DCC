//
//  WeXBorrowReceiveAddressModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Realm/Realm.h>

@interface WeXBorrowReceiveAddressModal : RLMObject

@property NSString *name;

@property NSString *address;

@property BOOL isDefault;

@end
