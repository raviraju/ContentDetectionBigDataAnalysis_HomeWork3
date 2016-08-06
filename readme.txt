langDetect/src
	langDetection.java			: To determine language diversity
	FileListingVisitor.java  	: Helper Utility to navigate files
	
langDetect/languages
	eng.pdf  frn.pdf  rus.pdf  spn.pdf : UHRD test docs

langDetect/languages_output (on UHRD test docs)
	OptimazeResults.txt   		: Results of OptimazeLangDetector on UHRD test docs
	langCloud.json  			: JSON for language word cloud viz
	langDiversity.json  		: JSON for language diversity viz
	output.json					: JSON for file level details of languages

langDetect/polar_output	(on polar dataset)
	langCloud.json  			: JSON for language word cloud viz
	langDiversity.json  		: JSON for language diversity viz
	output.json					: JSON for file level details of languages
	
langDetect/viz/d3_LangDiversity_PieChart/
	index.html , langDiversity.json  , 	pieCharts_Tika.js  
	viz.png						: Snapshot of viz
		
=========================================================================================
parserCallChain/
	src/
	english.stop  				: Stop words used to eliminate in finding tokens
	ParserCallChain.java		: To determine Parser Call Chain, Text and MetaData Word Cloud 
	
	1_output/  4_output/  6_output/      8_text_plain/  
	2_output/  5_output/  7_pdf_output/  9_text_html/
	   
	viz/
	index.html, liquidFillGauge.js, parserCallChainResult.json [NOTE : the following can be updated in d3.json("2_output/parserCallChainResult.json") to view respective dataset viz]
	1_output  5_output      8_text_plain  
	2_output  6_output      9_text_html   
	4_output  7_pdf_output      
	viz.png						: Snapshot of viz
=========================================================================================
wordCloudViz/
	HTML src files
	content-word-cloud.html  lang-word-cloud.html  metaData-word-cloud.html
	
	Content and Metadata json files for Word Cloud viz [NOTE in content-word-cloud.html, metaData-word-cloud.html json sources can be
	changed to load one of following at : d3.json("1_output/contentCloud.json")]
	1_output/  4_output/  6_output/      8_text_plain/  
	2_output/  5_output/  7_pdf_output/  9_text_html/
	
	Snapshots:
	contentWordCloud.png  langWordCloud.png  metaDataWordCloud.png


=======================
visualizations are also in the folder visualizations/team19/
for step 5 and step 10

related code is in folder yao
geotopicparser.py		yao_file_detector.py
helper.py			yao_file_size.py
main.py				yao_measurement_spectrum.py
sweetparser.py			yaoexiftool.py
tagratio.py			yaoner.py
utility.py			yaoutility.py
xml-parser.py


=====================


9.
For seperate opennlp,nltk,corenlp, we write three algorithms to finish it, they are CoreNER.java, NLTK.java and OpenNLP.java;

for opennlp: input is the file folder of dataset:
     usc-secure-wireless-071-030:src Kelly$ java OpenNLP /Users/Kelly/Documents/data/
the entities will output in command line;

for corenlp: input is the file folder of dataset:
     usc-secure-wireless-071-030:src Kelly$ java CoreNER /Users/Kelly/Documents/data/
the entities will output in command line;

for nltk: we should start the rest server first, then use the file folder of dataset as input:
     usc-secure-wireless-071-030:src Kelly$ java NLTK /Users/Kelly/Documents/data/

for the CompositeNERAgreementParser, the input is the file folder of dataset, output is a HashMap<String,Integer>, entities are put as the first element while it's occuring times are put as the second element.
     usc-secure-wireless-071-030:src Kelly$ java CompositeNERAgreementParser /Users/Kelly/Documents/test/

for the grobid quantities, the input is also the file folder of dataset, output is a HashMap<String,Set<String>>, which will use the extracted text by tika as the input(on cbor data), int the hashmap, measurement_numbers,measurement_units will be identified. 
     usc-secure-wireless-071-030:src Kelly$ java Grobid /Users/Kelly/Documents/data/

for  max_joint visualization, it's under the folder /max_joint_agreement/

4. d3 visualization is under folder /request_path_d3/, we used python to deal with cbor data and get json as the output, then use a java program OutCSV.java to transfrom from json to csv, use csv as the input for visualization.
