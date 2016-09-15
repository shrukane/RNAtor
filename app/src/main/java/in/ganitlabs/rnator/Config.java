package in.ganitlabs.rnator;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Config{

    long SPLASH_TIME_MIN = 1500;

    String DB_NAME = "rnator.db";

    int DB_VERSION = 1;

    String DB_ASSETS_PATH = "databases/" + DB_NAME;

    String HTML_ASSETS_DIR = "html/";

//    String HOST = "http://www.ganitlabs.in";

    String HOST = "http://192.168.174.1:8000";

    String Downloads_API= "/rnator/downloads/";

    String Versions_API = "/rnator/versions/";

    HashMap<Integer, Class> ROUTES = new HashMap<Integer, Class>(){{
        put(R.id.main, FragmentMain.class);
        put(R.id.faq, FragmentHTML.class);
        put(R.id.ref, FragmentHTML.class);
        put(R.id.about, FragmentHTML.class);
        put(R.id.updates, FragmentUpdates.class);
    }};

    HashMap<Integer, String> HTML_MENU_FILE_MAPPING = new HashMap<Integer, String>(){{
        put(R.id.faq, "faq.html");
        put(R.id.ref, "references.html");
        put(R.id.about, "about.html");
    }};

    ArrayList<String> HTML_FILES = new ArrayList<String>(){{
        this.add("faq.html");
        this.add("references.html");
        this.add("about.html");
    }};
}
