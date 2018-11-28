//
//  WeXDailyTaskItemCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDailyTaskItemCell.h"
#import "WeXTaskListResModel.h"


@interface WeXDailyTaskItemCell ()

@property (nonatomic, strong) UIImageView *iconImageView;
@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UILabel *subTitleLab;
@property (nonatomic, strong) UIImageView *arrowImageView;

@end

static CGFloat const kCellHeight = 66;
static CGFloat const kIconImageW = 19;

@implementation WeXDailyTaskItemCell

- (void)wex_addSubViews {
    [super wex_addSubViews];
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo(kCellHeight).priorityHigh();
    }];
    
    _iconImageView = [UIImageView new];
    [self.contentView addSubview:_iconImageView];
    
    _titleLab = CreateLeftAlignmentLabel(WeXPFFont(16), WexDefault4ATitleColor);
    [self.contentView addSubview:_titleLab];
    
    _subTitleLab = CreateLeftAlignmentLabel(WeXPFFont(14), ColorWithHex(0x7B40FF));
    [self.contentView addSubview:_subTitleLab];
    
    _arrowImageView = [UIImageView new];
    _arrowImageView.image = [UIImage imageNamed:@"garden_arrow"];
    [self.contentView addSubview:_arrowImageView];
}

- (void)wex_addConstraints {
    [super wex_addConstraints];
    [self.iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(20, 20));
    }];
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.iconImageView.mas_right).offset(15);
        make.centerY.equalTo(self.iconImageView);
    }];
    
    [self.subTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.right.equalTo(self.arrowImageView.mas_left).offset(-5);
    }];
}

- (void)setItemSectionModel:(WeXTaskListItemListModel*)sectionModel
                   rowModel:(WeXTaskListItemModel *)itemModel {
    [self setItemModel:itemModel sectionModel:sectionModel];
}

- (void)setItemModel:(WeXTaskListItemModel *)itemModel sectionModel:(WeXTaskListItemListModel*)sectionModel {
    WeXDailyTaskItemCellType type = WeXDailyTaskItemCellDefault;
//  认证任务
    if ([sectionModel.category isEqualToString:@"CERT_TASK"]) {
        if ([itemModel.status isEqualToString:FULFILLED]) {
            type = WeXDailyTaskItemCellWithGrren;
        } else {
            type = WeXDailyTaskItemCellWithArrow;
        }
    }
//  备份任务
    else if ([sectionModel.category isEqualToString:@"BACKUP_TASK"]) {
        if ([itemModel.status isEqualToString:FULFILLED]) {
            type = WeXDailyTaskItemCellWithGrren;
        } else {
            type = WeXDailyTaskItemCellWithArrow;
        }
    }
    else if ([itemModel.code isEqualToString:INVITE_FRIEND]
             || [itemModel.code isEqualToString:INVITE_FRIEND_CREATE_WALLET]) {
        type = WeXDailyTaskItemCellWithArrow;
    }
    else if ([itemModel.status isEqualToString:@"FULFILLED"]) {
        type = WeXDailyTaskItemCellWithGrren;
    }
    

//    if ([itemModel.code isEqualToString:@"ATTENDENCE"]) {
//        type = WeXDailyTaskItemCellDefault;
//    }
//    //邀请好友助力
//    else if ([itemModel.code isEqualToString:@"INVITE_FRIEND"]) {
//        type = WeXDailyTaskItemCellWithArrow;
//    }
//    else if ([itemModel.code isEqualToString:@"LOAN_BIT"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"REPAY_BIT"]) {
//
//    }
////  绑定微信
//    else if ([itemModel.code isEqualToString:@"BIND_WECHAT"]) {
//        type = WeXDailyTaskItemCellWithGrren;
//    }
////  创建钱包
//    else if ([itemModel.code isEqualToString:@"CREATE_WALLET"]) {
//        type = WeXDailyTaskItemCellWithGrren;
//    }
//    else if ([itemModel.code isEqualToString:@"OPEN_CLOUD_STORE"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"ID"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"BANK_CARD"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"COMMUNICATION_LOG"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"TN_COMMUNICATION_LOG"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"BACKUP_ID"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"BACKUP_BANK_CARD"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"BACKUP_COMMUNICATION_LOG"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"BACKUP_TN_COMMUNICATION_LOG"]) {
//
//    }
//    else if ([itemModel.code isEqualToString:@"BACKUP_WALLET"]) {
//
//    }
////  邀请好友创建钱包
//    else if ([itemModel.code isEqualToString:@"INVITE_FRIEND_CREATE_WALLET"]) {
//        type = WeXDailyTaskItemCellWithArrow;
//    }
//    if ([itemModel.status isEqualToString:@"FULFILLED"]) {
//        type = WeXDailyTaskItemCellWithGrren;
//    }
    [self setItemModel:itemModel cellType:type];
}


- (void)setItemModel:(WeXTaskListItemModel *)itemModel cellType:(WeXDailyTaskItemCellType)type {
    self.iconImageView.mas_key = @"iconImageView";
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    NSString *imageURL = [itemModel.img stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
#pragma clang diagnostic pop
    [self.iconImageView sd_setImageWithURL:[NSURL URLWithString:imageURL] placeholderImage:[UIImage imageNamed:@"garden_default"] completed:^(UIImage *  image, NSError * error, SDImageCacheType cacheType, NSURL *  imageURL) {
        if (image) {
            CGFloat kImageRatio = image.size.width / image.size.height;
            [self.iconImageView mas_updateConstraints:^(MASConstraintMaker *make) {
                make.left.mas_equalTo(14);
                make.centerY.mas_equalTo(0);
                make.size.mas_equalTo(CGSizeMake(kIconImageW, kIconImageW/kImageRatio));
            }];
        }
    }];
    [self.titleLab    setText:itemModel.name];
    switch (type) {
        case WeXDailyTaskItemCellDefault: {
            [self.arrowImageView mas_updateConstraints:^(MASConstraintMaker *make) {
                make.right.mas_equalTo(-10);
                make.centerY.mas_equalTo(0);
                make.size.mas_equalTo(CGSizeZero);
            }];
            [self.subTitleLab setTextColor:ColorWithHex(0x7B40FF)];
            [self.subTitleLab setText:itemModel.bonus];
        }
            break;
        case WeXDailyTaskItemCellWithGrren: {
            [self.arrowImageView mas_updateConstraints:^(MASConstraintMaker *make) {
                make.right.mas_equalTo(-10);
                make.centerY.mas_equalTo(0);
                make.size.mas_equalTo(CGSizeZero);
            }];
            [self.subTitleLab setTextColor:ColorWithHex(0x7ED321)];
            [self.subTitleLab setText:@"已完成"];
        }
            break;
        case WeXDailyTaskItemCellWithArrow: {
            [self.arrowImageView mas_updateConstraints:^(MASConstraintMaker *make) {
                make.right.mas_equalTo(-10);
                make.centerY.mas_equalTo(0);
                make.size.mas_equalTo(CGSizeMake(9, 17));
            }];
            [self.subTitleLab setTextColor:ColorWithHex(0x7B40FF)];
            [self.subTitleLab setText:itemModel.bonus];
        }
            break;
            
        default: {
            [self.arrowImageView mas_updateConstraints:^(MASConstraintMaker *make) {
                make.right.mas_equalTo(-10);
                make.centerY.mas_equalTo(0);
                make.size.mas_equalTo(CGSizeZero);
            }];
            [self.subTitleLab setTextColor:ColorWithHex(0x7B40FF)];
        }
            break;
    }
}


- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
