package com.example.mypersonallibrary;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
public class BookEditActivity extends AppCompatActivity{
    private static final String TAG = "BookEditActivity";
    private static String mode ="startMode";
    public static Intent newIntent(Context context,int startMode){
        Intent intent = new Intent(context,BookEditActivity.class);
        intent.putExtra(mode,startMode);
        return intent;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        int startMode = i.getIntExtra(mode,0);
        switch(startMode){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                Log.e(TAG,"活动以错误的启动模式启动"+startMode);
                break;
        }
    }
}
