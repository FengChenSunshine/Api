package com.duanlu.api;

import com.google.gson.stream.JsonReader;
import com.lzy.okgo.convert.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 段露 on 2017/09/06 14:26.
 *
 * @author 段露
 * @version 1.0.0
 * @class JsonConvert
 * @describe Json解析.
 */
public class JsonConvert<T> implements Converter<T> {

    private static String TAG = "JsonConvert";
    private Type mType;

    public JsonConvert(Type type) {
        this.mType = type;
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象，生成onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        if (null == mType) {
            //Log.d("--", "结果：" + response.body().string());
            Type genType = getClass().getGenericSuperclass();
            //从上述的类中取出真实的泛型参数，有些类可能有多个泛型，所以是数值
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            //我们的示例代码中，只有一个泛型，所以取出第一个，得到如下结果
            //com.lzy.demo.model.LzyResponse<com.lzy.demo.model.ServerModel>
            mType = params[0];
        }

        if (!(mType instanceof ParameterizedType)) {
            ResponseBody body = response.body();
            if (null != body) {
                return Convert.fromJson(new JsonReader(body.charStream()), mType);
            } else {
                throw new ApiException(-1, "ResponseBody==null");
            }
        }
        //如果确实还有泛型，那么我们需要取出真实的泛型，得到如下结果
        //class com.lzy.demo.model.LzyResponse
        //此时，rawType的类型实际上是 class，但 Class 实现了 Type 接口，所以我们用 Type 接收没有问题
        Type rawType = ((ParameterizedType) mType).getRawType();
        //这里获取最终内部泛型的类型 com.lzy.demo.model.ServerModel
        Type typeArgument = ((ParameterizedType) mType).getActualTypeArguments()[0];

        //这里我们既然都已经拿到了泛型的真实类型，即对应的 class ，那么当然可以开始解析数据了，我们采用 Gson 解析
        //以下代码是根据泛型解析数据，返回对象，返回的对象自动以参数的形式传递到 onSuccess 中，可以直接使用
        JsonReader jsonReader = new JsonReader(response.body().charStream());
        if (typeArgument == Void.class) {
            //无数据类型,表示没有data数据的情况（以  new DialogCallback<LzyResponse<Void>>(this)  以这种形式传递的泛型)
            ResultBean simpleResponse = Convert.fromJson(jsonReader, ResultBean.class);
            response.close();
            //noinspection unchecked
            return (T) simpleResponse;
        } else if (rawType == ResultBean.class) {
            //有数据类型，表示有data
            ResultBean resultBean = Convert.fromJson(jsonReader, mType);
            response.close();

            if (null == resultBean) throw new ApiException(-1, "解析失败");
            //noinspection unchecked
            return (T) resultBean;
        } else {
            response.close();
            throw new ApiException(-1, "必须是 ResultBean 泛型参数");
        }
    }

}