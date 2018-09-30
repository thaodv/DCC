//
//  WeXQueryProductByLenderCodeModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXQueryProductByLenderCodeParamModal : WeXBaseNetModal
@property (nonatomic,copy)NSString *lenderCode;
@end

@interface WeXQueryProductByLenderCodeResponseModal_currency : WeXBaseNetModal
@property (nonatomic,copy)NSString *symbol;
@property (nonatomic,copy)NSString *decimal;
@end

@protocol WeXQueryProductByLenderCodeResponseModal_period;
@interface WeXQueryProductByLenderCodeResponseModal_period : WeXBaseNetModal
@property (nonatomic,copy)NSString *unit;
@property (nonatomic,copy)NSString *value;
@end

@interface WeXQueryProductByLenderCodeResponseModal_lender : WeXBaseNetModal
@property (nonatomic,copy)NSString *code;
@property (nonatomic,copy)NSString *logoUrl;
@property (nonatomic,copy)NSString *defaultConfig;
@property (nonatomic,copy)NSString *name;
@end

@protocol WeXQueryProductByLenderCodeResponseModal_item;
@interface  WeXQueryProductByLenderCodeResponseModal_item : WeXBaseNetModal
@property (nonatomic,copy)NSString *productId;
@property (nonatomic,strong)WeXQueryProductByLenderCodeResponseModal_currency *currency;
@property (nonatomic,copy)NSArray *dccFeeScope;
@property (nonatomic,copy)NSString *productDescription;
@property (nonatomic,copy)NSArray *volumeOptionList;
@property (nonatomic,copy)NSString *loanRate;
@property (nonatomic,strong)NSMutableArray<WeXQueryProductByLenderCodeResponseModal_period> *loanPeriodList;
@property (nonatomic,copy)NSArray *requisiteCertList;
@property (nonatomic,assign)BOOL repayPermit;
@property (nonatomic,copy)NSString *repayAheadRate;
@property (nonatomic,strong)WeXQueryProductByLenderCodeResponseModal_lender *lender;
@property (nonatomic,copy)NSString *repayCyclesNo;
@property (nonatomic,copy)NSString *loanType;
@property (nonatomic,copy)NSString *logoUrl;
@property (nonatomic,copy)NSString *agreementTemplateUrl;
@end


@interface  WeXQueryProductByLenderCodeResponseModal : WeXBaseNetModal

@property (nonatomic,strong)NSMutableArray<WeXQueryProductByLenderCodeResponseModal_item> *resultList;

@end
