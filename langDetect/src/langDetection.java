import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;

import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.ContentHandler;

import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;

import java.io.*;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.ProfilingHandler;

public class langDetection{
    public static HashMap<String, ArrayList<String>> hashmap = new HashMap<String, ArrayList<String>>();
    public static void fetchLang(Path path){
        try {

        ContentHandler contentHandler 	= new BodyContentHandler();
        ProfilingHandler profileHandler = new ProfilingHandler();
        ContentHandler teeHandler 		= new TeeContentHandler(contentHandler, profileHandler);
        Metadata metadata = new Metadata();

        new AutoDetectParser().parse( new FileInputStream(path.toFile()), teeHandler, metadata, new ParseContext());

        //System.out.println(contentHandler.toString());
        //System.out.println(metadata);

        LanguageIdentifier identifier = profileHandler.getLanguage();
        System.out.println("fileName : " + path.getFileName());
        System.out.println("identifier : " +  identifier);
        System.out.println("Language detected as  : " + identifier.getLanguage());
        if (identifier.isReasonablyCertain()) { //this.distance < 0.022D;
            System.out.println("isReasonablyCertain as  : " + identifier.getLanguage());
        }else{
            System.out.println("Unsure about language");
        }
        String lang     = identifier.getLanguage();
        String fileName = (path.getFileName()).toString();
        if(hashmap.containsKey(lang)){
            (hashmap.get(lang)).add(fileName);
        }else{
            ArrayList<String> arraylist = new ArrayList<String>();
            arraylist.add(fileName);
            hashmap.put(lang, arraylist);
        }

        }catch (FileNotFoundException e){System.out.println(e);}
        catch (IOException e){System.out.println(e);}
        catch (SAXException e){System.out.println(e);}
        catch (TikaException e){System.out.println(e);}
    }

    public static void dumpHashMap(){
        for (Map.Entry<String, ArrayList<String>> entry : hashmap.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> valueList = entry.getValue();
            System.out.println(key + " : " + valueList.size() );
            for(int j=0; j<valueList.size(); ++j) {
                System.out.println(valueList.get(j));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void addJsonArray(JSONArray jsonArray, String obj){
        jsonArray.add(obj);
    }
    @SuppressWarnings("unchecked")
    public static void putKeyArray_JsonObj(JSONObject json_obj, String key, JSONArray jsonArray){
        json_obj.put(key,jsonArray);
    }
    @SuppressWarnings("unchecked")
    public static void putKeyValue_JsonObj(JSONObject json_obj, String key, String value){
        json_obj.put(key,value);
    }
    @SuppressWarnings("unchecked")
    public static void putKeyIntValue_JsonObj(JSONObject json_obj, String key, Integer value){
        json_obj.put(key,value);
    }


    public static void dumpHashMaptoJson() throws IOException {
        JSONObject jsonDetail = new JSONObject();
        JSONArray jsonSummary = new JSONArray();
        JSONObject jsonWordCloud = new JSONObject();
        for (Map.Entry<String, ArrayList<String>> entry : hashmap.entrySet()) {
            String lang = entry.getKey();
            ArrayList<String> valueList = entry.getValue();

            JSONObject jsonLang = new JSONObject();
            putKeyValue_JsonObj(jsonLang, "langType", lang);
            putKeyValue_JsonObj(jsonLang, "count", Integer.toString(valueList.size()));
            putKeyIntValue_JsonObj(jsonWordCloud,lang,valueList.size());
            //System.out.println(jsonLang.toJSONString());
            jsonSummary.add(jsonLang);

            JSONArray jsonArray = new JSONArray();
            for(int j=0; j<valueList.size(); ++j) {
                addJsonArray(jsonArray, valueList.get(j));
            }
            putKeyArray_JsonObj(jsonDetail, lang, jsonArray);
        }
        //System.out.println(jsonSummary.toJSONString());
        //System.out.println(jsonDetail.toJSONString());
        FileWriter fWrite ;
        fWrite = new FileWriter("langDiversity.json");
        fWrite.write(jsonSummary.toJSONString());
        fWrite.flush();
        fWrite.close();

        fWrite = new FileWriter("output.json");
        fWrite.write(jsonDetail.toJSONString());
        fWrite.flush();
        fWrite.close();

        fWrite = new FileWriter("langCloud.json");
        fWrite.write(jsonWordCloud.toJSONString());
        fWrite.flush();
        fWrite.close();
    }

    private static final class ProcessFile extends SimpleFileVisitor<Path> {
        @Override public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs) throws IOException {
          System.out.println("Processing file:" + aFile);
          fetchLang(aFile);
          return FileVisitResult.CONTINUE;
        }

        @Override  public FileVisitResult preVisitDirectory(Path aDir, BasicFileAttributes aAttrs) throws IOException {
          //System.out.println("Processing directory:" + aDir);
          return FileVisitResult.CONTINUE;
        }
    }
    public static void main(String args[]) throws IOException{
	    String input_dir = args[0];
	    FileVisitor<Path> fileProcessor = new ProcessFile();
	    Files.walkFileTree(Paths.get(input_dir), fileProcessor);

        //dumpHashMap();
        dumpHashMaptoJson();
    }
}
