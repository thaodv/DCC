//
//  WeXWalletDigitalListCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXWalletDigitalGetTokenResponseModal_item;

@interface WeXWalletDigitalListCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIImageView *tokenImageView;
@property (weak, nonatomic) IBOutlet UILabel *tokenNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *tokenSymbolLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UILabel *assetLabel;
@property (weak, nonatomic) IBOutlet UIImageView *assetBackImageView;

@property (nonatomic,strong)WeXWalletDigitalGetTokenResponseModal_item *model;

@end
