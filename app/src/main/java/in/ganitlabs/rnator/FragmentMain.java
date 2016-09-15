package in.ganitlabs.rnator;


import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import in.ganitlabs.rnator.Utils.CustomCursorAdapter;
import in.ganitlabs.rnator.Helpers.DataBaseHelper;
import in.ganitlabs.rnator.Utils.SeekbarWithIntervals;

public class FragmentMain extends Fragment {
    private boolean isTransCustom;
    private android.support.design.widget.TextInputLayout editTextLayout;
    private android.support.design.widget.TextInputLayout textViewLayout;
    private AutoCompleteTextView textView;
    private TextInputEditText editText;
    private DataBaseHelper dBHelper;

    public static class FragmentHomeResults extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_home_results_layout, container, false);
            Bundle bundle = this.getArguments();
            final float x = bundle.getFloat("read_size");
            TextView textView = (TextView) v.findViewById(R.id.tvRes);
            final float fold = bundle.getFloat("fold");
            final int rep = bundle.getInt("rep");
            final String rest = "Use " + ((int)x + 1) + " million reads to generate optimum results at " + fold + " fold-change and " + rep + " replicates.\n";
            textView.setText(rest);

            // TODO reorganise hacky table and pdf part from mainactivity and helper and move helper to helpers

            textView = (TextView) v.findViewById(R.id.pH0);
            textView.setText("" + (int) Math.ceil(30 / x));

            textView = (TextView) v.findViewById(R.id.pH1);
            textView.setText("" + (int) Math.ceil(30 / (x * 8)));

            textView = (TextView) v.findViewById(R.id.pH2);
            textView.setText("" + (int) Math.ceil(40 / x));

            textView = (TextView) v.findViewById(R.id.pH3);
            textView.setText("" + (int) Math.ceil(5 / x));

            textView = (TextView) v.findViewById(R.id.pH4);
            textView.setText("" + (int) Math.ceil(700 / x));

            textView = (TextView) v.findViewById(R.id.pH5);
            textView.setText("" + (int) Math.ceil(175 / (x * 2)));

            textView = (TextView) v.findViewById(R.id.pH6);
            textView.setText("" + (int) Math.ceil(2000 / x));

            textView = (TextView) v.findViewById(R.id.pH7);
            textView.setText("" + (int) Math.ceil(250 / x));

            textView = (TextView) v.findViewById(R.id.pH8);
            textView.setText("" + (int) Math.ceil(4000 / x));

            textView = (TextView) v.findViewById(R.id.pH9);
            textView.setText("" + (int) Math.ceil(500 / x));

            textView = (TextView) v.findViewById(R.id.pH10);
            textView.setText("" + (int) Math.ceil(4000 / x));

            textView = (TextView) v.findViewById(R.id.pH11);
            textView.setText("" + (int) Math.ceil(500 / x));

            textView = (TextView) v.findViewById(R.id.pH12);
            textView.setText("" + (int) Math.ceil(3000 / x));

            textView = (TextView) v.findViewById(R.id.pH13);
            textView.setText("" + (int) Math.ceil(375 / x));
            final Context context = getContext();

            final String[] res = new String[]{
                    "" + (int) Math.ceil(30 / x),
                    "" + (int) Math.ceil(30 / (x * 8)),
                    "" + (int) Math.ceil(40 / x),
                    "" + (int) Math.ceil(5 / x),
                    "" + (int) Math.ceil(700 / x),
                    "" + (int) Math.ceil(175 / (x * 2)),
                    "" + (int) Math.ceil(2000 / x),
                    "" + (int) Math.ceil(250 / x),
                    "" + (int) Math.ceil(4000 / x),
                    "" + (int) Math.ceil(500 / x),
                    "" + (int) Math.ceil(4000 / x),
                    "" + (int) Math.ceil(500 / x),
                    "" + (int) Math.ceil(3000 / x),
                    "" + (int) Math.ceil(375 / x)};


            Button btnPdf = (Button) v.findViewById(R.id.btnPdf);
            btnPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        File mfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        mfile.mkdir();
                        Document document = new Document();
                        PdfWriter.getInstance(document, new FileOutputStream(mfile.getPath() + "/RNAtor_" + new Date() + ".pdf"));
                        document.open();
                        Helper.addMetaData(document);
                        Helper.addTitlePage(document, rest);
                        Helper.addContent(document, res);
                        document.close();

                        MediaScannerConnection.scanFile(context,
                                new String[]{mfile.toString()}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String s, Uri uri) {
                                    }
                                });
                        Toast.makeText(context, "Done. Can be found in Downloads folder.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return v;
        }
    }

    public static FragmentMain newInstance() {
        return new FragmentMain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_layout, container, false);
        isTransCustom = false;
        dBHelper = DataBaseHelper.getInstance();

        final CustomCursorAdapter adapter = new CustomCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                null,
                new String[]{"name"}
        );
        editText = (TextInputEditText) v.findViewById(R.id.input_custom_trans);
        textView = (AutoCompleteTextView) v.findViewById(R.id.input_search_trans);
        adapter.setCursorToStringConverter(new CustomCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return cursor.getString(1);
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                return dBHelper.getOrganismNames(charSequence);
            }
        });
        textView.setAdapter(adapter);

        editTextLayout = (android.support.design.widget.TextInputLayout) v.findViewById(R.id.input_layout_custom_trans);
        textViewLayout = (android.support.design.widget.TextInputLayout) v.findViewById(R.id.input_layout_search_trans);

        editTextLayout.setVisibility(View.GONE);
        textViewLayout.setVisibility(View.VISIBLE);

        final ImageView imageView = (ImageView) v.findViewById(R.id.input_type);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), imageView);
                popup.getMenuInflater().inflate(R.menu.input_type_items, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_search:
                                isTransCustom = false;
                                editText.setText("");
                                editTextLayout.setVisibility(View.GONE);
                                textViewLayout.setVisibility(View.VISIBLE);
                                break;
                            case R.id.item_custom:
                                isTransCustom = true;
                                textView.setText("");
                                textViewLayout.setVisibility(View.GONE);
                                editTextLayout.setVisibility(View.VISIBLE);
                                break;
                        }
                        return true;
                    }
                });
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popup);
                    argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    popup.show();
                }
            }
        });
        final SeekbarWithIntervals sbiFold = (SeekbarWithIntervals) v.findViewById(R.id.sbiFold);
        final SeekbarWithIntervals sbiRep = (SeekbarWithIntervals) v.findViewById(R.id.sbiRep);

        final Cursor curFold = dBHelper.getFoldChanges();
        final Cursor curRep = dBHelper.getReplicates();

        final List<String> labelsFold = Helper.getLabelsFromFloat(curFold);
        final List<String> labelsRep = Helper.getLabelsFromInt(curRep);

        curFold.close();
        curRep.close();

        sbiFold.setIntervals(labelsFold);
        sbiRep.setIntervals(labelsRep);


        Button button = (Button) v.findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float transSize = isTransCustom ? Float.valueOf(editText.getText().toString()) : dBHelper.getTransSize(textView.getText().toString());

                    if (transSize < 0) {
                        String message = isTransCustom ? "Invalid (-ve) Transcriptome size." : "Choose from available organisms or use custom transcriptome size.";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        float fold_change = Float.valueOf(labelsFold.get(sbiFold.getProgress()));
                        int replicates = Integer.valueOf(labelsRep.get((sbiRep.getProgress())));
                        float readSizeMultiplier = dBHelper.getReadSizeMultiplier(fold_change, replicates);
                        Fragment frag = new FragmentHomeResults();
                        Bundle bundle = new Bundle();
                        bundle.putFloat("read_size", transSize * readSizeMultiplier / 3);
                        bundle.putFloat("fold", fold_change);
                        bundle.putInt("rep", replicates);
                        frag.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, frag);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Enter valid size(in numerals) or try using the Search view", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }
}
