//
//  WeXCPSaleInfoResModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXCPSaleInfoResModel : WeXBaseNetModal

//起始日期
@property (nonatomic, copy) NSString *startTime;
//结束日期
@property (nonatomic, copy) NSString *endTime;
//起息日
@property (nonatomic, copy) NSString *incomeTime;
//结束日
@property (nonatomic, copy) NSString *closeTime;
@property (nonatomic, copy) NSString *annualRate;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *period;
@property (nonatomic, copy) NSString *profitMethod;
@property (nonatomic, copy) NSString *presentation;
@property (nonatomic, copy) NSString <Optional> *presentationFormat;




@end
