//
//  WeXFindAwardModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXFindAwardModel : WeXBaseNetModal

@end


@interface WeXFindAwardParamModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *ownerId;

@end

@interface WeXFindAwardResDetailModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *idNum;
@property (nonatomic, copy) NSString *roundId;
@property (nonatomic, copy) NSString *ownerId;
@property (nonatomic, copy) NSString *assetCode;
@property (nonatomic, copy) NSString *preservedAmount;
@property (nonatomic, copy) NSString *sharedAmount;
@property (nonatomic, copy) NSString *createdTime;

@end

@protocol WeXFindAwardResDetailModel;

@interface WeXFindAwardResModel : WeXBaseNetModal

@property (nonatomic, strong) NSArray <WeXFindAwardResDetailModel *><WeXFindAwardResDetailModel> *result;

@end


