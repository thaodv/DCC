//
//  WeXIpfsSetNodeController.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXIpfsSetNodeController.h"
#import "WeXIpfsHelper.h"
#import "AFHTTPSessionManager.h"
#import "WeXIpfsContractHeader.h"

@interface WeXIpfsSetNodeController ()<UITextFieldDelegate>

@property(nonatomic,strong)UIView *addressTitleView;
@property(nonatomic,strong)UIImageView *iconView;
@property(nonatomic,strong)UITextField *addressTitleTextField;
@property(nonatomic,strong)UIView *oneLineView;

@property(nonatomic,strong)UIView *addressWalletView;
@property(nonatomic,strong)UILabel *oneDefaultLabel;
@property(nonatomic,strong)UITextField *addressWalletField;
@property(nonatomic,strong)UIView *twoLineView;

@property(nonatomic,strong)UIView *mainOperationView;
@property(nonatomic,strong)UILabel *twoDefaultLabel;
@property(nonatomic,strong)UILabel *threeDefaultLabel;
@property(nonatomic,strong)UIButton *saveBtn;

@end

@implementation WeXIpfsSetNodeController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    self.navigationItem.title = @"编辑节点";
    [self setupSubViews];
    [self setDefaultData];
    
//    self.addressTitleTextField.text = @"10.65.212.11";
//    self.addressWalletField.text = @"5001";
    // Do any additional setup after loading the view.
}

- (void)setDefaultData{
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *customUrlStr = [user objectForKey:WEX_IPFS_CUSTOM_NONEURL];
    NSString *publicStr = [user objectForKey:WEX_IPFS_CUSTOM_PUBLICADDRESS];
    NSString *portStr = [user objectForKey:WEX_IPFS_CUSTOM_PORTNUM];
    //判断本地已经有主节点则不存值
    if (![WeXIpfsHelper isBlankString:customUrlStr]) {
      if (![WeXIpfsHelper isBlankString:publicStr]) {
           self.addressTitleTextField.text = publicStr;
      }
      if (![WeXIpfsHelper isBlankString:portStr]) {
           self.addressWalletField.text = portStr;
      }
    }else{}
  
}

- (void)setupSubViews{
    self.view.backgroundColor = [UIColor whiteColor];
    //头部布局
    _addressTitleView = [[UIView alloc]init];
    //    _addressTitleView.backgroundColor = [UIColor orangeColor];
    [self.view addSubview:_addressTitleView];
    [_addressTitleView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.left.right.mas_equalTo(0);
        make.height.mas_offset(66);
    }];
    
    UILabel *deafultLabel = [[UILabel alloc]init];
    deafultLabel = [[UILabel alloc]init];
    deafultLabel.font = [UIFont systemFontOfSize:14];
    deafultLabel.text = @"公网地址";
    deafultLabel.textColor = ColorWithHex(0x4A4A4A);
    [self.addressTitleView addSubview:deafultLabel];
    [deafultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(25);
        make.left.mas_equalTo(15);
        make.width.mas_equalTo(80);
        make.height.mas_equalTo(30);
    }];
    
    _addressTitleTextField = [[UITextField alloc]init];
    _addressTitleTextField.textColor = ColorWithHex(0x4A4A4A);
    _addressTitleTextField.font = [UIFont systemFontOfSize:16.0f];
    _addressTitleTextField.placeholder = @"请填写公网域名或IP";
    [self.addressTitleView addSubview:_addressTitleTextField];
    _addressTitleTextField.delegate = self;
    [_addressTitleTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(25);
        make.leading.equalTo(deafultLabel.mas_trailing).offset(10);
        make.right.mas_equalTo(-20);
        make.height.mas_equalTo(30);
    }];
    _oneLineView = [[UIView alloc]init];
    _oneLineView.backgroundColor = ColorWithHex(0xEFEFEF);
    [self.addressTitleView addSubview:_oneLineView];
    [_oneLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.addressTitleTextField).offset(41);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
        make.height.mas_equalTo(1);
    }];
    
    //中部布局
    _addressWalletView = [[UIView alloc]init];
    //    _addressWalletView.backgroundColor = [UIColor orangeColor];
    [self.view addSubview:_addressWalletView];
    [_addressWalletView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.addressTitleView).offset(66);
        make.left.right.mas_equalTo(0);
        make.height.mas_offset(70);
    }];
    self.oneDefaultLabel = [[UILabel alloc]init];
    self.oneDefaultLabel.font = [UIFont systemFontOfSize:16];
    self.oneDefaultLabel.text = @"端口号";
    self.oneDefaultLabel.textColor = ColorWithHex(0x4A4A4A);
    // self.titleLabel.frame = CGRectMake(10, 10, 200, 30);
    [self.addressWalletView addSubview:_oneDefaultLabel];
    [_oneDefaultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(25);
        make.left.mas_equalTo(15);
        make.width.mas_equalTo(80);
        make.height.mas_equalTo(20);
    }];
    _addressWalletField = [[UITextField alloc]init];
    _addressWalletField.textColor = ColorWithHex(0x4A4A4A);
    _addressWalletField.font = [UIFont systemFontOfSize:16.0f];
    _addressWalletField.placeholder = @"请填写端口号";
    [self.addressWalletView addSubview:_addressWalletField];
    _addressWalletField.delegate = self;
    [_addressWalletField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.oneDefaultLabel);
        make.leading.equalTo(self.oneDefaultLabel.mas_trailing).offset(10);
        make.right.mas_equalTo(-20);
        make.height.mas_equalTo(30);
    }];
    _twoLineView = [[UIView alloc]init];
    _twoLineView.backgroundColor = ColorWithHex(0xEFEFEF);
    [self.addressWalletView addSubview:_twoLineView];
    [_twoLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.addressWalletField).offset(35);
        make.left.mas_equalTo(20);
        make.right.mas_equalTo(-20);
        make.height.mas_equalTo(1);
    }];
    //底部布局
    _mainOperationView = [[UIView alloc]init];
    [self.view addSubview:_mainOperationView];
    [_mainOperationView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.addressWalletView.mas_bottom).offset(10);
        make.left.right.mas_equalTo(0);
        make.height.mas_equalTo(100);
    }];
    
    _saveBtn = [[UIButton alloc]init];
    [_saveBtn setTitle:WeXLocalizedString(@"保存") forState:UIControlStateNormal];
    [_saveBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    [_saveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_saveBtn addTarget:self action:@selector(goSaveClick) forControlEvents:UIControlEventTouchUpInside];
    _saveBtn.layer.cornerRadius = 6;
    _saveBtn.clipsToBounds = YES;
    [self.mainOperationView addSubview:_saveBtn];
    [_saveBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(10);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
        make.height.mas_equalTo(40);
    }];
}

