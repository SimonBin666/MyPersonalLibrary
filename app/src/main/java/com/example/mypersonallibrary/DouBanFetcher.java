package com.example.mypersonallibrary;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
public class DouBanFetcher extends BookFetcher{
    private static final String TAG = "DoubanFetcher";
    @Override
    public void getBookInfo(final Context context, final String isbn){
        mContext = context;
        Retrofit mRetrofit;
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.douban.com/v2/book/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DB_API api = mRetrofit.create(DB_API.class);
        Call<DouBanJson> call = api.getDBResult(isbn);
        call.enqueue(new Callback<DouBanJson>() {
            @Override
            public void onResponse(Call<DouBanJson> call, Response<DouBanJson> response) {
                if(response.code() == 200) {
                    Log.i(TAG, "获取豆瓣信息成功，id = " + response.body().getId()
                            +"， 标题 = " + response.body().getTitle());
                    mBook = new Book();
                    mBook.setTitle(response.body().getTitle());
                    //mBook.setId(Long.parseLong(response.body().getId(),10));
                    mBook.setIsbn(isbn);
                    if(response.body().getAuthor().size()!=0){
                        mBook.setAuthors(response.body().getAuthor());
                    }else{
                        mBook.setAuthors(null);
                    }
                    if(response.body().getTranslator().size()!=0){
                        mBook.setTranslators(response.body().getTranslator());
                    }else{
                        mBook.setTranslators(null);
                    }
                    if(mBook.getWebIds() == null){
                        mBook.setWebIds(new HashMap<String, String>());
                    }
                    mBook.getWebIds().put("douban",response.body().getId());
                    mBook.setAddTime(Calendar.getInstance());
                    mBook.setPublisher(response.body().getPublisher());
                    DateFormat df = new SimpleDateFormat("yyyy-MM");
                    Date pubDate = new Date();
                    try {
                        pubDate = df.parse(response.body().getPubdate());
                    }catch (ParseException pe){
                        Log.e(TAG,"解析日期异常"+pe);
                    }
                    mBook.setPubtime(pubDate);
                    String imageURL = response.body().getImages().getLarge();
                    getAndSaveImg(imageURL);
                }else{
                    Log.w(TAG,"意外的响应代码" + response.code() + "，isbn = " + isbn);
                }
            }
            @Override
            public void onFailure(Call<DouBanJson> call, Throwable t) {
                Log.w(TAG,"获取豆瓣信息失败，" + t.toString());
            }
        });
    }
    private interface DB_API{
        @GET ("isbn/{isbn}")
        Call<DouBanJson> getDBResult(@Path("isbn") String isbn);
    }
}