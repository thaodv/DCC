//
//  WeXAuthorizeLoginRecordCell.m
//  WeXBlockChain
//
//  Created by wcc on 2017/12/4.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXAuthorizeLoginRecordCell.h"
#import "WeXAuthorizeLoginRecordRLMModel.h"

@implementation WeXAuthorizeLoginRecordCell

-(void)setModel:(WeXAuthorizeLoginRecordRLMModel *)model
{
    _model = model;
    
    self.loginImageView.image = [UIImage imageNamed:@"noNameSmall"];
    
    self.loginNameLabel.text = model.appName;
    self.loginNameLabel.textColor = ColorWithLabelDescritionBlack;
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
    NSString *dateStr = [formatter stringFromDate:model.date];
    self.dateLabel.text = dateStr;
    self.dateLabel.textColor = ColorWithLabelDescritionBlack;
}

@end
