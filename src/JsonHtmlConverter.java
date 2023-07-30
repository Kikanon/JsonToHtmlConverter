import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class JsonHtmlConverter {
    private final String inPath;
    private FileWriter outFile;
    private final Stack<String> tags;
    private JsonObject inputJsonObject;

    JsonHtmlConverter(String in){
        inPath = in;
        tags = new Stack<>();
        readJsonFile();
    }

    // read Json object from inPath to inputJsonObject
    private void readJsonFile(){
        Gson gson = new Gson();
        try (JsonReader reader = new JsonReader(new FileReader(inPath))){
            inputJsonObject = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e){
            System.out.println("Missing input file");
        }
    }

    // write current json data to file
    public void writeDataToFile(String out){
        if(inputJsonObject == null){
            System.out.println("No json file has been loaded!");
            return;
        }
        try(FileWriter writer = new FileWriter(out)) {
            outFile = writer;
            writeDocument();
            outFile = null;
        } catch (IOException e){
            System.out.println("Cannot write to path!");
        }

    }

    // write the inputJsonObject to output file
    private void writeDocument() throws IOException{

        // header
        outFile.write(String.format("<!DOCTYPE %s>\n", inputJsonObject.get("doctype").getAsString().replace("\"", "")));
        if(inputJsonObject.get("language") != null){
            outFile.write(String.format("<html lang=%s>\n", inputJsonObject.get("language")));
        }else{
            outFile.write("<html>\n");
        }
        tags.add("html");
        inputJsonObject.remove("language");
        inputJsonObject.remove("doctype");

        // document
        for (String childName: inputJsonObject.keySet()) {
            Object child = inputJsonObject.get(childName);
            writeObject(childName, (JsonObject) child);
        }

        // close
        while (!tags.empty()){
            writeEndTabs();
            closeTag(tags.pop());
        }
    }

    // write a JsonObject to output file
    private void writeObject(String name, JsonObject object) throws IOException{
        writeTabs();

        if(object.has("attributes")){
            tag(name, (JsonObject) object.get("attributes"));
            object.remove("attributes");
        }else{
            tag(name);
        }

        for (String childName:object.keySet()) {
            Object child = object.get(childName);
            switch (child.getClass().getSimpleName()){
                case "JsonObject": {
                    switch (childName){
                        case "meta": writeMeta((JsonObject) child); break;
                        default: writeObject( childName, (JsonObject) child);
                    }
                    break;
                }
                case "JsonArray": {
                    for(Object a:(JsonArray)child){
                        writeTabs();
                        tag(childName, (JsonObject) a);
                        tags.remove(childName);
                    }
                    break;
                }
                case "JsonPrimitive": {
                    write( childName, (JsonPrimitive) child);
                }
            }
        }
        writeEndTabs();
        closeTag(name);
    }

    // specialized function for writing elements with tag 'meta'
    private void writeMeta(JsonObject object) throws IOException{
        for(String attributeName:object.keySet()){
            writeTabs();
            if(attributeName.equals("charset")){
                outFile.write(String.format("<meta charset=%s>\n", object.get(attributeName)));
            }
            else {
                outFile.write("<meta ");
                outFile.write(String.format("name=\"%s\" ", attributeName));
                if(object.get(attributeName).getClass() == JsonObject.class){
                    JsonObject atr = object.get(attributeName).getAsJsonObject();
                    outFile.write("content=\"");
                    for(String n: atr.keySet()){
                        outFile.write(String.format("%s=%s, ", n.replace("\"",""), atr.get(n).toString().replace("\"","")));
                    }
                    outFile.write("\"");
                }else {
                    outFile.write(String.format("content=%s", object.get(attributeName)));
                }

                outFile.write(">\n");
            }
        }
    }

    // write \t based on size of stack
    private void writeTabs() throws IOException{
        for (int i = 0; i < tags.size(); i++) {
            outFile.write("\t");
        }
    }

    // write \t based on size of stack -1
    private void writeEndTabs() throws IOException{
        for (int i = 0; i < tags.size() -1; i++) {
            outFile.write("\t");
        }
    }

    // write a single line element with content
    private void write(String type, JsonPrimitive value) throws IOException{
        writeTabs();
        outFile.write(String.format("<%s>", type));
        outFile.write(value.toString().replace("\"", ""));
        outFile.write(String.format("</%s>\n", type));
    }

    // begin section tag
    private void tag(String type) throws IOException {
        outFile.write(String.format("<%s>\n", type));
        tags.add(type);
    }

    // begin section tag with attributes
    private void tag(String type, JsonObject attributes) throws IOException {
        outFile.write(String.format("<%s ", type));
        for(String attributeName : attributes.keySet()){
            if(attributeName.equals("style")){
                JsonObject styles = attributes.get("style").getAsJsonObject();
                outFile.write("style=\"");
                for(String styleName : styles.keySet()){
                    outFile.write(String.format("%s:%s;", styleName, styles.get(styleName).getAsString().replace("\"", "")));
                }
                outFile.write("\"");
            }else{
                outFile.write(String.format("%s=%s ", attributeName, attributes.get(attributeName)));
            }
        }
        outFile.write(">\n");
        tags.add(type);
    }

    // end section tag
    private void closeTag(String type) throws IOException{
        outFile.write(String.format("</%s>\n", type));
        tags.remove(type);
    }

}
