//
//  WeXMyIpfsSaveCell.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyIpfsSaveCell.h"

@interface WeXMyIpfsSaveCell()

@property (nonatomic,strong)WeXMyIpfsSaveModel *twoIpfsModel;

@end

@implementation WeXMyIpfsSaveCell

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
    
    if (_identifyTitle) {
        return;
    }
    self.identifyTitle = [UILabel new];
//    self.identifyTitle.text = @"实名认证数据";
    self.identifyTitle.font = [UIFont systemFontOfSize:16];
    self.identifyTitle.textColor = ColorWithHex(0x4A4A4A);
    //    self.titleLabel.frame = CGRectMake(10, 10, 200, 30);
    [self.contentView addSubview:_identifyTitle];
    [_identifyTitle mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(10);
        make.leading.equalTo(self.selectNameImg.mas_trailing).offset(10);
        make.width.mas_equalTo(140);
        make.height.mas_equalTo(20);
    }];
    
    if (_statusStr) {
        return;
    }
    self.statusStr = [UILabel new];
//    self.statusStr.text = @"未选中";
    self.statusStr.font = [UIFont systemFontOfSize:14];
    self.statusStr.textColor = ColorWithHex(0x9B9B9B);
    //    self.titleLabel.frame = CGRectMake(10, 10, 200, 30);
    [self.contentView addSubview:_statusStr];
    [_statusStr mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(10);
        make.right.equalTo(self.contentView).offset(-25);
        make.width.mas_equalTo(60);
        make.height.mas_equalTo(20);
    }];
    
    if (_fileSizeStr) {
        return;
    }
    self.fileSizeStr = [UILabel new];
//    self.fileSizeStr.text = @"1.7M";
    self.fileSizeStr.font = [UIFont systemFontOfSize:14];
    self.fileSizeStr.textColor = ColorWithHex(0x9B9B9B);
    //    self.titleLabel.frame = CGRectMake(10, 10, 200, 30);
    [self.contentView addSubview:_fileSizeStr];
    [_fileSizeStr mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.identifyTitle);
        make.leading.equalTo(self.identifyTitle.mas_trailing).offset(5);
        make.trailing.equalTo(self.statusStr.mas_leading).offset(-5);
        make.height.mas_equalTo(20);
    }];
    
    if (_statusDescribeStr) {
        return;
    }
    self.statusDescribeStr = [UILabel new];
//    self.statusDescribeStr.text = @"可上传";
    self.statusDescribeStr.font = [UIFont systemFontOfSize:14];
    self.statusDescribeStr.textColor = ColorWithHex(0x9B9B9B);
    [self.contentView addSubview:_statusDescribeStr];
    [_statusDescribeStr mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.identifyTitle.mas_bottom).offset(5);
        make.leading.equalTo(self.identifyTitle);
        make.trailing.mas_equalTo(-20);
        make.height.mas_equalTo(20);
    }];
    
    if (_lineView) {
        return;
    }
    _lineView = [UIView new];
    _lineView.backgroundColor = ColorWithHex(0xEFEFEF);
    [self.contentView addSubview:_lineView];
    [_lineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.statusDescribeStr.mas_bottom).offset(10);
        make.left.mas_equalTo(16);
        make.right.mas_equalTo(-16);
        make.height.mas_equalTo(1);
    }];
    
    if (_fileUploadStatusLabel) {
        return;
    }
    _fileUploadStatusLabel = [UILabel new];
    self.fileUploadStatusLabel.font = [UIFont systemFontOfSize:14];
    self.fileUploadStatusLabel.textColor = ColorWithHex(0xAAAAAB);
    self.fileUploadStatusLabel.hidden = YES;
    [self.contentView addSubview:_fileUploadStatusLabel];
    [_fileUploadStatusLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(14);
        make.trailing.mas_equalTo(-20);
        make.leading.equalTo(_fileSizeStr.mas_trailing).offset(5);
        make.height.mas_equalTo(20);
    }];
    
    if (_statusProgressView) {
        return;
    }
    self.statusProgressView = [[UIProgressView alloc]init];
    self.statusProgressView.progressTintColor = ColorWithHex(0x6766CC);//进度颜色
    self.statusProgressView.trackTintColor = [UIColor whiteColor];//原始值
    self.statusProgressView.hidden = YES;
    [self.contentView addSubview:_statusProgressView];
    [_statusProgressView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_fileUploadStatusLabel.mas_bottom);
        make.leading.equalTo(_fileUploadStatusLabel);
        make.trailing.equalTo(_fileUploadStatusLabel);
        make.height.mas_equalTo(5);
    }];
    
    if (_cloudAddressBtn) {
        return;
    }
    self.cloudAddressBtn = [[UIButton alloc]init];
    [self.cloudAddressBtn setTitle:@"云端地址" forState:UIControlStateNormal];
    [self.cloudAddressBtn setTitleColor:ColorWithHex(0x6766CC) forState:UIControlStateNormal];
    self.cloudAddressBtn.hidden = YES;
//    [self.cloudAddressBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    [self.cloudAddressBtn addTarget:self action:@selector(goEditClick) forControlEvents:UIControlEventTouchUpInside];
    //边框宽度
    self.cloudAddressBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    self.cloudAddressBtn.layer.borderColor = ColorWithHex(0x6766CC).CGColor;//设置边框颜色
    self.cloudAddressBtn.layer.borderWidth = 1.0f;//设置边框颜色
    self.cloudAddressBtn.layer.cornerRadius = 6;
    self.cloudAddressBtn.clipsToBounds = YES;
    [self.contentView addSubview:self.cloudAddressBtn];
    [_cloudAddressBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.contentView);
        make.right.mas_equalTo(-10);
        make.width.mas_equalTo(60);
        make.height.mas_equalTo(20);
    }];
    
}

- (void)setModel:(WeXMyIpfsSaveModel *)model{
    _twoIpfsModel =  model;
    if (model) {
        if (model.isAllowSelect) {
            if (model.isSelected) {
               self.selectNameImg.image = [UIImage imageNamed:@"2B"];
                if (model.fileIpfsType == WeXFileIpfsTypeDownload) {
                    self.statusStr.text = @"待下载";
                }
                if (model.fileIpfsType == WeXFileIpfsTypeUpload) {
                    self.statusStr.text = @"待上传";
                }
            }else{
               self.selectNameImg.image = [UIImage imageNamed:@"2B2"];
               self.statusStr.text = @"未选中";
            }
          }else{
            self.selectNameImg.image = [UIImage imageNamed:@"Group 3"];
            self.statusStr.text = @"";
        }
        if (model.fileIpfsType == WeXFileIpfsTypeNewest) {
            self.cloudAddressBtn.hidden = NO;
        }else{
            self.cloudAddressBtn.hidden = YES;
        }
        
        self.identifyTitle.text = model.identifyTitle;
        self.fileSizeStr.text = model.fileSizeStr;
        self.statusDescribeStr.text = model.statusDescribeStr;
        
    }
    
}

- (void)goEditClick{
    
    if (self.ipfsModelBlock) {
        self.ipfsModelBlock(_twoIpfsModel);
    }
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
