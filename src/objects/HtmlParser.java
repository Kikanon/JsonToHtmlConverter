package objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.LinkedList;

public class HtmlParser {

    static public HtmlDocumentObject documentFromJson(JsonObject _jsonObject){
        JsonObject jsonObject = _jsonObject.deepCopy();
        if(!jsonObject.has("doctype")){
            System.out.println("Invalid json!");
            return new HtmlDocumentObject("html");
        }
        HtmlDocumentObject data = new HtmlDocumentObject(jsonObject.get("doctype").getAsString());
        if(jsonObject.has("language")){
            data.addAttribute("lang", jsonObject.get("language").getAsString());
        }
        jsonObject.remove("doctype");
        jsonObject.remove("language");

        for(String childName : jsonObject.keySet()){
            JsonElement childData = jsonObject.get(childName);
            if(childName.equals("meta") || jsonObject.get(childName).getClass() == JsonArray.class){
                for(HtmlObject child: listFromJson(childName, childData)){
                    data.addChild(child);
                }
            }else
                data.addChild(fromJson(childName, childData));
        }

        return data;
    }

    static private HtmlObject fromJson(String tag, JsonElement jsonObject){
        HtmlObject htmlObject;

        switch (jsonObject.getClass().getSimpleName()){
            case "JsonObject": {
                htmlObject = new HtmlObjectWithChildren(tag);
                JsonObject objectData = jsonObject.getAsJsonObject();
                if(objectData.has("attributes")){
                    parseAttributes(htmlObject, (JsonObject) objectData.get("attributes"));
                    objectData.remove("attributes");
                }
                for(String childName : objectData.keySet()){
                    JsonElement childData = objectData.get(childName);
                    if(childName.equals("meta") || objectData.get(childName).getClass() == JsonArray.class){
                        for(HtmlObject child: listFromJson(childName, childData)){
                            ((HtmlObjectWithChildren) htmlObject).addChild(child);
                        }
                    }else
                        ((HtmlObjectWithChildren) htmlObject).addChild(fromJson(childName, childData));
                }
                break;
            }
            case "JsonPrimitive": {
                htmlObject = new HtmlObjectWithText(tag, jsonObject.getAsString());
                break;
            }

            // unknown type
            default: {
                htmlObject = new HtmlObject("unknown");
                break;
            }
        }

        return htmlObject;
    }

    static private LinkedList<HtmlObject> listFromJson(String tag, JsonElement jsonObject){
        LinkedList<HtmlObject> objects = new LinkedList<HtmlObject>();


        switch (jsonObject.getClass().getSimpleName()){
            case "JsonObject": {
                for (String objectAttribute : jsonObject.getAsJsonObject().keySet()) {
                    HtmlObject variant = new HtmlObject(tag);
                    JsonObject jsonData = jsonObject.getAsJsonObject();
                    if(objectAttribute.equals("charset")) {
                        variant.addAttribute(objectAttribute, jsonData.get(objectAttribute).getAsString());
                    }else if(tag.equals("meta")){
                        variant.addAttribute("name", objectAttribute);
                        JsonElement content = jsonData.get(objectAttribute);
                        if(content.getClass() == JsonPrimitive.class) {
                            variant.addAttribute("content", jsonData.get(objectAttribute).getAsString());
                        }
                        else if(content.getClass() == JsonObject.class){
                            JsonObject contentObject = content.getAsJsonObject();
                            boolean notFirst = false;
                            StringBuilder builder = new StringBuilder();
                            for(String contentAttributeName: contentObject.keySet()){
                                if(notFirst)builder.append(", ");
                                else notFirst = true;

                                builder.append(String.format("%s=%s",
                                        contentAttributeName.replace("\"", ""),
                                        contentObject.get(contentAttributeName).toString().replace("\"", "")));
                            }
                            variant.addAttribute("content", builder.toString());
                        }
                    }
                    objects.add(variant);
                }
                break;
            }
            case "JsonArray": {
                for(JsonElement objectAttributes:(JsonArray)jsonObject){
                    HtmlObject variant = new HtmlObject(tag);
                    parseAttributes(variant, (JsonObject) objectAttributes);
                    objects.add(variant);
                }
                break;
            }
            // unknown type
            default: break;
        }

        return objects;
    }

    static private void parseAttributes(HtmlObject object, JsonObject atrObject){
        for(String attributeName: atrObject.keySet()) {
            if(attributeName.equals("style")){
                boolean notFirst = false;
                JsonObject styleObject = atrObject.get("style").getAsJsonObject();
                StringBuilder builder = new StringBuilder();
                for(String styleName: styleObject.keySet()){
                    if(notFirst)builder.append(";");
                    else notFirst = true;

                    builder.append(String.format("%s:%s",
                            styleName.replace("\"", ""),
                            styleObject.get(styleName).toString().replace("\"", "")));
                }
                object.addAttribute(attributeName, builder.toString());
            }else {
                object.addAttribute(attributeName, atrObject.get(attributeName).getAsString());
            }
        }
    }


}
