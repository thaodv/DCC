//
//  WeXFindVariousTaskCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXFindVariousTaskCell.h"
#import "WeXLocalCacheManager.h"

@interface WeXFindVariousTaskCell ()
@property (nonatomic, strong) WexFindGardenView *gardenView;
@property (nonatomic, strong) WexFindTaskView *taskView;
@property (nonatomic, strong) WexFindInviteView *inviteView;

@end

@implementation WeXFindVariousTaskCell

- (void)wex_addSubViews {
    _gardenView = [WexFindGardenView new];
    _gardenView.subLab.userInteractionEnabled = true;
    _gardenView.sunImageView.userInteractionEnabled = true;
    UITapGestureRecognizer *tapSunshineGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapSunshine:)];
    [_gardenView.subLab addGestureRecognizer:tapSunshineGesture];
    
    UITapGestureRecognizer *tapSunGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapSunshine:)];
    [_gardenView.sunImageView addGestureRecognizer:tapSunGesture];
    
    [self.contentView addSubview:_gardenView];
    UITapGestureRecognizer *taskGardenGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clickEvent:)];
    [_gardenView.backImageView addGestureRecognizer:taskGardenGesture];
    
    
    _taskView = [WexFindTaskView new];
    [self.contentView addSubview:_taskView];
    UITapGestureRecognizer *taskTapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clickEvent:)];
    [_taskView.backImageView addGestureRecognizer:taskTapGesture];
    
    _inviteView = [WexFindInviteView new];
    [self.contentView addSubview:_inviteView];
    UITapGestureRecognizer *inviteTapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clickEvent:)];
    [_inviteView.backImageView addGestureRecognizer:inviteTapGesture];
}


- (void)clickEvent:(UITapGestureRecognizer *)gesture {
    UIEvent *event = [[UIEvent alloc] init];
    CGPoint location  = [gesture locationInView:gesture.view];
    UIView *touchView = [gesture.view hitTest:location withEvent:event];
    UIView *touchSuperView = [touchView superview];
    if ([touchSuperView isKindOfClass:[WexFindGardenView class]]) {
        !self.ClickTaskCell ? : self.ClickTaskCell(WexFindTaskCellClickGarden);
    }
    else if ([touchSuperView isKindOfClass:[WexFindTaskView class]]) {
        !self.ClickTaskCell ? : self.ClickTaskCell(WexFindTaskCellClickTask);
    }
    else if ([touchSuperView isKindOfClass:[WexFindInviteView class]]) {
        !self.ClickTaskCell ? : self.ClickTaskCell(WexFindTaskCellClickInvite);
    }
}
- (void)tapSunshine:(UITapGestureRecognizer *)gesture {
    !self.ClickTaskCell ? : self.ClickTaskCell(WexFindTaskCellClickSunshine);
}


- (void)wex_addConstraints {
    [_gardenView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(14).priorityHigh();
        make.left.mas_equalTo(14);
        make.size.mas_equalTo(CGSizeMake((kScreenWidth - 14 * 2 - 10 )/2.0, 133));
        make.bottom.mas_equalTo(-14);
    }];
    
    [_taskView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_gardenView);
        make.left.equalTo(_gardenView.mas_right).offset(10);
        make.size.mas_equalTo(CGSizeMake((kScreenWidth - 14 * 2 - 10 )/2.0, 61));
    }];
    
    [_inviteView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(_gardenView).offset(0);
        make.left.equalTo(_taskView);
        make.size.mas_equalTo(CGSizeMake((kScreenWidth - 14 * 2 - 10 )/2.0, 61));
    }];
}
- (void)setSunValue:(NSString *)value {
    [_gardenView.subLab setText:value];
    [_gardenView.describeLab setText:@"进入神秘花园"];
    [_gardenView.arrowLab setText:@">"];
    
    [_taskView.titleLab setText:@"日常任务"];
    [_taskView.subTitleLab setText:@"做任务赚阳光"];
    
    [_inviteView.titleLab setText:@"邀请好友"];
    [_inviteView.subTitleLab setText:@"+10阳光 / 人"];
}


- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end


@interface WexFindGardenView ()

@end

@implementation WexFindGardenView

- (void)wex_loadViews {
    _backImageView = [[UIImageView alloc] init];
    _backImageView.userInteractionEnabled = true;
    _backImageView.image = [WeXLocalCacheManager getFindLocalImageWithType:WexFindTaskTypeMisteryGarden];
    [self addSubview:_backImageView];
    
    _sunImageView = [UIImageView new];
    _sunImageView.image = [UIImage imageNamed:@"Sun"];
    [_backImageView addSubview:_sunImageView];
    
    _subLab = CreateCenterAlignmentLabel(WexFont(18), [UIColor whiteColor]);
    [_backImageView addSubview:_subLab];
    
    _describeLab = CreateCenterAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [_backImageView addSubview:_describeLab];
    
    _arrowLab = CreateRightAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [_backImageView addSubview:_arrowLab];
}

- (void)wex_layoutConstraints {
    [_backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(0).priorityHigh();
        make.right.bottom.left.mas_equalTo(0);
    }];
    [_sunImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(14);
        make.size.mas_equalTo(CGSizeMake(38 , 38 ));
        make.centerX.mas_equalTo(0);
    }];
    [_subLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_sunImageView.mas_bottom).offset(10);
        make.centerX.mas_equalTo(0);
    }];
    [_describeLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(-12);
        make.centerX.mas_equalTo(0);
    }];
    [_arrowLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-14);
        make.bottom.equalTo(_describeLab);
    }];
}


@end


@interface WexFindTaskView ()

@end

@implementation WexFindTaskView

- (void)wex_loadViews {
    _backImageView = [UIImageView new];
    _backImageView.userInteractionEnabled = true;
    _backImageView.image = [WeXLocalCacheManager getFindLocalImageWithType:WexFindTaskTypeDailyTask];
    [self addSubview:_backImageView];
    
    _titleLab = CreateLeftAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [_backImageView addSubview:_titleLab];
    
    _subTitleLab = CreateLeftAlignmentLabel(WexFont(12), [UIColor whiteColor]);
    [_backImageView addSubview:_subTitleLab];
    
    _imageView = [UIImageView new];
    _imageView.image = [UIImage imageNamed:@"white_star"];
    [_backImageView addSubview:_imageView];
}

- (void)wex_layoutConstraints {
    [_backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.right.bottom.left.mas_equalTo(0);
    }];
    [_titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(14);
    }];
    [_subTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.bottom.mas_equalTo(-14);
    }];
    [_imageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-14);
        make.top.mas_equalTo(14);
        make.size.mas_equalTo(CGSizeMake(21 * kDeviceRatio, 19 * kDeviceRatio));
    }];
}


@end


@interface WexFindInviteView ()

@end

@implementation WexFindInviteView

- (void)wex_loadViews {
    _backImageView = [UIImageView new];
    _backImageView.userInteractionEnabled = true;
    _backImageView.image = [WeXLocalCacheManager getFindLocalImageWithType:WexFindTaskTypeInviteFriend];
    [self addSubview:_backImageView];
    
    _titleLab = CreateLeftAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [_backImageView addSubview:_titleLab];
    
    _subTitleLab = CreateLeftAlignmentLabel(WexFont(12), [UIColor whiteColor]);
    [_backImageView addSubview:_subTitleLab];
    
    _imageView = [UIImageView new];
    _imageView.image = [UIImage imageNamed:@"white_person"];
    [_backImageView addSubview:_imageView];
}

- (void)wex_layoutConstraints {
    [_backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.right.bottom.left.mas_equalTo(0);
    }];
    [_titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(14);
    }];
    [_subTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.bottom.mas_equalTo(-14);
    }];
    [_imageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-14);
        make.bottom.mas_equalTo(-14);
        make.size.mas_equalTo(CGSizeMake(33 * kDeviceRatio, 21 * kDeviceRatio));
    }];
}

@end
