//
//  WeXPassportIDInfoCell.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDInfoCell.h"

#define kTitleFont 14
#define kContentFont 14

@implementation WeXPassportIDInfoModel

@end

@implementation WeXPassportIDInfoCell

- (void)awakeFromNib {
    [super awakeFromNib];
    self.titleLabel.textColor = [UIColor lightGrayColor];
    
    self.contentTextView.textColor = [UIColor lightGrayColor];
    self.contentTextView.backgroundColor = [UIColor clearColor];
    self.contentTextView.scrollEnabled = NO;
    self.contentTextView.textContainerInset = UIEdgeInsetsMake(0, 0, 0, 0);
    self.contentTextView.delegate = self;
}

- (void)configWithModel:(WeXPassportIDInfoModel *)model indexPath:(NSIndexPath *)indexPath
{
    _model = model;
    _indexPath = indexPath;
    self.titleLabel.text = model.title;
    
    self.contentTextView.text = model.content;
    
    if (indexPath.row == 4) {
        NSString *title = model.title;
        CGFloat titleWidth = [title boundingRectWithSize:CGSizeMake(MAXFLOAT, 20) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:kTitleFont]} context:nil].size.width;
        
        NSString *content = model.content;
        NSLog(@"content=%@",content);
        CGFloat contentHeight = [content boundingRectWithSize:CGSizeMake(kScreenWidth-10-titleWidth-5-10, MAXFLOAT) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:kTitleFont]} context:nil].size.height;
        NSLog(@"contentHeight=%f",contentHeight);
        self.contentHeightConstraint.constant = contentHeight+2<44?44:contentHeight+2;
    }
    
}

+ (CGFloat)heightWithModel:(WeXPassportIDInfoModel *)model indexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 4) {
        NSString *title = model.title;
        CGFloat titleWidth = [title boundingRectWithSize:CGSizeMake(MAXFLOAT, 20) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:kTitleFont]} context:nil].size.width;
        
        NSString *content = model.content;
        CGFloat contentHeight = [content boundingRectWithSize:CGSizeMake(kScreenWidth-10-titleWidth-5-10, MAXFLOAT) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:kTitleFont]} context:nil].size.height;
        if (contentHeight+1+24 < 44) {
            return 44;
        }
        return  contentHeight+1+24;
    }
    return 44;
}

-(void)textViewDidChange:(UITextView *)textView
{
    if (self.refreshBlock) {
        self.refreshBlock(textView.text, _indexPath);
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range
 replacementText:(NSString *)text
{
    // Any new character added is passed in as the "text" parameter
    if ([text isEqualToString:@"\n"]) {
        // Be sure to test for equality using the "isEqualToString" message
        [textView resignFirstResponder];
        
        // Return FALSE so that the final '\n' character doesn't get added
        return FALSE;
    }
    // For any other character return TRUE so that the text gets added to the view
    return TRUE;
}
@end
