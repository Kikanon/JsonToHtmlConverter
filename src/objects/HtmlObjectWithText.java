package objects;

public class HtmlObjectWithText extends HtmlObject{
    String content;

    HtmlObjectWithText(String tag, String text){
        super(tag);
        content = text;
    }

    @Override
    public String toText(String indent) {
        return indent +
                String.format("<%s%s>", tagName, attributeString()) +
                content +
                String.format("</%s>\n", tagName);
    }
}
