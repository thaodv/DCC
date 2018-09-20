//
//  WeXLoginManagerTopCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoginManagerTopCell.h"
#import "YLButton.h"
#import "NSObject+WeXMethodAddtion.h"


@interface WeXLoginManagerTopCell ()
@property (nonatomic, weak) UIImageView *rectBackImage;
@property (nonatomic, weak) UILabel *contentLab;
@property (nonatomic, weak) UIButton *scanButton;
@property (nonatomic, weak) YLButton *statusButton;

@end

@implementation WeXLoginManagerTopCell

static CGFloat const kImageRatio = 249 / 96.0;


- (void)wex_addSubViews {
    UIImageView *rectBackImage = [UIImageView new];
    rectBackImage.image = [UIImage imageNamed:@"Rectangle_big_bg"];
    [self.contentView addSubview:rectBackImage];
    self.rectBackImage = rectBackImage;
    BOOL largeIphone = [UIScreen mainScreen].bounds.size.width >= 375;
    UILabel *contentLab = CreateLeftAlignmentLabel(largeIphone ? WexFont(16): WexFont(14), [UIColor whiteColor]);
    contentLab.numberOfLines = 0;
    contentLab.text = @"您可以使用BitExpress账号扫码登陆其他支持我们账号验证体系的网站或者应用";
    contentLab.adjustsFontSizeToFitWidth = true;
    NSMutableAttributedString *attributeStr = [WeXLoginManagerTopCell getAttributeStringWithContent:contentLab.text lineSpacing:15 wordFont:WexFont(14)];
    [attributeStr addAttributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]} range:NSMakeRange(0, contentLab.text.length)];
    if (largeIphone) {
        contentLab.attributedText = attributeStr;
    }
    [self.rectBackImage addSubview:contentLab];
    self.contentLab = contentLab;
    
    UIButton *scanButton = [UIButton new];
    [scanButton setBackgroundImage:[UIImage imageNamed:@"WexScan"] forState:UIControlStateNormal];
    [scanButton addTarget:self action:@selector(scanEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.contentView addSubview:scanButton];
    self.scanButton = scanButton;
    
    YLButton *statusButton = [YLButton new];
    [statusButton setImage:[UIImage imageNamed:@"WexStausEnable"] forState:UIControlStateNormal];
    statusButton.titleLabel.font = WexFont(15.0);
    statusButton.imageRect = CGRectMake(45, 6, 16, 16);
    statusButton.titleRect = CGRectMake(0, 6, 45, 16);
    [statusButton setTitle:@"状态" forState:UIControlStateNormal];
    [statusButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [self.contentView addSubview:statusButton];
    self.statusButton = statusButton;
}

- (void)wex_addConstraints {
    self.rectBackImage.mas_key = @"rectBackImage";
    [self.rectBackImage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(15);
        make.top.mas_equalTo(8);
        make.right.mas_equalTo(-111);
        make.height.equalTo(self.rectBackImage.mas_width).dividedBy(kImageRatio);
    }];
    
    self.rectBackImage.mas_key = @"contentLab";
    [self.contentLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(9);
        make.right.mas_equalTo(-7);
    }];
    
    self.rectBackImage.mas_key = @"scanButton";
    [self.scanButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(13);
        make.right.mas_equalTo(-28);
        make.size.mas_equalTo(CGSizeMake(55, 55));
    }];
    
    self.statusButton.mas_key = @"statusButton";
    [self.statusButton mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.bottom.equalTo(self.rectBackImage.mas_bottom).offset(2);
        make.bottom.mas_equalTo(-8);
        make.centerX.equalTo(self.scanButton);
        make.size.mas_equalTo(CGSizeMake(65, 28));
    }];
}

+ (CGFloat)cellHeight {
    return  (kScreenWidth - 111 - 15 ) / kImageRatio + 10;
}
- (void)setStatus:(NSString *)status {
    
}

- (void)scanEvent:(UIButton *)sender {
    !self.DidClickScan ? : self.DidClickScan();
    WEXNSLOG(@"扫一扫");
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
