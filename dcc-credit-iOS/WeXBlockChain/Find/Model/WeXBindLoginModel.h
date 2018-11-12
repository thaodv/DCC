//
//  WeXBindLoginModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXBindLoginModel : WeXBaseNetModal

@end

@interface WeXBindLoginParmaModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *address;
@property (nonatomic, copy) NSString *nonce;


@end

@interface WeXBindLoginResIdentityListItemModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *idNum;
@property (nonatomic, copy) NSString *memberId;
@property (nonatomic, copy) NSString *identity;
@property (nonatomic, copy) NSString *type;

@end

@protocol WeXBindLoginResIdentityListItemModel;

@interface WeXBindLoginResMemberModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *idNum;
@property (nonatomic, copy) NSString *inviteId;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *profilePhoto;
@property (nonatomic, copy) NSString *status;
@property (nonatomic, strong) NSArray <WeXBindLoginResIdentityListItemModel *> <WeXBindLoginResIdentityListItemModel> *identityList;

@end

@interface WeXBindLoginResPyaerModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *idNum;
@property (nonatomic, copy) NSString *nickName;
@property (nonatomic, copy) NSString *portrait;
@property (nonatomic, copy) NSString *unionId;
@end


@protocol WeXBindLoginResPyaerModel;
@protocol WeXBindLoginResMemberModel;

@interface WeXBindLoginResModel : WeXBaseNetModal
@property (nonatomic, strong) WeXBindLoginResMemberModel <WeXBindLoginResMemberModel> *member;
@property (nonatomic, strong) WeXBindLoginResPyaerModel <WeXBindLoginResPyaerModel>*player;

@end



