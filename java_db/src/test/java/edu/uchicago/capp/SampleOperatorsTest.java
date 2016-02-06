package edu.uchicago.capp;

import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SampleOperatorsTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public SampleOperatorsTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(SampleOperatorsTest.class);
	}
	
	public void testSimple() {
		List<StudentTuple> students = getStudentTuples();
		assertTrue(students.contains(new StudentTuple("Bob", 2015, null)));
		StudentTuple[] studentsArray = new StudentTuple[] {
				new StudentTuple("Bob", 2015, null),
				new StudentTuple("Adam", 2013, "Qurk")};
		assertTrue(students.containsAll(Arrays.asList(studentsArray)));		
	}

	public void testSelect() {
		SampleOperators ops = SampleOperatorFactory.getSampleOperatorsImpl();
		List<StudentTuple> filtered = ops.selectStudents(getStudentTuples(), new SimplePredicate("classYear", 2014, SimplePredicate.Op.LESS_THAN_OR_EQ));

		StudentTuple[] students = new StudentTuple[] {
				new StudentTuple("Anant", 2014, "DataHub"),
				new StudentTuple("Sudipto", 2008, "Elastras"),
				new StudentTuple("Adam", 2013, "Qurk")};
		assertTrue(filtered.containsAll(Arrays.asList(students)));
		assertTrue(filtered.size()==students.length);

	}
	
	public void testJoin() {
		SampleOperators ops = SampleOperatorFactory.getSampleOperatorsImpl();
		List<JoinedTuple> joined= ops.naturalJoin(getStudentTuples(), getInstructorTuples());
		JoinedTuple[] joinedArray = new JoinedTuple[] {
				new JoinedTuple("Sam", "CSAIL", "DataHub","Anant", 2014),
				new JoinedTuple("Amr", "CS", "Elastras","Sudipto", 2008 )
				};
		assertTrue(joined.containsAll(Arrays.asList(joinedArray)));
		assertTrue(joined.size()==joinedArray.length);

		
	}

	public List<StudentTuple> getStudentTuples() {
		StudentTuple[] students = new StudentTuple[] {
				new StudentTuple("Bob", 2015, null),
				new StudentTuple("Adam", 2019, "Portage"),
				new StudentTuple("Anant", 2014, "DataHub"),
				new StudentTuple("Sudipto", 2008, "Elastras"),
				new StudentTuple("Adam", 2013, "Qurk")};
		return Arrays.asList(students);
	}

	public List<InstructorTuple> getInstructorTuples() {
		InstructorTuple[] instructors = new InstructorTuple[] { 
				new InstructorTuple("Sam", "CSAIL", "DataHub"),
				new InstructorTuple("Amr", "CS", "Elastras"),
				new InstructorTuple("Magda", "CSE", "Myria"),
				new InstructorTuple("Hector", "CS", "Deco"),
				new InstructorTuple("Ken", "Cheriton", "RemusDB"),};
		return Arrays.asList(instructors);
	}
}
