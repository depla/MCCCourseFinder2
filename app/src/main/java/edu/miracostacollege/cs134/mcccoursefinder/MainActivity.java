package edu.miracostacollege.cs134.mcccoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.miracostacollege.cs134.mcccoursefinder.model.Course;
import edu.miracostacollege.cs134.mcccoursefinder.model.DBHelper;
import edu.miracostacollege.cs134.mcccoursefinder.model.Instructor;
import edu.miracostacollege.cs134.mcccoursefinder.model.Offering;

/**
 * MCC Course Finder 2 - Lets user refine and search cs courses at MCC
 *
 * Dennis La
 * 
 * CS134
 */

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private static final String TAG = "MCC Course Finder";

    private List<Instructor> allInstructorsList;
    private List<Course> allCoursesList;
    private List<Offering> allOfferingsList;
    private List<Offering> filteredOfferingsList;

    private EditText courseTitleEditText;
    private Spinner instructorSpinner;
    private ListView offeringsListView;
    private OfferingListAdapter offeringsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        db.importOfferingsFromCSV("offerings.csv");

        allCoursesList = db.getAllCourses();
        allInstructorsList = db.getAllInstructors();
        allOfferingsList = db.getAllOfferings();

        //make a copy in the filteredOfferingsList
        filteredOfferingsList = new ArrayList<>(allOfferingsList);


        //DONE (1): Construct instructorSpinnerAdapter using the method getInstructorNames()
        //DONE: to populate the spinner.
        instructorSpinner = findViewById(R.id.instructorSpinner);
        final ArrayAdapter<String> instructorSpinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getInstructorNames());

        instructorSpinner.setAdapter(instructorSpinnerAdapter);
        instructorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //lets get the instructor at position in the parent
                String selectedInstructorName = String.valueOf(parent.getItemAtPosition(position));

                //if the selected instructor is [Select Instructor]
                if(position > 0)
                {
                    //clear the list adapter first, then filter
                    offeringsListAdapter.clear();

                    //loop through the offerings list and try to find the selectedInstructorName
                    for(Offering offering : allOfferingsList)
                    {
                        Instructor instructor = offering.getInstructor();

                        if(instructor.getFullName().equalsIgnoreCase(selectedInstructorName))
                        {
                            offeringsListAdapter.add(offering);
                        }
                    }
                }
                else
                {
                    offeringsListAdapter.clear();
                    offeringsListAdapter.addAll(allOfferingsList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //wire up the list view
        offeringsListView = findViewById(R.id.offeringsListView);
        offeringsListAdapter = new OfferingListAdapter(this, R.layout.offering_list_item, filteredOfferingsList);

        //connect the list view to the list adapter
        offeringsListView.setAdapter(offeringsListAdapter);


        //wire up the edit text
        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        courseTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                //clear out the list adapter
                offeringsListAdapter.clear();

                if(searchText.isEmpty())
                {
                    offeringsListAdapter.addAll(allOfferingsList);
                    return;
                }

                //loop through the offerings
                for(Offering offering : allOfferingsList)
                {
                    if(offering.getCourse().getTitle().contains(searchText))
                    {
                        offeringsListAdapter.addAll(offering);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }


    //DONE (2): Create a method getInstructorNames that returns a String[] containing the entry
    //DONE: "[SELECT INSTRUCTOR]" at position 0, followed by all the full instructor names in the
    //DONE: allInstructorsList
    private String[] getInstructorNames()
    {
        String instructorName[] = new String[allInstructorsList.size() + 1];

        instructorName[0] = "[Select Instructor]";

        for(int i = 1; i < instructorName.length; i++)
        {
            instructorName[i] = allInstructorsList.get(i-1).getFullName();
        }

        return instructorName;
    }


    //DONE (3): Create a void method named reset that sets the test of the edit text back to an
    //DONE: empty string, sets the selection of the Spinner to 0 and clears out the offeringListAdapter,
    //DONE: then rebuild it with the allOfferingsList
    public void reset(View v)
    {
        courseTitleEditText.setText("");

        instructorSpinner.setSelection(0);

        offeringsListAdapter.clear();
        offeringsListAdapter.addAll(allOfferingsList);
    }




    //DONE (4): Create a TextWatcher named courseTitleTextWatcher that will implement the onTextChanged method.
    //DONE: In this method, set the selection of the instructorSpinner to 0, then
    //DONE: Clear the offeringListAdapter
    //DONE: If the entry is an empty String "", the offeringListAdapter should addAll from the allOfferingsList
    //DONE: Else, the offeringListAdapter should add each Offering whose course title starts with the entry.



    //DONE (5): Create an AdapterView.OnItemSelectedListener named instructorSpinnerListener and implement
    //DONE: the onItemSelected method to do the following:
    //DONE: If the selectedInstructorName != "[Select Instructor]", clear the offeringListAdapter,
    //DONE: then rebuild it with every Offering that has an instructor whose full name equals the one selected.
}
