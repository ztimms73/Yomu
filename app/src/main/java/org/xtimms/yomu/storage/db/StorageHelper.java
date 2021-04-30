package org.xtimms.yomu.storage.db;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.xtimms.yomu.R;
import org.xtimms.yomu.util.ResourceUtil;
import org.xtimms.yomu.util.TextUtil;

public class StorageHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "storage";

    private final Resources mResources;

    public StorageHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mResources = context.getResources();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] parts = ResourceUtil.getRawString(mResources, R.raw.storage).split(";");
        db.beginTransaction();
        try {
            for (String query : parts) {
                db.execSQL(TextUtil.inline(query));
            }
            db.setTransactionSuccessful();
		/*} catch (Exception e) { //TODO handle it
			e.printStackTrace();*/
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
