import urlparse
import requests
import os
import bs4

######### DO NOT CHANGE THIS CODE  #########

def get_request(url):
    '''
    Open a connection to the specified URL and if successful
    read the data.

    Inputs:
        url: must be an absolute URL
    
    Outputs: 
        request object or None

    Examples:
        get_request("http://www.cs.uchicago.edu")
    '''

    if not is_absolute_url(url):
        return None

    try:
        html = requests.get(url)
        return html
    except:
        # fail on any kind of error
        return None

def read_request(request):
    '''
    Return data from request object.  Returns result or "" if the read
    fails..
    '''

    try:
        return request.text.encode('iso-8859-1')
    except:
        print "read failed: " + request.url
        return ""

def get_request_url(request):
    return request.url


def is_absolute_url(url):
    if url == "":
        return False
    return urlparse.urlparse(url).netloc != ""


def is_http_url(url):
    return urlparse.urlparse(url).scheme == "http"

def remove_fragment(url):
    '''remove the fragment from a url'''

    (url, frag) = urlparse.urldefrag(url)
    return url

def convert_if_relative_url(current_url, new_url):
    '''
    Attempt to determine whether new_url is a relative URL and if so,
    use current_url to determine the path and create a new absolute
    URL.  Will add the protocol, if that is all that is missing.

    Inputs:
        current_url: absolute URL
        new_url: 

    Outputs:
        new absolute URL or None, if cannot determine that
        new_url is a relative URL.

    Examples:
        convert_if_relative_url("http://cs.uchicago.edu", "pa/pa1.html") yields 
            'http://cs.uchicago.edu/pa/pa.html'

        convert_if_relative_url("http://cs.uchicago.edu", "foo.edu/pa.html") yields
            'http://foo.edu/pa.html'
    '''
    if new_url == "" or not is_absolute_url(current_url):
        return None

    if is_absolute_url(new_url):
        return new_url

    parsed_url = urlparse.urlparse(new_url)
    path_parts = parsed_url.path.split("/")

    if len(path_parts) == 0:
        return None

    ext = path_parts[0][-4:]
    if ext in [".edu", ".org", ".com", ".net"]:
        return "http://" + new_url
    elif new_url[:3] == "www":
        return "http://" + new_path
    else:
        return urlparse.urljoin(current_url, new_url)


ARCHIVES = "http://www.classes.cs.uchicago.edu/archive/2015/winter/12200-1/new.collegecatalog.uchicago.edu/thecollege/archives"
LEN_ARCHIVES = len(ARCHIVES)
    

def is_url_ok_to_follow(url, limiting_domain):
    '''
    Inputs:
        url: absolute URL
        limiting domain: domain name

    Outputs: 
        Returns True if the protocol for the URL is HTTP, the domain
        is in the limiting domain, and the path is either a directory
        or a file that has no extension or ends in .html

    Examples:
        is_url_ok_to_follow("http://cs.uchicago.edu/pa/pa1", "cs.uchicago.edu") yields
            True

        is_url_ok_to_follow("http://cs.cornell.edu/pa/pa1", "cs.uchicago.edu") yields
            False
    '''


    if "mailto:" in url:
        return False

    if "@" in url:
        return False

    if url[:LEN_ARCHIVES] == ARCHIVES:
        return False

    parsed_url =  urlparse.urlparse(url)
    if parsed_url.scheme != "http":
        return False

    if parsed_url.netloc == "":
        return False

    if parsed_url.fragment != "":
        return False

    if parsed_url.query != "":
        return False


    loc = parsed_url.netloc
    ld = len(limiting_domain)
    trunc_loc = loc[-(ld+1):]
    if not (limiting_domain == loc or (trunc_loc == "." + limiting_domain)):
        return False

    # does it have the right extension
    (filename, ext) = os.path.splitext(parsed_url.path)
    return (ext == "" or ext == ".html")


def is_subsequence(tag):
    return isinstance(tag, bs4.element.Tag) and 'class' in tag.attrs \
        and tag['class'] == ['courseblock', 'subsequence']


def find_sequence(tag):
    '''
    If tag is the header for a sequence, then
    find the tags for the courses in the sequence.
    '''
    rv = []
    sib_tag = tag.next_sibling
    while is_subsequence(sib_tag) or (sib_tag==u'\n'):
        if sib_tag!=u'\n':
            rv.append(sib_tag)
        sib_tag = sib_tag.next_sibling
    return rv
        

