//
//  WeXMyBorrowListCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXLoanQueryOrdersResponseModal_item;
@interface WeXMyBorrowListCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *backView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *valueLabel;
@property (weak, nonatomic) IBOutlet UIImageView *arrowImageViw;
@property (weak, nonatomic) IBOutlet UILabel *createStatusLabel;
@property (weak, nonatomic) IBOutlet UIImageView *refreshImageView;
@property (weak, nonatomic) IBOutlet UIImageView *logoImageView;
@property (weak, nonatomic) IBOutlet UIImageView *backImageView;

@property (nonatomic,strong)WeXLoanQueryOrdersResponseModal_item *model;

@end
