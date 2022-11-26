package com.example.mypersonallibrary;
import android.content.Context;
import android.util.Log;
import java.util.Calendar;
import java.util.HashMap;
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
import android.app.Activity;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
public class DouBanFetcher extends BookFetcher{
    private static final String TAG = "DouBanFetcher";
    @Override
    public void getBookInfo(final Context context, final String isbn,final int mode){
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
            public void onResponse(Call<DouBanJson> call, Response<DouBanJson> response){
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
                    mBook.getWebIds().put("豆瓣",response.body().getId());
                    String rawDate = response.body().getPubdate();
                    Log.i(TAG,"生日期 = " + rawDate);
                    String[] date = rawDate.split("-");
                    String year = date[0];
                    String month = date[1];
                    Log.i(TAG,"获取PubDate年份 = " + year + "，月== " + month);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Integer.parseInt(year),Integer.parseInt(month)-1,1);
                    mBook.setPubTime(calendar);
                    final String imageURL = response.body().getImages().getLarge();
                    mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            Intent i = new Intent(mContext,BookEditActivity.class);
                            i.putExtra(BookEditActivity.BOOK,mBook);
                            i.putExtra(BookEditActivity.downloadCover,true);
                            i.putExtra(BookEditActivity.imageURL,imageURL);
                            mContext.startActivity(i);
                            ((Activity)mContext).finish();
                        }
                    });
                }
                else{
                    Log.w(TAG,"意外的响应代码" + response.code() + "，isbn = " + isbn);
                    if(mode == 0){
                        ((SingleAddActivity)mContext).fetchFailed(
                                BookFetcher.fetcherID_DB,0,isbn
                        );
                    }
                }
            }
            @Override
            public void onFailure(Call<DouBanJson> call, Throwable t) {
                Log.w(TAG,"获取豆瓣信息失败，" + t.toString());
                if(mode==0){
                    ((SingleAddActivity)mContext).fetchFailed(
                            BookFetcher.fetcherID_DB,1,isbn
                    );
                }
            }
        });
    }
    private interface DB_API{
        @GET ("isbn/{isbn}")
        Call<DouBanJson> getDBResult(@Path("isbn") String isbn);
    }
}