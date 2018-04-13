//
//  WeXAuthorizeLoginRecordRLMModel.h
//  WeXBlockChain
//
//  Created by wcc on 2017/12/4.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import <Realm/Realm.h>

@interface WeXAuthorizeLoginRecordRLMModel : RLMObject


@property NSString *appId;

@property NSString *appName;

@property NSDate *date;

@end
