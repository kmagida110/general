package edu.uchicago.capp;

public class StudentTuple {
	private String studentName;
	private int classYear;
	private String project;
	
	public StudentTuple(String studenName, int classYear, String project) {
		super();
		this.studentName = studenName;
		this.classYear = classYear;
		this.project = project;
	}
	
	public String getStudenName() {
		return studentName;
	}
	public void setStudenName(String studenName) {
		this.studentName = studenName;
	}
	public int getClassYear() {
		return classYear;
	}
	public void setClassYear(int classYear) {
		this.classYear = classYear;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + classYear;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result
				+ ((studentName == null) ? 0 : studentName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentTuple other = (StudentTuple) obj;
		if (classYear != other.classYear)
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		if (studentName == null) {
			if (other.studentName != null)
				return false;
		} else if (!studentName.equals(other.studentName))
			return false;
		return true;
	}

	

}
