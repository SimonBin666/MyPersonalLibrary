package com.example.mypersonallibrary.database;
import android.database.Cursor;
import android.database.CursorWrapper;
import com.example.mypersonallibrary.Book;
public class BookCursorWrapper extends CursorWrapper{
    public BookCursorWrapper(Cursor cursor){
        super(cursor);
    }
    public Book getBook(){
        return null;
    }
}
