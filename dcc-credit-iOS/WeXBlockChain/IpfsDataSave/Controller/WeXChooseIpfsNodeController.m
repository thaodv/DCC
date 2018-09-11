//
//  WeXChooseIpfsNodeController.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXChooseIpfsNodeController.h"
#import "WeXIpfsSetNodeController.h"
#import "WeXMyIpfsNodeCell.h"
#import "WeXIpfsNodeModel.h"
#import "WeXIpfsHelper.h"
#import "AFHTTPSessionManager.h"
#import "WeXIpfsContractHeader.h"

static NSString *const kWeXMyIpfsNodeCellIdentifier = @"WeXMyIpfsNodeCellID";

@interface WeXChooseIpfsNodeController ()<UITableViewDelegate,UITableViewDataSource,UITextViewDelegate>

@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong)NSMutableArray *dataArray;
@property (nonatomic,strong)NSMutableArray *selectDataArray;

@property (nonatomic,strong)UIView *bottomView;
@property(nonatomic,assign)BOOL isCustomNoneAllowChoose;

@end

@implementation WeXChooseIpfsNodeController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];

    [self judgeCustomNoneIsExist];
    [self reSetDisplayData];
    [self setupSubViews];
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self judgeCustomNoneIsExist];
//    [self reSetDisplayData];
}
//判断有无自定义的节点
- (void)judgeCustomNoneIsExist{

    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord = [user objectForKey:WEX_IPFS_CUSTOM_NONEURL];
    NSString *publicStr = [user objectForKey:WEX_IPFS_CUSTOM_PUBLICADDRESS];
    NSString *portStr = [user objectForKey:WEX_IPFS_CUSTOM_PORTNUM];
    WeXIpfsNodeModel *twoModel = _dataArray[1];
    
    if ([WeXIpfsHelper isBlankString:passWord]) {
        _isCustomNoneAllowChoose = NO;
        twoModel.describeStr = @"尚未配置";
    }else{
        _isCustomNoneAllowChoose = YES;
         twoModel.describeStr = [NSString stringWithFormat:@"%@ 端口%@",publicStr,portStr];
    }
     [_tableView reloadData];
}

- (void)reSetDisplayData{
    
//    [self.dataArray removeAllObjects];
//    self.dataArray = nil;
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *defaultUrlStr = [user objectForKey:WEX_IPFS_DEFAULT_NONEURL];
    NSString *customUrlStr = [user objectForKey:WEX_IPFS_CUSTOM_NONEURL];
    NSString *mainUrlStr = [user objectForKey:WEX_IPFS_MAIN_NONEURL];
    NSString *publicStr = [user objectForKey:WEX_IPFS_CUSTOM_PUBLICADDRESS];
    NSString *portStr = [user objectForKey:WEX_IPFS_CUSTOM_PORTNUM];
    
    WeXIpfsNodeModel *model = [[WeXIpfsNodeModel alloc]init];
    model.nameTitle = @"默认节点";
    model.isDefault = YES;
    model.modeType = WeXIpfsNodeModelTypeDefault;
    if ([defaultUrlStr isEqualToString:mainUrlStr] && ![customUrlStr isEqualToString:mainUrlStr]) {
        model.isSelected = YES;
    }else{
        model.isSelected = NO;
    }
    [self.dataArray addObject:model];
    
    WeXIpfsNodeModel *twoModel = [[WeXIpfsNodeModel alloc]init];
    twoModel.nameTitle = @"自定义节点";
    twoModel.isDefault = NO;
    twoModel.modeType =  WeXIpfsNodeModelTypeCustom;
    if (_isCustomNoneAllowChoose) {
        twoModel.describeStr = [NSString stringWithFormat:@"%@ 端口%@",publicStr,portStr];
    }else{
        twoModel.describeStr = @"尚未配置";
    }
    if (![defaultUrlStr isEqualToString:mainUrlStr] && [customUrlStr isEqualToString:mainUrlStr]) {
        twoModel.isSelected = YES;
    }else{
        twoModel.isSelected = NO;
    }
    [self.dataArray addObject:twoModel];
    [_tableView reloadData];

}

