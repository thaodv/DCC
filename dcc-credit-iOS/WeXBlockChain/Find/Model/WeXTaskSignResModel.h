//
//  WeXTaskSignResModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXTaskSignResListModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *idNum;
@property (nonatomic, copy) NSString *incrementValue;
@property (nonatomic, copy) NSString *orderId;
@property (nonatomic, copy) NSString *playerId;
@property (nonatomic, copy) NSString *time;
@property (nonatomic, copy) NSString *weekStartTime;

@end

@protocol WeXTaskSignResListModel;

@interface WeXTaskSignResModel : WeXBaseNetModal
@property (nonatomic, strong) NSArray <WeXTaskSignResListModel *> <WeXTaskSignResListModel>*result;

@end




