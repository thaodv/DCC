//
//  WeXNewTokenViewController.m
//  WeXBlockChain
//
//  Created by zhuojian on 2018/1/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXNewTokenViewController.h"


@interface WeXNewTokenViewController ()
@property(nonatomic,weak)IBOutlet UITextView* txtView;
@end

@implementation WeXNewTokenViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    
    self.navigationItem.title = @"提交新Token";
    _txtView.text = @"如何提交新Token给BitExpress？\n如果需要在钱包显示你的Token信息，请务必使用官方邮箱提供一下信息，发送至yimtoken1@163.com我们会有专人处理\n必要信息\n公司或个人名字\n官方网站\n合约地址\n推荐用户转账所使用的 Gas 数量（没有提供，我们默认值 60000）\nLogo.png\n其它情况说明\nLogo 设计要求\n切图尺寸：132x132 像素\n透明背景 PNG\n品牌标识水平和垂直居中\n第一次提交，我们一般会在两个工作日处理.如有信息变动，请第一时间邮件跟我们联系更新，谢谢配合！犹豫以太坊代币种类繁杂，同时为了避免误导部分BitExpress用户，我们暂时只添加社区内相对流行的代币 logo 。\n如果你想修改代币的合约地址，请提供相关的官方公告给我们。\n注：BitExpress默认显示列表只显示合作方 Token ，其它 Token 是按余额 > 0 自动出现或用户搜索添加出现。";
    
    [self.view bringSubviewToFront:_txtView];
    
    CGRect rectOfStatusbar = [[UIApplication sharedApplication] statusBarFrame];
    CGRect frame= _txtView.frame;
    frame.origin.y+=rectOfStatusbar.size.height;
    _txtView.frame=frame;
    
    [self setNavigationNomalBackButtonType];
}




@end
