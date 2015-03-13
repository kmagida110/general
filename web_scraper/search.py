# CS122 W'15
# Course search engine
#
# kmagida-slarrain
# KYLE MAGIDA
# SANTIAGO LARRAIN
#

import re
import util
import bs4
import urllib2
import Queue


def build_course_search_engine(N):
    '''
    Build a search engine for courses from the 122 shadow copy of the catalog.
    
    Inputs:
      N - the maximum of pages to crawl number 

    outputs:

      function that takes a search string and returns the titles and
      URLs of courses that match the search string
    '''

    starting_url = "http://www.classes.cs.uchicago.edu/archive/2015/winter/12200-1/new.collegecatalog.uchicago.edu/index.html"
    limiting_domain = "cs.uchicago.edu"
    return build_search_engine(starting_url, limiting_domain, N)


def build_cs_course_search():
    '''                                                                                                        
    Function useful for testing indexing on a single page.                                                     
    '''
    starting_url = "http://www.classes.cs.uchicago.edu/archive/2015/winter/12200-1/new.collegecatalog.uchicago.edu/thecollege/computerscience/index.html"
    limiting_domain = "cs.uchicago.edu"
    return build_search_engine(starting_url, limiting_domain, 1)


def build_search_engine(starting_url, limiting_domain, max_num_pages_to_visit):
    
    if util.is_url_ok_to_follow(starting_url, limiting_domain)==False:
        print "The original URL provided is not OK to follow. Check the URL and the limiting domain."
        return None
    index = crawler(starting_url, max_num_pages_to_visit, limiting_domain)

    def search(search_string):
        return do_search(search_string, index)
    return search


def do_search(search_string, index):
    
    words = re.findall("[a-zA-Z]\w*",search_string.lower())
    if len(words)==0:
        return "Don't be a wise ass. Search for something valid"
    
    result = []
    if words[0] in index.keys():
        intersec = index[words[0]]
        for word in words[1:]:
            if word in index.keys():
                intersec = intersec.intersection(index[word])
            else:
                return []
        result = list(intersec)
    return result


def create_soup(req):
    
    html_string = util.read_request(req)    
    soup = bs4.BeautifulSoup(html_string)
    url_output = util.get_request_url(req)
    return soup, url_output


def crawler (top_url, max_num_pages_to_visit, limiting_domain):
    
    index = {}
    url_queue = Queue.Queue()
    url_queue.put(top_url)
    indexed_counter = 0
    added_urls = set()
    added_urls.add(top_url)

    while indexed_counter < max_num_pages_to_visit and url_queue.empty()==False:
        active_url = url_queue.get()
        req = util.get_request(active_url)
        if req != None:
            soup, new_url = create_soup(req)
            find_check_clean_add(soup, new_url, limiting_domain, url_queue, added_urls)
            do_index(soup, new_url, index)
            indexed_counter += 1
        else:
            continue
        
    return index


def find_check_clean_add (soup, active_url, limiting_domain, url_queue, added_urls):

    for a in soup.find_all("a", href=True):
        temp_url = util.remove_fragment(util.convert_if_relative_url(active_url, a['href']))
        if util.is_url_ok_to_follow(temp_url, limiting_domain) and (temp_url not in added_urls):
            added_urls.add(temp_url)
            url_queue.put(temp_url)


def do_index(soup, new_url, index):

    courses = soup.find_all('div', class_= "courseblock main")
    
    for course in courses:
        main_words_to_add, title = get_words(course)
        course_tuple = (new_url, title)
        subsequences = util.find_sequence(course)
        
        if len(subsequences) == 0:
            add_string_to_index(main_words_to_add, index, course_tuple)
        
        else:
            for subsequence in subsequences:
                subsequence_words, subsequence_title = get_words(subsequence)
                subsequence_words_to_add = subsequence_words + main_words_to_add
                subsequence_tuple = (new_url, subsequence_title)
                add_string_to_index(subsequence_words_to_add, index, subsequence_tuple)


def get_words(course):

    title = course.find('p', class_ = "courseblocktitle").text
    description = course.find('p', class_ = "courseblockdesc").text

    title_words = re.findall("[a-zA-Z]\w*",title.lower())
    desc_words = re.findall("[a-zA-Z]\w*",description.lower())
    
    words_to_add = title_words + desc_words
    return words_to_add, title


def add_string_to_index(word_list, index, course_tuple):
    
    for word in word_list:
        if word not in index:
            index[word] = set()
        index[word].add(course_tuple)


            




