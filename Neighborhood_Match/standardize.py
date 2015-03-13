import numpy as np
import sqlite3
import build
import import_files
import os
STANDARDIZED_FILE = os.path.join(os.path.dirname(__file__), '../..', 'output/standardized_scores.csv')
SQL_STANDARDIZE_FILE = os.path.join(os.path.dirname(__file__), 'standardize.sql')

# Import, clean and save data
def import_aggregated_table(score_columns):
	'''
	Reads aggregated score columns from SQL to standardize
	'''
	select_statement = 'SELECT tract, ' + ", ".join(score_columns) + ' from multiple_tracts'
	
	db = sqlite3.connect(build.DATABASE)
	c = db.cursor()
	data = c.execute(select_statement).fetchall()
	db.close()

	return data

def clean_standardize_and_save(data, score_titles):
	'''
	Converts NULL to 0, removes tracts, standardizes and saves
	'''
	numpy_data = np.array(data, dtype=np.float)
	
	# Convert no data to 0
	null_locations = np.isnan(numpy_data)
	numpy_data[null_locations] = 0
	
	# Split tracts off from regular data
	tracts = numpy_data[:,0]
	full_data = numpy_data[:,1:]

	# Standardize and add tracts back on
	new_data = standardize(full_data)
	rv = np.c_[tracts,new_data]
	
	save_file(score_titles,rv)

def standardize(full_data):
	'''
	Returns values as z-score distance from 0
	'''
	sd = np.std(full_data,axis = 0)
	avg = np.average(full_data,axis = 0)


	z_score = (full_data - avg) / sd
	max_col = np.max(z_score, axis = 0) # General idea of range pulled from http://stats.stackexchange.com/questions/70801/how-to-normalize-data-to-0-1-range
	min_col = np.min(z_score, axis = 0)
	new_data = 100*(z_score - min_col)/(max_col - min_col)

	return new_data

def create_score_table(score_titles, score_types):
	'''
	Write create statment for score table
	'''
	create_statement = 'CREATE TABLE score_table (tract int, '
	
	for num in range(len(score_titles)):
		create_statement = create_statement + str(score_titles[num]) + ' ' + str(score_types[num]) + ','

	create_statement = create_statement[:-1] + ');'
	
	return create_statement

def save_file(headers, data):
	'''
	Saves file for input into SQL
	'''

	f = open(STANDARDIZED_FILE, 'w')
	f.write('tract,')
	import_files.write_line(headers,f)
	for line in data:
		import_files.write_line(line,f)
	f.close()

# Write file to load new table in SQL
def write_import_standardize_sql(columns, file_dictionary):
	'''
	Creates statment to import standardized data
	'''

	score_types = build.break_down_column_dict(file_dictionary, columns,'type')	
	score_titles = build.break_down_column_dict(file_dictionary, columns,'title')
	create_statement = create_score_table(score_titles, score_types)
	drop_tables = 'DROP TABLE score_table;'

	f = open(SQL_STANDARDIZE_FILE,'w')
	f.write(drop_tables)
	f.write('\n')
	f.write(create_statement)
	f.write('\n.separator , \n')
	f.write('.import ' + STANDARDIZED_FILE + ' score_table')

	return score_titles
	

def go():
	columns, file_dictionary = build.open_active_columns()
	column_titles = write_import_standardize_sql(columns, file_dictionary)	
	sql_pull = import_aggregated_table(column_titles)
	clean_standardize_and_save(sql_pull,column_titles)





if __name__ == '__main__':
	go()


