//
//  WexNickNameViewController.m
//  WeXBlockChain
//
//  Created by zhuojian on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WexNickNameViewController.h"
#import "YYKeyboardManager.h"


@interface WexNickNameViewController ()<UITextFieldDelegate,WexBaseViewControllerProtocol,YYKeyboardObserver>
{
    WeXCustomButton *_commitBtn;
}
@property(nonatomic,strong)IBOutlet UITextField* txtNickName;
@property(nonatomic,weak)IBOutlet UIView* viewLine;
@end

@implementation WexNickNameViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.view bringSubviewToFront:self.txtNickName];
    [self.view bringSubviewToFront:self.viewLine];
    
    [[YYKeyboardManager defaultManager] addObserver:self];
    
    self.delegate=self;
    
    if(!self.nickName)
        self.txtNickName.text=WeXLocalizedString(@"钱包");
    else
        self.txtNickName.text=[[WexDefaultConfig instance] nickName];
    
    self.txtNickName.textColor = COLOR_LABEL_DESCRIPTION;
    
    self.txtNickName.delegate=self;
    
    [self setNavigationNomalBackButtonType];
    
    [self setupSubViews];
    
}

- (void)setupSubViews{
    WeXCustomButton *commitBtn = [WeXCustomButton button];
    [commitBtn setTitle:WeXLocalizedString(@"保存") forState:UIControlStateNormal];
    [commitBtn addTarget:self action:@selector(commitBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:commitBtn];
    [commitBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@170);
        make.height.equalTo(@50);
    }];
    _commitBtn = commitBtn;
}
-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [self.txtNickName becomeFirstResponder];
    
}

#pragma mark - UITextFieldDelegate
- (void)textFieldDidChange:(UITextField *)textField
{
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self.view endEditing:YES];
    return YES;
}


-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}



- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    
    //这里的if时候为了获取删除操作,如果没有次if会造成当达到字数限制后删除键也不能使用的后果.
    if (string.length == 0) {
        return YES;
    }
    
    //so easy
    else if (textField.text.length >0) {
//        [self setRightButtonEnabled:YES];
        return YES;
    }
    
    return YES;
}

- (void)commitBtnClick
{
    [self.view endEditing:YES];
    
    if(self.txtNickName.text.length==0)
    {
        UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"" message:WeXLocalizedString(@"没有输入昵称，请重新填写")
                                                      delegate:nil cancelButtonTitle:WeXLocalizedString(@"我知道了") otherButtonTitles: nil];
        [alert show];
        return;
    }
    else if(self.txtNickName.text.length>0)
    {
        [[WexDefaultConfig instance] setNickName:self.txtNickName.text];
        [[WexDefaultConfig instance] commit];

        [WeXPorgressHUD showText:WeXLocalizedString(@"昵称修改成功") onView:self.view];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:WEX_CHANGE_NICK_NAME_NOTIFY object:nil userInfo:nil];
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self.navigationController popViewControllerAnimated:YES];
        });
        
    }
}

- (void)keyboardChangedWithTransition:(YYKeyboardTransition)transition {
    [UIView animateWithDuration:transition.animationDuration delay:0 options:transition.animationOption animations:^{
        ///用此方法获取键盘的rect
        CGRect kbFrame = [[YYKeyboardManager defaultManager] convertRect:transition.toFrame toView:self.view];
        ///从新计算view的位置并赋值
        CGFloat y = kbFrame.origin.y;
        [_commitBtn mas_updateConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(self.view).offset(-(kScreenHeight-y)-30);
            
        }];
        [self.view layoutIfNeeded];
        
    } completion:^(BOOL finished) {
        
    }];
    
    
    
}



-(void)dealloc
{
    [[YYKeyboardManager defaultManager] removeObserver:self];
}

#pragma mark - WexBaseControllerDelegate
-(void)MyViewController:(WeXBaseViewController *)controller onRightButton:(id)sender{
    
    if(self.txtNickName.text.length==0)
    {
        UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"" message:WeXLocalizedString(@"没有输入昵称，请重新填写")
                                                      delegate:nil cancelButtonTitle:WeXLocalizedString(@"我知道了") otherButtonTitles: nil];
        [alert show];
        return;
    }
    else if(self.txtNickName.text.length>0)
    {
        [[WexDefaultConfig instance] setNickName:self.txtNickName.text];
        [[WexDefaultConfig instance] commit];
        
        //        NSString* nick2=[[WexDefaultConfig instance] nickName];
        //        [WexDefaultConfig clearAll]; // 清空default
//        [[IYNotifyManager shareInstance] postNotifyKey:IY_NOTIFY_NICKNAME_CHANGED]; // 通知昵称变化
        [WeXPorgressHUD showText:WeXLocalizedString(@"昵称修改成功") onView:self.view];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self.navigationController popViewControllerAnimated:YES];
        });
        
    }
}


@end

