package objects;

public class HtmlDocumentObject extends HtmlObjectWithChildren{
    String docType;
    HtmlDocumentObject(String type){
        super("html");
        docType = type;
    }

    public String toText(){
        return toText("");
    }

    @Override
    public String toText(String indent) {
        return String.format("<!DOCTYPE %s>\n", docType) +
                super.toText(indent);
    }
}
