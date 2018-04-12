//
//  WeXAuthorizeLoginRecordCell.h
//  WeXBlockChain
//
//  Created by wcc on 2017/12/4.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXAuthorizeLoginRecordRLMModel;
@interface WeXAuthorizeLoginRecordCell : UITableViewCell

@property (nonatomic,strong)WeXAuthorizeLoginRecordRLMModel *model;
@property (weak, nonatomic) IBOutlet UIImageView *loginImageView;
@property (weak, nonatomic) IBOutlet UILabel *loginNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *dateLabel;


@end
