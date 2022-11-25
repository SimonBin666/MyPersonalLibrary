package com.example.mypersonallibrary;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.widget.Toolbar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.Calendar;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import java.util.Arrays;
import java.util.UUID;
public class BookEditActivity extends AppCompatActivity{
    private static final String TAG = "BookEditActivity";
    public static String BOOK ="BOOKTOEDIT";
    public static String downloadCover = "DOWNLOADCOVER";
    public static String imageURL = "IMAGEURL";
    private Book mBook;
    private Toolbar mToolbar;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText translatorEditText;
    private EditText publisherEditText;
    private EditText pubyearEditText;
    private EditText pubmonthEditText;
    private EditText isbnEditText;
    private LinearLayout translator_layout;
    private ImageView coverImageView;
    private BookEditActivity mBookEditActivity;
    private Spinner readingStatusSpinner;
    private Spinner bookshelfSpinner;
    private EditText notesEditText;
    private EditText websiteEditText;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookedit);
        Intent i = getIntent();
        mBook = (Book)i.getSerializableExtra(BOOK);
        mBookEditActivity = this;
        mToolbar = (Toolbar)findViewById(R.id.bookedit_toolbar);
        mToolbar.setTitle(R.string.book_edit_activity_title);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_close);
        mToolbar.setNavigationContentDescription(R.string.tool_bar_navigation_description);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
        coverImageView = (ImageView)findViewById(R.id.book_cover_image_view);
        if(i.getBooleanExtra(downloadCover,false)){
            CoverDownloader coverDownloader = new CoverDownloader(this,mBook);
            String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ "/" + mBook.getCoverPhotoFileName();
            coverDownloader.downloadAndSaveImg(i.getStringExtra(imageURL),path);
        }
        setBookInfo();
        readingStatusSpinner = (Spinner)findViewById(R.id.reading_status_spinner);
        setReadingStatus();
        bookshelfSpinner = (Spinner)findViewById(R.id.book_shelf_spinner);
        setBookShelf();
        notesEditText = (EditText)findViewById(R.id.book_notes_edit_text);
        if(mBook.getNotes()!=null){
            notesEditText.setText(mBook.getNotes());
        }

        websiteEditText = (EditText)findViewById(R.id.book_website_edit_text);
        if(mBook.getWebsite()!=null){
            websiteEditText.setText(mBook.getWebsite());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bookedit,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_book_edit_save:
                int month = Integer.parseInt(pubmonthEditText.getText().toString());
                if(month>12 || month <1){
                    Toast.makeText(this,R.string.month_invalid,Toast.LENGTH_LONG).show();
                    pubmonthEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(pubmonthEditText,InputMethodManager.SHOW_IMPLICIT);
                }
                else{
                    mBook.setTitle(titleEditText.getText().toString());
                    String authors = authorEditText.getText().toString();
                    String[] authorArray;
                    if(authors.contains("、")){
                        authorArray = authors.split("、");
                    }
                    else {
                        authorArray = authors.split(" ");
                    }
                    List<String> authorList = new ArrayList<>(Arrays.asList(authorArray));
                    mBook.setAuthors(authorList);
                    if(translator_layout.getVisibility()!= View.GONE){
                        String translators = translatorEditText.getText().toString();
                        String[] translatorArray;
                        if(translators.contains("、")){
                            translatorArray = translators.split("、");
                        }
                        else {
                            translatorArray = translators.split(" ");
                        }
                        List<String> translatorList = new ArrayList<>(Arrays.asList(translatorArray));
                        mBook.setTranslators(translatorList);
                    }
                    mBook.setPublisher(publisherEditText.getText().toString());
                    int year = Integer.parseInt(pubyearEditText.getText().toString());
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, 1);
                    mBook.setPubTime(calendar);
                    mBook.setIsbn(isbnEditText.getText().toString());
                    mBook.setNotes(notesEditText.getText().toString());
                    mBook.setWebsite(notesEditText.getText().toString());
                    BookLab bookLab = BookLab.get(this);
                    bookLab.addBook(mBook);
                    for(int i=0;i<30;i++){
                        bookLab.addBook(mBook);
                    }
                    finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void setBookInfo(){
        titleEditText = (EditText)findViewById(R.id.book_title_edit_text);
        authorEditText = (EditText)findViewById(R.id.book_author_edit_text);
        translatorEditText = (EditText)findViewById(R.id.book_translator_edit_text);
        publisherEditText = (EditText)findViewById(R.id.book_publisher_edit_text);
        pubyearEditText = (EditText)findViewById(R.id.book_pubyear_edit_text);
        pubmonthEditText = (EditText)findViewById(R.id.book_pubmonth_edit_text);
        isbnEditText = (EditText)findViewById(R.id.book_isbn_edit_text);
        translator_layout = (LinearLayout)findViewById(R.id.translator_layout);
        titleEditText.setText(mBook.getTitle());
        if(mBook.getAuthors()!=null){
            StringBuilder stringBuilder1 = new StringBuilder();
            for(String author: mBook.getAuthors()){
                stringBuilder1.append(author);
                stringBuilder1.append(" ");
            }
            stringBuilder1.deleteCharAt(stringBuilder1.length()-1);
            authorEditText.setText(stringBuilder1);
        }
        if(mBook.getTranslators()!=null){
            translator_layout.setVisibility(View.VISIBLE);
            StringBuilder stringBuilder2 = new StringBuilder();
            for(String translator: mBook.getTranslators()){
                stringBuilder2.append(translator);
                stringBuilder2.append(" ");
            }
            stringBuilder2.deleteCharAt(stringBuilder2.length()-1);
            translatorEditText.setText(stringBuilder2);
        }
        publisherEditText.setText(mBook.getPublisher());
        isbnEditText.setText(mBook.getIsbn());
        int year = mBook.getPubTime().get(Calendar.YEAR);
        int mon = mBook.getPubTime().get(Calendar.MONTH) + 1;
        StringBuilder month = new StringBuilder();
        if(mon < 10){
            month.append("0");
        }
        month.append(String.valueOf(mon));
        pubyearEditText.setText(String.valueOf(year));
        pubmonthEditText.setText(month);
    }
    private int curBookshelfPos;
    private void setBookShelf(){
        final BookShelfLab bookShelfLab = BookShelfLab.get(this);
        final List<BookShelf> bookShelves = bookShelfLab.getBookShelves();
        List<BookShelf> spinnerBookshelf = new ArrayList<>();
        for(BookShelf bookShelfTemp : bookShelves){
            spinnerBookshelf.add(bookShelfTemp);
        }
        final ArrayAdapter<BookShelf> arrayAdapter = new ArrayAdapter<BookShelf>(
                this,R.layout.spinner_item,spinnerBookshelf);
        BookShelf customShelf = new BookShelf();
        customShelf.setTitle(getResources().getString(R.string.custom_spinner_item));
        arrayAdapter.add(customShelf);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookshelfSpinner.setAdapter(arrayAdapter);
        BookShelf curBookshelf = bookShelfLab.getBookShelf(mBook.getBookshelfID());
        curBookshelfPos = arrayAdapter.getPosition(curBookshelf);
        bookshelfSpinner.setSelection(curBookshelfPos);
        bookshelfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id){
                BookShelf selectedBS = (BookShelf) bookshelfSpinner.getSelectedItem();
                String selectedName = selectedBS.toString();
                if(selectedName.equals(getResources().getString(R.string.custom_spinner_item))){
                    Log.i(TAG,"点击自定义书架");
                    MaterialDialog inputDialog = new MaterialDialog.Builder(mBookEditActivity)
                            .title(R.string.custom_book_shelf_dialog_title)
                            .inputRange(1,10)
                            .input(R.string.custom_book_shelf_dialog_edit_text,0,new MaterialDialog.InputCallback(){
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input){
                                    BookShelf bookShelf = new BookShelf();
                                    bookShelf.setTitle(input.toString());
                                    bookShelfLab.addBookShelf(bookShelf);
                                    mBook.setBookshelfID(bookShelf.getId());
                                    Log.i(TAG,"新设书架 = " +bookShelf.getTitle());
                                    setBookShelf();
                                }
                            })
                            .dismissListener(new DialogInterface.OnDismissListener(){
                                @Override
                                public void onDismiss(DialogInterface dialogInterface){
                                    bookshelfSpinner.setSelection(curBookshelfPos);
                                }
                            })
                            .show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mBookEditActivity);
                    final EditText editText = new EditText(mBookEditActivity);
                    editText.setHint(R.string.custom_book_shelf_dialog_edit_text);
                    builder.setTitle(R.string.custom_book_shelf_dialog_title);
                    builder.setView(editText);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            BookShelf bookShelf = new BookShelf();
                            bookShelf.setTitle(editText.getText().toString());
                            bookShelfLab.addBookShelf(bookShelf);
                            mBook.setBookshelfID(bookShelf.getId());
                            Log.i(TAG,"新设书架 = " +bookShelf.getTitle());
                            setBookShelf();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            bookshelfSpinner.setSelection(curBookshelfPos);
                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setEnabled(false);
                    editText.addTextChangedListener(new TextWatcher(){
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
                        }
                        @Override
                        public void afterTextChanged(Editable editable){
                            if(editable.toString().length()==0){
                                positiveButton.setEnabled(false);
                            }
                            else{
                                positiveButton.setEnabled(true);
                            }
                        }
                    });
                    alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener(){
                        @Override
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent){
                            if(i == KeyEvent.KEYCODE_BACK){
                                bookshelfSpinner.setSelection(curBookshelfPos);
                                dialogInterface.dismiss();
                            }
                            return true;
                        }
                    });
                }
                else{
                    Log.i(TAG,"设置书架" + selectedBS.getTitle());
                    curBookshelfPos = pos;
                    mBook.setBookshelfID(selectedBS.getId());
                }
            }
            public void onNothingSelected(AdapterView<?> parent){
            }
        });
    }
    private void setReadingStatus(){
        ArrayAdapter<CharSequence> readingStatusArrayAdapter = ArrayAdapter.createFromResource(
                this,R.array.reading_status_array,R.layout.spinner_item);
        readingStatusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        readingStatusSpinner.setAdapter(readingStatusArrayAdapter);
        readingStatusSpinner.setSelection(mBook.getReadingStatus());
        readingStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
                mBook.setReadingStatus(i);
                Log.i(TAG,"单击并设置阅读状态" + i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
            }
        });
    }
    public void setBookCover(){
        if(coverImageView!=null && mBook.isHasCover()){
            String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ "/" + mBook.getCoverPhotoFileName();
            Bitmap bitmap1 = BitmapFactory.decodeFile(path);
            coverImageView.setImageBitmap(bitmap1);
        }
    }
}
