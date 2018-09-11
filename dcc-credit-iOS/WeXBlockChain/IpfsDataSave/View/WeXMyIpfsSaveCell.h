//
//  WeXMyIpfsSaveCell.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WeXMyIpfsSaveModel.h"
typedef void(^AIIpfsSaveBtnClickBlock) (WeXMyIpfsSaveModel *ipfsModel);

@interface WeXMyIpfsSaveCell : UITableViewCell

@property (nonatomic,strong)UIImageView *selectNameImg;
@property (nonatomic,strong)UILabel *identifyTitle;
@property (nonatomic,strong)UILabel *statusDescribeStr;
@property (nonatomic,strong)UILabel *statusStr;
@property (nonatomic,strong)UILabel *fileSizeStr;
@property (nonatomic,strong)UIView *lineView;
@property (nonatomic,strong)UILabel *fileUploadStatusLabel;
@property (nonatomic,strong)UIProgressView *statusProgressView;
@property (nonatomic,strong)UIButton *cloudAddressBtn;

@property (nonatomic,strong)WeXMyIpfsSaveModel *model;
@property (nonatomic,copy)AIIpfsSaveBtnClickBlock ipfsModelBlock;

@end
