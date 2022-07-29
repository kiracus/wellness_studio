package edu.neu.madcourse.wellness_studio.customCalendar;

// represents the property of a calendar date view
public class Property {
	
	/**
	 * Resource id for the layout to be inflated.
	 */
	public int layoutResource = -1;
	
	/**
	 * Resource id for the text view within the date view which will be used to display day of month.
	 */
	public int dateTextViewResource = -1;
	
	/**
	 * true if the date view should be enabled, false otherwise.
	 */
	public boolean enable = true;
	
}
