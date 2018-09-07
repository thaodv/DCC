//
//  WeXScanViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/15.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXScanViewController.h"
#import "ScannerView.h"
#import "ScannerBackgroundView.h"
#import "UIView+Frame.h"
#import <AVFoundation/AVFoundation.h>
#import "WeXAuthorizeLoginViewController.h"
#import "WeXImportViewController.h"
#import "WeXWalletTransferViewController.h"
#import "WeXAddReceiveAddressViewController.h"
#import "WeXInviteCodeViewController.h"

@interface WeXScanViewController ()<AVCaptureMetadataOutputObjectsDelegate,UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property (strong,nonatomic) UILabel *introudctionLabel;
@property (strong,nonatomic) ScannerView *scannerView;
@property (strong,nonatomic) ScannerBackgroundView *scannerBackgroundView;

@property (strong,nonatomic) AVCaptureSession *scannerSession;
@property (strong,nonatomic) AVCaptureVideoPreviewLayer *videoPreviewLayer;

@end


@implementation WeXScanViewController

- (void)viewDidLoad {
    self.backgroundType = WeXBaseViewBackgroundTypeNone;
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupNavgationType];
    self.navigationItem.title = WeXLocalizedString(@"扫一扫");
    [self.view setBackgroundColor:[UIColor blackColor]];
    
    [self.view addSubview:self.introudctionLabel];
    [self.view addSubview:self.scannerView];
    [self.view addSubview:self.scannerBackgroundView];
    [self.view.layer insertSublayer:self.videoPreviewLayer atIndex:0];
    
    [self addMasonry];
    
    self.scannerType = ScannerTypeQR;
}

- (void)setupNavgationType{
    
   UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithTitle:WeXLocalizedString(@"相册") style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;
    
}

- (void)rightItemClick{
    UIImagePickerController *ctrl = [[UIImagePickerController alloc]init];
    [ctrl setSourceType:UIImagePickerControllerSourceTypePhotoLibrary];
    ctrl.navigationBar.barStyle = UIStatusBarStyleLightContent;
    ctrl.delegate = self;
    [self presentViewController:ctrl animated:YES completion:nil];

}

#pragma mark -- <UIImagePickerControllerDelegate>--
// 获取图片后的操作
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info
{
    // 销毁控制器
    [picker dismissViewControllerAnimated:YES completion:nil];
    
    // 设置图片
    UIImage *image = info[UIImagePickerControllerOriginalImage];
    [self scannerQRCodeFromImage:image ans:^(NSString *response) {
        if (response == nil)
        {
            [WeXPorgressHUD showText:WeXLocalizedString(@"扫码失败") onView:self.view];
            [self startCodeReading];
        }
        else
        {
            [self analysisQRAnswer:response];
        }
        
    }];
}


-(void)addMasonry
{
    
    [self.scannerView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.view);
        make.centerY.mas_equalTo(self.view).mas_offset(-55);
        make.width.and.height.mas_equalTo(0);
    }];
    
    [self.scannerBackgroundView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(self.view);
    }];
    
    [_scannerBackgroundView addMasonryWithContainView:self.scannerView];
    
    [self.introudctionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.and.width.mas_equalTo(self.view);
        make.top.mas_equalTo(self.scannerView.mas_bottom).mas_offset(30);
    }];
}


/**
 *  Getter and Setter
 *
 */
-(AVCaptureSession *)scannerSession
{
    
    if (_scannerSession == nil) {
        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
        
        NSError *error = nil;
        AVCaptureDeviceInput *input = [AVCaptureDeviceInput deviceInputWithDevice:device error:&error];
        if (error) {//没有摄像头
            return nil;
        }
        AVCaptureMetadataOutput *output = [[AVCaptureMetadataOutput alloc]init];
        [output setMetadataObjectsDelegate:self queue:dispatch_get_main_queue()];
        
        AVCaptureSession *session = [[AVCaptureSession alloc]init];
        if ([session canSetSessionPreset:AVCaptureSessionPreset1920x1080]) {
            [session setSessionPreset:AVCaptureSessionPreset1920x1080];
        }else if([session canSetSessionPreset:AVCaptureSessionPreset1280x720]){
            [session setSessionPreset:AVCaptureSessionPreset1280x720];
        }else{
            [session setSessionPreset:AVCaptureSessionPresetPhoto];
        }
        
        if ([session canAddInput:input]) {
            [session addInput:input];
        }
        
        if ([session canAddOutput:output]) {
            [session addOutput:output];
        }
        [output setMetadataObjectTypes:@[AVMetadataObjectTypeEAN13Code,AVMetadataObjectTypeEAN8Code,AVMetadataObjectTypeCode128Code,AVMetadataObjectTypeQRCode]];
        _scannerSession = session;
    }
    return _scannerSession;
}

