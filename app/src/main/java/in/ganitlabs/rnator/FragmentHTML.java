package in.ganitlabs.rnator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

public class FragmentHTML extends Fragment implements Config{

    private WebView wv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_html_layout, container, false);
        final MainActivity activityInstance = (MainActivity) getActivity();
        String fileName = getArguments().getString("fileName");
        if (activityInstance.helperDirectory.containsKey(fileName)){
            String url = "file:///"+activityInstance.helperDirectory.get(fileName).getUrl();
            wv = (WebView) v.findViewById(R.id.wv);
            wv.setWebViewClient(activityInstance.wvClient);
            wv.loadUrl(url);
        }
        // TODO Try alternate to webview for Html or get webview to load without jerk on ui thread
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (wv != null){
            wv.destroy();
        }
    }
}
