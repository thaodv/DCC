//
//  WeXOpenResModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXOpenResResultModel : WeXBaseNetModal
//1:隐藏,2:显示
@property (nonatomic, copy) NSString *memo;
@property (nonatomic, copy) NSString *version;

@end


@protocol WeXOpenResResultModel;

@interface WeXOpenResModel : WeXBaseNetModal

@property (nonatomic, strong) WeXOpenResResultModel <WeXOpenResResultModel>*result;

@end
