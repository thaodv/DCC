//
//  WeXPassportManagerRecordCell.m
//  WeXBlockChain
//
//  Created by wcc on 2017/12/4.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPassportManagerRecordCell.h"
#import "WeXPassportManagerRLMModel.h"

@implementation WeXPassportManagerRecordCell

-(void)setModel:(WeXPassportManagerRLMModel *)model
{
    _model = model;
    self.typeLabel.text = model.type;
    self.typeLabel.textColor = ColorWithLabelDescritionBlack;
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
    NSString *dateStr = [formatter stringFromDate:model.date];
    self.dateLabel.text = dateStr;
    self.dateLabel.textColor = ColorWithLabelDescritionBlack;
}



@end
