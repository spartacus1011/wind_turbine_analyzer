package com.example.rossf.audiospectrumanalyzer;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class ShowDatabaseView extends AppCompatActivity {

    TableLayout tableDatabase;

    private List<RecordedItemInfo> allItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_database_view);

        tableDatabase = (TableLayout) findViewById(R.id.TableDatabase);

        allItems = MainActivity.dbHelper.GetAllRecords();

        //Header column
        TableRow newHeaderRow = new TableRow(this);
        TableRow.LayoutParams layoutParamsHeader = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        newHeaderRow.setLayoutParams(layoutParamsHeader);

        TextView FirstSeperatorHeader = CreateSeperatorCell();
        newHeaderRow.addView(FirstSeperatorHeader);

        TextView nameHeader = CreateHeaderCell();
        nameHeader.setText("Name");
        newHeaderRow.addView(nameHeader);

        TextView nameSeperatorHeader = CreateSeperatorCell();
        newHeaderRow.addView(nameSeperatorHeader);

        TextView dateHeader = CreateHeaderCell();
        dateHeader.setText("Date/Time Recorded");
        newHeaderRow.addView(dateHeader);

        TextView dateSeperatorHeader = CreateSeperatorCell();
        newHeaderRow.addView(dateSeperatorHeader);

        TextView recordingLengthHeader = CreateHeaderCell();
        recordingLengthHeader.setText("Recording Length (s)");
        newHeaderRow.addView(recordingLengthHeader);

        TextView recordingSeperatorHeader = CreateSeperatorCell();
        newHeaderRow.addView(recordingSeperatorHeader);

        TextView locationHeader = CreateHeaderCell();
        locationHeader.setText("Location Recorded (Latitude/Longitude)");
        newHeaderRow.addView(locationHeader);

        TextView locationSeperatorHeader = CreateSeperatorCell();
        newHeaderRow.addView(locationSeperatorHeader);

        tableDatabase.addView(newHeaderRow);

        for(RecordedItemInfo itemInfo: allItems) {

            TableRow newRow = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            newRow.setLayoutParams(layoutParams);

            TextView FirstSeperator = CreateSeperatorCell();
            newRow.addView(FirstSeperator);

            TextView name = CreateNewCell();
            name.setText(itemInfo.Name);
            newRow.addView(name);

            TextView nameSeperator = CreateSeperatorCell();
            newRow.addView(nameSeperator);

            TextView date = CreateNewCell();
            date.setText(android.text.format.DateFormat.format("dd-MM-yyyy h:mm:ss", itemInfo.DateTime));
            newRow.addView(date);

            TextView dateSeperator = CreateSeperatorCell();
            newRow.addView(dateSeperator);

            TextView recordingLength = CreateNewCell();
            recordingLength.setText(Float.toString(GetWavLength(itemInfo.UniqueID)));
            newRow.addView(recordingLength);

            TextView recordingSeperator = CreateSeperatorCell();
            newRow.addView(recordingSeperator);

            TextView location = CreateNewCell();
            location.setText(itemInfo.Location == null ? itemInfo.LocationLat + "/" + itemInfo.LocationLong:itemInfo.Location);
            newRow.addView(location);

            TextView locationSeperator = CreateSeperatorCell();
            newRow.addView(locationSeperator);



            tableDatabase.addView(newRow);

        }

    }

    //This is so that we can change all the cells formatting easily
    private TextView CreateNewCell() {
        TextView cell = new TextView(this);
        cell.setTextColor(getColor(R.color.colorText));

        return cell;
    }

    private TextView CreateSeperatorCell() {
        TextView sepCell = new TextView(this);
        sepCell.setTextColor(getColor(R.color.colorText));
        sepCell.setTypeface(null, Typeface.BOLD);
        sepCell.setText(" |  ");

        return sepCell;
    }

    private TextView CreateHeaderCell() {
        TextView cell = new TextView(this);
        cell.setTextColor(getColor(R.color.colorText));
        cell.setTypeface(null, Typeface.BOLD);
        cell.setTextSize(20);

        return cell;

    }

    private float GetWavLength(String uniqueId) { //not sure why i made this a function
        try {
            WavReader wav = new WavReader(MainActivity.RecordingsDirectory + uniqueId + ".wav", true);
            return 2 * wav.getByteArrayDouble().length / 44100f;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
