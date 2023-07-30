package objects;

import java.util.LinkedList;

public class HtmlObjectWithChildren extends HtmlObject{
   LinkedList<HtmlObject> children;

    HtmlObjectWithChildren(String tag){
        super(tag);
        children = new LinkedList<>();
    }

    public void addChild(HtmlObject object){
        children.add(object);
    }

    public void removeChild(HtmlObject object){
        children.remove(object);
    }

    @Override
    public String toText(String indent) {
        StringBuilder builder = new StringBuilder();
        for(HtmlObject child : children){
            builder.append(child.toText(indent + "\t"));
        }
        return indent +
                String.format("<%s%s>\n", tagName, attributeString()) +
                builder +
                indent + String.format("</%s>\n", tagName);
    }
}
