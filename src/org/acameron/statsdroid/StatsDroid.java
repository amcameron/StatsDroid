package org.acameron.statsdroid;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class StatsDroid extends Activity {
	private ArrayList<Integer> callDurations;
	private int callType;
	
    /** Called when the activity is first created. */
    //@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Spinner spinner = (Spinner) findViewById(R.id.call_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.call_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        spinner.setSelection(0);
    }
    
    private void update() {
        Cursor c = getContentResolver().query(Calls.CONTENT_URI,
        		null, null, null, null);
        
        int durationColumn = c.getColumnIndex(Calls.DURATION);
        int typeColumn = c.getColumnIndex(Calls.TYPE);
        
        callDurations = new ArrayList<Integer>();
        
        if (c.moveToFirst()) {
        	do {
        		if (c.getInt(typeColumn) == callType)
	        		callDurations.add(c.getInt(durationColumn));
        	} while (c.moveToNext());
        }

        int sum = 0;
        for (int duration : callDurations)
        	sum += duration;
        Double Mean = new Double((double)sum/(double)callDurations.size());
        double stdev = 0.0;
        for (int duration : callDurations)
        	stdev += ((double)duration - Mean)*((double)duration - Mean);
        stdev = Math.sqrt(stdev / (callDurations.size() - 1));
        Double Stdev = new Double(stdev); 
        
        TextView tv = (TextView) findViewById(R.id.stats_text);
        tv.setText("Mean: " + Mean.toString() + " StDev: " + Stdev.toString());
    }
    
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
          View view, int pos, long id) {
        	String type = parent.getItemAtPosition(pos).toString();
        	if (type.compareTo("Incoming") == 0)
        		callType = Calls.INCOMING_TYPE;
        	else if (type.compareTo("Outgoing") == 0)
        		callType = Calls.OUTGOING_TYPE;
        	else if (type.compareTo("Both") == 0)
        		callType = 0;
        	
        	update();
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
}