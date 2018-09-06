//
//  WeXCoinProfitOnlyTextCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCoinProfitOnlyTextCell.h"
#import "WeXCPSaleInfoResModel.h"

@interface WeXCoinProfitOnlyTextCell ()

@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *subTextLab;

@end

@implementation WeXCoinProfitOnlyTextCell

- (void)wex_addSubViews {
    [super wex_addSubViews];
    UILabel *titleLab = CreateLeftAlignmentLabel(WexFont(16), ColorWithHex(0x4A4A4A));
    [self.contentView addSubview:titleLab];
    self.titleLab = titleLab;
        
    UILabel *subTextLab = CreateRightAlignmentLabel(WexFont(14), ColorWithHex(0x404040));
    [self.contentView addSubview:subTextLab];
    self.subTextLab = subTextLab;
    
}
- (void)wex_addConstraints {
    [super wex_addConstraints];
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(55).priorityHigh();
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
    }];
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.left.mas_equalTo(10);
    }];
    [self.subTextLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-10);
        make.centerY.mas_equalTo(0);
        make.left.mas_greaterThanOrEqualTo(self.titleLab.mas_right);
    }];
}
- (void)setTitle:(NSString *)title
         subText:(NSString *)subText
        cellType:(WeXCoinProfitOnlyTextCellType)type {
    [self.titleLab setText:title];
    [self.subTextLab setText:subText];
    switch (type) {
        case WeXCoinProfitOnlyTextCellTypeOnlyTitle: {
            self.titleLab.font = WexFont(16);
            [self.subTextLab setHidden:true];
            [self.titleLab setTextAlignment:NSTextAlignmentLeft];
            [self.titleLab setTextColor:WexDefault4ATitleColor];
            [self.titleLab mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.centerY.mas_equalTo(0);
                make.left.mas_equalTo(10);
            }];
        }
            
            break;
        case WeXCoinProfitOnlyTextCellTypeDefaultSubtextColor: {
            [self.subTextLab setHidden:false];
            self.titleLab.font = WexFont(16);
            [self.titleLab setTextAlignment:NSTextAlignmentLeft];
            [self.titleLab setTextColor:WexDefault4ATitleColor];
            [self.subTextLab setTextColor:WexDefault4ATitleColor];
            [self.titleLab mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.centerY.mas_equalTo(0);
                make.left.mas_equalTo(10);
            }];
        }
            break;
        case WeXCoinProfitOnlyTextCellTypeRedSubTextColor: {
            [self.subTextLab setHidden:false];
            self.titleLab.font = WexFont(16);
            [self.titleLab setTextAlignment:NSTextAlignmentLeft];
            [self.titleLab setTextColor:WexDefault4ATitleColor];
            [self.subTextLab setTextColor:WexRedTextColor];
            [self.titleLab mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.centerY.mas_equalTo(0);
                make.left.mas_equalTo(10);
            }];
        }
            break;
        case WeXCoinProfitOnlyTextCellTypeOnlyCenterTitle: {
            self.titleLab.font = WexFont(16);
            [self.subTextLab setHidden:true];
            [self.titleLab setTextColor:WexDefault4ATitleColor];
            [self.titleLab setTextAlignment:NSTextAlignmentCenter];
            [self.titleLab mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.centerY.mas_equalTo(0);
                make.left.mas_equalTo(10);
                make.right.mas_equalTo(-15);
            }];
        }
            break;
        case WeXCoinProfitOnlyTextCellTypeOnlyBoldTitle: {
            self.titleLab.font = WexBFont(16);
            [self.subTextLab setHidden:true];
            [self.titleLab setTextAlignment:NSTextAlignmentLeft];
            [self.titleLab setTextColor:WexDefault4ATitleColor];
            [self.titleLab mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.centerY.mas_equalTo(0);
                make.left.mas_equalTo(10);
            }];
        }
            break;
        default:
            break;
    }
}

- (void)setTitle:(NSString *)title
        highText:(NSString *)highText {
    [self setTitle:title subText:nil cellType:WeXCoinProfitOnlyTextCellTypeOnlyTitle];
    NSRange range = [title rangeOfString:highText];
    if (range.location) {
        NSMutableAttributedString *attribute = [[NSMutableAttributedString alloc] initWithString:title];
        NSDictionary *attributes = @{NSForegroundColorAttributeName:WexRedTextColor,
                                     NSFontAttributeName:self.titleLab.font,
                                     };
        [attribute addAttributes:attributes range:NSMakeRange(range.location, highText.length)];
        self.titleLab.attributedText = [attribute copy];
    }
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end

@interface WeXCoinProfitAutoHeightTextCell ()

@property (nonatomic, weak) UILabel *contentLab;

@end

@implementation WeXCoinProfitAutoHeightTextCell
- (void)wex_addSubViews {
    [super wex_addSubViews];
    UILabel *titleLab = CreateLeftAlignmentLabel(WexFont(14), ColorWithHex(0x4A4A4A));
    titleLab.numberOfLines = 0;
    [self.contentView addSubview:titleLab];
    self.contentLab = titleLab;

}

- (void)wex_addConstraints {
    [super wex_addConstraints];
    [self.contentLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(15);
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.bottom.mas_equalTo(-15);
    }];
}

- (void)setTextContent:(NSString *)text {
    [self.contentLab setText:text];
}

- (void)setTest {
    [self setTextContent:@"8月10日晚间，证监会一则处罚公告却引爆了娱乐圈，黄晓明疑卷入18亿股票操纵案。黄晓明工作室随即发声明，否认股票操纵大案。然而据财新8月13日晚报道，该股票操纵案中的自然人账户之一确实为黄晓明名下账户。《每日经济新闻》记者在梳理资料时发现，黄晓明名下公司有48家，涉及科技、餐饮、文化、服装、商贸、投资等多个方面，其中最多的当属投资类公司，多达14家。"];
}

- (void)setSaleInfoResModel:(WeXCPSaleInfoResModel *)resModel {
    [self setTextContent:resModel.presentation];
}


@end
