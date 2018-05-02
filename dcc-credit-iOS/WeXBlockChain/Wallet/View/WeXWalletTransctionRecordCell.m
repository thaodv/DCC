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
    if ([recordmodel.timeStamp isEqualToString:@"待上链"]) {
        self.timeLabel.text = recordmodel.timeStamp;
         NSString *valueStr = [NSString stringWithFormat:@"-%@%@",recordmodel.value,tokenModel.symbol];
        self.valueLabel.text = valueStr;
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

//    self.typeLabel.text = @"转账";
    
    self.addressLabel.textColor = ColorWithLabelWeakBlack;
    self.timeLabel.textColor = ColorWithLabelWeakBlack;
    self.valueLabel.textColor = ColorWithLabelDescritionBlack;
    self.typeLabel.textColor = ColorWithLabelWeakBlack;
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
