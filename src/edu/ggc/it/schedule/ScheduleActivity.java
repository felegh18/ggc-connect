package edu.ggc.it.schedule;

import edu.ggc.it.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ScheduleActivity extends Activity {

	private Context scheduleContext;
	private ScheduleDatabase database;
	private SimpleCursorAdapter cursorAdapter;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		scheduleContext = this;
		
		list = (ListView) findViewById(R.id.schedule_list_view);
		list.setOnItemClickListener(new ScheduleActivityListListener());

		// open new database
		database = new ScheduleDatabase(scheduleContext);
		database.open();

		if (!classesExist()) {
			CharSequence text = "No courses found on your schedule. Use the menu to add a course.";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(scheduleContext, text, duration);
			toast.show();;
		} else {
			populateList();
		}

	}

	private void populateList() {
		Cursor cursor = database.queryAll();
		startManagingCursor(cursor);

		String[] from = new String[] { ScheduleDatabase.KEY_NAME, ScheduleDatabase.KEY_BUILDING_LOCATION };
		int[] to = new int[] { R.id.class_name, R.id.building_location };
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.schedule_list_row,
				cursor, from, to);

		list.setAdapter(cursorAdapter);
		registerForContextMenu(list.getRootView());
	}

	private boolean classesExist() {
		Cursor c = database.queryAll();
		if (c.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private void showAddScheduleItemDialog() {
		new AlertDialog.Builder(scheduleContext)
				.setTitle("No Classes Found")
				.setMessage(R.string.schedule_no_classes_popup)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								startActivity(new Intent(scheduleContext,
										ScheduleUpdateActivity.class));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_schedule, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_class:
			startActivity(new Intent(scheduleContext,
					ScheduleUpdateActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void updateDatabase(long rowId, boolean create) {
		Intent intent = new Intent(scheduleContext, ScheduleUpdateActivity.class);
		if (!create) {
			intent.putExtra("rowID", rowId);
		}
		startActivity(intent);
	}
	
	public class ScheduleActivityListListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long rowId) {
			updateDatabase(rowId, false);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

}
