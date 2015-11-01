package alpha.com.scanit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SQLite extends SQLiteOpenHelper {


    @SuppressLint("SpellCheckingInspection")

    /**
     * Database Variables
     */

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BCData";
    private static final String TABLE_NAME = "Barcodes";
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "Barcode";
    private static final String KEY_Barcode = "Company";
    String TAG = "DbHelper";

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creating Tables
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_Barcode + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    /**
     * Upgrading database
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Adding new barcode
     */

    void addBarcodes(Barcodes Barcodes) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, Barcodes.getBarcode());
        values.put(KEY_Barcode, Barcodes.getCompany());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Getting All Barcodes
     */

    public List<Barcodes> getBarCodes() {

        List<Barcodes> BarcodesList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Barcodes Barcodes = new Barcodes();
                Barcodes.setID(Integer.parseInt(cursor.getString(0)));
                Barcodes.setBarcode(cursor.getString(1));
                Barcodes.setCompany(cursor.getString(2));

                BarcodesList.add(Barcodes);
            } while (cursor.moveToNext());
        }


        return BarcodesList;
    }

    /**
     * Deleting Barcodes
     */

    public void deleteBarcodes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
        db.close();
    }

    /**
     * Get All Barcodes
     */

    public Cursor getBarcodesRaw() {
        Cursor c;
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        c = db.rawQuery(countQuery, null);

        return c;
    }

        /**
         * Helper function that parses a given table into a string
         * and returns it for easy printing. The string consists of
         * the table name and then each row is iterated through with
         * column_name: value pairs printed out.
         *
         * @param tableName the the name of the table to parse
         * @return the table tableName as a string
         */

        public String getTableAsString(String tableName) {
            SQLiteDatabase db = this.getReadableDatabase();
            Log.i(TAG, "getTableAsString called");
            String tableString = String.format("Table %s:\n", tableName);
            Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
            if (allRows.moveToFirst() ){
                String[] columnNames = allRows.getColumnNames();
                do {
                    for (String name: columnNames) {
                        tableString += String.format("%s: %s\n", name,
                                allRows.getString(allRows.getColumnIndex(name)));
                    }
                    tableString += "\n";

                } while (allRows.moveToNext());
            }
            db.close();
            return tableString;
        }

    }

