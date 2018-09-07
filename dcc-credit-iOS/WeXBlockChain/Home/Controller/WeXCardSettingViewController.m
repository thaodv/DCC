//
//  WeXCardSettingViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/15.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXCardSettingViewController.h"
#import "WeXAboutUsWebViewController.h"
#import "WeXPassportDescriptionViewController.h"
#import "WeXPassportDeleteViewController.h"
#import "WeXPassportModifyPasswordController.h"
#import "WeXPassportBackupViewController.h"
#import "WeXPassportLocationViewController.h"

#import "WeXPrivacyPolicyWebViewController.h"
#import "WeXAddressBookController.h"

#import "WeXCardSettingCell.h"
#import "UIImage-Extensions.h"
#import "IYFileManager.h"
#import "WexNickNameViewController.h"
#import "SettingFaceViewCell.h"
#import "WeXVersionUpdateManager.h"
#import "WeXSelectedNodeViewController.h"


#define kTagNormalPicture 100
typedef void(^SafeVertifyResponse)(void);

@interface WeXCardSettingViewController ()<UIActionSheetDelegate,UIImagePickerControllerDelegate,UINavigationControllerDelegate,UIPopoverControllerDelegate,UITableViewDelegate,UITableViewDataSource,WeXPasswordManagerDelegate>
{
    UITableView *_tableView;
    UILabel *_safetyDescriptionLabel;
    WeXPasswordCacheModal *_model;
    
    WeXPasswordManager *_manager;

}

@property (nonatomic,copy)SafeVertifyResponse response;

@end

@implementation WeXCardSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"钱包设置");
    [self setNavigationNomalBackButtonType];
    [self commonInit];
    [self setupSubViews];
    
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    if(_tableView)
        [_tableView reloadData];
}

- (void)commonInit{
    
    _model = [WexCommonFunc getPassport];
    NSLog(@"_model=%@",_model);
    
}

// 昵称发生变化
-(void)notifyNickNameChanged:(NSDictionary*)info{
    [WeXPorgressHUD showText:WeXLocalizedString(@"昵称修改成功") onView:self.view];
}

//初始化滚动视图
-(void)setupSubViews{
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.tableFooterView = [UIView new];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    //    _footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 200)];
    //    _footerView.backgroundColor = [UIColor clearColor];
    //    _tableView.tableFooterView = _footerView;
    //    _footerView.hidden = YES;
    [self.view addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+5);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(self.view);
    }];
    
    
}

