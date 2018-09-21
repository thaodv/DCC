//
//  WeXBorrowConfirmCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXBorrowConfirmCell : UITableViewCell

@end

@interface WeXBorrowConfirmMoneyCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *moneyFirstButton;
@property (weak, nonatomic) IBOutlet UIButton *moneySecondButton;
@property (weak, nonatomic) IBOutlet UIButton *moneyThirdButton;
@property (weak, nonatomic) IBOutlet UIButton *otherMoneyButton;
@property (weak, nonatomic) IBOutlet UITextField *otherMoneyTextField;

@end

@interface WeXBorrowConfirmTimeCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *periodFirstButton;
@property (weak, nonatomic) IBOutlet UIButton *periodSecondButton;
@property (weak, nonatomic) IBOutlet UIButton *periodThirdButton;

@end

@interface WeXBorrowConfirmFeeCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *contentFeeView;

@end

@interface WeXBorrowConfirmAddressCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;

@end

@interface WeXBorrowConfirmConditionCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *contentLabel;

@end
