//
//  WeXCPUserPotAssetResModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXCPUserPotAssetParamModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *userAddress;


@end

NS_ASSUME_NONNULL_BEGIN

@interface WeXCPUserPotAssetResModel : WeXBaseNetModal

@property (nonatomic, assign) double corpus;

@property (nonatomic, assign) double profit;

@end

NS_ASSUME_NONNULL_END
