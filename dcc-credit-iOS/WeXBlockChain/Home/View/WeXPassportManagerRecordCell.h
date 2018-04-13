//
//  WeXPassportManagerRecordCell.h
//  WeXBlockChain
//
//  Created by wcc on 2017/12/4.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXPassportManagerRLMModel;
@interface WeXPassportManagerRecordCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *typeLabel;
@property (weak, nonatomic) IBOutlet UILabel *dateLabel;

@property (nonatomic,strong)WeXPassportManagerRLMModel *model;

@end
