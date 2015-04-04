import csv

FILENAME = '../Downloads/mock_student_data.csv'
ID = 0
FIRST_NAME = 1
LAST_NAME = 2
STATE = 3
GENDER = 4
AGE = 5
GPA = 6
DAYS_MISSED = 7
GRADUATED = 8

def read_file():
	f = open(FILENAME,'rU')
	data = csv.reader(f)
	data.next()

	return data

	#for line in data:


