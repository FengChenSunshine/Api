package com.duanlu.api;

import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by 段露 on 2017/09/06 14:26.
 *
 * @author 段露
 * @version 1.0.0
 * @class MyCallback
 * @describe Json格式网络请求.
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    public JsonCallback() {

    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        //Log.d("--", "结果：" + response.body().string());
        Type genType = getClass().getGenericSuperclass();
        //从上述的类中取出真实的泛型参数，有些类可能有多个泛型，所以是数值
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        //我们的示例代码中，只有一个泛型，所以取出第一个，得到如下结果
        //com.lzy.demo.model.LzyResponse<com.lzy.demo.model.ServerModel>
        return new JsonConvert<T>(params[0]).convertResponse(response);
    }

}
