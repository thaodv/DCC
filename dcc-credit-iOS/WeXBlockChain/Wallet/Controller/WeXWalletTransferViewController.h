//
//  WeXWalletTransferViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"
#import "WeXWalletDigitalGetTokenModal.h"
#import "WeXWalletEtherscanGetRecordModal.h"

typedef NS_ENUM(NSInteger,WeXWalletTransferType) {
    WeXWalletTransferTypeNormal,
    WeXWalletTransferTypeEdit,
};

@interface WeXWalletTransferViewController : WeXBaseViewController
//代币名称
@property (nonatomic,strong)WeXWalletDigitalGetTokenResponseModal_item *tokenModel;

@property (nonatomic,copy)NSString *feeRate;
@property (nonatomic,assign)BOOL isPrivateChain;
//收款地址
@property (nonatomic,copy)NSString *addressStr;

@property (nonatomic,assign)WeXWalletTransferType transferType;
//包含金额,(收款地址)
@property (nonatomic,strong)WeXWalletEtherscanGetRecordResponseModal_item *recordModel;

//2018.8.7 直接使用原始的金额数据 (针对还款流程的修改)
@property (nonatomic, assign) BOOL useOriginalAmount;
//已经发出转账申请
@property (nonatomic,copy) void (^DidTransferAmount)(void);






@end
