//
//  WeXAgentMarketModel.h
//  WeXBlockChain
//
//  Created by wh on 2018/7/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXAgentMarketModel : WeXBaseNetModal

@property (nonatomic,copy)NSString *coinTypes;

@end

@protocol WeXAgentMarketResponseModel_item;
@interface WeXAgentMarketResponseModel_item :WeXBaseNetModal

@property (nonatomic,copy)NSString *fullName;
@property (nonatomic,copy)NSString *symbol;
@property (nonatomic,copy)NSString *price;
@property (nonatomic,copy)NSString *price_change_24;
@property (nonatomic,copy)NSString *volume_24;
@property (nonatomic,copy)NSString *percent_change_1h;
@property (nonatomic,copy)NSString *percent_change_24h;
@property (nonatomic,copy)NSString *percent_change_7d;
@property (nonatomic,copy)NSString *timeStamp;
@end

@protocol WeXAgentMarketResponseModel_item;
@interface  WeXAgentMarketResponseModel : WeXBaseNetModal
@property (nonatomic,strong)NSMutableArray<WeXAgentMarketResponseModel_item*><WeXAgentMarketResponseModel_item> *data;
@end
