//
//  WeXMyBorrowListCell.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyBorrowListCell.h"
#import "WeXLoanQueryOrdersModal.h"

//信贷业务状态
//#define WEX_LOAN_STATUS_CREATED  @"CREATED"//已创建
//#define WEX_LOAN_STATUS_CANCELLED  @"CANCELLED"//已撤销
//#define WEX_LOAN_STATUS_AUDITING  @"AUDITING"//审核中
//#define WEX_LOAN_STATUS_REJECTED  @"REJECTED"//拒绝
//#define WEX_LOAN_STATUS_APPROVED  @"APPROVED"//审核通过
//#define WEX_LOAN_STATUS_FAILURE  @"FAILURE"//放款失败
//#define WEX_LOAN_STATUS_DELIVERIED  @"DELIVERIED"// 已放款
//#define WEX_LOAN_STATUS_REPAID  @"REPAID"//已还款

#define GRAY_COLOR ColorWithRGB(132, 132, 132);
#define PINK_COLOR ColorWithRGB(244, 73, 148);
#define BLUE_COLOR ColorWithRGB(83, 58, 135);

@implementation WeXMyBorrowListCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    self.createStatusLabel.hidden = YES;
    self.refreshImageView.hidden = YES;
    
    _titleLabel.textColor = [UIColor whiteColor];
    _timeLabel.textColor = [UIColor whiteColor];
    _valueLabel.textColor = [UIColor whiteColor];
    _statusLabel.textColor = [UIColor whiteColor];
    _createStatusLabel.textColor = [UIColor whiteColor];
    
     _statusLabel.backgroundColor = [UIColor clearColor];
    _statusLabel.layer.cornerRadius = 2;
    _statusLabel.layer.masksToBounds = YES;
    _statusLabel.layer.borderWidth = 1;
    _statusLabel.layer.borderColor = [UIColor whiteColor].CGColor;
}

-(void)setModel:(WeXLoanQueryOrdersResponseModal_item *)model
{
    _model = model;
    _titleLabel.text = model.orderId;
    _timeLabel.text = [WexCommonFunc formatterTimeStringWithTimeStamp:model.applyDate formatter:@"yyyy-MM-dd"];
    _valueLabel.text = [NSString stringWithFormat:@"%.4f%@",[model.amount floatValue],model.currency.symbol];
    [_logoImageView sd_setImageWithURL:[NSURL URLWithString:model.productLogoUrl]];

    
    _titleLabel.hidden = NO;
    _timeLabel.hidden = NO;
    _valueLabel.hidden = NO;
    _statusLabel.hidden = NO;
    _logoImageView.hidden = NO;
    _createStatusLabel.hidden = YES;
    _refreshImageView.hidden = YES;
    
    //创建
    if ([model.status isEqualToString:WEX_LOAN_STATUS_CREATED]) {
        
        _titleLabel.hidden = YES;
        _timeLabel.hidden = YES;
        _valueLabel.hidden = YES;
        _statusLabel.hidden = YES;
         _logoImageView.hidden = YES;
        _createStatusLabel.hidden = NO;
        _refreshImageView.hidden = NO;
        
        _backImageView.image = [UIImage imageNamed:@"borrow_detail_card_3"];
        _createStatusLabel.text = WeXLocalizedString(@"订单创建中");
        [self benginRefreshWithImageView:_refreshImageView];
    }
    //审核中
    else if ([model.status isEqualToString:WEX_LOAN_STATUS_AUDITING])
    {
        _backImageView.image = [UIImage imageNamed:@"borrow_detail_card_2"];
        _statusLabel.text = WeXLocalizedString(@"审核中");
    }
    //拒绝
    else if ([model.status isEqualToString:WEX_LOAN_STATUS_REJECTED])
    {
        _backImageView.image = [UIImage imageNamed:@"borrow_detail_card_2"];
        _statusLabel.text = WeXLocalizedString(@"审核失败");
    }
    //审核成功
    else if ([model.status isEqualToString:WEX_LOAN_STATUS_APPROVED])
    {
        _backImageView.image = [UIImage imageNamed:@"borrow_detail_card_5"];
        _statusLabel.text = WeXLocalizedString(@"审核成功");
    }
    //放币失败
    else if ([model.status isEqualToString:WEX_LOAN_STATUS_FAILURE])
    {
        _backImageView.image = [UIImage imageNamed:@"borrow_detail_card_2"];
        _statusLabel.text = WeXLocalizedString(@"放币失败");
    }
    //已放款
    else if ([model.status isEqualToString:WEX_LOAN_STATUS_DELIVERED])
    {
        _backImageView.image = [UIImage imageNamed:@"borrow_detail_card_4"];
        _statusLabel.text = WeXLocalizedString(@"已放币");
    }
    //已还款
    else if ([model.status isEqualToString:WEX_LOAN_STATUS_REPAID])
    {
        _backImageView.image = [UIImage imageNamed:@"borrow_detail_card_1"];
        _statusLabel.text = WeXLocalizedString(@"已还币");
    }
   
}

- (void)benginRefreshWithImageView:(UIImageView *)iamgeView{
    
    CABasicAnimation *animtion = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    animtion.toValue = [NSNumber numberWithFloat:M_PI *2];
    animtion.duration = 3;
    animtion.repeatCount = CGFLOAT_MAX;
    animtion.removedOnCompletion = NO;
    animtion.fillMode = kCAFillModeForwards;
    [iamgeView.layer addAnimation:animtion forKey:nil];
    
    
}



@end
