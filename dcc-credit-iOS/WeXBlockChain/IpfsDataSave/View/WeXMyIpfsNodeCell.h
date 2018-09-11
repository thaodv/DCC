//
//  WeXMyIpfsNodeCell.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WeXIpfsNodeModel.h"
typedef void(^GoSettingIpfsNoneVcBlock)(void);

@interface WeXMyIpfsNodeCell : UITableViewCell

@property (nonatomic,strong)UIImageView *selectNameImg;
@property (nonatomic,strong)UILabel *nameTitleLabel;
@property (nonatomic,strong)UILabel *statusDescribeLabel;
@property (nonatomic,strong)UIButton *defaultImgBtn;
@property (nonatomic,strong)UIView *lineView;
@property (nonatomic,strong)WeXIpfsNodeModel *model;

@property (nonatomic,copy)GoSettingIpfsNoneVcBlock goSettingNoneVcBlock;

@end
