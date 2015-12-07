# capp-hw0

## Requirements
 - Install Java 7
 - Install Maven 3.2+
 - Clone repository
 - To download required packages: $ mvn install  
 - Make the unit test pass: $ mvn test  

## TODO
 - Implement a class for the interface:
 ```
public interface SampleOperators {

     public List<StudentTuple> selectStudents(List<StudentTuple> students, SimplePredicate predicate);
     public List<JoinedTuple> naturalJoin(List<StudentTuple> students, List<InstructorTuple> instructors);

 }
 ```
 - Update function:
 ``` 
  public static SampleOperators getSampleOperatorsImpl(){
  
      //TODO
      return null;
  
  } 
  ```
 - Make unit tests in SampleOperatorsTest pass (can run via $mvn test) 
 - run *maven clean* and tar or zip your submission and email Adam and myself by Tuesday 10/20 at 1:30 pm CST
