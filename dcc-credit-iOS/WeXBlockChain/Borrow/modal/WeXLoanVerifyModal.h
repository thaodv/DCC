//
//  WeXLoanVerifyModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface  WeXLoanVerifyParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *orderId;

@property (nonatomic,copy)NSString *loanProductId;

@property (nonatomic,copy)NSString *borrowName;

@property (nonatomic,copy)NSString *borrowAmount;

@property (nonatomic,copy)NSString *borrowDuration;

@property (nonatomic,copy)NSString *durationUnit;

@property (nonatomic,copy)NSString *certNo;

@property (nonatomic,copy)NSString *mobile;

@property (nonatomic,copy)NSString *bankCard;

@property (nonatomic,copy)NSString *bankMobile;

@property (nonatomic,copy)NSString *applyDate;

@property (nonatomic,copy)NSString *personalPhoto;

@property (nonatomic,copy)NSString *frontPhoto;

@property (nonatomic,copy)NSString *backPhoto;

@property (nonatomic,copy)NSString *version;

@property (nonatomic, copy) NSString *communicationLog;

@end

@interface  WeXLoanVerifyResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
