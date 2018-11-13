//
//  WeXSunBalanceDetailModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXSunBalanceDetailModel : WeXBaseNetModal

@end


@interface WeXSunBalanceDetailParamModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *idNum;

@end


@interface WeXSunBalanceDetailResModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *idNum;
@property (nonatomic, copy) NSString *amount;
@property (nonatomic, copy) NSString *direction;
@property (nonatomic, copy) NSString *memo;
@property (nonatomic, copy) NSString *parentId;
@property (nonatomic, copy) NSString *parentType;
@property (nonatomic, copy) NSString *playerId;
@property (nonatomic, copy) NSString *status;
@end


