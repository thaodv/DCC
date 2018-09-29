//
//  WeXBaseTableViewCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/6/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@interface  WeXBaseTableViewCell ()


@end

@implementation WeXBaseTableViewCell

- (void)awakeFromNib {
    [self wex_addSubViews];
    [self wex_addConstraints];
    [self setSelectionStyle:UITableViewCellSelectionStyleNone];
    [super awakeFromNib];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self setSelectionStyle:UITableViewCellSelectionStyleNone];
        [self wex_addSubViews];
        [self wex_addConstraints];
    }
    return self;
}


- (void)wex_addSubViews {
    UIImageView *bottomLineV = [UIImageView new];
    bottomLineV.backgroundColor = ColorWithHex(0XEFEFEF);
    [self.contentView addSubview:bottomLineV];
    self.bottomLine = bottomLineV;
//    NSAssert(false, @"子类重写该方法添加相应控件");
}

- (void)wex_addConstraints {
    [self.bottomLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.bottom.mas_equalTo(-1);
        make.height.mas_equalTo(1.0);
    }];
//    NSAssert(false, @"子类重写该方法添加相应控件的约束");
}
- (void)setBottomLineLeft:(CGFloat)left
                    right:(CGFloat)right {
    [self.bottomLine mas_updateConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(left);
        make.right.mas_equalTo(right);
        make.bottom.mas_equalTo(-1);
        make.height.mas_equalTo(1.0);
    }];
}

- (void)setBottomLineHidden:(BOOL)bottomLineHidden {
    [self.bottomLine setHidden:bottomLineHidden];
}

- (void)setTest {
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
