package com.company;

import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.ProfilingHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

class Stats{
    private long contentSize;
    private long metaDataSize;
    private String resource;
    private String metaDataString;

    Stats(long contentSize,long metaDataSize,String resource, String metaDataString){
        this.contentSize = contentSize;
        this.metaDataSize = metaDataSize;
        this.resource = resource;
        this.metaDataString = metaDataString;
    }

    public String getResourceName(){
        return resource;
    }
    public long getContentSize(){
        return contentSize;
    }
    public long getMetaDataSize(){
        return metaDataSize;
    }
    public String getMetaDataString(){ return metaDataString;}
    public String toString(){
        return  contentSize + " , " + metaDataSize + " : " + resource;
    }
}

public class ParserCallChain {

    //public static HashMap<Long, ArrayList<Stats>> statMap = new HashMap<>();
    //public static HashMap<Long, HashMap<Long, ArrayList<Stats>>> rangeMap = new HashMap<>();
    //public static HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> parserChainMap = new HashMap<>();
    public static HashMap<String, HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> > mimeParserChainMap = new HashMap<>();
    public static HashMap<String, String> errorMap = new HashMap<>();
    public static HashMap<String, Integer> contentWordMap = new HashMap<>();
    public static HashMap<String, Integer> metaDataWordMap = new HashMap<>();

    public static void dumpStatMap(HashMap<Long, ArrayList<Stats>> statMap){
        System.out.println("FileSize ContentSize MetaDataSize Resource");
        for (Map.Entry<Long, ArrayList<Stats>> entry : statMap.entrySet()) {
            Long key = entry.getKey();
            ArrayList<Stats> valueList = entry.getValue();
            //System.out.println(key + " : " + valueList.size() );
            //System.out.print(key + "   ");
            for(int j=0; j<valueList.size(); ++j) {
                System.out.println(key + "   " + valueList.get(j));
            }
        }
    }
    public static void dumpRangeMap(HashMap<Long, HashMap<Long, ArrayList<Stats>>> tempMap){
        System.out.println("dumpRangeMap");
        for (Map.Entry<Long, HashMap<Long, ArrayList<com.company.Stats>>> entry : tempMap.entrySet()) {
            Long range = entry.getKey();
            HashMap<Long, ArrayList<com.company.Stats>> valueMap = entry.getValue();
            System.out.println(range + " : " + valueMap.size() );
            dumpStatMap(valueMap);
        }
    }

    public static void dumpParserChainMap(HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> tempMap){
        System.out.println("dumpParserChainMap");
        for (Map.Entry<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> entry : tempMap.entrySet()) {
            String parserChain = entry.getKey();
            HashMap<Long, HashMap<Long, ArrayList<Stats>>> valueMap = entry.getValue();
            System.out.println(parserChain + " : " + valueMap.size() );
            dumpRangeMap(valueMap);
        }
    }
    public static void dumpMimeParserChainMap(){
        System.out.println("dumpMimeParserChainMap");
        for (Map.Entry<String, HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> > entry : mimeParserChainMap.entrySet()) {
            String mimeType = entry.getKey();
            HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> valueMap = entry.getValue();
            System.out.println(mimeType + " : " + valueMap.size() );
            dumpParserChainMap(valueMap);
        }
    }
    @SuppressWarnings("unchecked")
    public static void putKeyArray_JsonObj(JSONObject json_obj, String key, JSONArray jsonArray){
        json_obj.put(key,jsonArray);
    }
    @SuppressWarnings("unchecked")
    public static void putKeyObj_JsonObj(JSONObject json_obj, String key, JSONObject jsonObj){
        json_obj.put(key,jsonObj);
    }
    @SuppressWarnings("unchecked")
    public static void putLongObj_JsonObj(JSONObject json_obj, Long key, JSONObject jsonObj){
        json_obj.put(key,jsonObj);
    }
    @SuppressWarnings("unchecked")
    public static void putLong_JsonArray(JSONObject json_obj, Long key, JSONArray jsonArray){
        json_obj.put(key,jsonArray);
    }
    @SuppressWarnings("unchecked")
    public static void putKeyValue_JsonObj(JSONObject json_obj, String key, String value){
        json_obj.put(key,value);
    }
    @SuppressWarnings("unchecked")
    public static void putKeyIntValue_JsonObj(JSONObject json_obj, String key, int value){
        json_obj.put(key,value);
    }
    @SuppressWarnings("unchecked")
    public static void putKeyLongValue_JsonObj(JSONObject json_obj, String key, Long value){
        json_obj.put(key,value);
    }
    @SuppressWarnings("unchecked")
    public static void addObjToJsonArray(JSONArray jsonArray, JSONObject obj){
        jsonArray.add(obj);
    }

