import sys
import bordering_census
import import_files
import sqlite3
import json
import os
DATABASE = os.path.join(os.path.dirname(__file__), '..', 'tracts.db')
SQL_FILE = os.path.join(os.path.dirname(__file__), 'build_base_tables.sql')
COLUMN_STORAGE = os.path.join(os.path.dirname(__file__), '../..', 'output/score_columns.json') 
FILE_DICTIONARY = os.path.join(os.path.dirname(__file__), '../..', 'output/file_dictionary.json')

def write_base_tables_file(ordered_columns, file_dictionary):
	'''
	Writes create table statements based on columns included
	'''
	drop_tables = 'DROP TABLE nearby_tracts; DROP TABLE raw_tract_data; DROP TABLE multiple_tracts;'
	create_nearby_tracts = 'CREATE TABLE nearby_tracts (pk INTEGER, census_1 INTEGER, census_2 INTEGER);'

	
	ordered_titles = break_down_column_dict(file_dictionary, ordered_columns, 'title')
	ordered_types = break_down_column_dict(file_dictionary, ordered_columns, 'type')
	
	create_tract_statement = write_tract_table(ordered_titles, ordered_types)

	# Score titles are the columns in the multiple tracts table that will be used to calculate the final score
	create_aggregated_data, score_columns = write_aggregated_table(ordered_columns, file_dictionary)

	f = open(SQL_FILE, 'w')
	f.write(drop_tables)
	f.write('\n')
	f.write(create_nearby_tracts)
	f.write('\n')
	f.write(create_tract_statement)
	f.write('\n.separator , \n')
	f.write('.import ' + bordering_census.BORDERING_CENSUS + ' nearby_tracts')
	f.write('\n')
	f.write('.import ' + import_files.OUTPUT_FILE + ' raw_tract_data')
	f.write('\n')
	f.write(create_aggregated_data)
	f.close()
	return score_columns, file_dictionary

def write_tract_table(ordered_titles, ordered_types):
	'''
	Writes raw table with data for individual tracts
	'''

	# Loop over columns and types to build table create statement

	tract_data = 'CREATE TABLE raw_tract_data (pk INTEGER, tract INTEGER,'

	if len(ordered_titles) != len(ordered_types):
		print "Mismatched lengths"
	for num in (range(len(ordered_titles) - 1)):
		tract_data = tract_data + ordered_titles[num] + ' ' + ordered_types[num] + ','
	tract_data = tract_data + ordered_titles[-1] + ' ' + ordered_types[-1] + ');'
	return tract_data

def write_aggregated_table(ordered_columns, file_dictionary):
	'''
	Creates select statement and column list for aggregated table
	'''
	
	output_create = 'CREATE TABLE multiple_tracts AS SELECT r1.pk key, r1.tract tract, count(*) nearby_tracts, '
	score_columns = []
	# Need to duplication dict for rate columns
	rates = break_down_column_dict(file_dictionary, ordered_columns, 'rate')
	titles = break_down_column_dict(file_dictionary, ordered_columns, 'title')
	agg = break_down_column_dict(file_dictionary, ordered_columns, 'agg')
	category = break_down_column_dict(file_dictionary, ordered_columns, 'category')
	weight = break_down_column_dict(file_dictionary, ordered_columns, 'weight')
	datatype = break_down_column_dict(file_dictionary, ordered_columns, 'type')
	include = break_down_column_dict(file_dictionary, ordered_columns, 'include')
	column_id = break_down_column_dict(file_dictionary, ordered_columns, 'num')

	# Counter for added rate columns
	added_counter = -1
	for num in range(len(titles)):
		
		if include[num] == '0':
			continue		

		sum_statement = 'SUM(r2.' + titles[num] + ') '
		
		rate_list = rates[num].split(",")
		for rate in rate_list:
			# Returns only one tract's data for mean and medians
			if rate == 'single':
				output_create = output_create + 'r1.' + titles[num] + ' as ' + titles[num] + ', '
				score_columns.append(ordered_columns[num])
				continue
			
			# Takes a rate from the specified column
			elif rate != '0':
				output_create = output_create + sum_statement + '/ SUM(r2.' + rate +') as ' + titles[num] + '_by_' + rate + ', '
				
				filename = ordered_columns[num][0]
				column_number = ordered_columns[num][1]
				
				# Add a new column for the rate with the same information to the file dictionary with a negative counter
				file_dictionary[filename]['columns'][added_counter] = {}
				file_dictionary[filename]['columns'][added_counter]['title'] = titles[num] + '_by_' + rate
				file_dictionary[filename]['columns'][added_counter]['rate'] = rates[num]
				file_dictionary[filename]['columns'][added_counter]['include'] = include[num]
				file_dictionary[filename]['columns'][added_counter]['agg'] = agg[num]
				file_dictionary[filename]['columns'][added_counter]['category'] = category[num]
				file_dictionary[filename]['columns'][added_counter]['weight'] = weight[num]
				file_dictionary[filename]['columns'][added_counter]['type'] = datatype[num]
				file_dictionary[filename]['columns'][added_counter]['num'] = column_id[num]

				new_column = (filename, added_counter)
				added_counter -= 1
				score_columns.append(new_column)
				continue			
			else: 
				# Only pass on columns that are supposed to be scored, some absolutes are just for display
				output_create = output_create + sum_statement + ' as ' + titles[num] + ', '
				if include[num] != "-1":
					score_columns.append(ordered_columns[num])
					continue

	output_create = output_create[:-2] + ' FROM raw_tract_data as r1 INNER JOIN nearby_tracts as n ON r1.tract = n.census_1 '
	output_create = output_create + 'INNER JOIN raw_tract_data as r2 ON n.census_2 = r2.tract GROUP BY r1.pk, r1.tract;'

	return output_create, score_columns

def break_down_column_dict(file_dictionary, ordered_columns, output_variable):
	'''
	Returns list of ordered information about columns based on type requested
	'''
	rv = []
	for column in ordered_columns:
		filename = column[0]
		try:
			column_number = column[1]
			rv.append(file_dictionary[filename]['columns'][column_number][output_variable])
		except:
			column_number = str(column[1])
			rv.append(file_dictionary[filename]['columns'][column_number][output_variable])
	return rv

def open_active_columns():
	'''
	Pulls columns to be used for scoring from saved, allows for easier changing of columns to be scored
	'''
	f = open(COLUMN_STORAGE,'rb')
	columns = json.load(f)
	f.close()

	g = open(FILE_DICTIONARY, 'rb')
	file_dictionary = json.load(g)
	g.close()
	return columns, file_dictionary





