//
//  WeXIpfsTokenModel.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface WeXIpfsTokenModel : NSObject

@property (nonatomic,copy)NSString *walletAddress;
@property (nonatomic,copy)NSString *contractAddress;
@property (nonatomic,copy)NSString *version;
@property (nonatomic,copy)NSString *cipher;
@property (nonatomic,copy)NSString *nonce;
@property (nonatomic,copy)NSString *token;
@property (nonatomic,copy)NSString *digest1;
@property (nonatomic,copy)NSString *digest2;

@end
