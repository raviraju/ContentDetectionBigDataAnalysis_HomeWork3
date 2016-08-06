import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.tika.parser.ner.grobid.GrobidNERecogniser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

/**
 * Created by Kelly on 16/5/2.
 */
public class KellyGrobidNER extends GrobidNERecogniser {
    private static final Logger LOG = LoggerFactory.getLogger(GrobidNERecogniser.class);
    private static boolean available = false;
    private static final String GROBID_REST_HOST = "http://localhost:8080";
    private String restHostUrlStr;

    private static String readRestEndpoint() throws IOException {
        Properties grobidProperties = new Properties();
        grobidProperties.load(GrobidNERecogniser.class.getResourceAsStream("GrobidServer.properties"));
        return grobidProperties.getProperty("grobid.endpoint.text");
    }

    @Override
    public Map<String, Set<String>> recognise(String text) {
        HashMap entities = new HashMap();
        HashSet measurementNumberSet = new HashSet();
        HashSet unitSet = new HashSet();
        HashSet measurementSet = new HashSet();
        HashSet normalizedMeasurementSet = new HashSet();
        Map<String, String> params = new HashMap<>();
        params.put("text",text);

        try {
            String e = "http://localhost:8080" + readRestEndpoint();
            WebClient webClient = WebClient.create(e).accept(new String[]{"application/json"});
            webClient.query("text",params.get(text));
            Response response = webClient.get();
            int status=response.getStatus();
            System.out.println("status"+status);
            //Response response = WebClient.create(e).accept(new String[]{"application/json"}).post("text=" + text);
            int responseCode = response.getStatus();
            if(responseCode == 200) {
                String result = (String)response.readEntity(String.class);
                JSONObject jsonObject = this.convertToJSONObject(result);
                JSONArray measurements = this.convertToJSONArray(jsonObject, "measurements");

                for(int i = 0; i < measurements.size(); ++i) {
                    StringBuffer measurementString = new StringBuffer();
                    StringBuffer normalizedMeasurementString = new StringBuffer();
                    JSONObject quantity = (JSONObject)this.convertToJSONObject(measurements.get(i).toString()).get("quantity");
                    if(quantity != null) {
                        String jsonObj;
                        if(quantity.containsKey("rawValue")) {
                            jsonObj = (String)this.convertToJSONObject(quantity.toString()).get("rawValue");
                            measurementString.append(jsonObj);
                            measurementString.append(" ");
                            measurementNumberSet.add(jsonObj);
                        }

                        if(quantity.containsKey("normalizedQuantity")) {
                            jsonObj = this.convertToJSONObject(quantity.toString()).get("normalizedQuantity").toString();
                            normalizedMeasurementString.append(jsonObj);
                            normalizedMeasurementString.append(" ");
                        }

                        JSONObject var21 = this.convertToJSONObject(quantity.toString());
                        String normalizedUnitName;
                        JSONObject normalizedUnit;
                        if(var21.containsKey("rawUnit")) {
                            normalizedUnit = (JSONObject)var21.get("rawUnit");
                            normalizedUnitName = (String)this.convertToJSONObject(normalizedUnit.toString()).get("name");
                            unitSet.add(normalizedUnitName);
                            measurementString.append(normalizedUnitName);
                        }

                        if(var21.containsKey("normalizedUnit")) {
                            normalizedUnit = (JSONObject)var21.get("normalizedUnit");
                            normalizedUnitName = (String)this.convertToJSONObject(normalizedUnit.toString()).get("name");
                            normalizedMeasurementString.append(normalizedUnitName);
                        }

                        if(!measurementString.toString().equals("")) {
                            measurementSet.add(measurementString.toString());
                        }

                        if(!normalizedMeasurementString.toString().equals("")) {
                            normalizedMeasurementSet.add(normalizedMeasurementString.toString());
                        }
                    }
                }

                entities.put("MEASUREMENT_NUMBERS", measurementNumberSet);
                entities.put("MEASUREMENT_UNITS", unitSet);
                entities.put("MEASUREMENTS", measurementSet);
                entities.put("NORMALIZED_MEASUREMENTS", normalizedMeasurementSet);
            }
        } catch (Exception var20) {
            LOG.info(var20.getMessage(), var20);
        }

        ENTITY_TYPES.clear();
        ENTITY_TYPES.addAll(entities.keySet());
        return entities;
    }
}
