//
//  WeXDCCPublicToPrivateChainCell.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXDCCPublicToPrivateChainCell : UITableViewCell

@end
@interface WeXDCCPublicToPrivateChainFirstCell : UITableViewCell

@property (nonatomic,strong)UILabel *privateBalanceLabel;

@property (nonatomic,strong)UILabel *publicBalanceLabel;

+ (CGFloat)height;

@end

typedef void(^AllButtonBlock)(void);
@interface WeXDCCPublicToPrivateChainSecondCell : UITableViewCell
@property (nonatomic,copy)AllButtonBlock allButtonBlock;
@property (nonatomic,strong)UITextField *valueTextField;
@property (nonatomic,strong)UILabel *publicBalanceLabel;
+ (CGFloat)height;
@end

@interface WeXDCCPublicToPrivateChainThirdCell : UITableViewCell
@property (nonatomic,strong)UILabel *titleLabel;
@property (nonatomic,strong)UILabel *symbolLabel;
@property (nonatomic,strong)UITextField *valueTextField;
+ (CGFloat)height;
@end

@interface WeXDCCPublicToPrivateChainFourthCell : UITableViewCell

@property (nonatomic,strong)UILabel *feeLabel;
+ (CGFloat)height;

@end
typedef void(^NextButtonBlock)(void);


@interface WeXDCCPublicToPrivateChainFifthCell : UITableViewCell
@property (nonatomic,copy)NextButtonBlock nextButtonBlock;

+ (CGFloat)height;
@end
