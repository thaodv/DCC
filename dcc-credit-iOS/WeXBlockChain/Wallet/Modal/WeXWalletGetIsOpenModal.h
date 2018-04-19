//
//  WeXWalletGetIsOpenModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/2/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXWalletGetIsOpenParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *version;
//QUANXIANG  CHAIN
@property (nonatomic,copy)NSString *appName;
@end



@interface  WeXWalletGetIsOpenResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *data;


@end

