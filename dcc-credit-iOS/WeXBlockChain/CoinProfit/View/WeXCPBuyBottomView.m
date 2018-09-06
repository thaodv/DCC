//
//  WeXCPBuyBottomView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPBuyBottomView.h"

@interface WeXCPBuyBottomView ()

@property(nonatomic, copy) void (^WeXCPBuyBottomClick)(WeXCPBuyBottomViewType);
@property (nonatomic, weak) UILabel *tipsLab;
@property (nonatomic, weak) YYLabel *contentLab;
@property (nonatomic, weak) UIButton *buyInButton;

@end

@implementation WeXCPBuyBottomView

+ (instancetype)createBuyBottomView:(CGRect)frame
                         clickEvent:(void(^)(WeXCPBuyBottomViewType))click {
    WeXCPBuyBottomView *bottomView = [[WeXCPBuyBottomView alloc] initWithFrame:frame];
    [bottomView setBackgroundColor:[UIColor whiteColor]];
    bottomView.WeXCPBuyBottomClick = click;
    return bottomView;
}
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self wex_loadViews];
        [self wex_layoutConstraints];
    }
    return self;
}
- (void)wex_loadViews {
    UILabel *tipsLab = CreateLeftAlignmentLabel(WexFont(14), WexDefault4ATitleColor);
    NSMutableAttributedString *attri = [[NSMutableAttributedString alloc] initWithString:@"温馨提示："];
    //NSTextAttachment可以将要插入的图片作为特殊字符处理
    NSTextAttachment *attch = [[NSTextAttachment alloc] init];
    //定义图片内容及位置和大小
    attch.image = [UIImage imageNamed:@"ic_warning_black_24dp_2x"];
    attch.bounds = CGRectMake(0, -2, 17, 14);
    NSAttributedString *string = [NSAttributedString attributedStringWithAttachment:attch];
    [attri insertAttributedString:string atIndex:0];
    tipsLab.attributedText = attri;
    [self addSubview:tipsLab];
    self.tipsLab = tipsLab;
    
    NSString *text = @"• 币生息只能使用私链上的DCC；\n• 通过跨链交易将公链上的DCC转移到私链上，即可购买币生息产品。公链转到私链 >";
    NSString *highText = @"公链转到私链 >";
    YYLabel *contentLab = [YYLabel new];
    contentLab.textColor = WexDefault4ATitleColor;
    contentLab.font = WexFont(14.0f);
    contentLab.textAlignment = NSTextAlignmentLeft;
    contentLab.numberOfLines = 0;
    contentLab.preferredMaxLayoutWidth = kScreenWidth - 20;
    NSMutableAttributedString *attribute = [[NSMutableAttributedString alloc] initWithString:text];
    attribute.yy_lineSpacing = 10;
    attribute.yy_color = WexDefault4ATitleColor;
    attribute.yy_font = WexFont(14.0);
    NSRange range = [text rangeOfString:highText];
    __weak typeof(self) weakSelf     = self;
    [attribute yy_setTextHighlightRange:range color:ColorWithHex(0x6766CC) backgroundColor:nil tapAction:^(UIView *  containerView, NSAttributedString *  text, NSRange range, CGRect rect) {
        !weakSelf.WeXCPBuyBottomClick ? : weakSelf.WeXCPBuyBottomClick(WeXCPBuyBottomViewTypeLink);
    }];
    contentLab.numberOfLines = 0;
    contentLab.attributedText = attribute;
    [self addSubview:contentLab];
    self.contentLab = contentLab;
    
    WeXCustomButton *buyButton = [WeXCustomButton button];
    [buyButton setTitle:@"立即认购" forState:UIControlStateNormal];
    [buyButton addTarget:self action:@selector(buyInEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:buyButton];
    self.buyInButton = buyButton;
}
- (void)buyInEvent:(WeXCustomButton *)button {
    !self.WeXCPBuyBottomClick ? : self.WeXCPBuyBottomClick(WeXCPBuyBottomViewTypeBuyIn);
}
- (void)wex_layoutConstraints {
    [self.buyInButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.height.mas_equalTo(40);
        make.bottom.mas_equalTo(-30);
    }];
    
    [self.contentLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.bottom.equalTo(self.buyInButton.mas_top).offset(-30);
    }];
    
    [self.tipsLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.bottom.equalTo(self.contentLab.mas_top).offset(-10);
    }];
}
- (void)setBuyInButtonType:(WexCPBuyButtonType)type
                     title:(NSString *)title {
    switch (type) {
        case WexCPBuyButtonTypeNormal: {
            [self.buyInButton setEnabled:true];
            [self.buyInButton setTitle:title forState:UIControlStateNormal];
        }
            break;
        case WexCPBuyButtonTypeDisable: {
            [self.buyInButton setEnabled:false];
            [self.buyInButton setTitle:title forState:UIControlStateDisabled];
        }
            break;
        default:
            break;
    }
}


@end