-(AVCaptureVideoPreviewLayer *)videoPreviewLayer
{
    if (_videoPreviewLayer == nil) {
        _videoPreviewLayer = [AVCaptureVideoPreviewLayer layerWithSession:self.scannerSession];
        [_videoPreviewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
        [_videoPreviewLayer setFrame:self.view.layer.bounds];
    }
    return _videoPreviewLayer;
}

-(UILabel *)introudctionLabel
{
    if (_introudctionLabel == nil) {
        _introudctionLabel = [[UILabel alloc]init];
        [_introudctionLabel setBackgroundColor:[UIColor clearColor]];
        [_introudctionLabel setTextAlignment:NSTextAlignmentCenter];
        [_introudctionLabel setFont:[UIFont systemFontOfSize:12.0f]];
        [_introudctionLabel setTextColor:[UIColor whiteColor]];
    }
    return _introudctionLabel;
}

-(ScannerView *)scannerView
{
    if (_scannerView == nil) {
        _scannerView = [[ScannerView alloc]init];
    }
    return _scannerView;
}

-(ScannerBackgroundView *)scannerBackgroundView
{
    if (_scannerBackgroundView == nil) {
        _scannerBackgroundView = [[ScannerBackgroundView alloc] init];
    }
    return _scannerBackgroundView;
}

-(void)setScannerType:(ScannerType)scannerType
{
    if (_scannerType == scannerType) {
        return;
    }
    _scannerType = scannerType;
    CGFloat width = 0;
    CGFloat height = 0;
    if (scannerType == ScannerTypeQR) {
        [self.introudctionLabel setText:WeXLocalizedString(@"将二维码/条码放入框内，即可自动扫描")];
        width = height = kScreenWidth * 0.7;
    }else if(scannerType == ScannerTypeCover){
        [self.introudctionLabel setText:WeXLocalizedString(@"将书、CD、电影海报放入框内，即可自动扫描")];
        width = height = kScreenWidth * 0.85;
    }else if(scannerType == ScannerTypeStreet){
        [self.introudctionLabel setText:WeXLocalizedString(@"扫一下周围环境，寻找附近街景")];
        width = height = kScreenWidth * 0.85;
    }else if(scannerType == ScannerTypeTranslate){
        width = kScreenWidth * 0.7;
        height = 55;
        [self.introudctionLabel setText:WeXLocalizedString(@"将英文单词放入框内")];
    }
    
    [self.scannerView setHiddenScannerIndicator:scannerType == ScannerTypeTranslate];
    [UIView animateWithDuration:0.3 animations:^{
        [self.scannerView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.width.mas_equalTo(width);
            make.height.mas_equalTo(height);
        }];
        [self.view layoutIfNeeded];
    }];
    
    //rect值范围0-1，基准点在右上角
    CGRect rect = CGRectMake(self.scannerView.y / kScreenHeight,self.scannerView.x / kScreenWidth, self.scannerView.frameHeight / kScreenHeight, self.scannerView.frameWidth / kScreenWidth);
    [self.scannerSession.outputs[0] setRectOfInterest:rect];
    if (!self.isRunning) {
        [self startCodeReading];
    }
}

//识别图片中二维码
- (void)scannerQRCodeFromImage:(UIImage *)image ans:(void (^)(NSString *))ans
{
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSData *imageData = (UIImagePNGRepresentation(image) ? UIImagePNGRepresentation(image) :UIImageJPEGRepresentation(image, 1));
        CIImage *ciImage = [CIImage imageWithData:imageData];
        NSString  *ansStr = nil;
        if (ciImage) {
            CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeQRCode context:[CIContext contextWithOptions:nil] options:@{CIDetectorAccuracy:CIDetectorAccuracyHigh}];
            NSArray *features = [detector featuresInImage:ciImage];
            if (features.count) {
                for (CIFeature *feature in features) {
                    if ([feature isKindOfClass:[CIQRCodeFeature class]]) {
                        ansStr = ((CIQRCodeFeature *)feature).messageString;
                        break;
                    }
                }
            }
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            ans(ansStr);
        });
    });
}


/**
 *  开始扫描
 */
-(void)startCodeReading
{
    [self.scannerView startScanner];
    [self.scannerSession startRunning];
    
}

/**
 * 停止扫描
 *
 */
-(void)stopCodeReading
{
    [self.scannerView stopScanner];
    [self.scannerSession stopRunning];
}

-(BOOL)isRunning
{
    return [self.scannerSession isRunning];
}

