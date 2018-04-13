//
//  WeXPassportIDInfoCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/2/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXPassportIDInfoModel : NSObject
@property (nonatomic,copy)NSString *title;
@property (nonatomic,copy)NSString *content;

@end

typedef void(^RefreshBlock)(NSString *content,NSIndexPath *indexPath);

@interface WeXPassportIDInfoCell : UITableViewCell<UITextViewDelegate>
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UITextView *contentTextView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *contentHeightConstraint;

@property (nonatomic,strong)WeXPassportIDInfoModel *model;
@property (nonatomic,strong)NSIndexPath *indexPath;
@property (nonatomic,copy)RefreshBlock refreshBlock;

- (void)configWithModel:(WeXPassportIDInfoModel *)model indexPath:(NSIndexPath *)indexPath;

+ (CGFloat)heightWithModel:(WeXPassportIDInfoModel *)model indexPath:(NSIndexPath *)indexPath;

@end
