//
//  WeXPassportDescriptionCell.h
//  WeXBlockChain
//
//  Created by wcc on 2017/12/8.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^RefreshCellBlock)(UITableViewCell *cell);

@class WeXPassportDescriptionModal;

@interface WeXPassportDescriptionCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *arrowBtn;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *titleLabelHeightConst;

@property (strong, nonatomic)WeXPassportDescriptionModal *model;

@property (nonatomic,copy)RefreshCellBlock refreshCellBlock;


+ (CGFloat)heightWithModel:(WeXPassportDescriptionModal *)model;

- (void)arrowBtnClick;

@end
