//
//  WeXPassportIDCameraController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDCameraController.h"
#import <MGIDCard/MGIDCard.h>

@interface WeXPassportIDCameraController ()

@end

@implementation WeXPassportIDCameraController

- (void)viewDidLoad {
    [super viewDidLoad];
    [MGLicenseManager licenseForNetWokrFinish:^(bool License) {
        NSLog(@"%@", [NSString stringWithFormat:@"授权%@", License ? @"成功" : @"失败"]);
    }];
    
//    __unsafe_unretained ViewController *weakSelf = self;
    BOOL idcard = [MGIDCardManager getLicense];
    if (!idcard) {
        [[[UIAlertView alloc] initWithTitle:@"提示" message:@"SDK授权失败，请检查" delegate:self cancelButtonTitle:@"完成" otherButtonTitles:nil, nil] show];
        return;
    }
    
    MGIDCardManager *cardManager = [[MGIDCardManager alloc] init];
    [cardManager setScreenOrientation:MGIDCardScreenOrientationLandscapeLeft];
    [cardManager IDCardStartDetection:self
                           IdCardSide: IDCARD_SIDE_FRONT
                               finish:^(MGIDCardModel *model) {
//                                   _imageView.image = [model croppedImageOfIDCard];
                               }
                                 errr:^(MGIDCardError errorType) {
//                                     _imageView.image = nil;
                                 }];
}


@end
