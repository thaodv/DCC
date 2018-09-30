//
//  WeXBorrowProductChooseCell.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowProductChooseCell.h"
#import "WeXQueryProductByLenderCodeModal.h"

@implementation WeXBorrowProductChooseCell

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated
{
    [super setHighlighted:highlighted animated:animated];
    if (highlighted) {
         self.backView.layer.borderColor =COLOR_THEME_ALL.CGColor;
    }
    else {
         self.backView.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    }
    
}

-(void)awakeFromNib
{
    [super awakeFromNib];
    self.backView.layer.cornerRadius = 12;
    self.backView.layer.masksToBounds = YES;
    self.backView.layer.borderWidth = 1;
    self.backView.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    
    
    _periodLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _periodLabel.font = [UIFont systemFontOfSize:15];
    
    _valueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _valueLabel.font = [UIFont systemFontOfSize:15];
    
}

-(void)setModel:(WeXQueryProductByLenderCodeResponseModal_item *)model
{
    _model = model;
    NSMutableArray *periodArray = model.loanPeriodList;
    if (periodArray.count >= 3) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel1 = periodArray[0];
        WeXQueryProductByLenderCodeResponseModal_period *periodModel3 = periodArray[2];
        NSMutableString *periodStr = [NSMutableString stringWithFormat:@"%@-%@%@",periodModel1.value,periodModel3.value,[WexCommonFunc transferChinesePeriod:periodModel3.unit]];
        _periodLabel.text = [NSString stringWithFormat:@"%@:%@",WeXLocalizedString(@"周期"),periodStr];
    }
    NSArray *valueArray = model.volumeOptionList;
    if (valueArray.count >= 3) {
        _valueLabel.text = [NSString stringWithFormat:@"%@:%@-%@%@",WeXLocalizedString(@"额度"),valueArray[0],valueArray[2],model.currency.symbol];

    }
    [_logoImageView sd_setImageWithURL:[NSURL URLWithString:model.logoUrl]];
}

@end

@implementation WeXBorrowProductChooseEmailCell

-(void)awakeFromNib
{
    [super awakeFromNib];
    self.backView.layer.cornerRadius = 12;
    self.backView.layer.masksToBounds = YES;
    self.backView.layer.borderWidth = 1;
    self.backView.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    
    _titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _titleLabel.font = [UIFont systemFontOfSize:16];
    
    _emailLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _emailLabel.font = [UIFont systemFontOfSize:15];
}

@end
