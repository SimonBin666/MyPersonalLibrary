package com.example.mypersonallibrary;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;
import android.os.Environment;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public abstract class BookFetcher {
    private static final String TAG = "BookFetcher";
    Context mContext;
    protected abstract void getBookInfo(Context context,String isbn);
    private int result;
    private boolean saveSuccess;
    protected void getAndSaveImg(String url,final Long id){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                //.baseUrl("https://smartjinyu.com/")
                .build();
        downloadImgApi downImgApi = retrofit.create(downloadImgApi.class);
        Call<ResponseBody> call = downImgApi.downloadFileWithDynamicUrlSync(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG,"获取下载图片响应，代码= " + response.code());
                if(!saveImgToDisk(response.body(),id)){
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                saveSuccess = false;
                Log.w(TAG,"下载图片响应失败，" + t.toString());
                Toast.makeText(mContext,"",Toast.LENGTH_LONG).show();
            }
        });
    }
    private interface downloadImgApi {
        @GET
        Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
    }
    private boolean saveImgToDisk(ResponseBody responseBody,Long id) {
        try {
            Log.d(TAG, "Begin to save cover to external storage");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            inputStream = responseBody.byteStream();
            outputStream = new FileOutputStream(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Cover_" + id.toString() + ".jpg");
            try {
                int c;
                while ((c = inputStream.read()) != -1) {
                    outputStream.write(c);
                }
            } catch (IOException ioe) {
                Log.e(TAG, "IOException, " + ioe.toString());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                outputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found exception, " + e.toString());
            return false;
        } catch (IOException ioe) {
            Log.e(TAG, "IOException, " + ioe.toString());
            return false;
        }
        Log.i(TAG, "Save image successfully.");
        return true;
    }
}
