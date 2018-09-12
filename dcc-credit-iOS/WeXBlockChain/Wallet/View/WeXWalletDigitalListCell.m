//
//  WeXWalletDigitalListCell.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalListCell.h"
#import "WeXWalletDigitalGetTokenModal.h"
#import  "UIImageView+WebCache.h"

@implementation WeXWalletDigitalListCell

-(void)setModel:(WeXWalletDigitalGetTokenResponseModal_item *)model
{
//    NSLog(@"model=%@",model);
    
    _model = model;
    self.tokenNameLabel.text = model.name;
    self.tokenSymbolLabel.text = model.symbol;
    
    
    self.tokenImageView.layer.cornerRadius = 6;
    self.tokenImageView.layer.masksToBounds = YES;
//    if([model.symbol isEqualToString:@"ETH"])
//    {
//        self.tokenImageView.image = [UIImage imageNamed:@"digital_ethereum"];
//    }
//    else
//    {
       [self.tokenImageView sd_setImageWithURL: [NSURL URLWithString:model.iconUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
//    }
    
    
    self.balanceLabel.textColor = COLOR_THEME_ALL;
    if (model.balance) {
        self.balanceLabel.text = model.balance;
    }
    else
    {
        self.balanceLabel.text = @"--";
    }
    
    self.assetBackImageView.layer.cornerRadius = 15;
    self.assetBackImageView.layer.masksToBounds = YES;
    self.assetBackImageView.backgroundColor = ColorWithRGB(246, 246, 247);
    
    self.assetLabel.textColor = [UIColor lightGrayColor];
    NSString *asset = @"--";
    if (model.balance &&model.price) {
//        NSLog(@"model.balance) = %@",model.balance);
//        NSLog(@"model.price = %@",model.price);
        asset = [NSString stringWithFormat:@"≈¥%.4f",[model.balance floatValue]*[model.price floatValue]];
    }
    
    self.assetLabel.text = asset;
   
}

@end
