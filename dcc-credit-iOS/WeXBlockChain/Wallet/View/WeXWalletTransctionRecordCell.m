//
//  WeXWalletTransctionRecordCell.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletTransctionRecordCell.h"
#import "WeXWalletEtherscanGetRecordModal.h"
#import "WeXWalletDigitalGetTokenModal.h"

@implementation WeXWalletTransctionRecordCell

- (void)configWithRecodModel:(WeXWalletEtherscanGetRecordResponseModal_item *)recordmodel tokenModel:(WeXWalletDigitalGetTokenResponseModal_item *)tokenModel{
    _model = recordmodel;
    self.addressLabel.text = [WexCommonFunc formatterStringWithContractAddress:recordmodel.hashStr];
    if (recordmodel.isPending) {
        self.timeLabel.text = @"待上链";
         NSString *valueStr = [WexCommonFunc formatterStringWithContractBalance:recordmodel.value decimals:[tokenModel.decimals integerValue]];
        self.valueLabel.text = [NSString stringWithFormat:@"-%@%@",valueStr,tokenModel.symbol];;
        self.refreshImageView.hidden = NO;
        [self benginRefresh];
    }
    else
    {
         NSString *valueStr = [WexCommonFunc formatterStringWithContractBalance:recordmodel.value decimals:[tokenModel.decimals integerValue]];
        if ([[WexCommonFunc getFromAddress] isEqualToString: recordmodel.from]) {
            self.valueLabel.text = [NSString stringWithFormat:@"-%@%@",valueStr,tokenModel.symbol];
        }
        else
        {
            self.valueLabel.text = [NSString stringWithFormat:@"+%@%@",valueStr,tokenModel.symbol];
        }
        
        
       self.timeLabel.text = [WexCommonFunc formatterTimeStringWithTimeStamp:recordmodel.timeStamp];
        self.refreshImageView.hidden = YES;
    }
    
    self.addressLabel.textColor = COLOR_LABEL_DESCRIPTION;
    self.timeLabel.textColor = COLOR_LABEL_DESCRIPTION;
    self.valueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    self.typeLabel.textColor = COLOR_LABEL_DESCRIPTION;
}

- (void)benginRefresh{
    
    CABasicAnimation *animtion = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    animtion.toValue = [NSNumber numberWithFloat:M_PI *2];
    animtion.duration = 3;
    animtion.repeatCount = CGFLOAT_MAX;
    animtion.removedOnCompletion = NO;
    animtion.fillMode = kCAFillModeForwards;
    [self.refreshImageView.layer addAnimation:animtion forKey:nil];
    
    
}



@end
