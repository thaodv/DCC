//
//  WeXFindVariousTaskCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"
#import "WeXBaseView.h"

typedef NS_ENUM(NSInteger,WexFindTaskCellClickType) {
    WexFindTaskCellClickGarden = 0, //神秘花园
    WexFindTaskCellClickTask   = 1, //日常任务
    WexFindTaskCellClickInvite = 2, //邀请好友
    WexFindTaskCellClickSunshine = 3, //点击阳光
};

@interface WeXFindVariousTaskCell : WeXBaseTableViewCell

@property (nonatomic,copy) void (^ClickTaskCell)(WexFindTaskCellClickType type);

- (void)setSunValue:(NSString *)value;

@end


@interface WexFindGardenView : WeXBaseView
@property (nonatomic, strong) UIImageView *backImageView;
@property (nonatomic, strong) UIImageView *sunImageView;
@property (nonatomic, strong) UILabel *subLab;
@property (nonatomic, strong) UILabel *describeLab;
@property (nonatomic, strong) UILabel *arrowLab;

@end


@interface WexFindTaskView : WeXBaseView
@property (nonatomic, strong) UIImageView *backImageView;
@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UILabel *subTitleLab;
@property (nonatomic, strong) UIImageView *imageView;

@end



@interface WexFindInviteView : WeXBaseView
@property (nonatomic, strong) UIImageView *backImageView;
@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UILabel *subTitleLab;
@property (nonatomic, strong) UIImageView *imageView;

@end

