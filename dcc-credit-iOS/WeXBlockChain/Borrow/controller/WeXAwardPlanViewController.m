//
//  WeXAwardPlanViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAwardPlanViewController.h"
#import "WeXAwardPlanCell.h"

#define contentLabelFont 15

#define contentLabelLineSpace 7

static const CGFloat kHeaderHeightWidthRatio = 199.0/339.0;

@interface WeXAwardPlanViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
}

@property (nonatomic,strong)NSMutableArray *datasArray;

@end

@implementation WeXAwardPlanViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"激励计划");
    [self setupSubViews];
    [self setNavigationNomalBackButtonType];
}




-(NSMutableArray *)datasArray
{
    if (_datasArray == nil) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}

//初始化滚动视图
-(void)setupSubViews{
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight) style:UITableViewStyleGrouped];;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.estimatedRowHeight = 150;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.rowHeight = UITableViewAutomaticDimension;
    [self.view addSubview:_tableView];
   
    
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, (kScreenWidth-30)*kHeaderHeightWidthRatio)];
    _tableView.tableHeaderView= headerView;
    
    UIImageView *headImageView = [[UIImageView alloc] init];
    headImageView.image = [UIImage imageNamed:WeXLocalizedString(@"award_head_card")];
    [headerView addSubview:headImageView];
    [headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(headerView);
        make.leading.equalTo(headerView).offset(15);
        make.trailing.equalTo(headerView).offset(-15);
   make.height.equalTo(headImageView.mas_width).multipliedBy(kHeaderHeightWidthRatio);
    }];
    
   
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        WeXAwardPlanCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXAwardPlanCell" owner:self options:nil] firstObject];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.contentLabel.numberOfLines = 0;
        cell.contentLabel.textColor = COLOR_LABEL_DESCRIPTION;
        cell.contentLabel.font = [UIFont systemFontOfSize:contentLabelFont];
        
        NSString *content = WeXLocalizedString(@"AwardPlanExplainText");
        //设置间距
        NSMutableAttributedString *attrStr1 = [[NSMutableAttributedString alloc] initWithString:content];
        NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
        paragraphStyle.lineSpacing = contentLabelLineSpace;
        paragraphStyle.alignment = NSTextAlignmentLeft;
        paragraphStyle.hyphenationFactor = 1.0;
        paragraphStyle.firstLineHeadIndent = 0.0;
        paragraphStyle.paragraphSpacingBefore = 0.0;
        paragraphStyle.headIndent = 0;
        paragraphStyle.tailIndent = 0;
        [attrStr1 addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content.length)];
        cell.contentLabel.attributedText = attrStr1;
        return cell;
    }
   else
   {
       WeXAwardPlanCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXAwardPlanCell" owner:self options:nil] lastObject];
       cell.backgroundColor = [UIColor clearColor];
       cell.selectionStyle = UITableViewCellSelectionStyleNone;
       cell.contentLabel.textColor = COLOR_LABEL_DESCRIPTION;
       cell.contentLabel.font = [UIFont systemFontOfSize:contentLabelFont];
       if (indexPath.row == 1) {
           //
           NSString *content = WeXLocalizedString(@"AwardPlanExplainTextData");
           //设置间距
           NSMutableAttributedString *attrStr1 = [[NSMutableAttributedString alloc] initWithString:content];
           NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
           paragraphStyle.lineSpacing = contentLabelLineSpace;
           paragraphStyle.alignment = NSTextAlignmentLeft;
           paragraphStyle.hyphenationFactor = 1.0;
           paragraphStyle.firstLineHeadIndent = 0.0;
           paragraphStyle.paragraphSpacingBefore = 0.0;
           paragraphStyle.headIndent = 0;
           paragraphStyle.tailIndent = 0;
           [attrStr1 addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content.length)];
           cell.contentLabel.attributedText = attrStr1;
           
       }
       else if (indexPath.row == 2)
       {
           //
           NSString *content = WeXLocalizedString(@"AwardPlanExplainTextSpread");
           //设置间距
           NSMutableAttributedString *attrStr1 = [[NSMutableAttributedString alloc] initWithString:content];
           NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
           paragraphStyle.lineSpacing = contentLabelLineSpace;
           paragraphStyle.alignment = NSTextAlignmentLeft;
           paragraphStyle.hyphenationFactor = 1.0;
           paragraphStyle.firstLineHeadIndent = 0.0;
           paragraphStyle.paragraphSpacingBefore = 0.0;
           paragraphStyle.headIndent = 0;
           paragraphStyle.tailIndent = 0;
           [attrStr1 addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content.length)];
           cell.contentLabel.attributedText = attrStr1;
       }
       else if (indexPath.row == 3)
       {
           //
           NSString *content = WeXLocalizedString(@"AwardPlanExplainTextContibuteOrDig");
           //设置间距
           NSMutableAttributedString *attrStr1 = [[NSMutableAttributedString alloc] initWithString:content];
           NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
           paragraphStyle.lineSpacing = contentLabelLineSpace;
           paragraphStyle.alignment = NSTextAlignmentLeft;
           paragraphStyle.hyphenationFactor = 1.0;
           paragraphStyle.firstLineHeadIndent = 0.0;
           paragraphStyle.paragraphSpacingBefore = 0.0;
           paragraphStyle.headIndent = 0;
           paragraphStyle.tailIndent = 0;
           [attrStr1 addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content.length)];
           cell.contentLabel.attributedText = attrStr1;
       }
       else if (indexPath.row == 4)
       {
           //
           NSString *content = WeXLocalizedString(@"AwardPlanExplainTextEco");
           //设置间距
           NSMutableAttributedString *attrStr1 = [[NSMutableAttributedString alloc] initWithString:content];
           NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
           paragraphStyle.lineSpacing = contentLabelLineSpace;
           paragraphStyle.alignment = NSTextAlignmentLeft;
           paragraphStyle.hyphenationFactor = 1.0;
           paragraphStyle.firstLineHeadIndent = 0.0;
           paragraphStyle.paragraphSpacingBefore = 0.0;
           paragraphStyle.headIndent = 0;
           paragraphStyle.tailIndent = 0;
           [attrStr1 addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content.length)];
           cell.contentLabel.attributedText = attrStr1;
       }
       return cell;
   }
    return nil;
    
    
}

@end
