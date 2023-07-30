package objects;

import java.util.LinkedHashMap;

public class HtmlObject {
    LinkedHashMap<String, String> attributes;
    String tagName;
    HtmlObject(String name){
        attributes = new LinkedHashMap<>();
        tagName = name;
    }

    protected String attributeString(){
        if(attributes.isEmpty()) return "";

        StringBuilder rtn = new StringBuilder();

        for(String name: attributes.keySet()){
            rtn.append(String.format(" %s=\"%s\"", name, attributes.get(name)));
        }
        return rtn.toString();
    }

    public void addAttribute(String name, String value){
        attributes.put(name, value);
    }

    public void removeAttribute(String name){
        attributes.remove(name);
    }

    public String toText(String indent){
        return indent +
                String.format("<%s%s>\n", tagName, attributeString());
    };
}
