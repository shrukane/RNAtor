package in.ganitlabs.rnator.Helpers;


import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import in.ganitlabs.rnator.Config;
import in.ganitlabs.rnator.Helper;


public class HTMLHelper implements Config {

    private String HTML_PATH;

    private String HTML_NAME;

    private int HTML_VERSION;

    private final Context myContext;


    public HTMLHelper(Context context, String name) throws IOException {
        myContext = context;
        HTML_NAME = name;
        File file = context.getDatabasePath(HTML_NAME);
        HTML_PATH = file.getPath();
        HTML_VERSION = findVersion(context.getAssets().open(HTML_ASSETS_DIR + HTML_NAME));
        setStraight();
    }

    public void installHTML(InputStream from) throws IOException {
        Helper.copyData(from, new FileOutputStream(HTML_PATH));
        setStraight();
    }

    public int getVersion() {
        return HTML_VERSION;
    }

    public String getUrl(){
        return HTML_PATH;
    }

    private void setStraight()throws IOException{
        boolean isCreate = false, isUpgrade = false;
        File htmlFile = new File(HTML_PATH);
        if (htmlFile.exists()){
            int version = findVersion(new FileInputStream(htmlFile));
            if (version > HTML_VERSION){
                HTML_VERSION = version;
            }else if(version < HTML_VERSION){
                isUpgrade = true;
            }
        }else {
            isCreate = true;
        }
        if (isCreate || isUpgrade) {
            installHTML(myContext.getAssets().open(HTML_ASSETS_DIR + HTML_NAME));
        }
    }

    public static int findVersion(InputStream is) throws IOException{
        return Integer.valueOf(Jsoup.parse(is, null, "").select("meta").first().attr("data-version"));
    }
}