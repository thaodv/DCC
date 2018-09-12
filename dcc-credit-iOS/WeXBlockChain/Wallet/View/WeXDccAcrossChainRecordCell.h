//
//  WeXDccAcrossChainRecordCell.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXWalletGetAcrossChainTransferRecordResponseModal_item;
@interface WeXDccAcrossChainRecordCell : UITableViewCell

@property (nonatomic,strong)UILabel *leftSymbolLabel;
@property (nonatomic,strong)UILabel *leftNameLabel;
@property (nonatomic,strong)UILabel *rightSymbolLabel;
@property (nonatomic,strong)UILabel *rightNameLabel;

@property (nonatomic,strong)UILabel *valueLabel;

@property (nonatomic,strong)UILabel *statusLabel;
@property (nonatomic,strong)UILabel *timeLabel;

@property (nonatomic,strong)WeXWalletGetAcrossChainTransferRecordResponseModal_item *model;

@end