- (void)showToastInfo{
    
//    //粘贴背景框
//    UIImageView *backImageView = [[UIImageView alloc] init];
//    backImageView.image = [UIImage imageNamed:@"frame2"];
//    [self.view addSubview:backImageView];
//    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.centerY.equalTo(self.view);
//        make.centerX.equalTo(self.view);
//        make.height.equalTo(@60);
//        make.width.equalTo(@240);
//    }];
//
//    //粘贴框提示文字
//    UILabel *closeLabel = [[UILabel alloc] init];
//    closeLabel.text = WeXLocalizedString(@"关闭本地安全保护成功，您可以开启其他安全保护方式。");
//    closeLabel.font = [UIFont systemFontOfSize:13];
//    closeLabel.textColor = COLOR_LABEL_DESCRIPTION;
//    closeLabel.textAlignment = NSTextAlignmentCenter;
//    closeLabel.numberOfLines = 0;
//    [backImageView addSubview:closeLabel];
//    [closeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(backImageView).offset(10);
//        make.leading.equalTo(backImageView).offset(10);
//        make.trailing.equalTo(backImageView).offset(-10);
//        make.bottom.equalTo(backImageView).offset(-10);
//    }];
    
    
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        [closeLabel removeFromSuperview];
//        [backImageView removeFromSuperview];
//    });
    
    [WeXPorgressHUD showText:WeXLocalizedString(@"关闭本地安全保护成功，您可以开启其他安全保护方式。") onView:self.view];

    
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    if(indexPath.row==0)
    {
        return 90.f;
    }
    
    return 60.f;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 11;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    static NSString *cellid_face= @"cellID_face";
    UITableViewCell* tableCell=nil;
    if(indexPath.row>0)
    {
        WeXCardSettingCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
        if (cell == nil) {
            cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXCardSettingCell" owner:self options:nil] firstObject];
            cell.backgroundColor = [UIColor clearColor];
            cell.titleLabel.textColor = COLOR_LABEL_TITLE;
            cell.titleLabel.font = [UIFont systemFontOfSize:18];
            cell.desLabel.textColor = COLOR_LABEL_TITLE;
            cell.desLabel.font = [UIFont systemFontOfSize:15];
            cell.accessoryType = UITableViewCellAccessoryNone;
            
//            UIView *backView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(cell.frame), CGRectGetHeight(cell.frame))];
//            backView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.1];
//            cell.selectedBackgroundView = backView;
        }
        
        tableCell=cell;
       
        if (indexPath.row == 1)
        {
            cell.titleLabel.text = WeXLocalizedString(@"昵称");
            if([WexDefaultConfig instance].nickName)
            {
//                cell.desLabel.text=[WexDefaultConfig instance].nickName;
            }
            else{
                cell.desLabel.text=@"";
            }
        }
        else if (indexPath.row == 2) {
            cell.titleLabel.text = WeXLocalizedString(@"钱包地址");
        }
        else if (indexPath.row == 3)
        {
            cell.titleLabel.text = WeXLocalizedString(@"备份钱包");
        }
        else if (indexPath.row == 4)
        {
            cell.titleLabel.text = WeXLocalizedString(@"WeXCardSettingVCChangeWalletPSD");
        }
        else if (indexPath.row == 5)
        {
            cell.titleLabel.text = WeXLocalizedString(@"地址簿");
        }
        else if (indexPath.row == 6) {
            cell.titleLabel.text = WeXLocalizedString(@"选择节点");
        }
        else if (indexPath.row == 7)
        {
            cell.titleLabel.text = WeXLocalizedString(@"本地安全保护");
            _model = [WexCommonFunc getPassport];
            if (_model.passwordType == WeXPasswordTypeNone) {
                cell.desLabel.text = WeXLocalizedString(@"已关闭");
            }
            else
            {
                cell.desLabel.text = WeXLocalizedString(@"已开启");
            }
            _safetyDescriptionLabel = cell.desLabel;
        }
//        else if (indexPath.row == 6)
//        {
//            cell.titleLabel.text = WeXLocalizedString(@"钱包介绍");
//        }
        else if (indexPath.row == 8)
        {
            cell.titleLabel.text = WeXLocalizedString(@"关于我们");
        }

//        else if (indexPath.row == 7)
//        {
//            cell.titleLabel.text = WeXLocalizedString(@"隐私协议");
//        }
        else if (indexPath.row == 9)

        {
            cell.titleLabel.text = WeXLocalizedString(@"删除钱包(注销)");
        }
      
        else if (indexPath.row == 10)
        {
            cell.titleLabel.text = WeXLocalizedString(@"版本更新");
            NSDictionary *infoDict = [[NSBundle mainBundle] infoDictionary];
            NSString *content = [NSString stringWithFormat:@"%@%@",WeXLocalizedString(@"当前版本"), [infoDict objectForKey:@"CFBundleShortVersionString"]];
            cell.desLabel.text = content;
        }
        
    }
    
    else{
        
        SettingFaceViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellid_face];
        if (cell == nil) {
            cell = [[[NSBundle mainBundle] loadNibNamed:@"SettingFaceViewCell" owner:self options:nil] firstObject];
            cell.backgroundColor = [UIColor clearColor];
            cell.accessoryType = UITableViewCellAccessoryNone;
            
            UIView *backView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(cell.frame), CGRectGetHeight(cell.frame))];
            backView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.1];
            cell.selectedBackgroundView = backView;
            tableCell=cell;
            
            if (indexPath.row == 0) {
                UIImage* face=[IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE];
                
                if(!face)
                    face=[UIImage imageNamed:@"digital_head"];
                
                cell.viewFace.image=face;
                
                if(cell.viewFace) {
                    cell.viewFace.layer.masksToBounds=YES;
                    cell.viewFace.layer.cornerRadius=5.f; //设置为图片宽度的一半出来为圆形
                }
                CGRect frame=cell.viewLine.frame;
                frame.size.height=0.5f;
                cell.viewLine.frame=frame;
            }
        }
    }
    
    return tableCell;
    
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    if (indexPath.row == 0) {
        [self clickedSelectPicture];
    }
    else if (indexPath.row == 1)
    {
        WexNickNameViewController* ctrl=[[WexNickNameViewController alloc] init];
        ctrl.nickName=[[WexDefaultConfig instance]nickName];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.row == 2) {
        WeXPassportLocationViewController *ctrl = [[WeXPassportLocationViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.row == 3)
    {
        WeXPassportBackupViewController *ctrl = [[WeXPassportBackupViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.row == 4)
    {
        WeXPassportModifyPasswordController *ctrl = [[WeXPassportModifyPasswordController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.row == 5)
    {
        WeXAddressBookController *ctrl = [[WeXAddressBookController alloc] init];
        ctrl.addressBookType = WeXMainAddressBookTypeRead;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.row == 6) {
        WeXSelectedNodeViewController *selectedNodeVC = [WeXSelectedNodeViewController new];
        [self.navigationController pushViewController:selectedNodeVC animated:YES];
    }
    
    else if (indexPath.row == 7)
    {
        _model = [WexCommonFunc getPassport];
        if (_model.passwordType == WeXPasswordTypeNone) {
            WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeCreate parentController:self];
            manager.delegate = self;
            [manager loadPassword];
            _manager = manager;
        }
        else
        {
            WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
            manager.delegate = self;
            [manager loadPassword];
            _manager = manager;
            
        }
    }
//    else if (indexPath.row == 6)
//    {
//        WeXPassportDescriptionViewController *ctrl = [[WeXPassportDescriptionViewController alloc] init];
//        [self.navigationController pushViewController:ctrl animated:YES];
//    }
    else if (indexPath.row == 8)
    {
        WeXAboutUsWebViewController *ctrl = [[WeXAboutUsWebViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    

//    else if (indexPath.row == 7)
//    {
//        WeXPrivacyPolicyWebViewController *ctrl = [[WeXPrivacyPolicyWebViewController alloc] init];
//        [self.navigationController pushViewController:ctrl animated:YES];
//    }
    else if (indexPath.row == 9)
    {
        WeXPassportDeleteViewController *ctrl = [[WeXPassportDeleteViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
  
    else if (indexPath.row == 10)
    {
        WeXVersionUpdateManager *manager = [WeXVersionUpdateManager shareManager];
        [manager configVersionUpdateViewOnView:self.view isUpdate:false];
    }

    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    cell.selected = NO;
}

#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerDidSetPasswordSuccess
{
    _safetyDescriptionLabel.text = WeXLocalizedString(@"已开启");
}

- (void)passwordManagerVerifySuccess{
    _safetyDescriptionLabel.text = WeXLocalizedString(@"已关闭");
    
    [self deletePassword];
    //关闭秘密后，显示提示
    [self showToastInfo];
}

- (void)passwordManagerVerifyException
{
     _safetyDescriptionLabel.text = WeXLocalizedString(@"已关闭");
}

- (void)deletePassword{
    //删除passport中密码
    _model = [WexCommonFunc getPassport];
    _model.numberPassword = nil;
    _model.gesturePassword = nil;
    _model.passwordType = WeXPasswordTypeNone;
    [WexCommonFunc savePassport:_model];
}

#pragma mark - selectPicture
// 头像选取
-(void)clickedSelectPicture{
    UIActionSheet* actionSheet = [[UIActionSheet alloc]
                                  initWithTitle:WeXLocalizedString(@"请选择来源") delegate:self cancelButtonTitle:WeXLocalizedString(@"取消") destructiveButtonTitle:nil
                                  otherButtonTitles:WeXLocalizedString(@"拍照"),WeXLocalizedString(@"从相册选择"),nil];
    actionSheet.tag=kTagNormalPicture;
    actionSheet.delegate=self;
    [actionSheet showInView:self.view];
}
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    switch(buttonIndex)
    {
        case 0:  // 取消
        {
            [self startCameraControllerFromViewController:self usingDelegate:self];
            break;
        }
        case 1: // 相册选择
        {
            [self startAlbumControllerFromViewController:self usingDelegate:self];
            break;
        }
        case 2: // 从相册选择
        {
            break;
        }
        default:
            break;
    }
}

- (BOOL) startCameraControllerFromViewController: (UIViewController*) controller
                                   usingDelegate: (id <UIImagePickerControllerDelegate,
                                                   UINavigationControllerDelegate>) delegate {
    
    if (([UIImagePickerController isSourceTypeAvailable:
          UIImagePickerControllerSourceTypeCamera] == NO)
        || (delegate == nil)
        || (controller == nil))
        return NO;
    
    
    UIImagePickerController *cameraUI = [[UIImagePickerController alloc] init];
    cameraUI.sourceType = UIImagePickerControllerSourceTypeCamera;
    
    // Displays a control that allows the user to choose picture or
    // movie capture, if both are available:
    cameraUI.mediaTypes =[NSArray arrayWithObject:@"public.image"];  //[UIImagePickerController availableMediaTypesForSourceType:UIImagePickerControllerSourceTypeCamera];
    
    // Hides the controls for moving & scaling pictures, or for
    // trimming movies. To instead show the controls, use YES.
    cameraUI.allowsEditing = true;
    cameraUI.showsCameraControls=YES;
    cameraUI.delegate = delegate;
    
    [controller presentModalViewController: cameraUI animated: YES];
    return YES;
}

-(void)startAlbumControllerFromViewController:(UIViewController*) controller
                                usingDelegate: (id <UIImagePickerControllerDelegate,
                                                UINavigationControllerDelegate,UIPopoverControllerDelegate>) delegate{
    UIImagePickerController *picker=[[UIImagePickerController alloc]init];
    picker.delegate=delegate;
    picker.allowsEditing=true;
    picker.sourceType=UIImagePickerControllerSourceTypePhotoLibrary;
    //    UIPopoverController * popOver = [[UIPopoverController alloc]initWithContentViewController: picker];
    //    [picker release];
    [controller presentModalViewController: picker animated: YES];
    //    popOver.delegate = delegate;
    //    self.popOverController = popOver;
    //    [popOver presentPopoverFromRect:CGRectMake(0,100, 0, 0) inView:self.view
    //                          permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
}

// For responding to the user tapping Cancel.
- (void) imagePickerControllerDidCancel: (UIImagePickerController *) picker {
    [picker dismissModalViewControllerAnimated:YES];
}



// For responding to the user accepting a newly-captured picture or movie
- (void) imagePickerController: (UIImagePickerController *) picker
 didFinishPickingMediaWithInfo: (NSDictionary *) info {
    
    [picker dismissModalViewControllerAnimated:YES];
    
    UIImage *faceImage = nil;
    //    if(self.editMode)
    faceImage=[info objectForKey:UIImagePickerControllerEditedImage];
    //    else
    //        image=[info objectForKey:UIImagePickerControllerOriginalImage];
    
    UIImage* image=faceImage;
    UIImage* thumbImage=[image imageByScalingProportionallyToMinimumSize:CGSizeMake(180, 180)];
    //    UIImage* face= [UIImage maskImage:thumbImage withMask:[UIImage imageNamed:@"face"]];
    
    [IYFileManager setCacheFileWithKey:WEX_FILE_USER_FACE WithValue:UIImagePNGRepresentation(thumbImage)];
    UIImage* image2=[IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE]; // 获取已保存头像180X180
    NSLog(@"FinishPickingMedia:%@",info);
    
    if(image2)
        [WeXPorgressHUD showText:WeXLocalizedString(@"头像已保存") onView:self.view];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:WEX_CHANGE_HEAD_IMAGE_NOTIFY object:nil userInfo:nil];
}

@end

