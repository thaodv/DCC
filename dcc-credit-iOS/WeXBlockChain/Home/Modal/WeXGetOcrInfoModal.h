//
//  WeXGetOcrInfoModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/2/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetImageModal.h"

@interface WeXGetOcrInfoParamModal : WeXBaseNetImageModal

@end

@protocol WeXGetOcrInfoResponseModal_item;
@interface  WeXGetOcrInfoResponseModal_item : WeXBaseNetImageModal
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *authority;
@property (nonatomic,copy)NSString *day;
@property (nonatomic,copy)NSString *month;
@property (nonatomic,copy)NSString *year;
@property (nonatomic,copy)NSString *name;

@property (nonatomic,copy)NSString *nation;
@property (nonatomic,copy)NSString *number;
@property (nonatomic,copy)NSString *sex;
@property (nonatomic,copy)NSString *timelimit;

@end

@interface  WeXGetOcrInfoResponseModal : WeXBaseNetImageModal

@property (nonatomic,strong)WeXGetOcrInfoResponseModal_item *idCardInfo;

@property (nonatomic,copy)NSString *side;

@end
