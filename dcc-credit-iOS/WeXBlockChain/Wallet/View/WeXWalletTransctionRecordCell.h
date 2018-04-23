//
//  WeXWalletTransctionRecordCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXWalletEtherscanGetRecordResponseModal_item;
@class WeXWalletDigitalGetTokenResponseModal_item;
@interface WeXWalletTransctionRecordCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *valueLabel;
@property (weak, nonatomic) IBOutlet UILabel *typeLabel;
@property (weak, nonatomic) IBOutlet UIImageView *refreshImageView;

@property (nonatomic,strong)WeXWalletEtherscanGetRecordResponseModal_item *model;

- (void)configWithRecodModel:(WeXWalletEtherscanGetRecordResponseModal_item *)recordmodel tokenModel:(WeXWalletDigitalGetTokenResponseModal_item *)tokenModel;

@end
