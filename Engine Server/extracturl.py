from BeautifulSoup import BeautifulSoup
import urllib
import os
import csv
import re

def extract_words(path):

	def clean_html(html):
		cleaned = re.sub(r"(?is)<(script|style).*?>.*?(</\1>)", "", html.strip())
		cleaned = re.sub(r"(?s)<!--(.*?)-->[\n]?", "", cleaned)
		cleaned = re.sub(r"(?s)<.*?>", " ", cleaned)
		cleaned = re.sub(r"&nbsp;", " ", cleaned)
		cleaned = re.sub(r"  ", " ", cleaned)
		cleaned = re.sub(r"  ", " ", cleaned)
		cleaned = re.sub(r'[^a-zA-Z\s]+', '',cleaned)
		return [word for word in cleaned.strip().replace('\n',' ').replace('\t',' ').split(' ') if word!='']

	for file in os.listdir(path):
		if file.endswith('.html'):
			filename = path+file
			print "Processing file: "+filename
			page = urllib.urlopen(filename).read()
			for word in clean_html(page): 
				yield word

def writeToFile(words):
	outFile = open('./big.txt','w+')
	for word in words:
		outFile.write(word+" ")

if __name__=='__main__':	
	words = extract_words('/Users/yiboji/Google Drive/CS572/HW4/static/')
	writeToFile(words)
	print "Done"


