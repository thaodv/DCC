//
//  WeXWalletDigitalAddCell.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalAddCell.h"
#import "WeXWalletDigitalGetTokenModal.h"
#import  "UIImageView+WebCache.h"
#import "WeXDigitalAssetRLMModel.h"

@implementation WeXWalletDigitalAddCell

-(void)setModel:(WeXWalletDigitalGetTokenResponseModal_item *)model
{
    _model = model;
    self.tokenSymbol.text = model.name;
    self.tokenName.text = model.symbol;
    self.tokenAddress.text = [WexCommonFunc formatterStringWithContractAddress:model.contractAddress];
    [self.tokenImageView sd_setImageWithURL: [NSURL URLWithString:model.iconUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    
    RLMResults <WeXDigitalAssetRLMModel *>*results = [WeXDigitalAssetRLMModel allObjects];
    NSMutableArray *cacheSymbolArray = [NSMutableArray array];
    for (WeXDigitalAssetRLMModel *rlmModel in results) {
        [cacheSymbolArray addObject:rlmModel.symbol];
    }
    
    if ([cacheSymbolArray containsObject:model.symbol]) {
        self.addButton.hidden = YES;
        self.addLabel.hidden = NO;
    }
    else
    {
        self.addButton.hidden = NO;
        self.addLabel.hidden = YES;
    }
    
    [self.addButton addTarget:self action:@selector(addButtonClick) forControlEvents:UIControlEventTouchUpInside];
}

- (void)addButtonClick
{
    self.addButton.hidden = YES;
    
    self.addLabel.hidden = NO;
    
    [self saveDigitalAsset];
    
}

- (void)saveDigitalAsset{
    WeXDigitalAssetRLMModel *rlmModel = [[WeXDigitalAssetRLMModel alloc] init];
    rlmModel.name = _model.name;
    rlmModel.symbol = _model.symbol;
    rlmModel.iconUrl = _model.iconUrl;
    rlmModel.decimals = _model.decimals;
    rlmModel.contractAddress = _model.contractAddress;
    
    RLMRealm *realm = [RLMRealm defaultRealm];
    [realm beginWriteTransaction];
    [realm addObject:rlmModel];
    [realm commitWriteTransaction];
}



@end
