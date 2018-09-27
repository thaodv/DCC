//
//  WeXTokenPlusViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTokenPlusViewController.h"
#import "WeXTokenPlusCardCell.h"
#import "WeXTokenPlusTextCell.h"

@interface WeXTokenPlusViewController () <UIWebViewDelegate>

@property (nonatomic, strong) NSArray <NSString *> *titles;
@property (nonatomic, strong) NSArray <NSString *> *subtitles;

@property (nonatomic, copy) NSString *webURL;




@end

static NSString * const kCardCellID = @"WeXTokenPlusCardCellID";
static NSString * const kTextCellID = @"WeXTokenPlusTextCellID";

@implementation WeXTokenPlusViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"TokenPlus介绍");
    [self setNavigationNomalBackButtonType];
    self.webView.delegate = self;
    NSString *webURL;
    if ([[WeXLocalizedManager shareManager] isChinese]) {
        webURL = [WEX_WEB_BASE_URL stringByAppendingString:@"dapp/tokenPlus/discovery.html"];
    } else {
        webURL = [WEX_WEB_BASE_URL stringByAppendingString:@"dapp/tokenPlus/discovery.html"];
    }
    self.webURL = webURL;
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:_webURL]]];
    [WeXPorgressHUD showLoadingAddedTo:self.view];
}

-(void)webViewDidStartLoad:(UIWebView *)webView {
    NSLog(@"start");
}

-(void)webViewDidFinishLoad:(UIWebView *)webView {
    [WeXPorgressHUD hideLoading];
    NSLog(@"Finish");
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    NSString *failURL= error.userInfo[NSErrorFailingURLStringKey];
#pragma clang diagnostic pop
    NSString *webURL = self.webURL;
    if ([failURL hasPrefix:@"/"]) {
        webURL = [webURL stringByAppendingString:@"/"];
    }
    if ([failURL isEqualToString:webURL]) {
        [WeXPorgressHUD hideLoading];
        WEXNSLOG(@"webView error: %@", error);
    } else if ([failURL containsString:webURL]) {
        [WeXPorgressHUD hideLoading];
        WEXNSLOG(@"webView error: %@", error);
    }
}

//- (void)configureStaticData {
//    [self wex_unistallRefreshHeader];
//    _titles = @[@"• 什么是Tokenplus",
//                @"• 有什么特色？",
//                @"• 如何免费获取10万usdt交易额度？"];
//    _subtitles = @[@"Tokenplus是一款面向专业数字资产投资用户的资产自动套利工具 。",
//                   @"基于Linux环境搭建的量化交易系统，速度提升不止3倍。\n无需自行购买服务器和配置交易软件，仅需注册账户，即可轻松套利。\n每个用户独立服务器和IP，四重加密全面保障你的账户key安全。\n遍布全球的服务器网络节点，优选离交易所最近节点，力争不错过每一次交易机会。\n目前已支持国内前11大交易所，并持续增加中。",
//                   @"如果你持有数字资产，请填写以下信息即有机会免费获得10万交易额度。"];
//    [self.tableView reloadData];
//}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

//- (void)wexRegisterTableViewCell:(UITableView *)tableView {
//    [tableView registerClass:[WeXTokenPlusCardCell class] forCellReuseIdentifier:kCardCellID];
//    [tableView registerClass:[WeXTokenPlusTextCell class] forCellReuseIdentifier:kTextCellID];
//}
//
//- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
//    return 0.01;
//}
//- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
//    return 1;
//}
//- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
//    return _titles.count > 0 ? _titles.count + 1 : 0;
//}
//
//- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
//    return 0.01;
//}
//
//- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
//    if (indexPath.row == 0) {
//        return [self wexTableView:tableView cellIdentifier:kCardCellID indexPath:indexPath];
//    }
//    return [self wexTableView:tableView cellIdentifier:kTextCellID indexPath:indexPath];
//}
//
//- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
//    if (indexPath.row == 0) {
//        return [self wexTableview:tableView cellForRowWithIdentifier:kCardCellID indexPath:indexPath];
//    }
//    return [self wexTableview:tableView cellForRowWithIdentifier:kTextCellID indexPath:indexPath];
//}
//- (void)wexTableView:(UITableView *)tableView
//       configureCell:(UITableViewCell *)currentCell
//           indexPath:(NSIndexPath *)indexPath {
//    if (indexPath.row > 0) {
//        WeXTokenPlusTextCell *textCell = (WeXTokenPlusTextCell *)currentCell;
//        [textCell setTitle:_titles[indexPath.row - 1] subText:_subtitles[indexPath.row - 1]];
//    }
//}
//




@end
