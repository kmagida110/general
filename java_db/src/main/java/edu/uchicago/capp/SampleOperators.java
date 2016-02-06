package edu.uchicago.capp;

import java.util.List;


public interface SampleOperators 
{
	//A select operator that returns StudentTuples that match the predicate (evaluates to true)
	public List<StudentTuple> selectStudents(List<StudentTuple> students, SimplePredicate predicate);
	//A natural join between the two lists of tuples
	public List<JoinedTuple> naturalJoin(List<StudentTuple> students, List<InstructorTuple> instructors);
}
