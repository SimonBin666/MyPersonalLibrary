package com.example.mypersonallibrary;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.mypersonallibrary.DouBanFetcher;
import com.google.zxing.Result;
import java.util.Calendar;
import java.util.List;
public class SingleAddActivity extends AppCompatActivity{
    private static final String TAG = "SingleAddActivity";
    private Toolbar mToolbar;
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context,SingleAddActivity.class);
        return intent;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_add);
        mToolbar = (Toolbar)findViewById(R.id.singleAddToolbar);
        mToolbar.setTitle(R.string.single_add_toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.singleAddFrame);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuItem menuItem;
        menuItem = menu.add(Menu.NONE,R.id.menu_simple_add_manually,0,R.string.menu_single_add_manually);
        MenuItemCompat.setShowAsAction(menuItem,MenuItemCompat.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_simple_add_manually:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(R.string.input_isbn_manually_title)
                        .content(R.string.input_isbn_manually_content)
                        .positiveText(R.string.input_isbn_manually_positive)
                        .onPositive(new MaterialDialog.SingleButtonCallback(){
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which){
                                addBook(dialog.getInputEditText().getText().toString());
                            }
                        })
                        .negativeText(android.R.string.cancel)
                        .onNegative(new MaterialDialog.SingleButtonCallback(){
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //resumeCamera();
                            }
                        })
                        .alwaysCallInputCallback()
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input(R.string.input_isbn_manually_edit_text, 0, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                int length = dialog.getInputEditText().getText().length();
                                if(length == 10 || length == 13){
                                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                }else
                                {
                                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                }
                            }
                        })
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void addBook(final String isbn){
        final Context context = this;
        BookLab bookLab = BookLab.get(this);
        List<Book> mBooks = bookLab.getBooks();
        boolean isExist = false;
        for(Book book:mBooks){
            if (book.getIsbn().equals(isbn)){
                isExist = true;
                break;
            }
        }
        if(isExist){
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.book_duplicate_dialog_title)
                    .content(R.string.book_duplicate_dialog_content)
                    .positiveText(R.string.book_duplicate_dialog_positive)
                    .onPositive(new MaterialDialog.SingleButtonCallback(){
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            DouBanFetcher fetcher = new DouBanFetcher();
                            fetcher.getBookInfo(context,isbn,0);
                        }
                    })
                    .negativeText(android.R.string.cancel)
                    .onNegative(new MaterialDialog.SingleButtonCallback(){
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .show();
        }
        else{
            DouBanFetcher fetcher = new DouBanFetcher();
            fetcher.getBookInfo(this,isbn,0);
        }
    }
    public void handleResult(Result rawResult){
        Log.i(TAG,"ScanResult Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString());
        addBook(rawResult.getText());
    }
    public void fetchFailed(int fetcherID,int event,String isbn){
        if(fetcherID == BookFetcher.fetcherID_DB){
            if(event == 0){
                event0Dialog(isbn);
            }
            else if(event == 1){
                event1Dialog(isbn);
            }
        }
    }
    private void event0Dialog(final String isbn){
        String dialogCotent = String.format(getResources().getString(
                R.string.isbn_unmatched_dialog_content),isbn);
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.isbn_unmatched_dialog_title)
                .content(dialogCotent)
                .positiveText(R.string.isbn_unmatched_dialog_positive)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Book mBook = new Book();
                        mBook.setIsbn(isbn);
                        mBook.setAddTime(Calendar.getInstance());
                        Intent i = new Intent(SingleAddActivity.this,BookEditActivity.class);
                        i.putExtra(BookEditActivity.BOOK,mBook);
                        i.putExtra(BookEditActivity.downloadCover,false);
                        startActivity(i);
                        finish();
                    }
                })
                .show();
    }
    private void event1Dialog(final String isbn){
        String dialogCotent = String.format(getResources().getString(
                R.string.request_failed_dialog_content),isbn);
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.isbn_unmatched_dialog_title)
                .content(dialogCotent)
                .positiveText(R.string.isbn_unmatched_dialog_positive)
                .onPositive(new MaterialDialog.SingleButtonCallback(){
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which){
                        Book mBook = new Book();
                        mBook.setIsbn(isbn);
                        Intent i = new Intent(SingleAddActivity.this,BookEditActivity.class);
                        i.putExtra(BookEditActivity.BOOK,mBook);
                        i.putExtra(BookEditActivity.downloadCover,false);
                        startActivity(i);
                        finish();
                    }
                })
                .show();
    }
}
