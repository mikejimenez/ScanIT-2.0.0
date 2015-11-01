package alpha.com.scanit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.ZXing.android.IntentIntegrator;
import com.alpha.ZXing.android.IntentResult;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {

    /**
     * Private Strings
     */

    private static final String PREFS_NAME = "Scan_IT";
    private static final String TAG = "MAKE_BARCODES";
    String ScanFromFedEXG;
    String ScanFromFedEXE;
    private TextView CounterTxt;
    TextView CounterTxtSet;
    TextView CounterTxtSave;
    private Integer Counter = 0;
    private String[] log = new String[100];
    String Output;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * For hiding Window Title
         */

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        LoadData();
    }


    @Override
    protected void onStop() {
        super.onStop();
        savePreferences();
    }


    /**
     * Accept ScanResult
     * Add to database and update view
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            final String scanContent = scanningResult.getContents();

            LayoutInflater Results = LayoutInflater.from(this);

            @SuppressLint("InflateParams") final View textEntryView = Results.inflate(R.layout.scan_entry, null);
            final EditText scanData = (EditText) textEntryView.findViewById(R.id.scanData);
            final SQLite db = new SQLite(this);
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setIcon(R.drawable.perm_group_user_dictionary).setTitle("Information").setView(textEntryView).setPositiveButton("Save",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                            Editable value = scanData.getText();
                            String Result = Filter(scanContent);

                            db.addBarcodes(new Barcodes(Result, value.toString()));
                            Counter++;
                            db.close();
                            CreateListView();
                            //Todo: Fix Boxing
                            String setText = Integer.valueOf(Counter).toString();
                            CounterTxt.setText(setText);
                        }
                    }).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            ScanDataEmpty();
                        }
                    });
            alert.show();
        } else if (resultCode == RESULT_CANCELED) {
            ScanDataEmpty();
        }
    }


    /**
     * Reading from database and Displaying
     */

    private void CreateListView() {
        ListView listView;
        Button scanBtn = (Button) findViewById(R.id.scan_button);
        Button clrBtn = (Button) findViewById(R.id.clr_button);
        Button mnlBtn = (Button) findViewById(R.id.manual_button);
        CounterTxt = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.listView);
        final SQLite db = new SQLite(this);
        Cursor cursor = db.getBarcodesRaw();

        scanBtn.setOnClickListener(new ScanButton());
        mnlBtn.setOnClickListener(new ManButton());
        clrBtn.setOnClickListener(new ClearButton());

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursor,
                new String[]{"Barcode", "Company"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0);
        listView.setDivider(null);
        listView.setSelector(android.R.color.transparent);
        listView.setAdapter(adapter);
        db.close();
    }
    private String Filter(String Number) {

        if (Number.length() == 34) {

            /**
             * System.out.println("Added Type Fedex Express");
             * System.out.println("Added Type Fedex Ground New");
             */
            ScanFromFedEXE = Number.substring(Number.length() - 12, Number.length());
            return ScanFromFedEXE;
        }
        if (Number.length() == 32) {

            /**
             * System.out.println("Added Type Fedex Express Old");
             * System.out.println("Added Type Fedex Ground");
             */
            ScanFromFedEXG = Number.substring(Number.length() - 16, Number.length() - 4);
            return ScanFromFedEXG;
        }
        if (Number.length() == 22) {

            /**
             * System.out.println("Added Type Fedex Ground Old");
             */

            ScanFromFedEXG = Number.substring(Number.length() - 22, Number.length());
            return ScanFromFedEXG;
        }
        if (Number.length() < 11) {

            /**
             * Error
             */
            ScanDataEmpty();
        }

        return Number;
    }


    /**
     *
     */

    private void InputManual() {

        LayoutInflater Manual = LayoutInflater.from(this);

        //Todo: Remove Null
        @SuppressLint("AndroidLintInflateParams")
        final View textEntryView = Manual.inflate(R.layout.manual_entry, null);
        final EditText infoTrack = (EditText) textEntryView.findViewById(R.id.InfoTrack);
        final EditText infoData = (EditText) textEntryView.findViewById(R.id.InfoData);

        final SQLite db = new SQLite(this);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setIcon(R.drawable.ic_dialog_alert_holo_light).setTitle("Manual Entry").setView(textEntryView).setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                        Editable value = infoData.getText();
                        String Result = infoTrack.getText().toString();

                        if (Result.length() == 0) {
                            ScanDataEmpty();
                        }

                        if (Result.length() == 21) {
                            final String ManualScanFedExG = Result;

                            db.addBarcodes(new Barcodes(ManualScanFedExG, value.toString()));
                            Counter++;
                            db.close();
                            CreateListView();
                            UpdateLog();
                            //Todo: Fix Boxing
                            CounterTxt.setText(Integer.valueOf(Counter).toString());

                        } else if (Result.length() != 0) {
                            db.addBarcodes(new Barcodes(Result, value.toString()));
                            Counter++;
                            db.close();
                            UpdateLog();
                            CreateListView();
                            CounterTxt.setText(Integer.valueOf(Counter).toString());
                        }
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        ScanDataEmpty();
                    }
                });
        alert.show();
    }
    public void UpdateLog() {

        /**
         * Display all bar codes
         * */

        SQLite db = new SQLite(this);
        List<Barcodes> Barcodes = db.getBarCodes();

        db.close();
        int i = 0;
        for (Barcodes cn : Barcodes) {
            String bar = cn.getBarcode();

            /**
             * UPS Manual - 23 Characters
             */

            if (bar.length() == 23 && bar.contains("Z")) {
                Output = FormatString.ManualUPS(bar);
                log[i] = "\n" + Output + " / " + cn.getCompany();
                i++;
            }

            /**
             * Fedex Ground OLD - 22 Characters
             */

            if (bar.length() == 22) {
                Output = FormatString.FedEXGO(bar);
                log[i] = "\n" + Output + " / " + cn.getCompany();
                i++;
            }

            /**
             * Fedex Ground Manual - 23 Characters
             */

            if (bar.length() == 21 && !bar.contains("Z")) {
                Output = FormatString.ManualFedEXG(bar);
                log[i] = "\n" + Output + " / " + cn.getCompany();
                i++;
            }


            /**
             * UPS - 18 Characters
             */

            if (bar.length() == 18) {
                Output = FormatString.UPS(bar);
                log[i] = "\n" + Output + " / " + cn.getCompany();
                i++;
            }

            /**
             * Fedex Express Manual - 14 Characters
             */

            if (bar.length() == 14) {
                Output = FormatString.ManualFedEXE(bar);
                log[i] = "\n" + Output + " / " + cn.getCompany();
                i++;
            }

            /**
             * Fedex Ground New  - 34 Characters
             * Fedex Express New - 34 Characters
             * Fedex Express OLD - 32 Characters
             */

            if (bar.length() == 12) {
                Output = FormatString.FedEXE(bar);
                log[i] = "\n" + Output + " / " + cn.getCompany();
                i++;
            }
        }

    }


    /**
     * Email Log
     * Update log
     * Removed empty Null in log
     * Refill Array with null
     * Reset Counter
     */

    private void emailResults() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "Tracking Numbers");
        String[] TO = {"Receiving@cdaresort.com"};
        i.putExtra(Intent.EXTRA_EMAIL, TO);
        UpdateLog();
        String newString = Arrays.toString(log);
        String FilterA = newString.replace(", null", "");
        String FilterB = FilterA.replace("[", "");
        String FilterC = FilterB.replace("]", "");
        String FilterD = FilterC.replace(",", "");
        i.putExtra(Intent.EXTRA_TEXT, FilterD);
        Arrays.fill(log, null);
        Counter = 0;
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Dump Shared Preferences
     */

    private void DisplaySharedPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Map<String, ?> allPrefs = settings.getAll();
        Set<String> set = allPrefs.keySet();

        for (String s : set) {
            Log.i(TAG, s + "<" + allPrefs.get(s).getClass().getSimpleName() + "> =  "
                    + allPrefs.get(s).toString());
        }
    }


    /**
     * Error Message
     */

    private void ScanDataEmpty() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "No scan data received!", Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * Save / Load State Data
     */

    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        TinyDB tinydb = new TinyDB(this);

        if (settings != null && settings.contains("counter")) {
            CounterTxtSave = (TextView) findViewById(R.id.textView2);
            String val = tinydb.getString("counter");

            CounterTxtSave.setText(val);
            Counter = Integer.parseInt(val);

            CreateListView();
            UpdateLog();
        }
    }
    private void LoadData() {

        /**
         * Restore preferences
         */

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        TinyDB tinydb = new TinyDB(this);

        if (settings != null && settings.contains("paused")) {
            loadPreferences();
            tinydb.remove("paused");
        } else {
            CreateListView();
            UpdateLog();
            CounterTxtSet = (TextView) findViewById(R.id.textView2);
            CounterTxtSet.setText("0");
        }
    }
    private void savePreferences() {
        String value = CounterTxt.getText().toString();
        TinyDB tinydb = new TinyDB(this);
        tinydb.putString("counter", value);
        tinydb.putString("paused", "Y");
    }


    /**
     * Buttons
     */

    private void ClearButtonData() {
        SQLite db = new SQLite(this);
        db.deleteBarcodes();
        CreateListView();
        db.close();
        CounterTxt.setText("0");
    }
    private class ScanButton implements View.OnClickListener {
        public void onClick(View v) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.initiateScan();
        }
    }
    private class ManButton implements View.OnClickListener {
        public void onClick(View v) {
            InputManual();
        }
    }
    private class ClearButton implements View.OnClickListener {
        public void onClick(View v) {
            if (Counter == 0) {
                ScanDataEmpty();
            } else {
                emailResults();
                ClearButtonData();
            }
        }
    }
}
