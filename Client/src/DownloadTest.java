public class DownloadTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        String filepath = "http://127.0.0.1:8080/Client/data/123.flv";
        MultiTheradDownLoad load = new MultiTheradDownLoad(filepath ,4);    
        load.downloadPart();    
    }
}