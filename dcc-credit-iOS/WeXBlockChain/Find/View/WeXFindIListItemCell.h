//
//  WeXFindIListItemCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@class WeXLastPKResModel;
@class WeXFindAwardResModel;
@class WeXFindAwardResDetailModel;

@interface WeXFindIListItemCell : WeXBaseTableViewCell

- (void)setTitle:(NSString *)title
       imageName:(NSString *)imageName
        subTitle:(NSString *)subTitle
          subDes:(NSString *)subDesc;

- (void)setLastPkResModel:(WeXLastPKResModel *)model;

- (void)setSunValueModel:(WeXFindAwardResDetailModel *)model;


@end

