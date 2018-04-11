//
//  WeXAuthenVerifyModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/2/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetImageModal.h"


@interface WeXAuthenVerifyParamModal : WeXBaseNetImageModal

@property (nonatomic,copy)NSString *orderId;
@property (nonatomic,copy)NSString *realName;
@property (nonatomic,copy)NSString *certNo;


@end

@interface  WeXAuthenVerifyResponseModal : WeXBaseNetModal
@property (nonatomic,copy)NSString *orderId;
@end
