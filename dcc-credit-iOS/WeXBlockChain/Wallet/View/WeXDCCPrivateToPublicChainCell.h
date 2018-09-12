//
//  WeXDCCPrivateToPublicChainCell.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXDCCPrivateToPublicChainCell : UITableViewCell

@end

@interface WeXDCCPrivateToPublicChainFirstCell : UITableViewCell

@property (nonatomic,strong)UILabel *privateBalanceLabel;

@property (nonatomic,strong)UILabel *publicBalanceLabel;

+ (CGFloat)height;

@end

typedef void(^AllButtonBlock)(void);

@interface WeXDCCPrivateToPublicChainSecondCell : UITableViewCell

@property (nonatomic,strong)UITextField *valueTextField;
@property (nonatomic,strong)UILabel *privateBalanceLabel;

@property (nonatomic,copy)AllButtonBlock allButtonBlock;
+ (CGFloat)height;

@end

@interface WeXDCCPrivateToPublicChainThirdCell : UITableViewCell
@property (nonatomic,strong)UILabel *titleLabel;
@property (nonatomic,strong)UILabel *contentLabel;
+ (CGFloat)height;
@end


typedef void(^NextButtonBlock)(void);

@interface WeXDCCPrivateToPublicChainFourthCell : UITableViewCell

@property (nonatomic,copy)NextButtonBlock nextButtonBlock;
+ (CGFloat)height;

@end

