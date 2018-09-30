//
//  WeXDeleteReceivieAddressToastView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDeleteReceivieAddressToastView.h"
#import "WeXBorrowReceiveAddressModal.h"

@interface WeXDeleteReceivieAddressToastView()
{
    NSInteger _index;
}

@property (nonatomic,strong)UILabel *addressNameLabel;

@property (nonatomic,strong)UILabel *addressContentLabel;

@end

@implementation WeXDeleteReceivieAddressToastView

-(instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        [self setupSubViews];
    }
    return self;
    
}

- (void)commonInit{
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    [self addGestureRecognizer:tap];
    
}

- (void)tapClick{
    [self endEditing:YES];
}

- (void)setupSubViews{
    
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = [UIColor blackColor];
    backView.alpha = COVER_VIEW_ALPHA;
    [self addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(self);
        make.bottom.equalTo(self);
    }];
    
    UIView *contentView = [[UIView alloc] init];
    contentView.backgroundColor = [UIColor whiteColor];
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.centerY.equalTo(self);
        make.height.equalTo(@200);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"确认删除以下信息?";
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.numberOfLines = 0;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(15);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UILabel * descriptionLabel1 = [[UILabel alloc] init];
    descriptionLabel1.font = [UIFont systemFontOfSize:17];
    descriptionLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    descriptionLabel1.textAlignment = NSTextAlignmentCenter;
    descriptionLabel1.adjustsFontSizeToFitWidth = YES;

    [contentView addSubview:descriptionLabel1];
    [descriptionLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(15);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
    }];
    _addressNameLabel = descriptionLabel1;
    
    UILabel * descriptionLabel2 = [[UILabel alloc] init];
    descriptionLabel2.font = [UIFont systemFontOfSize:15];
    descriptionLabel2.textColor = COLOR_LABEL_DESCRIPTION;
    descriptionLabel2.textAlignment = NSTextAlignmentCenter;
    descriptionLabel2.adjustsFontSizeToFitWidth = YES;
    [contentView addSubview:descriptionLabel2];
    [descriptionLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(descriptionLabel1.mas_bottom).offset(15);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
    }];
    _addressContentLabel = descriptionLabel2;
    
    
    UIButton *cancelBtn = [WeXCustomButton button];
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(15);
        make.bottom.equalTo(contentView).offset(-15);
        make.height.equalTo(@40);
    }];
    
    
    UIButton *deleteBtn = [WeXCustomButton button];
    [deleteBtn setTitle:@"删除钱包" forState:UIControlStateNormal];
    [deleteBtn addTarget:self action:@selector(deleteBtnClick) forControlEvents:UIControlEventTouchUpInside];
    deleteBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [contentView addSubview:deleteBtn];
    [deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(cancelBtn.mas_trailing).offset(15);
        make.trailing.equalTo(contentView).offset(-15);
        make.width.equalTo(cancelBtn);
        make.height.equalTo(cancelBtn);
        make.bottom.equalTo(cancelBtn);
    }];
    
    
}

- (void)configWithModel:(WeXBorrowReceiveAddressModal *)model
{
    _addressNameLabel.text = model.name;
    _addressContentLabel.text = model.address;
}


- (void)deleteBtnClick
{
    if ([self.delegate respondsToSelector:@selector(deleteReceivieAddressToastViewDidDeletePassport)]) {
        [self.delegate deleteReceivieAddressToastViewDidDeletePassport];
    }
    
    if (self.deleteButtonClickBlock) {
        self.deleteButtonClickBlock();
    }
    
    [self dismiss];
    
}

- (void)cancelBtnClick{
    [self dismiss];
}

- (void)dismiss
{
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    
    [self removeFromSuperview];
}

-(void)dealloc
{
    NSLog(@"%s",__func__);
}


@end
