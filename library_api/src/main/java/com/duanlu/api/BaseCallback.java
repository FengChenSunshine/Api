package com.duanlu.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.JsonParseException;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.net.ssl.SSLHandshakeException;

/**
 * Created by 段露 on 2017/9/6  14:32.
 *
 * @author 段露
 * @version 1.0.0
 * @class BaseCallback
 * @describe 基础Callback
 * 注：该类的回调具有如下顺序,虽然顺序写的很复杂,但是理解后,是很简单,并且合情合理的
 * 1.无缓存模式{@link CacheMode#NO_CACHE}
 * ---网络请求成功 onStart -> convertResponse -> onSuccess -> onFinish
 * ---网络请求失败 onStart -> onError -> onFinish
 * <p>
 * 2.默认缓存模式,遵循304头{@link CacheMode#DEFAULT}
 * ---网络请求成功,服务端返回非304 onStart -> convertResponse -> onSuccess -> onFinish
 * ---网络请求成功服务端返回304 onStart -> onCacheSuccess -> onFinish
 * ---网络请求失败 onStart -> onError -> onFinish
 * <p>
 * 3.请求网络失败后读取缓存{@link CacheMode#REQUEST_FAILED_READ_CACHE}
 * ---网络请求成功,不读取缓存 onStart -> convertResponse -> onSuccess -> onFinish
 * ---网络请求失败,读取缓存成功 onStart -> onCacheSuccess -> onFinish
 * ---网络请求失败,读取缓存失败 onStart -> onError -> onFinish
 * <p>
 * 4.如果缓存不存在才请求网络，否则使用缓存{@link CacheMode#IF_NONE_CACHE_REQUEST}
 * ---已经有缓存,不请求网络 onStart -> onCacheSuccess -> onFinish
 * ---没有缓存请求网络成功 onStart -> convertResponse -> onSuccess -> onFinish
 * ---没有缓存请求网络失败 onStart -> onError -> onFinish
 * <p>
 * 5.先使用缓存，不管是否存在，仍然请求网络{@link CacheMode#FIRST_CACHE_THEN_REQUEST}
 * ---无缓存时,网络请求成功 onStart -> convertResponse -> onSuccess -> onFinish
 * ---无缓存时,网络请求失败 onStart -> onError -> onFinish
 * ---有缓存时,网络请求成功 onStart -> onCacheSuccess -> convertResponse -> onSuccess -> onFinish
 * ---有缓存时,网络请求失败 onStart -> onCacheSuccess -> onError -> onFinish
 */
public abstract class BaseCallback<T> extends JsonCallback<T> {

    protected Context mContext;

    public BaseCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        request.tag(mContext);
    }

    @Override
    public void onCacheSuccess(Response<T> response) {
        //将缓存数据传递给onSuccess
        onSuccess(response);
    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        T result = super.convertResponse(response);
        if (result instanceof ResultBean) {
            ResultBean resultBean = (ResultBean) result;
            //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
            if (!resultBean.isSuccess()) {
                throw new ApiException(resultBean.getCode(), resultBean.getMessage());
            }
        }
        return result;
    }

    @Override
    public final void onError(Response<T> response) {
        super.onError(response);
        //网络请求失败时提示错误信息给用户.
        Throwable throwable = response.getException();
        if (null != throwable) {

            ApiException exception;

            if (throwable instanceof ConnectException) {
                exception = new ApiException(-1, "连接失败");
            } else if (throwable instanceof SocketException) {
                exception = new ApiException(-1, "连接失败");
            } else if (throwable instanceof ConnectTimeoutException) {
                exception = new ApiException(-1, "连接超时");
            } else if (throwable instanceof SocketTimeoutException) {
                exception = new ApiException(-1, "请求超时");
            } else if (throwable instanceof UnknownHostException) {
                exception = new ApiException(-1, "请求错误");
            } else if (throwable instanceof SSLHandshakeException) {
                exception = new ApiException(-1, "证书验证失败");
            } else if (throwable instanceof JsonParseException
                    || throwable instanceof JSONException
                    || throwable instanceof ParseException) {
                exception = new ApiException(-1, "解析错误");
            } else {
                exception = new ApiException(-1, "未知错误");
            }

            this.onError(exception);
        }
    }

    public abstract void onError(@NonNull ApiException exception);

}