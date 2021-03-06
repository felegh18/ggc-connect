package edu.ggc.it.directory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SavedSearchDatabase {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "savedsearchesdb";
	public static final String DATABASE_TABLE = "SavedSearches";

	public static final String KEY_ROWID = "_id";
	public static final int INDEX_ROWID = 0;
	public static final String KEY_FIRSTNAME = "firstname";
	public static final int INDEX_FIRSTNAME = 1;
	public static final String KEY_LASTNAME = "lastname";
	public static final int INDEX_LASTNAME = 2;
	public static final String KEY_URL = "url";
	public static final int INDEX_URL = 3;

	public static final String[] KEYS_ALL = { SavedSearchDatabase.KEY_ROWID,
			SavedSearchDatabase.KEY_FIRSTNAME,
			SavedSearchDatabase.KEY_LASTNAME, SavedSearchDatabase.KEY_URL };

	private Context context;
	private SQLiteDatabase database;
	private SavedSearchDatabaseHelper helper;
	private static SavedSearchDatabase instance = null;
	
	public SavedSearchDatabase(Context context) {
		this.context = context;
	}

	
	public void open() throws SQLException {
		helper = new SavedSearchDatabaseHelper(context);
		database = helper.getWritableDatabase();
	}
	
	public void close() {
		helper.close();
		helper = null;
		database = null;
	}
	
	public long createRow(ContentValues values) {
		return database.insert(DATABASE_TABLE, null, values);
	}
	
	public boolean updateRow(long rowId, ContentValues values) {
		return database.update(DATABASE_TABLE, values,
				SavedSearchDatabase.KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteRow(long rowId) {
		return database.delete(DATABASE_TABLE, SavedSearchDatabase.KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public Cursor queryAllByRowID() {
		return database.query(DATABASE_TABLE, KEYS_ALL, null, null, null, null,
				" ROWID");
	}
	
	public Cursor queryAllByAscending() {
		return database.query(DATABASE_TABLE, KEYS_ALL, null, null, null, null,
				SavedSearchDatabase.KEY_FIRSTNAME + " ASC");
	}
	
	public Cursor query(long rowId) throws SQLException {
		Cursor cursor = database.query(true, DATABASE_TABLE, KEYS_ALL,
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public ContentValues createContentValues(String task) {
		ContentValues values = new ContentValues();
		values.put(SavedSearchDatabase.KEY_URL, task);
		return values;
	}
	
	
	public static class SavedSearchDatabaseHelper extends SQLiteOpenHelper {

		public static void init(Context context) {
		    if (null == instance) {
		        instance = new SavedSearchDatabase(context);
		    }
		}
		
		private static final String DATABASE_CREATE = "create table "
				+ DATABASE_TABLE + " (" + SavedSearchDatabase.KEY_ROWID
				+ " integer primary key autoincrement, "
				+ SavedSearchDatabase.KEY_FIRSTNAME + " text not null, "
						+ SavedSearchDatabase.KEY_LASTNAME + " text not null, "
						+ SavedSearchDatabase.KEY_URL + " text not null " 
				        + ");";

		public SavedSearchDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

		}

	}

}
