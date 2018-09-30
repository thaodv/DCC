//
//  WeXBorrowApplyModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface  WeXBorrowApplyParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *ticket;

@property (nonatomic,copy)NSString *signMessage;

@property (nonatomic,copy)NSString *code;


@end

@interface  WeXBorrowApplyResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
