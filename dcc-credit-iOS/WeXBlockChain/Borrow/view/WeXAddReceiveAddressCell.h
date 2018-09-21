//
//  WeXAddReceiveAddressCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXAddReceiveAddressCell : UITableViewCell

@end

@interface WeXAddReceiveAddressTextFiedCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UITextField *nameTextField;

@end


@interface WeXAddReceiveAddressTextFiedAndButtonCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UITextField *addressTextField;
@property (weak, nonatomic) IBOutlet UIButton *scanButton;

@end


@interface WeXAddReceiveAddressLabelAndButtonCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIButton *defaultButton;
@property (weak, nonatomic) IBOutlet UILabel *defaultLabel;

@end