#pragma mark --初始化滚动视图
- (void)setupSubViews{
    self.navigationItem.title = @"选择节点";
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.sectionIndexBackgroundColor=[UIColor clearColor];
    _tableView.sectionIndexColor = ColorWithHex(0x5756B3);
    [self.view addSubview:_tableView];
    _tableView.tableFooterView = [UIView new];//当cell较少时影藏多余的cell
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+5);
        make.leading.trailing.equalTo(self.view);
        make.height.mas_equalTo(200);
    }];
    [_tableView registerClass:[WeXMyIpfsNodeCell class] forCellReuseIdentifier:kWeXMyIpfsNodeCellIdentifier];
    [_tableView reloadData];
    self.view.backgroundColor = [UIColor whiteColor];
    
    _bottomView = [[UIView alloc]init];
    [self.view addSubview:_bottomView];
    [_bottomView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(0);
        make.trailing.leading.mas_equalTo(0);
        make.height.mas_equalTo(200);
    }];
    
//    UILabel *deafultLabel = [[UILabel alloc]init];
//    deafultLabel.font = [UIFont systemFontOfSize:14];
//    deafultLabel.textColor = ColorWithHex(0x9B9B9B);
//    deafultLabel.text = @"说明:\n云端数据默认存储在Bit Express提供的节点,您也可以自己搭建节点,选择把数据存储在自建节点上。\n搭建节点的教程可以访问 https://open.dcc.finance";
//    deafultLabel.numberOfLines = 0;//多行显示
//    deafultLabel.lineBreakMode = NSLineBreakByWordWrapping;
//    [self.bottomView addSubview:deafultLabel];
//    [deafultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(self.bottomView).offset(15);
//        make.left.equalTo(self.bottomView).offset(15);
//        make.right.equalTo(self.bottomView).offset(-15);
//    }];
    
    UITextView *defaultTextView = [[UITextView alloc]init];

    defaultTextView.font = [UIFont systemFontOfSize:16];
    defaultTextView.textColor = ColorWithHex(0x9B9B9B);
    defaultTextView.editable = NO;
    defaultTextView.delegate = self;
    NSString *defaultStr = @"说明:\n云端数据默认存储在Bit Express提供的节点,您也可以自己搭建节点,选择把数据存储在自建节点上。搭建节点的教程可以访问https://open.dcc.finance";
    WEXNSLOG(@"defaultStr.length = %ld",defaultStr.length);
    WEXNSLOG(@"Str.length = %ld",@"https://open.dcc.finance".length);
    NSMutableAttributedString *attrStr = [[NSMutableAttributedString alloc] initWithString:defaultStr];
    WEXNSLOG(@"attrStr.length = %ld",attrStr.length);
    [attrStr addAttribute:NSFontAttributeName
                    value:[UIFont systemFontOfSize:14.0f]
                    range:NSMakeRange(0, attrStr.length-1)];
    
    [attrStr addAttribute:NSForegroundColorAttributeName
                    value:[UIColor blueColor]
                    range:NSMakeRange(66, 24)];
    
    [attrStr addAttribute:NSUnderlineStyleAttributeName
                    value:[NSNumber numberWithInteger:NSUnderlineStyleSingle]
                    range:NSMakeRange(66, 24)];
    [attrStr addAttribute:NSLinkAttributeName
                    value:@"click://"
                    range:NSMakeRange(66, 24)];
    defaultTextView.attributedText = attrStr;
    [self.bottomView addSubview:defaultTextView];
    [defaultTextView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.bottomView).offset(15);
        make.left.equalTo(self.bottomView).offset(15);
        make.right.equalTo(self.bottomView).offset(-15);
        make.bottom.equalTo(self.bottomView).offset(0);
    }];
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXMyIpfsNodeCell *cell = [tableView dequeueReusableCellWithIdentifier:kWeXMyIpfsNodeCellIdentifier];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;

    WeXIpfsNodeModel *model = _dataArray[indexPath.row];
    cell.model = model;
    cell.goSettingNoneVcBlock = ^{
        [self goAddNodeVc];
    };
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 70;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    WeXIpfsNodeModel *dataModel = _dataArray[indexPath.row];
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *twoCustomUrlStr = [user objectForKey:WEX_IPFS_CUSTOM_NONEURL];
    
    if (indexPath.row == 1 && ![WeXIpfsHelper isBlankString:twoCustomUrlStr] && !dataModel.isSelected) {
        [self judgeNoneIsUse:[NSString stringWithFormat:@"%@/version",twoCustomUrlStr]];
    }
    
    if (!_isCustomNoneAllowChoose && (indexPath.row == 1)) {
        [self goAddNodeVc];
    }else{
    //一次只允许选一个节点
//     WeXIpfsNodeModel *dataModel = _dataArray[indexPath.row];
    if (!dataModel.isSelected) {
        for (WeXIpfsNodeModel *twoModel in _dataArray) {
            if (![dataModel isEqual:twoModel]) {
                twoModel.isSelected  = NO;
            }
        }
    }
    dataModel.isSelected = !dataModel.isSelected;
        //存储主节点
//        NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
        if (indexPath.row == 0) {
            NSString *defaultUrlStr = [user objectForKey:WEX_IPFS_DEFAULT_NONEURL];
            [user setObject:defaultUrlStr forKey:WEX_IPFS_MAIN_NONEURL];
        }else if(indexPath.row == 1){
            NSString *customUrlStr = [user objectForKey:WEX_IPFS_CUSTOM_NONEURL];
            [user setObject:customUrlStr forKey:WEX_IPFS_MAIN_NONEURL];
        }else{}
        
    [_tableView reloadData];
    }
}

