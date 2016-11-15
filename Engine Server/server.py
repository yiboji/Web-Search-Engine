from flask import Flask, url_for, request,Response, send_from_directory
import json
import os
import urllib
from urllib2 import *
from urllib2 import *
import simplejson
from spell import *
# connection = urlopen('http://localhost:8983/solr/collection_name/select?q=cheese&wt=json')
solr_conn = 'http://localhost:8983/solr/newhw3/'
# response = simplejson.load(connection)

app = Flask(__name__)

@app.route('/')
def app_root():
	return app.send_static_file('index.html')

@app.route('/search')
def app_search():
	text = request.args['search']
	query = {
		'q':text,
		'wt':'json'
	}
	query = solr_conn+'select?'+urllib.urlencode(query)
	jsonstr = urlopen(query)
	resp = Response(jsonstr,status=200,mimetype='appliation/json')
	return resp

@app.route('/searchpagerank')
def app_searchPageRank():
	text = request.args['search']
	query = {
		'q':text,
		'sort':'pageRankFile desc',
		'wt':'json'
	}
	query = solr_conn+'select?'+urllib.urlencode(query)
	print query
	jsonstr = urlopen(query)
	resp = Response(jsonstr, status=200, mimetype='application/json')
	return resp

@app.route('/getHTML')
def app_getHTML():
	filename = request.args['filename']
	htmlpath = "/Users/yiboji/NbcNewsWSJData/mergepages/" + filename
	print htmlpath
	return app.send_static_file(htmlpath)

@app.route('/suggest')
def app_suggest():
	text = request.args['search']
	query = {
		'q':text,
		'wt':'json'
	}
	query = solr_conn+'suggest?'+urllib.urlencode(query)
	print query
	jsonstr = urlopen(query);
	resp = Response(jsonstr, status=200, mimetype='appliaciton/json')
	return resp

@app.route('/spellcheck')
def app_spellCheck():
	text = request.args['search']
	return correction(text)

@app.route('/getSnippet')
def app_getSnippet():
	filename = request.args['filename']
	keyword = str(request.args['search'])
	filename = "/Users/yiboji/Google Drive/CS572/HW4/static/"+filename
	print filename
	def clean_html(html):
		cleaned = re.sub(r"(?is)<(script|style).*?>.*?(</\1>)", "", html.strip())
		cleaned = re.sub(r"(?s)<!--(.*?)-->[\n]?", "", cleaned)
		cleaned = re.sub(r"(?s)<.*?>", " ", cleaned)
		cleaned = re.sub(r"&nbsp;", " ", cleaned)
		cleaned = re.sub(r"  ", " ", cleaned)
		cleaned = re.sub(r"  ", " ", cleaned)
		# cleaned = re.sub(r'[^a-zA-Z\s]+', '',cleaned)
		return " ".join([word for word in cleaned.strip().replace('\n',' ').replace('\t',' ').split(' ') if word!=''])
	# try:
	page = urllib.urlopen(filename).read()
	page = clean_html(page)
	for sentence in page.split('.'):
		sentence = sentence.lower()
		if keyword.lower() in sentence:
			print clean_html(sentence)
			if sentence.startswith("http"): continue
			return sentence
	# except:
		# print "No such file ERROR"
	return ""
if __name__=='__main__':
	app.run()