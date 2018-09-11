//
//  WeXIpfsHashPostModel.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXIpfsHashPostModel : WeXBaseNetModal
@property (nonatomic,copy)NSString *getIpfsToken;
@end


@interface WeXIpfsHashPostResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
