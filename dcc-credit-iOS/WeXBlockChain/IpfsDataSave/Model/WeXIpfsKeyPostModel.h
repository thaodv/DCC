//
//  WeXIpfsKeyPostModel.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXIpfsKeyPostModel : WeXBaseNetModal

@property (nonatomic,copy)NSString *putIpfsKey;

@end

@interface  WeXIpfsKeyPostResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
