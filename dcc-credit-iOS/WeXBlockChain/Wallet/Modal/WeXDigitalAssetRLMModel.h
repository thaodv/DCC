//
//  WeXDigitalAssetRLMModel.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Realm/Realm.h>

//realm保存本地数字资产的模型
@interface WeXDigitalAssetRLMModel : RLMObject

@property NSString *name;

@property NSString *symbol;

@property NSString *contractAddress;

@property NSString *iconUrl;

@property NSString *decimals;


@end
