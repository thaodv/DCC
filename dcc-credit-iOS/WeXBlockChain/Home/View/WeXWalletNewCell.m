//
//  WeXWalletNewCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletNewCell.h"
#import "WeXWalletDigitalGetTokenModal.h"

@interface WeXWalletNewCell ()

@property (weak, nonatomic)  UIImageView *tokenImageView;
@property (weak, nonatomic)  UILabel *tokenNameLabel;
@property (weak, nonatomic)  UILabel *tokenSymbolLabel;
@property (weak, nonatomic)  UILabel *balanceLabel;
@property (weak, nonatomic)  UILabel *assetLabel;
@property (weak, nonatomic)  UIImageView *assetBackImageView;

@end

@implementation WeXWalletNewCell

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)wex_addSubViews {
    [super wex_addSubViews];
    
    [self.bottomLine setBackgroundColor:ColorWithHexAndAlpha(0xe9e9e9, 0.4)];
    
    UIImageView *tokenImageView = [UIImageView new];
    [self.contentView addSubview:tokenImageView];
    self.tokenImageView = tokenImageView;
    
    UILabel *tokenNameLabel = CreateLeftAlignmentLabel(WexFont(13),  [UIColor lightGrayColor]);
    [self.contentView addSubview:tokenNameLabel];
    self.tokenNameLabel = tokenNameLabel;
    
    UILabel *tokenSymbolLabel = CreateLeftAlignmentLabel(WexFont(15.0f),[UIColor blackColor]);
    [self.contentView addSubview:tokenSymbolLabel];
    self.tokenSymbolLabel = tokenSymbolLabel;
    
    UIImageView *assetBackImageView = [UIImageView new];
    assetBackImageView.layer.cornerRadius = 15;
    assetBackImageView.layer.masksToBounds = YES;
    assetBackImageView.backgroundColor = ColorWithRGB(246, 246, 247);
    [self.contentView addSubview:assetBackImageView];
    self.assetBackImageView = assetBackImageView;
    
    UILabel *balanceLabel = CreateRightAlignmentLabel(WexFont(14.0), COLOR_THEME_ALL);
    [self.assetBackImageView addSubview:balanceLabel];
    self.balanceLabel = balanceLabel;
    
    UILabel *assetLabel = CreateRightAlignmentLabel(WexFont(13.0), [UIColor lightGrayColor]);
    [self.contentView addSubview:assetLabel];
    self.assetLabel =assetLabel;
    
}
- (void)wex_addConstraints {
    [super wex_addConstraints];
    [self.tokenImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(20);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(45, 45));
    }];
    
    [self.tokenSymbolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(75);
        make.top.equalTo(self.tokenImageView);
    }];
    
    [self.tokenNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(75);
        make.top.equalTo(self.tokenSymbolLabel.mas_bottom).offset(10);
    }];
    
    [self.assetBackImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-10);
        make.height.mas_equalTo(30);
        make.width.mas_equalTo(120);
        make.centerY.equalTo(self.tokenSymbolLabel.mas_centerY);
    }];
    
    [self.balanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-10);
        make.centerY.mas_equalTo(0);
    }];
    
    [self.assetLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-20);
        make.centerY.equalTo(self.tokenNameLabel.mas_centerY);
    }];
    
    [self setBottomLineLeft:75 right:-20];
}


- (void)setModel:(WeXWalletDigitalGetTokenResponseModal_item *)model {
    self.tokenNameLabel.text = model.name;
    self.tokenSymbolLabel.text = model.symbol;
    self.tokenImageView.layer.cornerRadius = 6;
    self.tokenImageView.layer.masksToBounds = YES;
    [self.tokenImageView sd_setImageWithURL: [NSURL URLWithString:model.iconUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    self.balanceLabel.textColor = COLOR_THEME_ALL;
    if (model.balance) {
        self.balanceLabel.text = model.balance;
    }
    else {
        self.balanceLabel.text = @"--";
    }
    self.assetBackImageView.layer.cornerRadius = 15;
    self.assetBackImageView.layer.masksToBounds = YES;
    self.assetBackImageView.backgroundColor = ColorWithRGB(246, 246, 247);
    
    self.assetLabel.textColor = [UIColor lightGrayColor];
    NSString *asset = @"--";
    if (model.balance &&model.price) {
        asset = [NSString stringWithFormat:@"≈¥%.4f",[model.balance floatValue]*[model.price floatValue]];
    }
    self.assetLabel.text = asset;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

}

@end
