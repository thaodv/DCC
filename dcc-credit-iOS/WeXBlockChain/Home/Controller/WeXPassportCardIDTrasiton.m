//
//  WeXPassportCardIDTrasiton.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportCardIDTrasiton.h"
#import "WeXPassportViewController.h"
#import "WeXPassportIDViewController.h"
#import "WeXPassportCardCell.h"

@interface WeXPassportCardIDTrasiton()

/**
 *  动画过渡代理管理的是push还是pop
 */
@property (nonatomic, assign) WeXPassportCardTrasitonType type;


@end

@implementation WeXPassportCardIDTrasiton

+ (instancetype)transitionWithType:(WeXPassportCardTrasitonType)type{
    return [[self alloc] initWithTransitionType:type];
}

- (instancetype)initWithTransitionType:(WeXPassportCardTrasitonType)type{
    self = [super init];
    if (self) {
        _type = type;
    }
    return self;
}
/**
 *  动画时长
 */
- (NSTimeInterval)transitionDuration:(id<UIViewControllerContextTransitioning>)transitionContext{
    return 0.75;
}
/**
 *  如何执行过渡动画
 */
- (void)animateTransition:(id<UIViewControllerContextTransitioning>)transitionContext{
    switch (_type) {
        case WeXPassportCardTrasitonPush:
            [self doPushAnimation:transitionContext];
            break;
            
        case WeXPassportCardTrasitonPop:
            [self doPopAnimation:transitionContext];
            break;
    }
    
}

/**
 *  执行push过渡动画
 */
- (void)doPushAnimation:(id<UIViewControllerContextTransitioning>)transitionContext{
    WeXPassportViewController *fromVC = (WeXPassportViewController *)[transitionContext viewControllerForKey:UITransitionContextFromViewControllerKey];
    WeXPassportIDViewController *toVC = (WeXPassportIDViewController *)[transitionContext viewControllerForKey:UITransitionContextToViewControllerKey];
    //拿到当前点击的cell的imageView
    WeXPassportCardIDCell *cell = (WeXPassportCardIDCell *)[fromVC.cardTabelView cellForRowAtIndexPath:fromVC.currentIndexPath];
    UIView *containerView = [transitionContext containerView];
    //snapshotViewAfterScreenUpdates 对cell的imageView截图保存成另一个视图用于过渡，并将视图转换到当前控制器的坐标
    UIView *tempView = [cell.cardView snapshotViewAfterScreenUpdates:NO];
    tempView.frame = [cell.cardView convertRect:cell.cardView.bounds toView:containerView];
    NSLog(@"tempView.frame=%@",NSStringFromCGRect(tempView.frame));
    //设置动画前的各个控件的状态
    cell.cardView.hidden = YES;
    toVC.view.alpha = 0;
    toVC.cardView.hidden = YES;
    //tempView 添加到containerView中，要保证在最前方，所以后添加
    [containerView addSubview:toVC.view];
    [containerView addSubview:tempView];
    //开始做动画
    [UIView animateWithDuration:[self transitionDuration:transitionContext] delay:0.0 usingSpringWithDamping:0.55 initialSpringVelocity:1 / 0.55 options:0 animations:^{
        [toVC.view layoutIfNeeded];
        tempView.frame = [toVC.cardView convertRect:toVC.cardView.bounds toView:containerView];
        NSLog(@"tempView.frame=%@",NSStringFromCGRect(tempView.frame));
        toVC.view.alpha = 1;
    } completion:^(BOOL finished) {
        tempView.hidden = YES;
        toVC.cardView.hidden = NO;
        //如果动画过渡取消了就标记不完成，否则才完成，这里可以直接写YES，如果有手势过渡才需要判断，必须标记，否则系统不会中动画完成的部署，会出现无法交互之类的bug
        [transitionContext completeTransition:YES];
    }];
}
/**
 *  执行pop过渡动画
 */
- (void)doPopAnimation:(id<UIViewControllerContextTransitioning>)transitionContext{
    WeXPassportIDViewController *fromVC = (WeXPassportIDViewController *)[transitionContext viewControllerForKey:UITransitionContextFromViewControllerKey];
    WeXPassportViewController *toVC = (WeXPassportViewController *)[transitionContext viewControllerForKey:UITransitionContextToViewControllerKey];
    WeXPassportCardIDCell *cell = (WeXPassportCardIDCell *)[toVC.cardTabelView cellForRowAtIndexPath:toVC.currentIndexPath];
    UIView *containerView = [transitionContext containerView];
    
    UIView *tempView = [fromVC.cardView snapshotViewAfterScreenUpdates:NO];
    tempView.frame = [fromVC.cardView convertRect:fromVC.cardView.bounds toView:containerView];
    NSLog(@"tempView.frame=%@",NSStringFromCGRect(tempView.frame));
    //这里的lastView就是push时候初始化的那个tempView
//    UIView *tempView = containerView.subviews.lastObject;
    //设置初始状态
    cell.cardView.hidden = YES;
    fromVC.cardView.hidden = YES;
    tempView.hidden = NO;
    [containerView addSubview:toVC.view];
    [containerView addSubview:tempView];
//    [containerView insertSubview:toVC.view atIndex:0];
    [UIView animateWithDuration:[self transitionDuration:transitionContext] delay:0.0 usingSpringWithDamping:0.55 initialSpringVelocity:1 / 0.55 options:0 animations:^{
        tempView.frame = [cell.cardView convertRect:cell.cardView.bounds toView:containerView];
        NSLog(@"tempView.frame=%@",NSStringFromCGRect(tempView.frame));
        fromVC.view.alpha = 0;
    } completion:^(BOOL finished) {
        //由于加入了手势必须判断
        [transitionContext completeTransition:![transitionContext transitionWasCancelled]];
        if ([transitionContext transitionWasCancelled]) {//手势取消了，原来隐藏的imageView要显示出来
            //失败了隐藏tempView，显示fromVC.imageView
            tempView.hidden = YES;
            fromVC.cardView.hidden = NO;
        }else{//手势成功，cell的imageView也要显示出来
            //成功了移除tempView，下一次pop的时候又要创建，然后显示cell的imageView
            cell.cardView.hidden = NO;
            [tempView removeFromSuperview];
        }
    }];
}


@end
