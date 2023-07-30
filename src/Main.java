
public class Main {
    public static void main(String[] args) {
//        JsonHtmlConverter temp = new JsonHtmlConverter("res/testFiles/helloWorld.json");
//        temp.writeDataToFile("res/output/helloWorld.html");
        JsonHtmlConverterV2 temp = new JsonHtmlConverterV2();
        temp.readJsonFile("res/testFiles/helloWorld.json");
        temp.writeDataToFile("res/output/helloWorld.html");
        temp.readJsonFile("res/testFiles/pageNotFound.json");
        temp.writeDataToFile("res/output/pageNotFound.html");
        temp.readJsonFile("res/testFiles/pageNotFoundV2.json");
        temp.writeDataToFile("res/output/pageNotFoundV2.html");
    }
}
