package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by admin on 2018/3/5.
 */
@Data
@ToString
@NoArgsConstructor
public class CoursePreviewResult extends ResponseResult {
    String previewUrl;//页面预览URL，必须得到页面id才可以拼装
    public CoursePreviewResult(ResultCode resultCode, String previewUrl) {
        super(resultCode);
        this.previewUrl = previewUrl;
    }

}
