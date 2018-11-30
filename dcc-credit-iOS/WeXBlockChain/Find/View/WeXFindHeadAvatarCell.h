//
//  WeXFindHeadAvatarCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@interface WeXFindHeadAvatarCell : WeXBaseTableViewCell

- (void)setAvatarImage:(UIImage *)avatarImage
              nickName:(NSString *)nickName;

- (void)setCacheModel:(WeXPasswordCacheModal *)cacheModel;

@end


