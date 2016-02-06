package edu.uchicago.capp;


public class SimplePredicate {

	private String fieldName;
	private int compareValue;
	private Op op;

	public enum Op {
		EQUALS, GREATER_THAN, LESS_THAN, LESS_THAN_OR_EQ, GREATER_THAN_OR_EQ, NOT_EQUALS;
	}

	public SimplePredicate(String fieldName, int compareValue, Op op) {
		super();
		this.fieldName = fieldName;
		this.compareValue = compareValue;
		this.op = op;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(int compareValue) {
		this.compareValue = compareValue;
	}

	public Op getOp() {
		return op;
	}

	public void setOp(Op op) {
		this.op = op;
	}
}
