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
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import java.io.FileInputStream;
public class DouBanFetcher extends BookFetcher{
    private static final String TAG = "DoubanFetcher";
    @Override
    public void getBookInfo(final Context context, final String isbn){
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
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
                            +"，标题 = " + response.body().getTitle());
                    mBook = new Book();
                    mBook.setTitle(response.body().getTitle());
                    //mBook.setId(Long.parseLong(response.body().getId(),10));
                    mBook.setIsbn(isbn);
                    if(response.body().getAuthor().size()!=0){
                        mBook.setAuthors(response.body().getAuthor());
                    }
                    else{
                        mBook.setAuthors(null);
                    }
                    if(response.body().getTranslator().size()!=0){
                        mBook.setTranslators(response.body().getTranslator());
                    }
                    else{
                        mBook.setTranslators(null);
                    }
                    if(mBook.getWebIds() == null){
                        mBook.setWebIds(new HashMap<String, String>());
                    }
                    mBook.getWebIds().put("douban",response.body().getId());
                    mBook.setAddTime(Calendar.getInstance());
                    String rawDate = response.body().getPubdate();
                    Log.i(TAG,"生日期 = " + rawDate);
                    int firstSplit  = rawDate.indexOf("-");
                    int lastSplit = rawDate.lastIndexOf("-");
                    String year = rawDate.substring(0,firstSplit);
                    if(firstSplit == lastSplit){
                        lastSplit = rawDate.length();
                    }
                    String month = rawDate.substring(firstSplit+1,lastSplit);
                    Log.i(TAG,"第一 = " + firstSplit + "，第二 = " + lastSplit + "，月 = " + month + "，年 = " + year);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Integer.parseInt(year),Integer.parseInt(month)-1,1);
                    mBook.setPubtime(calendar);
                    final String imageURL = response.body().getImages().getLarge();
                    mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            Intent i = new Intent(mContext,BookEditActivity.class);
                            i.putExtra(BookEditActivity.BOOK,mBook);
                            i.putExtra(BookEditActivity.downloadCover,true);
                            i.putExtra(BookEditActivity.imageURL,imageURL);
                            mContext.startActivity(i);
                        }
                    });
                }
                else{
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