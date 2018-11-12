//
//  WeXBindWeChatModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXBindWeChatModel : WeXBaseNetModal

@end

@interface WeXBindWeChatLoginModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *address;
@property (nonatomic, copy) NSString *code;

@end

@interface WeXBindWeChatResModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *result;

@end


