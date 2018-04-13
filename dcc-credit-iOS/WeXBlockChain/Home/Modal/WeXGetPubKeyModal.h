//
//  WeXGetPubKeyModal.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/27.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXGetPubKeyParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *address;


@end

@interface  WeXGetPubKeyResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
