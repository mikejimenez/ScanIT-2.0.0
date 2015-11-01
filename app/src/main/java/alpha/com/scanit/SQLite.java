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


class SQLite extends SQLiteOpenHelper {


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

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creating Tables
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " String,"
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
     * Deleting single Barcodes
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
}
