import csv
import sys
import build
import bordering_census
import geocoding
import json
import os

DATA_DIRECTORY = os.path.join(os.path.dirname(__file__), '../..', 'data/')
OUTPUT_FILE = os.path.join(os.path.dirname(__file__), '../..', 'output/initial_output.csv')
FILE_LIST = os.path.join(os.path.dirname(__file__), '../..', 'data/list_of_files.csv')

# Structure of imported schema
DATATYPE = 0
COLUMN_NUM = 1
COLUMN_TITLE = 2
FILENAME = 3
LAT = 4
LONG = 5
PAIR = 6
CENSUS_TRACT = 7
ADDRESS = 8
AGGREGATE = 9
HEADERS = 10
TYPE = 11
RATE = 12
INCLUDE = 13
WEIGHT = 14	

def import_files():
	'''
	Runs main function with no geocoding for external use if needed
	'''
	columns = create_final_files()
	score_columns = build.write_base_tables_file(columns)
	dump_to_file(score_columns,build.COLUMN_STORAGE)

# Main function
def create_final_files(run_geocode=False):
	'''
	Reads all files and creates new geocoded files
	'''
	
	file_dictionary = create_file_dictionary()
	geocode_dictionary = geocoding.create_geocode_dictionary(file_dictionary, run_geocode)
	block_dict = {}
	final_column_order = []

	# Loop through files and add to block dictionary
	for name in file_dictionary.keys():
		column_order = 0
		filename = DATA_DIRECTORY + name
		data = read_and_geocode_file(file_dictionary,name, geocode_dictionary)
		add_to_block_dict(data, file_dictionary[name],block_dict)
		
		# Build titles for final file aligns columns with SQL titles
		column_dict = file_dictionary[name]['columns']
		ordered_columns = order_columns(column_dict)
		for column in ordered_columns:
			final_column_order.append((name,column))
	write_dictionary(block_dict, final_column_order, file_dictionary)
	return final_column_order, file_dictionary

# Develop schema
def create_file_dictionary():
	'''
	Create relevant information for files based on input file
	'''	
	file_dictionary = {}

	f = open(FILE_LIST, 'rU')
	reader = csv.reader(f)
	reader.next()
	column_counter = 0

	for line in reader:
		filename = line[FILENAME]
		
		# Add file to dictionary
		if filename not in file_dictionary:
			file_dictionary[filename] = {}
			file_dictionary[filename]['columns'] = {}
			# Tests if file has headers for reading
			file_dictionary[filename]['headers'] = int(line[HEADERS])
		

		# Add location data
		if ((int(line[LAT]) == 1) or (int(line[LONG]) == 1)  or (int(line[ADDRESS]) == 1) or (int(line[CENSUS_TRACT])) == 1):

			if int(line[LAT]) == 1:
				if 'lat/long' not in file_dictionary[filename]:
					file_dictionary[filename]['lat/long'] = []
				
				file_dictionary[filename]['lat/long'].append(('y',int(line[COLUMN_NUM])))
				file_dictionary[filename]['lat/long'].sort()
			
			# Add lat/long or address info, sort to keep keys in geocode dictionary uniform
			elif (int(line[LONG]) == 1):
				if 'lat/long' not in file_dictionary[filename]:
					file_dictionary[filename]['lat/long'] = []

				file_dictionary[filename]['lat/long'].append(('x',int(line[COLUMN_NUM])))
				file_dictionary[filename]['lat/long'].sort()
			
			elif (int(line[ADDRESS]) == 1):
				
				if 'address' not in file_dictionary[filename]:
					file_dictionary[filename]['address'] = []
				file_dictionary[filename]['address'].append((line[COLUMN_TITLE],int(line[COLUMN_NUM])))
				file_dictionary[filename]['address'].sort()
			else:
				file_dictionary[filename]['census_tract'] = int(line[COLUMN_NUM])
		
		# If not a location column, it is a data column, build dictionary
		else:
			file_dictionary[filename]['columns'][column_counter] = {}
			file_dictionary[filename]['columns'][column_counter]['num'] = int(line[COLUMN_NUM])
			file_dictionary[filename]['columns'][column_counter]['title'] = line[DATATYPE] + '_' + line[COLUMN_TITLE]
			file_dictionary[filename]['columns'][column_counter]['agg'] = line[AGGREGATE]
			file_dictionary[filename]['columns'][column_counter]['type'] = line[TYPE]
			file_dictionary[filename]['columns'][column_counter]['rate'] = line[RATE]
			file_dictionary[filename]['columns'][column_counter]['include'] = line[INCLUDE]
			file_dictionary[filename]['columns'][column_counter]['category'] = line[DATATYPE]
			file_dictionary[filename]['columns'][column_counter]['weight'] = line[WEIGHT]
			column_counter += 1


	return file_dictionary