- (void)goAddNodeVc{
    WeXIpfsSetNodeController *vc = [[WeXIpfsSetNodeController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
}

//判断节点是否可用
- (void)judgeNoneIsUse:(NSString *)urlStr{
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/html",@"text/plain",@"image/jpeg",@"image/png",@"application/zip",@"application/octet-stream",nil];
    //    [manager.requestSerializer setValue:@"text/plain; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    [manager GET:urlStr parameters:nil progress:^(NSProgress * _Nonnull downloadProgress) {
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        WEXNSLOG(@"responseObject = %@",responseObject);
        NSDictionary *dictFromData = [NSJSONSerialization JSONObjectWithData:responseObject
                                                                     options:NSJSONReadingAllowFragments error:nil];
        WEXNSLOG(@"dictFromData = %@",dictFromData);
        NSString *versionStr = dictFromData[@"Version"];
        if (versionStr.length>0) {
          
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        WEXNSLOG(@"Error: %@", error);
        [self ipfsEncopyTips];
        [self swichDefaultNoneEvent];
        
    }];
    
}

//自定义节点不可用时的弹框
- (void)ipfsEncopyTips{
    
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"提示" message:@"自定义节点无法访问，将为您切换到默认节点。" preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *delete = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    }];
    [alert addAction:delete];
    [self presentViewController:alert animated:YES completion:nil];
}

//自定义节点不可用时切换到默认节点
- (void)swichDefaultNoneEvent {
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *defaultUrlStr = [user objectForKey:WEX_IPFS_DEFAULT_NONEURL];
    
    WeXIpfsNodeModel *dataModel = _dataArray[0];
    WeXIpfsNodeModel *twoDataModel = _dataArray[1];
    dataModel.isSelected  = YES;
    [user setObject:defaultUrlStr forKey:WEX_IPFS_MAIN_NONEURL];
    twoDataModel.isSelected = NO;
    [self.tableView reloadData];
    
//    for (WeXIpfsNodeModel *oneModel in _dataArray) {
//        if (oneModel.modeType == WeXIpfsNodeModelTypeDefault) {
//            oneModel.isSelected = YES;
//
//        }
//    }
}

- (BOOL)textView:(UITextView *)textView shouldInteractWithURL:(NSURL *)URL inRange:(NSRange)characterRange{
    
    if ([[URL scheme] isEqualToString:@"click"]) {
        NSAttributedString *abStr = [textView.attributedText attributedSubstringFromRange:characterRange];
        WEXNSLOG(@"%@",abStr);
        UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
        pasteboard.string = [abStr string];
        WEXNSLOG(@"[abStr string] = %@",[abStr string]);
        [WeXPorgressHUD showText:WeXLocalizedString(@"复制成功!") onView:self.view];
//        if (self.eventBlock) {
//            self.eventBlock(abStr);
//        }
        
        return NO;
    }
    

    return YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -- 懒加载
- (NSMutableArray *)dataArray{
    if (_dataArray == nil) {
        _dataArray = [NSMutableArray array];
    }
    return _dataArray;
}

- (NSMutableArray *)selectDataArray{
    if (_selectDataArray == nil) {
        _selectDataArray = [NSMutableArray array];
    }
    return _selectDataArray;
}




/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
