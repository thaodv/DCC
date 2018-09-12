//
//  WeXDccAcrossChainRecordDetailView.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>




typedef void(^HashClickBlock)(NSInteger index);
@class WeXWalletGetDccAcrossChainRecordDetailResponseModal;

@interface WeXDccAcrossChainRecordDetailView : UIView

@property (nonatomic,copy)HashClickBlock HashClickBlock;
+ (instancetype)recordDetailViewWithModal:(WeXWalletGetDccAcrossChainRecordDetailResponseModal *)model;

@end
