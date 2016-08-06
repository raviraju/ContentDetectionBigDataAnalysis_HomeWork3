import java.util.Map;
import java.util.Set;
import org.apache.tika.parser.ner.grobid.GrobidNERecogniser;
/**
 * Created by Kelly on 16/5/1.
 */
public class Grobid {
    public static void main(String[] args)
    {
        //GrobidNERecogniser grobidNERecogniser=new GrobidNERecogniser();
        KellyGrobidNER grobidNERecogniser=new KellyGrobidNER();
        GrobidNERecogniser grobidNERecogniser1=new GrobidNERecogniser();
        //System.out.println(grobidNERecogniser.isAvailable());
        //Set<String> set=grobidNERecogniser.getEntityTypes();
        Map<String,Set<String>> map=grobidNERecogniser1.recognise("13 were eliminated from analysis, because they were incomplete,unclear or unreadable.40 journals were analysed: 19 were journals of subjects of race ");
        System.out.println("hello");


    }
}
