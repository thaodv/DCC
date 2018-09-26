//
//  WeXCPBuyFlowTableViewCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPBuyFlowTableViewCell.h"
#import "WeXCPFlowStatusView.h"
#import "WeXCPSaleInfoResModel.h"
#import "NSString+WexTool.h"

@interface WeXCPBuyFlowTableViewCell ()

@property (nonatomic, weak) WeXCPFlowStatusView *statusView;
@property (nonatomic, weak) UILabel *subTextLab;

@end

@implementation WeXCPBuyFlowTableViewCell

- (void)wex_addSubViews {
    WeXCPFlowStatusView *statusView = [WeXCPFlowStatusView new];
    [self.contentView addSubview:statusView];
    [statusView setHidden:true];
    self.statusView = statusView;
    
    UILabel *subTextLab = CreateCenterAlignmentLabel(WexFont(14), WexDefault4ATitleColor);
    [self.contentView addSubview:subTextLab];
    self.subTextLab = subTextLab;
    
    
}
- (void)wex_addConstraints {
    [self.statusView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(20).priorityHigh();
        make.left.right.mas_equalTo(0);
        make.height.mas_equalTo(110);
    }];
    [self.subTextLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.statusView.mas_bottom).offset(15);
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.bottom.mas_equalTo(-20);
    }];
}

- (void)setTest {
    [self.statusView setBuyTitle:@"申购日" buyDay:@"今天" startTitle:@"起息日" startDay:@"2018-8-15" endTitle:@"到期日" endDay:@"2018-9-13"];
    [self.statusView setFlowStatusViewType:WeXCPFlowStatusViewTypeStart];
    [self.subTextLab setText:@"到期日如为节假日,则在节假日后的第一个工作日到期"];
}
- (void)setSaleInfoModel:(WeXCPSaleInfoResModel *)resModel {
    NSString *startDay = [resModel.incomeTime yearToDayTimeString];
    NSString *endDay   = [resModel.endTime    yearToDayTimeString];
    [self.statusView setHidden:NO];
    [self.statusView setBuyTitle:@"认购开始" buyDay:@"今天" startTitle:@"起息日" startDay:startDay endTitle:@"到期日" endDay:endDay];
    [self.statusView setFlowStatusViewType:WeXCPFlowStatusViewTypeEnd];
    [self.subTextLab setText:@"到期日如为节假日,则在节假日后的第一个工作日到期"];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
