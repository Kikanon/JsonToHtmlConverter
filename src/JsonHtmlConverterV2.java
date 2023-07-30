import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import objects.HtmlDocumentObject;
import objects.HtmlParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonHtmlConverterV2 {
    HtmlDocumentObject data;

    // read Json object from inPath to inputJsonObject
    public void readJsonFile(String inPath){
        Gson gson = new Gson();
        try (JsonReader reader = new JsonReader(new FileReader(inPath))){
            JsonObject jsonData = gson.fromJson(reader, JsonObject.class);
            data = HtmlParser.documentFromJson(jsonData);
        } catch (IOException e){
            System.out.println("Missing input file");
        }
    }

    // write current json data to file
    public void writeDataToFile(String outPath){
        if(data == null){
            System.out.println("No json file has been loaded!");
            return;
        }
        try(FileWriter writer = new FileWriter(outPath)) {
            writer.write(data.toText());
        } catch (IOException e){
            System.out.println("Cannot write to path!");
        }

    }
}
