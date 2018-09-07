//
//  WeXPassportCardCell.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportCardCell.h"

@implementation WeXPassportCardCell

@end

@implementation WeXPassportCardPassCell

@end

@implementation WeXPassportCardIDCell

@end

@implementation WeXPassportCardDigitalCell

- (void)setNodeNetworkDelayModel:(WeXNetworkCheckModel *)model {
    switch (model.nodeCheckState) {
        case WexNetworkCheckStateGood: {
            [self.networkImageView setImage:[UIImage imageNamed:@"Oval-Good"]];
        }
            
            break;
        case WexNetworkCheckStateCommon: {
            [self.networkImageView setImage:[UIImage imageNamed:@"Oval-Common"]];

        }
            break;
        
        default: {
            [self.networkImageView setImage:[UIImage imageNamed:@"Oval-Bad"]];

        }
            break;
    }
}



@end

@implementation WeXPassportCardChooseCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.activityButton.layer.cornerRadius = 29;
    self.activityButton.layer.masksToBounds = YES;
    self.activityButton.backgroundColor = ColorWithRGB(240, 74, 148);
    self.activityLabel.adjustsFontSizeToFitWidth = YES;
    
    self.creditButton.layer.cornerRadius = 29;
    self.creditButton.layer.masksToBounds = YES;
    self.creditButton.backgroundColor = ColorWithRGB(82, 129, 238);
    self.creditLabel.adjustsFontSizeToFitWidth = YES;
    
    self.borrowButton.layer.cornerRadius = 29;
    self.borrowButton.layer.masksToBounds = YES;
    self.borrowButton.backgroundColor = ColorWithRGB(141, 113, 248);
    self.loanLabel.adjustsFontSizeToFitWidth = YES;
    
    self.inviteButton.layer.cornerRadius = 29;
    self.inviteButton.layer.masksToBounds = YES;
    self.inviteButton.backgroundColor = ColorWithRGB(251, 197, 66);
    self.reportLabel.adjustsFontSizeToFitWidth = YES;
}

@end

@implementation WeXPassportCardChoose2Cell

@end
