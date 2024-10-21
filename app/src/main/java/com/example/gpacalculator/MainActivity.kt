package com.example.gpacalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.math.RoundingMode
import java.util.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, TextWatcher{

    private var rootLinearLayout : LinearLayout? = null
    private val idMap : HashMap<String, Int> = HashMap()
    private var tv_gpa : TextView? = null
    private var courses = ArrayList<Course>(4)
    private var totalCourses = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLinearLayout =  findViewById(R.id.root)
        tv_gpa=findViewById(R.id.tv_gpa)
        //initial screen
        while (totalCourses!=4){
            addItem(null)
        }
    }

    private fun gpaCalculator(courses:ArrayList<Course>) : Double{
        var totalGradePoints = 0.0
        var totalCredits = 0.0
        for(course in courses){
            totalGradePoints += course.grade * course.credits
            totalCredits+=course.credits
        }


        return if (totalCredits > 0) {
            (totalGradePoints / totalCredits).toBigDecimal().setScale(3,RoundingMode.DOWN).toDouble()
        } else {
            0.0 // or some default value
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        var grade = 0.0
        var credits : Double = 0.0
        // Get the parent view of the selected spinner
        val parentLinearLayout = p0?.parent as LinearLayout
        val coursePosition = rootLinearLayout?.indexOfChild(parentLinearLayout as View)
        val parentLinearLayoutId = "LL$coursePosition"

        Log.i("Parent of spinner check", "${parentLinearLayoutId}")
        //Get the number of layout and creating id of edittext
//        val parentLLId:String = "et_course_"+  parentLinearLayout?.
//        let {
//            resources.getResourceEntryName(it.id)
//        }
//        Log.d("ParentLayoutId", "Parent Layout ID: $parentLLId")
        val editTextId: Int? = getIdFromName("et_course_$parentLinearLayoutId")
        Log.d("EditTextId", "EditText ID: $editTextId and actual ID: ${getIdFromName("et_course_$parentLinearLayoutId")}")
        // Find the EditText within the parent linear layout
        val etCourse: EditText? = parentLinearLayout.findViewById<EditText>(editTextId!!)

        // Check if the EditText is not null and has a non-empty text
        credits = if (etCourse != null && !etCourse.text.isNullOrBlank()) {
            etCourse.text.toString().toDouble()
        } else {
            0.0
        }
        //Assigning grade
        when(p3.toInt()){
            0 -> grade = 0.0
            1 -> grade = 4.0
            2 -> grade = 3.7
            3 -> grade = 3.33
            4 -> grade = 3.0
            5 -> grade = 2.7
            6 -> grade = 2.33
            7 -> grade = 2.00
            8 -> grade = 1.7
            9 -> grade = 1.33
            10 -> grade = 1.0
            11 -> grade = 0.0
            else -> 0.0

        }
        //storing grade and credits

        if(coursePosition!! >=0 && coursePosition!!<courses.size){
            courses[coursePosition!!] = Course(grade, credits)

        }

        tv_gpa?.text = gpaCalculator(courses).toString()
        Log.i("grades & credits", "${courses[coursePosition!!].grade} ${courses[coursePosition!!].credits}" )


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        var credits : Double = 0.0
        val et = currentFocus
        val parentLL = et?.parent?.parent?.parent
        val coursePosition = rootLinearLayout?.indexOfChild(parentLL as View)
        Log.i("CoursePos", "${et?.parent?.parent} $coursePosition ")
        // Check if the EditText is not null and has a non-empty text
        credits = if (p0 != null && !p0.isNullOrBlank()) {
            p0.toString()!!.toDouble()
        } else {
            0.0
        }
        if(coursePosition!! >=0 && coursePosition!!<courses.size){
            courses[coursePosition!!].credits = credits
        }
        tv_gpa?.text = gpaCalculator(courses).toString()
    }

    override fun afterTextChanged(p0: Editable?) {
    }


    fun addItem(view: View?){
        //new item
        val newItemLayout = LayoutInflater.from(this).inflate(R.layout.item_layout, null) as LinearLayout
        //generate id's
        val layoutId = View.generateViewId()
        val spinnerId = View.generateViewId()
        val editTextId = View.generateViewId()
        //assign id's to the views
        newItemLayout.id = layoutId
        newItemLayout.findViewById<Spinner>(R.id.custom_spinner).id =spinnerId
        newItemLayout.findViewById<EditText>(R.id.custom_et).id = editTextId
        //String representation of id's
        val layoutIdString = "LL$totalCourses"
        val spinnerIdString = "spinner_$layoutIdString"
        val editTextIdString = "et_course_$layoutIdString"
        Log.i("ids", "$totalCourses $spinnerIdString $editTextIdString" )

        //storing in hash

        idMap[spinnerIdString] = spinnerId
        idMap[editTextIdString] = editTextId
        //arrayadapter
        ArrayAdapter.createFromResource(
            this,
            R.array.grades_array,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            newItemLayout.findViewById<Spinner>(spinnerId).adapter = arrayAdapter
        }
        newItemLayout.findViewById<Spinner>(spinnerId).onItemSelectedListener = this
        //textwhatcher
        newItemLayout.findViewById<EditText>(editTextId).addTextChangedListener(this)
        /////////////////////////
        rootLinearLayout?.addView(newItemLayout)
        courses.add(Course(0.0,0.0))
        totalCourses++
    }

    private fun getIdFromName(name:String):Int{
        return idMap[name] ?: View.NO_ID
    }
}