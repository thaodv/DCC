//
//  WeXSunBlanceModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXSunBlanceParamModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *number;
@property (nonatomic, copy) NSString *size;


@end

@interface WeXSunBlanceModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *idNum;
@property (nonatomic, copy) NSString *amount;
@property (nonatomic, copy) NSString *direction;
@property (nonatomic, copy) NSString *memo;
@property (nonatomic, copy) NSString *parentId;
@property (nonatomic, copy) NSString *parentType;
@property (nonatomic, copy) NSString *playerId;
@property (nonatomic, copy) NSString *status;
@property (nonatomic, copy) NSString *createdTime;
@property (nonatomic, copy) NSString *lastUpdatedTime;


@end

@protocol  WeXSunBlanceModel;
@interface WeXSunBlanceResModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *totalElements;
@property (nonatomic, copy) NSString *totalPages;
@property (nonatomic, strong) NSArray <WeXSunBlanceModel *>  <WeXSunBlanceModel> *items;

@end


@protocol  WeXSunBlanceResModel;
@interface WeXSunBlanceResultModel : WeXBaseNetModal
@property (nonatomic, strong) WeXSunBlanceResModel <WeXSunBlanceResModel>*result;

@end
