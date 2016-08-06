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