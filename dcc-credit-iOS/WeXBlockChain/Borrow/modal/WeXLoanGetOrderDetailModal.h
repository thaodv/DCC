//
//  WeXLoanGetOrderDetailModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface  WeXLoanGetOrderDetailParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *chainOrderId;

@end

@interface WeXLoanGetOrderDetailResponseModal_currency : WeXBaseNetModal
@property (nonatomic,copy)NSString *symbol;
@property (nonatomic,copy)NSString *decimal;
@end


@interface WeXLoanGetOrderDetailResponseModal_lender : WeXBaseNetModal
@property (nonatomic,copy)NSString *code;
@property (nonatomic,copy)NSString *logoUrl;
@property (nonatomic,copy)NSString *defaultConfig;
@property (nonatomic,copy)NSString *name;
@end


@interface  WeXLoanGetOrderDetailResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *orderId;
@property (nonatomic,copy)NSString *status;
@property (nonatomic,strong)WeXLoanGetOrderDetailResponseModal_currency *currency;
@property (nonatomic,strong)WeXLoanGetOrderDetailResponseModal_lender *lender;
@property (nonatomic,copy)NSString *amount;
@property (nonatomic,copy)NSString *applyDate;

@property (nonatomic,copy)NSString *durationUnit;
@property (nonatomic,copy)NSString *borrowDuration;
@property (nonatomic,copy)NSString *fee;

@property (nonatomic,copy)NSString *deliverDate;
@property (nonatomic,copy)NSString *repayDate;
@property (nonatomic,copy)NSString *expectRepayDate;
@property (nonatomic,copy)NSString *loanInterest;
@property (nonatomic,copy)NSString *expectLoanInterest;

@property (nonatomic,copy)NSString *receiverAddress;

@property (nonatomic,assign)BOOL allowRepayPermit;
@property (nonatomic,assign)BOOL earlyRepayAvailable;
@end