    public static void dumpStatMapObj(JSONObject statMapObj, HashMap<Long, ArrayList<Stats>> statMap){
        for (Map.Entry<Long, ArrayList<Stats>> entry : statMap.entrySet()) {
            Long key = entry.getKey();
            ArrayList<Stats> valueList = entry.getValue();
            //System.out.println(key + " : " + valueList.size() );
            //System.out.print(key + "   ");
            JSONArray stats = new JSONArray();
            for(int j=0; j<valueList.size(); ++j) {
                Stats s = valueList.get(j);
                JSONObject statObj = new JSONObject();
                putKeyValue_JsonObj(statObj,"fileName",s.getResourceName());
                putKeyLongValue_JsonObj(statObj,"contentSize",s.getContentSize());
                putKeyLongValue_JsonObj(statObj,"metaDataSize",s.getMetaDataSize());
                putKeyValue_JsonObj(statObj,"metaDataString",s.getMetaDataString());
                addObjToJsonArray(stats,statObj);
                //System.out.println(key + "   " + s);
            }
            putLong_JsonArray(statMapObj, key, stats);
        }
    }

    public static void dumpRangeMapObj(JSONObject rangeMapObj, HashMap<Long, HashMap<Long, ArrayList<Stats>>> tempMap){
        //System.out.println("dumpRangeMap");
        for (Map.Entry<Long, HashMap<Long, ArrayList<com.company.Stats>>> entry : tempMap.entrySet()) {
            Long range = entry.getKey();
            HashMap<Long, ArrayList<com.company.Stats>> valueMap = entry.getValue();
            //System.out.println(range + " : " + valueMap.size() );
            if(valueMap.size() > 0) {
                JSONObject statMapObj = new JSONObject();
                dumpStatMapObj(statMapObj, valueMap);
                putLongObj_JsonObj(rangeMapObj, range, statMapObj);
            }
        }
    }
    public static void dumpParserChainMapObj(JSONObject parserChainMapObj, HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> tempMap){
        //System.out.println("dumpParserChainMap_ToJson");
        for (Map.Entry<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> entry : tempMap.entrySet()) {
            String parserChain = entry.getKey();
            HashMap<Long, HashMap<Long, ArrayList<Stats>>> valueMap = entry.getValue();
            //System.out.println(parserChain + " : " + valueMap.size() );
            JSONObject rangeMapObj = new JSONObject();
            dumpRangeMapObj(rangeMapObj, valueMap);
            putKeyObj_JsonObj(parserChainMapObj, parserChain, rangeMapObj);
        }
        //System.out.println(parserChainMapObj.toJSONString());
    }

