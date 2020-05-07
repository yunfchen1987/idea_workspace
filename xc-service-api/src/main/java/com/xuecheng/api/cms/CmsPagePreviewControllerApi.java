package com.xuecheng.api.cms;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;

@Api(value="cms页面预览接口",description = "cms页面预览")
public interface CmsPagePreviewControllerApi {
    @ApiOperation("根据页面id预览页面")
    public void preview(String pageId) throws IOException;
}
