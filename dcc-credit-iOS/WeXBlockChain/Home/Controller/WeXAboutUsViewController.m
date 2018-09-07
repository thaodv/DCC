//
//  WeXAboutUsViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXAboutUsViewController.h"
#import "WeXOfficialWebViewController.h"

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
    label1.textColor = COLOR_LABEL_DESCRIPTION;
    label1.textAlignment = NSTextAlignmentLeft;
    label1.numberOfLines = 0;
    [self.view addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+20);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
    }];
    
    NSString *content1 = @"BitExpress是基于Distributed Credit Chain发布的一款帮助用户解决去中心化场景中数字资产借贷问题的Dapp。需要借贷的用户在Dapp中将个人信息通过认证方认证后,用户可以将认证后的信息通过Dapp发送给数字资产出借方，出借方审核后，决定是否放款给需要借贷的用户。在BitExpress平台中借款方可以获得应急所需的数字资产，出借方可以获得出借本金及出借时间和金额所对应的利息。";
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
    label2.textColor = COLOR_LABEL_DESCRIPTION;
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
    label3.text = @"邮箱 yimtoken1@163.com";
    label3.font = [UIFont systemFontOfSize:17];
    label3.textColor = COLOR_LABEL_DESCRIPTION;
    label3.textAlignment = NSTextAlignmentLeft;
    label3.numberOfLines = 0;
    [self.view addSubview:label3];
    [label3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label2.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
    }];
    
    UILabel *label4 = [[UILabel alloc] init];
    label4.text = @"官网";
    label4.font = [UIFont systemFontOfSize:17];
    label4.textColor = COLOR_LABEL_DESCRIPTION;
    label4.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label4];
    [label4 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label3.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@20);
    }];
    
    UILabel *label5 = [[UILabel alloc] init];
    label5.text = @"http://dcc.finance/";
    label5.font = [UIFont systemFontOfSize:17];
    label5.textColor = [UIColor blueColor];
    label5.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label5];
    [label5 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label3.mas_bottom).offset(10);
        make.leading.equalTo(label4.mas_trailing).offset(5);
        make.height.equalTo(@20);
    }];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    label5.userInteractionEnabled = YES;
    [label5 addGestureRecognizer:tap];
    WEXNSLOG(@"%@",tap);
}

- (void)tapClick
{
    WEXNSLOG(@"%s",__func__);
    WeXOfficialWebViewController *ctrl = [[WeXOfficialWebViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
