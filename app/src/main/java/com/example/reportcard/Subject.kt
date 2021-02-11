package com.example.reportcard

class Subject(val name: String, val exam: Boolean) {


    var grades = mutableListOf<Int>();
    var examGrade: Int = 0
    var sumOfGrades: Int = 0
    var examSet: Boolean = false
    //var average: Double = 0.0

    fun addGrade(grade: Int): Unit {
        grades.add(grade);
    }

    fun getNrOfGrades(): Int {
        return grades.size;
    }

    fun setExam(grade: Int): Unit {
        if (exam) {
            examGrade = grade
        }
    }

    override fun toString(): String {
        return name
    }

}







