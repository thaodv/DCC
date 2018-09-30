//
//  WeXLoanGetLoanInterestModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface  WeXLoanGetLoanInterestParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *productId;
@property (nonatomic,copy)NSString *amount;
@property (nonatomic,copy)NSString *loanPeriodValue;

@end

@interface  WeXLoanGetLoanInterestResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
