//
//  WeXWalletTransferCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXWalletTransferCell : UITableViewCell

@end

@interface WeXWalletTransferNormalCell : WeXWalletTransferCell
@property (weak, nonatomic) IBOutlet UITextField *contentTextField;

@end

@interface WeXWalletTransferWithButtonCell : WeXWalletTransferCell
@property (weak, nonatomic) IBOutlet UITextField *contentTextField;
@property (weak, nonatomic) IBOutlet UIButton *scanButton;

@end


@interface WeXWalletTransferWithLabelCell : WeXWalletTransferCell
@property (weak, nonatomic) IBOutlet UITextField *contentTextField;

@end

@interface WeXWalletTransferWithTwoLabelCell : WeXWalletTransferCell
@property (weak, nonatomic) IBOutlet UILabel *costLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

@end
