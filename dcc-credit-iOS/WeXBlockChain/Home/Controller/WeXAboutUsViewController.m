//
//  WeXAboutUsViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXAboutUsViewController.h"

#define contentLabelFont 15

#define contentLabelLineSpace 7

@interface WeXAboutUsViewController ()

@end

@implementation WeXAboutUsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"关于我们";
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.font = [UIFont systemFontOfSize:17];
    label1.textColor = ColorWithLabelDescritionBlack;
    label1.textAlignment = NSTextAlignmentLeft;
    label1.numberOfLines = 0;
    [self.view addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+20);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
    }];
    
    NSString *content1 = @"全向口袋是由Allxsports基于区块链技术发布的一款帮助用户解决去中心化的场景中身份认证问题的平台。全向口袋的口袋就是您在数字社会的数字身份证，它可以有效的在区块链中认定用户的唯一身份，黑客无法通过反向计算等方式仿冒用户的唯一身份。为确保用户的隐私，用户可根据自身意愿来决定传递至第三方的信息内容。而第三方可根据获得的个人信息来决定用户应获得的服务内容。";
    //设置间距
    NSMutableAttributedString *attrStr1 = [[NSMutableAttributedString alloc] initWithString:content1];
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineSpacing = contentLabelLineSpace;
    paragraphStyle.alignment = NSTextAlignmentLeft;
    paragraphStyle.hyphenationFactor = 1.0;
    paragraphStyle.firstLineHeadIndent = 0.0;
    paragraphStyle.paragraphSpacingBefore = 0.0;
    paragraphStyle.headIndent = 0;
    paragraphStyle.tailIndent = 0;
    [attrStr1 addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content1.length)];
    label1.attributedText = attrStr1;
    
    
    
    UILabel *label2 = [[UILabel alloc] init];
    label2.font = [UIFont systemFontOfSize:17];
    label2.textColor = ColorWithLabelDescritionBlack;
    label2.textAlignment = NSTextAlignmentLeft;
    label2.numberOfLines = 0;
    [self.view addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(30);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
    }];
    
    NSString *content2 = @"下面是我们的联系方式，如果您对我们感兴趣，寻求合作，业务咨询或发送简历等，欢迎与我们联系。";
    //设置间距
    NSMutableAttributedString *attrStr2 = [[NSMutableAttributedString alloc] initWithString:content2];
    NSMutableParagraphStyle *paragraphStyle2 = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle2.lineSpacing = contentLabelLineSpace;
    paragraphStyle2.alignment = NSTextAlignmentLeft;
    paragraphStyle2.hyphenationFactor = 1.0;
    paragraphStyle2.firstLineHeadIndent = 0.0;
    paragraphStyle2.paragraphSpacingBefore = 0.0;
    paragraphStyle2.headIndent = 0;
    paragraphStyle2.tailIndent = 0;
    [attrStr2 addAttribute:NSParagraphStyleAttributeName value:paragraphStyle2 range:NSMakeRange(0, content2.length)];
    label2.attributedText = attrStr2;
    
    UILabel *label3 = [[UILabel alloc] init];
    label3.text = @"邮箱：allxwallet@126.com";
    label3.font = [UIFont systemFontOfSize:17];
    label3.textColor = ColorWithLabelDescritionBlack;
    label3.textAlignment = NSTextAlignmentLeft;
    label3.numberOfLines = 0;
    [self.view addSubview:label3];
    [label3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label2.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
    }];
}

@end
