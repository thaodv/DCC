//
//  WeXMyIpfsNodeCell.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyIpfsNodeCell.h"

@implementation WeXMyIpfsNodeCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self reSetConstant];
    }
    return self;
}

- (void)reSetConstant{
    
    if (_selectNameImg) {
        return;
    }
    self.selectNameImg = [UIImageView new];
    //    self.selectNameImg.image = [UIImage imageNamed:@"2B2"];
    [self.contentView addSubview:_selectNameImg];
    [_selectNameImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.contentView).offset(16);
        make.size.mas_equalTo(CGSizeMake(20, 20));
        make.top.equalTo(self.contentView).offset(10);
    }];
    
    if (_nameTitleLabel) {
        return;
    }
    self.nameTitleLabel = [UILabel new];
    self.nameTitleLabel.text = @"默认节点";
    self.nameTitleLabel.font = [UIFont systemFontOfSize:16];
    self.nameTitleLabel.textColor = ColorWithHex(0x4A4A4A);
    //    self.titleLabel.frame = CGRectMake(10, 10, 200, 30);
    [self.contentView addSubview:_nameTitleLabel];
    [_nameTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(10);
        make.leading.equalTo(self.selectNameImg.mas_trailing).offset(10);
        make.trailing.equalTo(self.contentView).offset(-40);
        make.height.mas_equalTo(20);
    }];
    
    if (_statusDescribeLabel) {
        return;
    }
    self.statusDescribeLabel = [UILabel new];
    //    self.identifyTitle.text = @"实名认证数据";
    self.statusDescribeLabel.font = [UIFont systemFontOfSize:14];
    self.statusDescribeLabel.textColor = ColorWithHex(0x9B9B9B);
    //    self.titleLabel.frame = CGRectMake(10, 10, 200, 30);
    [self.contentView addSubview:_statusDescribeLabel];
    [_statusDescribeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.nameTitleLabel.mas_bottom).offset(2);
        make.leading.equalTo(self.nameTitleLabel.mas_leading).offset(0);
        make.trailing.equalTo(self.nameTitleLabel.mas_trailing).offset(0);
        make.height.mas_equalTo(16);
    }];
    
    if (_defaultImgBtn) {
        return;
    }
    self.defaultImgBtn = [UIButton new];
    [self.defaultImgBtn setImage: [UIImage imageNamed:@"information"] forState:UIControlStateNormal ];
    [self.defaultImgBtn addTarget:self action:@selector(twoTapGesClick) forControlEvents:UIControlEventTouchUpInside];
    [self.contentView addSubview:_defaultImgBtn];
    [_defaultImgBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self.contentView).offset(-16);
        make.size.mas_equalTo(CGSizeMake(30, 30));
        make.top.equalTo(self.contentView).offset(10);
    }];
    
    if (_lineView) {
        return;
    }
    _lineView = [UIView new];
    _lineView.backgroundColor = ColorWithHex(0xEFEFEF);
    [self.contentView addSubview:_lineView];
    [_lineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.statusDescribeLabel.mas_bottom).offset(13);
        make.left.mas_equalTo(16);
        make.right.mas_equalTo(-16);
        make.height.mas_equalTo(1);
    }];
    
}

-(void)setModel:(WeXIpfsNodeModel *)model{
    
    if (model) {
        if (model.isSelected) {
            self.selectNameImg.image = [UIImage imageNamed:@"2B"];
        }else{
            self.selectNameImg.image = [UIImage imageNamed:@""];
        }

        if (model.isDefault) {
            self.defaultImgBtn.hidden = YES;
        }else{
            self.defaultImgBtn.hidden = NO;
        }
        self.nameTitleLabel.text = model.nameTitle;
        self.statusDescribeLabel.text = model.describeStr;
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)twoTapGesClick{
    if (self.goSettingNoneVcBlock) {
        self.goSettingNoneVcBlock();
    }
}

@end
