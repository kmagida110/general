package edu.uchicago.capp;
import java.util.ArrayList;
import java.util.List;
import edu.uchicago.capp.SimplePredicate.Op;

public class KyleImplementation implements SampleOperators {
	public static final int LISTSIZE = 5;

	public List<StudentTuple> selectStudents(List<StudentTuple> students, SimplePredicate predicate) {
		List<StudentTuple> filteredList = new ArrayList<StudentTuple>(LISTSIZE);
		for (int i =0;i<students.size();i++) {
			StudentTuple student = students.get(i);
			if (matchesPredicate(student,predicate)) {
				filteredList.add(student);
			}			
		}

		return filteredList;
	}

	private boolean matchesPredicate(StudentTuple student, SimplePredicate predicate){

		String fieldName = predicate.getFieldName();

		// Error check for correct field
		if (fieldName != "classYear"){
			System.out.printf("Invalid Field, use classYear");
		}
		
		Op op = predicate.getOp();
		int compareValue = predicate.getCompareValue();
		int classYear = student.getClassYear();

		switch (op) {

			case EQUALS:
				if (classYear == compareValue) {
					return true;					
				}
				else {
					return false;
					}
			case GREATER_THAN:
				if (classYear > compareValue) {
					return true;					
				}
				else {
					return false;
					}
			case LESS_THAN:
				if (classYear < compareValue) {
					return true;					
				}
				else {
					return false;
					}

			case LESS_THAN_OR_EQ:
				if (classYear <= compareValue) {
					return true;					
				}
				else {
					return false;
					}

			case GREATER_THAN_OR_EQ:
				if (classYear >= compareValue) {
					return true;					
				}
				else {
					return false;
					}

			case NOT_EQUALS:
				if (classYear != compareValue) {
					return true;					
				}
				else {
					return false;
					}

			default:
				System.out.printf("Invalid Predicate");
		}
		return false;
	}
	@Override
	public List<JoinedTuple> naturalJoin(List<StudentTuple> students, List<InstructorTuple> instructors){
		// Assuming that natural join includes name here

		List<JoinedTuple> joinedList = new ArrayList<JoinedTuple>(LISTSIZE);

		for (int i = 0;i<students.size();i++) {
			for (int j = 0;j<instructors.size();j++) {

				if (matchesJoin(students.get(i),instructors.get(j))) {
					JoinedTuple joinedAdd = joinTuples(students.get(i),instructors.get(j));
					joinedList.add(joinedAdd);
					
				}				
			}			
		}

		return joinedList;

	}
	
	private JoinedTuple joinTuples(StudentTuple student, InstructorTuple instructor){

		String studentProject = student.getProject();
		String studentName = student.getStudenName();
		String instructorName = instructor.getInstructorName();
		int classYear = student.getClassYear();
		String department = instructor.getDepartment();


		JoinedTuple joinedAdd = new JoinedTuple(instructorName,department,studentProject,studentName,classYear);

		return joinedAdd;
	}
	
	private boolean matchesJoin(StudentTuple student, InstructorTuple instructor){
		// Checks if a student and instructor tuple match for a natural join

		String studentProject = student.getProject();
		String instructorProject = instructor.getProject();
		
		// Only join on non-null strings
		if(!isNullString(studentProject) && !isNullString(instructorProject)){
			if (studentProject == instructorProject) {
				return true;
			}
		}
		// Name may join in theory, won't in these test cases

		String studentName = student.getStudenName();
		String instructorName = instructor.getInstructorName();

		if(!isNullString(studentName) && !isNullString(instructorName)){
			if (studentName == instructorName) {
				return true;
			}
		}	
		return false;
	}
	// Check is string is null
	private boolean isNullString(String str){
		if(str != null && !str.isEmpty()){
			return false;
		}
		else{
			return true;
		}		
	}
}
