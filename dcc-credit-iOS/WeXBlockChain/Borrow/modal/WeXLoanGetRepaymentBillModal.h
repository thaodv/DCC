//
//  WeXLoanGetRepaymentBillModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/5/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface  WeXLoanGetRepaymentBillParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *chainOrderId;

@end

@interface  WeXLoanGetRepaymentBillResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *chainOrderId;
/**待还利息*/
@property (nonatomic,assign)double repaymentInterest;
/**待还本金*/
@property (nonatomic,assign)double repaymentPrincipal;
/**滞纳金*/
@property (nonatomic,assign)double overdueFine;
/**提前还款罚金*/
@property (nonatomic,assign)double penaltyAmount;
/**应还总金额*/
@property (nonatomic,assign)double amount;
/**资产代码*/
@property (nonatomic,copy)NSString *assetCode;
/**地址*/
@property (nonatomic,copy)NSString *repaymentAddress;

/**地址*/
@property (nonatomic,assign)double noPayAmount;

@end
