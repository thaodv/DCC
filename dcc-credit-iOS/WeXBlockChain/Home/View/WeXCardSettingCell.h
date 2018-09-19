//
//  WeXCardSettingCell.h
//  WeXBlockChain
//
//  Created by wcc on 2017/12/6.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXCardSettingCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *desLabel;
//20180905 ipfs节点图标
//@property (nonatomic,strong)UIImageView *loadImgView;
@property (strong, nonatomic) IBOutlet UIImageView *loadImgView;

@end
