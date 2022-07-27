package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.wellness_studio.customCalendar.CustomCalendar;
import edu.neu.madcourse.wellness_studio.customCalendar.OnDateSelectedListener;
import edu.neu.madcourse.wellness_studio.customCalendar.OnNavigationButtonClickedListener;
import edu.neu.madcourse.wellness_studio.customCalendar.Property;

public class Profile extends AppCompatActivity implements OnNavigationButtonClickedListener {
    public static int MONTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Calendar calendar = Calendar.getInstance();
        MONTH = calendar.get(Calendar.MONTH);

        // get VI component
        final CustomCalendar customCalendar = (CustomCalendar) findViewById(R.id.custom_calendar);

        // initialize hashmap to hold properties,
        // key is a string of description, value is a property obj
        HashMap<Object, Property> propertyMap = new HashMap<>();

        // default property (no bg color, only date)
        Property propDefault = new Property();
        propDefault.layoutResource = R.layout.default_view;
        propDefault.dateTextViewResource = R.id.default_text;
        propertyMap.put("default", propDefault);

        // initialize current date
        Property propCurrent = new Property();
        propCurrent.layoutResource = R.layout.current_view;
        propCurrent.dateTextViewResource = R.id.current_text;
        propertyMap.put("current", propCurrent);

        // checked date (when exercise goal finished)
        Property propChecked = new Property();
        propChecked.layoutResource = R.layout.checked_view;
        propChecked.dateTextViewResource = R.id.checked_text;
        propertyMap.put("checked", propChecked);

        // selected date (when exercise goal finished)
        Property propSelected = new Property();
        propSelected.layoutResource = R.layout.selected_view;
        propSelected.dateTextViewResource = R.id.selected_text;
        propertyMap.put("selected", propSelected);


//        Property propUnavailable = new Property();
//        propUnavailable.layoutResource = R.layout.unavailable_view;
//        //You can leave the text view field blank. Custom calendar won't try to set a date on such views
//        propUnavailable.enable = false;
//        mapDescToProp.put("unavailable", propUnavailable);
//
//        Property propHoliday = new Property();
//        propHoliday.layoutResource = R.layout.holiday_view;
//        propHoliday.dateTextViewResource = R.id.holiday_datetextview;
//        mapDescToProp.put("holiday", propHoliday);

        customCalendar.setPropertyMap(propertyMap);

        // initialize hashmap holding dates with description and properties
        // key is date in integer form, value is a string description
        HashMap<Integer, Object> dateMap = new HashMap<>();

        // set default calendar
        //Calendar calendar = Calendar.getInstance();
        dateMap.put(calendar.get(Calendar.DAY_OF_MONTH), "current");  // today is the current date
        //MONTH = calendar.get(Calendar.MONTH);

        // test date input
        dateMap.put(2, "checked");
        dateMap.put(5, "checked");
        dateMap.put(6, "checked");
        dateMap.put(9, "checked");


        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        customCalendar.setDate(calendar, dateMap);

        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                Snackbar.make(customCalendar, selectedDate.get(Calendar.DAY_OF_MONTH)
                        + " / " + (selectedDate.get(Calendar.MONTH) + 1)
                        + " / " + selectedDate.get(Calendar.YEAR)
                        + " selected", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Calendar calendar = Calendar.getInstance();

        System.out.println("+++++++++++++++++");
        System.out.println(MONTH);
        System.out.println(Calendar.MONTH);
        System.out.println(Calendar.JULY);
        System.out.println("+++++++++++++++++");


        final int MONTH_final = MONTH;
        Map<Integer, Object>[] arr = new Map[2];
        switch(newMonth.get(Calendar.MONTH)) {
            case Calendar.AUGUST:
                arr[0] = new HashMap<>(); //This is the map linking a date to its description
                arr[0].put(3, "checked");

                arr[1] = null; //Optional: This is the map linking a date to its tag.
                break;
            case Calendar.JUNE:
                arr[0] = new HashMap<>();
                arr[0].put(5, "checked");
                break;
            case Calendar.JULY:
                arr[0] = new HashMap<>(); //This is the map linking a date to its description
                arr[0].put(2, "checked");
                arr[0].put(5, "checked");
                arr[0].put(6, "checked");
                arr[0].put(9, "checked");
                arr[0].put(calendar.get(Calendar.DAY_OF_MONTH), "current");
                break;
            default:
                arr[0] = new HashMap<>();

        }
        return arr;
    }
}