#pragma mark - AVCaptureMetadataOutputObjectsDelegate
-(void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection
{
    if (metadataObjects.count > 0) {
        AVMetadataMachineReadableCodeObject *obj = metadataObjects[0];
        [self analysisQRAnswer:obj.stringValue];
    }
}

-(void)analysisQRAnswer:(NSString *)ansStr
{
    NSLog(@"ansStr=%@",ansStr);
    //导入的流程
    if (self.handleType == WeXScannerHandleTypeImport) {
        if (self.responseBlock) {
            self.responseBlock(ansStr);
        }
        for (UIViewController *ctrl in self.navigationController.viewControllers) {
            if ([ctrl isKindOfClass:[WeXImportViewController class]]) {
                [self.navigationController popToViewController:ctrl animated:YES];
            }
        }
    }
    else if (self.handleType == WeXScannerHandleTypeLogin)
    {
        NSData *jsonData = [ansStr dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *responseDict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:nil];
        NSLog(@"responseDict=%@",responseDict);
        if (responseDict == nil) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"不支持该类型二维码") onView:self.view];
            [self startCodeReading];
            return;
        }
        NSString *protocol = [responseDict objectForKey:@"protocol"];
        if (![protocol isEqualToString:@"wtx.wexchain.io"]) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"不支持该类型二维码") onView:self.view];
            [self startCodeReading];
            return;
        }
        NSNumber *version = [responseDict objectForKey:@"version"];
        if ([version integerValue] > [[WexCommonFunc getVersion] integerValue]) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"不支持该类型二维码") onView:self.view];
            [self startCodeReading];
            return;
        }
        NSString *appId = [responseDict objectForKey:@"appId"];
        if (appId == nil||appId.length == 0) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"不支持该类型二维码") onView:self.view];
            [self startCodeReading];
            return;
        }
        NSString *appName = [responseDict objectForKey:@"appName"];
        if (appName == nil||appName.length == 0) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"不支持该类型二维码") onView:self.view];
            [self startCodeReading];
            return;
        }
        NSString *nonce = [responseDict objectForKey:@"nonce"];
        if (nonce == nil||nonce.length == 0) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"不支持该类型二维码") onView:self.view];
            [self startCodeReading];
            return;
        }
        
        NSString *callbackUrl = [responseDict objectForKey:@"callbackUrl"];
        if (callbackUrl == nil||callbackUrl.length == 0) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"不支持该类型二维码") onView:self.view];
            [self startCodeReading];
            return;
        }
 
        WeXAuthorizeLoginViewController *ctrl = [[WeXAuthorizeLoginViewController alloc] init];
        ctrl.type = WeXAuthorizeLoginTypeWebpage;
        ctrl.paramsDict = responseDict;
        [self.navigationController pushViewController:ctrl animated:YES];
        if ([self.scannerSession isRunning]) {
            [self stopCodeReading];
        }
        
    }
    else if (self.handleType == WeXScannerHandleTypeToAddress)
    {
        if (self.responseBlock) {
            if ([ansStr hasPrefix:@"ethereum:"]) {
              ansStr = [ansStr substringFromIndex:9];
            }
            
            self.responseBlock(ansStr);
        }
        for (UIViewController *ctrl in self.navigationController.viewControllers) {
            if ([ctrl isKindOfClass:[WeXWalletTransferViewController class]]) {
                [self.navigationController popToViewController:ctrl animated:YES];
            }
        }
    }
    else if (self.handleType == WeXScannerHandleTypeManagerReceiveAddress)
    {
        if (self.responseBlock) {
            if ([ansStr hasPrefix:@"ethereum:"]) {
                ansStr = [ansStr substringFromIndex:9];
            }
            
            self.responseBlock(ansStr);
        }
        for (UIViewController *ctrl in self.navigationController.viewControllers) {
            if ([ctrl isKindOfClass:[WeXAddReceiveAddressViewController class]]) {
                [self.navigationController popToViewController:ctrl animated:YES];
            }
        }
    }
    else if (self.handleType == WeXScannerHandleTypeManagerInviteFriend)
    {
        if (self.responseBlock) {
            self.responseBlock(ansStr);
        }
        for (UIViewController *ctrl in self.navigationController.viewControllers) {
            if ([ctrl isKindOfClass:[WeXInviteCodeViewController class]]) {
                [self.navigationController popToViewController:ctrl animated:YES];
            }
        }
        
    }
    else
    {
         [WeXPorgressHUD showText:ansStr onView:self.view];
    }
}


-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self startCodeReading];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    if ([self.scannerSession isRunning]) {
        [self stopCodeReading];
    }
}

-(void)dealloc
{
    NSLog(@"%s",__func__);
}



@end
