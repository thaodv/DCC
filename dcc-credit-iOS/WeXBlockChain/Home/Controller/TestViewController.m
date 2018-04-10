//
//  TestViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/10.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "TestViewController.h"
//#import <MGLivenessDetection/MGLivenessDetection.h>
//#import <MGIDCard/MGIDCard.h>

@interface TestViewController ()
{
    UIImageView *_imageView;
}

@end

@implementation TestViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    UIButton *btn  = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = CGRectMake(50, 100, 100, 60);
    [btn setTitle:@"IDCard" forState:UIControlStateNormal];
    btn.backgroundColor = [UIColor blackColor];
    [btn addTarget:self action:@selector(btnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
    
    UIButton *btn1  = [UIButton buttonWithType:UIButtonTypeCustom];
    btn1.frame = CGRectMake(200, 100, 100, 60);
    [btn1 setTitle:@"LIVE" forState:UIControlStateNormal];
    btn1.backgroundColor = [UIColor blackColor];
    [btn1 addTarget:self action:@selector(btn1Click) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
    
    _imageView = [[UIImageView alloc] initWithFrame:CGRectMake(10,200, 400, 500)];
    _imageView.backgroundColor = [UIColor blueColor];
    [self.view addSubview:_imageView];
//    [MGLicenseManager licenseForNetWokrFinish:^(bool License) {
//        NSLog(@"%@", [NSString stringWithFormat:@"授权%@", License ? @"成功" : @"失败"]);
//    }];
    
}

- (void)btnClick{
    
//    __unsafe_unretained TestViewController *weakSelf = self;
//    BOOL idcard = [MGIDCardManager getLicense];
//    if (!idcard) {
//        [[[UIAlertView alloc] initWithTitle:@"提示" message:@"SDK授权失败，请检查" delegate:self cancelButtonTitle:@"完成" otherButtonTitles:nil, nil] show];
//        return;
//    }
//
//    MGIDCardManager *cardManager = [[MGIDCardManager alloc] init];
//    [cardManager setScreenOrientation:MGIDCardScreenOrientationLandscapeLeft];
//    [cardManager IDCardStartDetection:self
//                           IdCardSide: IDCARD_SIDE_FRONT
//                               finish:^(MGIDCardModel *model) {
//                                   _imageView.image = [model croppedImageOfIDCard];
//                               }
//                                 errr:^(MGIDCardError errorType) {
//                                     _imageView.image = nil;
//                                 }];
//
}
//
//- (void)btn1Click{
//
//    MGLiveManager *liveManager = [[MGLiveManager alloc] init];
//    liveManager.actionCount = 3;
//    liveManager.actionTimeOut = 10;
//    liveManager.randomAction = NO;
//    if (1) {
//        NSMutableArray* actionMutableArray = [[NSMutableArray alloc] initWithCapacity:liveManager.actionCount];
//        for (int i = 1; i <= liveManager.actionCount; i++) {
//            [actionMutableArray addObject:[NSNumber numberWithInt:i]];
//        }
//        liveManager.actionArray = (NSArray *)actionMutableArray;
//    }
//    [liveManager startFaceDecetionViewController:self
//                                          finish:^(FaceIDData *finishDic, UIViewController *viewController) {
//                                              [viewController dismissViewControllerAnimated:YES completion:nil];
//                                              NSData *resultData = [[finishDic images] valueForKey:@"image_best"];
//                                              UIImage *resultImage = [UIImage imageWithData:resultData];
//                                              _imageView.image = resultImage;
//                                              NSLog(@"resultImage=%@",resultImage);
//                                          }
//                                           error:^(MGLivenessDetectionFailedType errorType, UIViewController *viewController) {
//                                               [viewController dismissViewControllerAnimated:YES completion:nil];
//                                               NSLog(@"ERROR");
//                                           }];
//}
//
//
//- (void)didReceiveMemoryWarning {
//    [super didReceiveMemoryWarning];
//    // Dispose of any resources that can be recreated.
//}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
