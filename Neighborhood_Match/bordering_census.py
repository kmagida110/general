import math
import csv
import import_files
import os

EARTH_RADIUS = 6378.1
DEGREES_TO_RADIANS = math.pi / 180
CENSUS_LAT = 6
CENSUS_LONG = 7
CENSUS_TRACT = 1
DISTANCE = 1.25
CENSUS = os.path.join(os.path.dirname(__file__), '../..', 'data/census_tracts.csv')
MAX_LAT = 42.02269
MAX_LONG = -87.52469
MIN_LAT = 41.64469
MIN_LONG = -87.91444
BORDERING_CENSUS = os.path.join(os.path.dirname(__file__), '../..', 'output/output_census_tracts' + str(DISTANCE) + '.csv')



def distance_to_center(lat1, long1, lat2, long2):
	'''
	Returns boolean if points are closer than constant distance
	'''
	long1_rad, lat1_rad, long2_rad, lat2_rad = map(math.radians, [long1, lat1, long2, lat2])

	dlong = long2_rad - long1_rad
	dlat = lat2_rad - lat1_rad
	a = (math.sin(dlat/2))**2 + math.cos(lat1_rad) * math.cos(lat2_rad) * ((math.sin(dlong/2)**2))	
	c = 2 * math.asin(math.sqrt(a))
	d = EARTH_RADIUS * c

	return d < DISTANCE

def log_nearby_census_blocks(comparison_row,cen_data, census_block_dictionary):
	'''
	Adds new census blocks to a dictionary based on centroids within fixed distance
	'''
	
	comparison_tract = comparison_row[CENSUS_TRACT]
	comparison_lat = comparison_row[CENSUS_LAT]
	comparison_lon = comparison_row[CENSUS_LONG]

	if comparison_tract not in census_block_dictionary:
		census_block_dictionary[comparison_tract] = []

	# Checks each remaining row and adds to dictionary if within range
	for row in cen_data:
		current_lat = float(row[CENSUS_LAT])
		current_lon = float(row[CENSUS_LONG])
		current_tract = row[CENSUS_TRACT]
		if distance_to_center(float(comparison_lat),float(comparison_lon),current_lat,current_lon):
			census_block_dictionary[comparison_tract].append(current_tract)

def run_full_data_set():
	'''
	Takes census centroid file and returns file of all census pairs
	'''
	census_block_dictionary = {}
	counter = 1

	cen = open(CENSUS, 'rU')
	cen_reader = csv.reader(cen)
	cen_data = []
	# Assume file has header
	cen_reader.next()

	# Build census list
	for line in cen_reader:
		if line != []:
			cen_data.append(line)
	
	# Build census dictionary of nearby census blocks
	for line in cen_data:		
		log_nearby_census_blocks(line,cen_data[counter:],census_block_dictionary)
		counter += 1

	
	# Writes dictionary to file with pairs repeated so that each item is in the first column
	f = open(BORDERING_CENSUS, 'w')
	header = ['PK','census_1','census_2']
	import_files.write_line(header,f)
	primary_key = 0
	for primary_block in census_block_dictionary.keys():
		import_files.write_line([primary_key, primary_block, primary_block],f)
		primary_key += 1
		for block in census_block_dictionary[primary_block]:
			import_files.write_line([primary_key, primary_block, block],f)
			primary_key += 1
			import_files.write_line([primary_key, block, primary_block], f)
			primary_key += 1


	f.close()

if __name__ == '__main__':
	run_full_data_set()