    public static void dumpMimeParserChainMap_ToJson(){
        System.out.println("dumpMimeParserChainMap_ToJson");
        JSONObject mimeParserChainObj = new JSONObject();
        for (Map.Entry<String, HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> > entry : mimeParserChainMap.entrySet()) {
            String mimeType = entry.getKey();
            HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> valueMap = entry.getValue();
            //System.out.println(mimeType + " : " + valueMap.size() );
            JSONObject parserChainMapObj = new JSONObject();
            dumpParserChainMapObj(parserChainMapObj, valueMap);
            putKeyObj_JsonObj(mimeParserChainObj, mimeType, parserChainMapObj);
        }
        //System.out.println(mimeParserChainObj.toJSONString());
        FileWriter fWrite ;
        try {
            fWrite = new FileWriter("parserCallChainResult.json");
            fWrite.write(mimeParserChainObj.toJSONString());
            fWrite.flush();
            fWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dumpErrorMap(){
        System.out.println("dumpErrorMap");
        for (Map.Entry<String, String> entry : errorMap.entrySet()) {
            String fileName = entry.getKey();
            String exception = entry.getValue();
            System.out.println(fileName + " : " + exception );
        }
    }

    public static void dumpErrorMap_ToJson(){
        System.out.println("dumpErrorMap_ToJson");
        JSONObject errorMapObj = new JSONObject();
        for (Map.Entry<String, String> entry : errorMap.entrySet()) {
            String fileName = entry.getKey();
            String exception = entry.getValue();
            putKeyValue_JsonObj(errorMapObj,fileName,exception);
        }
        FileWriter fWrite ;
        try {
            fWrite = new FileWriter("parserCallChainError.json");
            fWrite.write(errorMapObj.toJSONString());
            fWrite.flush();
            fWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dumpContentWordMap_ToJson(){
        System.out.println("dumpContentWordMap_ToJson");
        JSONObject contentWordMapObj = new JSONObject();
        for (Map.Entry<String, Integer> entry : contentWordMap.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
            putKeyIntValue_JsonObj(contentWordMapObj,word,count);
        }
        FileWriter fWrite ;
        try {
            fWrite = new FileWriter("contentCloud.json");
            fWrite.write(contentWordMapObj.toJSONString());
            fWrite.flush();
            fWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dumpMetaDataWordMap_ToJson(){
        System.out.println("dumpMetaDataWordMap_ToJson");
        JSONObject contentWordMapObj = new JSONObject();
        for (Map.Entry<String, Integer> entry : metaDataWordMap.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
            putKeyIntValue_JsonObj(contentWordMapObj,word,count);
        }
        FileWriter fWrite ;
        try {
            fWrite = new FileWriter("metaDataCloud.json");
            fWrite.write(contentWordMapObj.toJSONString());
            fWrite.flush();
            fWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> toptokens(boolean contentMode, String input){

        Set<String> stopWords = new HashSet<String>();
        File stp_file = new File("english.stop");
        BufferedReader fis;
        try {
            fis = new BufferedReader(new FileReader(stp_file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not open stopwords file ",e);
        }
        String word;
        try {
            while((word =fis.readLine()) != null){
                stopWords.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error while reading stopwords",e);
        }

        //System.out.println("stopWords : " + stopWords.toString());

        //System.out.println(content);
        int top = 10;
        final Map<String, Integer> wordMap = new HashMap<String, Integer>();
        if(input==null || input.isEmpty()) {
            return wordMap;
        }

        //System.out.println("input : " + input);
        String[]tokens;
        if(contentMode)
            tokens = input.split("\\s+");
        else
            tokens = input.split("\\|");

        for(String token : tokens) {
            //System.out.println("token : " + token);
            int count = 0;
            word = token.replaceAll("[,:?()&\";]", "").toLowerCase();
            word = word.replaceAll("\\s", "");
            if(word.length()<=2)
                continue;
            if(stopWords.contains(word)){
                //System.out.println("Stop word encountered : " + word);
                continue;
            }
            if (wordMap.containsKey(word)) {
                count = wordMap.get(word);
            }
            //System.out.println(word + " : " + word.length());
            wordMap.put(word, count + 1);
        }
        //System.out.println(wordMap);

        List<String> keySet = new ArrayList<String>();
        keySet.addAll(wordMap.keySet());
        if(keySet.size()<=top){
            return wordMap;
        }

        final Map<String, Integer> topWordMap = new HashMap<String, Integer>();
        PriorityQueue<String> minPriorQueue = new PriorityQueue<String>(top, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (wordMap.get(o1)-wordMap.get(o2));
            }
        });

        for(int i=0;i<top;i++)
        {
            minPriorQueue.add(keySet.get(i));
        }
        for(int i=top;i<keySet.size();i++)
        {
            String key = keySet.get(i);
            int count = wordMap.get(key);
            if(count>wordMap.get(minPriorQueue.peek()))
            {
                minPriorQueue.poll();
                minPriorQueue.add(key);
            }
        }
        Iterator it = minPriorQueue.iterator();
        while(it.hasNext()){
            word = (String) it.next();
            topWordMap.put(word, wordMap.get(word));
        }

        return topWordMap;
    }

    public static void fetchParserCallChain(Path path) {
        String fileName = path.getFileName().toString();
        try {

            File inputFile = path.toFile();
            InputStream stream = TikaInputStream.get(inputFile);

            ContentHandler contentHandler   = new BodyContentHandler();
            Metadata metadata               = new Metadata();
            AutoDetectParser parser         = new AutoDetectParser();

            parser.parse(stream, contentHandler, metadata, new ParseContext());


            long fileSize = inputFile.length();
            int digits = String.valueOf(inputFile.length()).length();
            long range = Math.round(Math.pow(10,digits));
            String mimeType = metadata.get("Content-Type");
            String[] parsers = metadata.getValues("X-Parsed-By");
            StringBuffer parserChainBuf = new StringBuffer();
            for (String p : parsers) {
                parserChainBuf.append(p + ";");
            }
            String parserChain = parserChainBuf.toString();
            long contentSize = contentHandler.toString().length();

            //System.out.println("MetaData : \n" + metadata.toString());

            StringBuffer metadataValuesBuffer = new StringBuffer();
            String[] metadata_keys = metadata.names();
            for(int i = 0; i < metadata_keys.length; ++i) {
                String[] values = metadata.getValues(metadata_keys[i]);

                for(int j = 0; j < values.length; ++j) {
                    if(values[j].length() > 0) {
                        String[] metaTokens = values[j].split("\\s+");
                        for(String metaToken : metaTokens) {
                            metadataValuesBuffer.append(metaToken).append("|");
                        }
                    }
                }
            }
            //System.out.println("MetaDataValues : \n" + metadataValuesBuffer.toString());
            Map<String, Integer> topTokensMetaDataMap = toptokens(false, metadataValuesBuffer.toString());
            //contentWordMap
            for (Map.Entry<String, Integer> entry : topTokensMetaDataMap.entrySet()) {
                String word = entry.getKey();
                int count = entry.getValue();
                //System.out.println(word + " : " + count);
                if(metaDataWordMap.containsKey(word)){
                    int existingCount = metaDataWordMap.get(word);
                    //System.out.println("metaDataWordMap existingCount : " + existingCount + " of : " + word);
                    metaDataWordMap.put(word,existingCount + count);
                    //System.out.println("metaDataWordMap update to : " + (existingCount + count) + " for : " + word);
                }else{
                    metaDataWordMap.put(word, count);
                    //System.out.println("metaDataWordMap set to : " + count + " for : " + word);
                }
            }
            //System.out.println(topTokensMetaDataMap.toString());






            Map<String, Integer> topTokensMap = toptokens(true,contentHandler.toString());
            //contentWordMap
            for (Map.Entry<String, Integer> entry : topTokensMap.entrySet()) {
                String word = entry.getKey();
                int count = entry.getValue();
                //System.out.println(word + " : " + count);
                if(contentWordMap.containsKey(word)){
                    int existingCount = contentWordMap.get(word);
                    //System.out.println("contentWordMap existingCount : " + existingCount + " of : " + word);
                    contentWordMap.put(word,existingCount + count);
                    //System.out.println("contentWordMap update to : " + (existingCount + count) + " for : " + word);
                }else{
                    contentWordMap.put(word, count);
                    //System.out.println("contentWordMap set to : " + count + " for : " + word);
                }
            }
            //System.out.println(topTokensMap.toString());

            long metaDataSize = metadata.toString().length();
            String metaDataString = metadata.toString();


            HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> parserChainMap;
            //System.out.println("\n\tDetected mimeType : " + mimeType);
            //System.out.println("\tmimeParserChainMap keys : " + mimeParserChainMap.keySet());
            if(mimeParserChainMap.containsKey(mimeType)){
                parserChainMap = mimeParserChainMap.get(mimeType);
            }else{
                HashMap<String, HashMap<Long, HashMap<Long, ArrayList<Stats>>>> tempMap = new HashMap<>();
                mimeParserChainMap.put(mimeType, tempMap);
                parserChainMap = mimeParserChainMap.get(mimeType);
            }

            HashMap<Long, HashMap<Long, ArrayList<Stats>>> rangeMap;
            //System.out.println("\n\tDetected Parser Chain : " + parserChain);
            //System.out.println("\tparserChainMap keys : " + parserChainMap.keySet());
            if(parserChainMap.containsKey(parserChain)){
                rangeMap = parserChainMap.get(parserChain);
            }else{
                HashMap<Long, HashMap<Long, ArrayList<Stats>>> tempMap = new HashMap<>();
                initRangeMap(tempMap);
                //System.out.println("Initialize Range Map for Parser Chain : " + parserChain);
                parserChainMap.put(parserChain, tempMap);
                rangeMap = parserChainMap.get(parserChain);
            }

            if(rangeMap.containsKey(range)){
                HashMap<Long, ArrayList<Stats>> statMap = rangeMap.get(range);

                Stats statObj = new Stats(contentSize, metaDataSize, fileName, metaDataString);
                if(statMap.containsKey(fileSize)){
                    (statMap.get(fileSize)).add(statObj);
                }else{
                    ArrayList<Stats> statObjects = new ArrayList<>();
                    statObjects.add(statObj);
                    statMap.put(fileSize, statObjects);
                }

            }else{
                System.out.println(range + " not found in rangeMap");
            }


            /*System.out.println("fileName : " + fileName);
            System.out.println("fileSize : " + fileSize);
            System.out.println("rangeBucket : " + range);
            System.out.println("mimeType : " + mimeType);
            System.out.print("X-Parsed-By : " + parserChain);

            System.out.println("Parsed Content Size: " + contentSize);
            //System.out.println(contentHandler);
            System.out.println("Parsed Metadata Size: " + metaDataSize);
            //System.out.println(metadata);
            */



        }catch (FileNotFoundException e){   errorMap.put(fileName, e.toString());System.out.println(e);}
        catch (IOException e){              errorMap.put(fileName, e.toString());System.out.println(e);}
        catch (SAXException e){             errorMap.put(fileName, e.toString());System.out.println(e);}
        catch (TikaException e){            errorMap.put(fileName, e.toString());System.out.println(e);}
    }



    private static final class ProcessFile extends SimpleFileVisitor<Path> {
        private long count = 1;
        @Override
        public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs) throws IOException {
            System.out.println("Processing file:" + count++ + " : " + aFile);
            fetchParserCallChain(aFile);
            return FileVisitResult.CONTINUE;
        }
    }
    public static void main(String[] args) throws IOException {
        String input_dir = args[0];
        FileVisitor<Path> fileProcessor = new ProcessFile();
        Files.walkFileTree(Paths.get(input_dir), fileProcessor);
        //dumpParserChainMap();
        //dumpParserChainMap_ToJson();
        //dumpMimeParserChainMap();
        dumpMimeParserChainMap_ToJson();
        //dumpErrorMap();
        dumpErrorMap_ToJson();
        dumpContentWordMap_ToJson();
        dumpMetaDataWordMap_ToJson();
    }

    public static void initRangeMap(HashMap<Long, HashMap<Long, ArrayList<Stats>>> tempMap) {
        for(int i=0;i<10;i++){
            double range = Math.pow(10,i);
            HashMap<Long, ArrayList<Stats>> statMaps = new HashMap<>();
            tempMap.put((long) range, statMaps);
        }
        //dumpRangeMap(tempMap);
    }
}

