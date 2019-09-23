package com.duanlu.api;

/********************************
 * @name State
 * @author 段露
 * @createDate 2019/09/23 08:47
 * @updateDate 2019/09/23 08:47
 * @version V1.0.0
 * @describe API接口请求的数据的状态.
 ********************************/
public interface State {

    int DEFAULT_RESULT_SUCCEED_CODE = 200;

    boolean isSuccess();
}
