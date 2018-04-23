//
//  WeXWalletDigitalAddCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXWalletDigitalGetTokenResponseModal_item;
@interface WeXWalletDigitalAddCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *tokenImageView;
@property (weak, nonatomic) IBOutlet UILabel *tokenName;
@property (weak, nonatomic) IBOutlet UILabel *tokenSymbol;
@property (weak, nonatomic) IBOutlet UILabel *tokenAddress;
@property (weak, nonatomic) IBOutlet UIButton *addButton;
@property (weak, nonatomic) IBOutlet UILabel *addLabel;

@property (nonatomic,strong)WeXWalletDigitalGetTokenResponseModal_item *model;

@end