- (void)goSaveClick{
    
    if ([WeXIpfsHelper isBlankString:_addressTitleTextField.text] || [WeXIpfsHelper isBlankString:_addressWalletField.text]) {
        [WeXPorgressHUD showText:@"内容不能为空" onView:self.view];
        return;
    }
    NSString *customNoneStr = [NSString stringWithFormat:@"http://%@:%@/api/v0/version",_addressTitleTextField.text,_addressWalletField.text];
    NSString *twoCustomNoneStr = [NSString stringWithFormat:@"http://%@:%@/api/v0",_addressTitleTextField.text,_addressWalletField.text];
    WEXNSLOG(@"customNoneStr = %@",customNoneStr);
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *defaultUrlStr = [user objectForKey:WEX_IPFS_DEFAULT_NONEURL];
    NSString *customUrlStr = [user objectForKey:WEX_IPFS_CUSTOM_NONEURL];
  
    if ([customUrlStr isEqualToString:twoCustomNoneStr] && ![WeXIpfsHelper isBlankString:customUrlStr]) {
        [WeXPorgressHUD showText:@"修改节点不能和原节点一致" onView:self.view];
        return;
    }
    if ([defaultUrlStr isEqualToString:twoCustomNoneStr]) {
        [WeXPorgressHUD showText:@"新增节点不能和默认节点一致" onView:self.view];
        return;
    }
    
    [self judgeNoneIsUse:customNoneStr];

}
//判断节点是否可用
- (void)judgeNoneIsUse:(NSString *)urlStr{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
   
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
            NSString *customNoneUrl =  [NSString stringWithFormat:@"http://%@:%@/api/v0",_addressTitleTextField.text,_addressWalletField.text];
            WEXNSLOG(@"customNoneUrl = %@",customNoneUrl);
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"保存成功" onView:self.view];
            NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
            [user setObject:customNoneUrl forKey:WEX_IPFS_CUSTOM_NONEURL];
            [user setObject:self.addressTitleTextField.text forKey:WEX_IPFS_CUSTOM_PUBLICADDRESS];
            [user setObject:self.addressWalletField.text forKey:WEX_IPFS_CUSTOM_PORTNUM];
            [self.navigationController popViewControllerAnimated:YES];
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        WEXNSLOG(@"Error: %@", error);
        [WeXPorgressHUD hideLoading];
        [WeXPorgressHUD showText:@"保存失败,该节点无相应" onView:self.view];
    }];
    
}


#pragma mark -- TextField代理方法

- (BOOL)textFieldShouldReturn:(UITextField *)textField{

    [_addressWalletField resignFirstResponder];
    [_addressTitleTextField resignFirstResponder];

    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == self.addressTitleTextField) {
        if (range.location +string.length > 63) {
            return NO;
        }else{
            return YES;
        }
    }else if (textField == self.addressWalletField){
        if (range.location +string.length > 10) {
            return NO;
        }else{
            return YES;
        }
    }
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField{

    [_addressWalletField resignFirstResponder];
    [_addressTitleTextField resignFirstResponder];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