# Reading functions

def order_columns(dictionary):
	'''
	Maintains order of columns for future use
	'''

	ordered_columns = dictionary.keys()
	ordered_columns.sort()
	return ordered_columns

def add_to_block_dict(data,file_attributes,block_dict):	
	'''
	Adds values to a dictionary keyed by tracts according to specified column aggregator
	'''
	column_dict = file_attributes['columns']
	ordered_columns = order_columns(column_dict)

	for row in data[1:]:
		
		tract = row[0]
		if tract not in block_dict:
			block_dict[tract] = {}
		
		for column in ordered_columns:
			column_title = column_dict[column]['title']
			agg_type = column_dict[column]['agg']
			if agg_type == 'sum' or agg_type =='percentage':
				try:
					amount_to_add = float(row[column_dict[column]['num']])
				except ValueError:
					amount_to_add = 0
			else:
				# Accounts for datasets where each item is counted eg. crime
				amount_to_add = 1.0
			block_dict[tract][column_title] = block_dict[tract].get(column_title, 0) + amount_to_add

def read_and_geocode_file(file_dictionary, input_file, geocode_dictionary):
	'''
	Reads in a file with only the specified columns and adds a primary key
	'''

	filename = DATA_DIRECTORY + input_file
	column_dict = file_dictionary[input_file]['columns']

	ordered_columns = order_columns(column_dict)
	
	# Check what data is provided in a given file and its location
	address = check_viable_columns(file_dictionary[input_file], 'address')
	lat_long = check_viable_columns(file_dictionary[input_file], 'lat/long')
	census_tract = check_viable_columns(file_dictionary[input_file], 'census_tract')

	f = open(filename, 'rU')
	reader = csv.reader(f)
	if file_dictionary[input_file]['headers'] == 1:
		reader.next()
	rv = [[]]
	rv[0].append('TRACT')

	# Add header
	for column in ordered_columns:
		rv[0].append(column_dict[column]['title'])

	# Add rows with census tract first, then data
	for line in reader:
		if census_tract == -1:
			tract_number = geocoding.look_up_census_tract(line, address,lat_long,geocode_dictionary)
		else:
			tract_number = line[census_tract]
		
		try:
			newline = [int(tract_number)]
		except (TypeError, ValueError):
			newline = [tract_number]
		
		# Add Data for each column specified based on given field
		for column in ordered_columns:
			if len(line) < column_dict[column]['num']:
				newline.append("Null")
			else:
				newline.append(line[column_dict[column]['num']])
		
		rv.append(newline)

	return rv

def check_viable_columns(dictionary, key):
	'''
	Return -1 if a key is not in the dictionary, returns the values otherwise
	'''
	if key in dictionary:
		rv = dictionary[key]
	else:
		rv = -1
	return rv

# Writing functions
def write_line(line, filename):
	'''
	Writes a line into a file in csv form with strings
	'''
	row_to_write = ''

	for item in line[:-1]:
		row_to_write = row_to_write + str(item) +  ','
	
	row_to_write = row_to_write + str(line[-1])

	filename.write(row_to_write + '\n')

def write_dictionary(block_dict, final_column_order, file_dictionary):
	'''
	Saves block dictionary to file
	'''
	f = open(OUTPUT_FILE, 'w')
	header = ['pk','tract']
	for column in final_column_order:
		filename = column[0]
		column_id = column[1]
		header.append(file_dictionary[filename]['columns'][column_id]['title'])
	
	write_line(header, f)
	primary_key = 0

	for key in block_dict.keys():
		newline = [primary_key, key]
		for column in final_column_order:
			filename = column[0]
			column_id = column[1]
			title = file_dictionary[filename]['columns'][column_id]['title']
			if title in block_dict[key]:
				newline.append(block_dict[key][title])
			else:
				newline.append(0)
		write_line(newline, f)
		primary_key += 1
	f.close()

def dump_to_file(dictionary, filename):
	'''
	Encodes dictionary in json
	'''
	o = open(filename,'wb')
	json.dump(dictionary,o)
	o.close()

if __name__ == '__main__':
	
	if len(sys.argv) > 1 and sys.argv[1] == 'True':
		ordered_columns, file_dictionary = create_final_files(True)
	else:
		ordered_columns, file_dictionary = create_final_files()
	
	if len(sys.argv) > 2 and sys.argv[2] == 'Nearby':
		bordering_census.run_full_data_set()
	
	score_columns, file_dictionary = build.write_base_tables_file(ordered_columns, file_dictionary)
	dump_to_file(score_columns,build.COLUMN_STORAGE)
	dump_to_file(file_dictionary,build.FILE_DICTIONARY)
