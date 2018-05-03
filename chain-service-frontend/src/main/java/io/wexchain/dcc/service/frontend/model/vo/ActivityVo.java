package io.wexchain.dcc.service.frontend.model.vo;

import io.wexchain.dcc.marketing.api.constant.ActivityStatus;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * 营销活动
 *
 * @author zhengpeng
 */
public class ActivityVo {

    /**
     * 商户code
     */
    private String merchantCode;

    /**
     * 活动code
     */
    private String code;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动开始时间
     */
    private DateTime startTime;

    /**
     * 活动结束时间
     */
    private DateTime endTime;

    /**
     * 活动介绍
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImgUrl;

    /**
     * Banner图片URL
     */
    private String bannerImgUrl;

    /**
     * Banner链接URL
     */
    private String bannerLinkUrl;

    /**
     * 活动状态
     */
    private ActivityStatus status;



    /**
     * 活动标签
     */
    private Set<String> tags;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getBannerImgUrl() {
        return bannerImgUrl;
    }

    public void setBannerImgUrl(String bannerImgUrl) {
        this.bannerImgUrl = bannerImgUrl;
    }

    public String getBannerLinkUrl() {
        return bannerLinkUrl;
    }

    public void setBannerLinkUrl(String bannerLinkUrl) {
        this.bannerLinkUrl = bannerLinkUrl;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
