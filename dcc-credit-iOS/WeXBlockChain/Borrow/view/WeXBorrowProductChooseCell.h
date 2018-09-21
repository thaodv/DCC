//
//  WeXBorrowProductChooseCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WeXQueryProductByLenderCodeResponseModal_item;

@interface WeXBorrowProductChooseCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *backView;
@property (weak, nonatomic) IBOutlet UILabel *periodLabel;
@property (weak, nonatomic) IBOutlet UILabel *valueLabel;
@property (weak, nonatomic) IBOutlet UIImageView *logoImageView;

@property (nonatomic,strong)WeXQueryProductByLenderCodeResponseModal_item *model;

@end

@interface WeXBorrowProductChooseEmailCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *backView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *emailLabel;


@end
