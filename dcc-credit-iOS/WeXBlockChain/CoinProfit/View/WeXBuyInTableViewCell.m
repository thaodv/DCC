//
//  WeXBuyInTableViewCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBuyInTableViewCell.h"

@interface WeXBuyInTableViewCell ()

@property (nonatomic, weak) UIButton *buyInButton;

@end

@implementation WeXBuyInTableViewCell

- (void)wex_addSubViews {
     
    UIButton *button = [UIButton new];
    [button setTitle:@"认购" forState:UIControlStateNormal];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateDisabled];
    [button setTitleColor:COLOR_THEME_ALL forState:UIControlStateHighlighted];
    [button setBackgroundImage:[WexCommonFunc imageWithColor:ColorWithHex(0x7B40FF)] forState:UIControlStateNormal];
    [button setBackgroundImage:[WexCommonFunc imageWithColor:[UIColor clearColor]] forState:UIControlStateHighlighted];
    [button setBackgroundImage:[WexCommonFunc imageWithColor:[UIColor grayColor]] forState:UIControlStateDisabled];
    button.titleLabel.font = [UIFont systemFontOfSize:15];
    [button addTarget:self action:@selector(buyInEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.contentView addSubview:button];
    self.buyInButton = button;
}

- (void)wex_addConstraints {
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(50).priorityHigh();
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
    }];
    [self.buyInButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.top.mas_equalTo(0);
    }];
}
- (void)setBuyInCellType:(WeXBuyInTableViewCellType)type {
    switch (type) {
        case WeXBuyInTableViewCellTypeBuyIn: {
            [self.buyInButton setEnabled:YES];
            [self.buyInButton setTitle:@"认购" forState:UIControlStateNormal];
        }
            break;
        case WeXBuyInTableViewCellTypeSellOver: {
            [self.buyInButton setEnabled:NO];
            [self.buyInButton setTitle:@"已售罄" forState:UIControlStateNormal];
        }
            break;
        case WeXBuyInTableViewCellTypeEnd: {
            [self.buyInButton setEnabled:NO];
            [self.buyInButton setTitle:@"已结束" forState:UIControlStateNormal];
        }
            break;
            
        default:
            break;
    }
}

- (void)buyInEvent:(UIButton *)sender {
    !self.BuyInEvent ? : self.BuyInEvent();
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
