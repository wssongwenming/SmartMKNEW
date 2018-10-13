package com.dtmining.latte.net;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * author:songwenming
 * Date:2018/9/22
 * Description:
 */
public interface RestService {
    @GET
    Call<String>get(@Url String url, @QueryMap Map<String,Object>params);

    @FormUrlEncoded
    @POST
    Call<String>post(@Url String url, @FieldMap Map<String ,Object>params);

    @POST
    Call<String>postRaw(@Url String url, @Body RequestBody body);

    @FormUrlEncoded
    @PUT
    Call<String> put(@Url String url,@FieldMap Map<String,Object>params);

    @PUT
    Call<String> putRaw(@Url String url, @Body RequestBody body);
    @DELETE
    Call<String> delete(@Url String url,@QueryMap Map<String,Object>params);

    //retrofit方式是一次性把文件down到内存然后再写入文件，但容易内存溢出，所以应该边下载，一边存储
    //所以加上@Streaming注解，表示一边下载一边在文件系统进行写入，这时需要把文件的写入放到一个单独的线程
    //
    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url,@QueryMap Map<String,Object>params);

    @Multipart
    @POST
    Call<String>upload(@Url String url, @Part MultipartBody.Part body);



}
