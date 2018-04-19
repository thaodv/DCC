//
//  WeXWalletDigitalGetQuoteModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXWalletDigitalGetQuoteParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *varietyCodes;

@end

@protocol WeXWalletDigitalGetQuoteResponseModal_item;

@interface  WeXWalletDigitalGetQuoteResponseModal_item : WeXBaseNetModal

@property (nonatomic,copy)NSString *sourceCode;
@property (nonatomic,copy)NSString *varietyCode;
@property (nonatomic,copy)NSString *close;
@property (nonatomic,assign)CGFloat high;
@property (nonatomic,assign)CGFloat low;
@property (nonatomic,assign)CGFloat volume;
@property (nonatomic,assign)CGFloat price;
@property (nonatomic,copy)NSString *sourceName;
@end

@interface  WeXWalletDigitalGetQuoteResponseModal : WeXBaseNetModal

@property (nonatomic,strong)NSArray<WeXWalletDigitalGetQuoteResponseModal_item > *data;

@end
