//
//  WeXLoanQueryOrdersModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface  WeXLoanQueryOrdersParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *number;

@property (nonatomic,copy)NSString *size;
@end

@interface WeXLoanQueryOrdersResponseModal_currency : WeXBaseNetModal
@property (nonatomic,copy)NSString *symbol;
@property (nonatomic,copy)NSString *decimal;
@end


@interface WeXLoanQueryOrdersResponseModal_lender : WeXBaseNetModal
@property (nonatomic,copy)NSString *code;
@property (nonatomic,copy)NSString *logoUrl;
@property (nonatomic,copy)NSString *defaultConfig;
@property (nonatomic,copy)NSString *name;
@end

@protocol WeXLoanQueryOrdersResponseModal_item;
@interface  WeXLoanQueryOrdersResponseModal_item : WeXBaseNetModal
@property (nonatomic,copy)NSString *orderId;
@property (nonatomic,copy)NSString *status;
@property (nonatomic,strong)WeXLoanQueryOrdersResponseModal_currency *currency;
@property (nonatomic,strong)WeXLoanQueryOrdersResponseModal_lender *lender;
@property (nonatomic,copy)NSString *amount;
@property (nonatomic,copy)NSString *applyDate;
@property (nonatomic,copy)NSString *productLogoUrl;
@end

@interface  WeXLoanQueryOrdersResponseModal : WeXBaseNetModal

@property (nonatomic,strong)NSMutableArray<WeXLoanQueryOrdersResponseModal_item> *items;
@property (nonatomic,copy)NSString *totalPages;
@property (nonatomic,copy)NSString *totalElements;

@end
