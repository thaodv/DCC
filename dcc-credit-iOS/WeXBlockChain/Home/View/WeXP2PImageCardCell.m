//
//  WeXP2PImageCardCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/6/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXP2PImageCardCell.h"
#import <Masonry.h>


#define kCardRatio  (345.0 / 134.0)

@interface WeXP2PImageCardCell  ()

@property (nonatomic, weak) UIImageView *cardImageView;

@end

@implementation WeXP2PImageCardCell

- (void)wex_addSubViews {
    UIImageView *cardImageView = [[UIImageView alloc]init];
    cardImageView.userInteractionEnabled = YES;
    [self.contentView addSubview:cardImageView];
    self.cardImageView = cardImageView;
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(clickCardEvent:)];
    [self.cardImageView addGestureRecognizer:tapGesture];
}

- (void)wex_addConstraints {
    [self.cardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(10);
        make.left.equalTo(self.contentView).offset(15);
        make.right.equalTo(self.contentView).offset(-15);
        make.bottom.equalTo(self.contentView).offset(-15);
    }];
}

- (void)clickCardEvent:(UITapGestureRecognizer *)gesture {
    !self.DidClickP2PLoanCard ? : self.DidClickP2PLoanCard();
}

- (void)setCardBackgroundImage:(NSString *)imageName {
    [self.cardImageView setImage:[UIImage imageNamed:imageName]];
}

+ (CGFloat)p2pImageCellHeight {
    return  (kScreenWidth - 20 ) / kCardRatio + 20;
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
