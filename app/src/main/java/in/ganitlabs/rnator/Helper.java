package in.ganitlabs.rnator;


import android.database.Cursor;
import android.util.Log;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class Helper {

    public static void copyData(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = from.read(buffer)) > 0) {
            to.write(buffer, 0, length);
        }
        to.flush();
        to.close();
        from.close();
    }

    public static java.util.List<String> getLabelsFromFloat(Cursor c){
        c.moveToFirst();
        ArrayList<String> res = new ArrayList<>();
        for (int i=0; i < c.getCount(); i++){
            res.add(i, String.valueOf(c.getFloat(0)));
            c.moveToNext();
        }
        return res;
    }
    public static java.util.List<String> getLabelsFromInt(Cursor c){
        c.moveToFirst();
        ArrayList<String> res = new ArrayList<>();
        for (int i=0; i < c.getCount(); i++){
            res.add(i, String.valueOf(c.getInt(0)));
            c.moveToNext();
        }
        return res;
    }

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    public static void addMetaData(Document document) {
        document.addTitle("RNAtor");
        document.addSubject("Sequencing Recommendations");
        document.addAuthor("Shruti Kane");
        document.addCreator("Himanshu Garg");
    }

    public static void addTitlePage(Document document, String st)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("DNA Sequencing Recommendations by RNAtor ( GANIT Labs )", catFont));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(st, smallBold));
        addEmptyLine(preface, 1);
        document.add(preface);
    }

    public static void addContent(Document document, String[] res) throws DocumentException {
        PdfPTable table = new PdfPTable(5);

        String[] cellStrings = new String[]{"Series", "System", "Kit", "Samples/\nFlowcell", "Samples/\nLane",
                "MiniSeq\nSystem", "MiniSeq\nSystem", "kit v2", res[0], res[1], "kit v3", res[2], res[3], "NextSeq\nSeries",
                "NextSeq\n500 System", "",res[4], res[5], "HiSeq\nSeries", "HiSeq 3000","", res[6], res[7], "HiSeq 4000\nSystem",
                "", res[8], res[9], "HiSeq 2500\nSystem", "HISEQ\nSBS V4", res[10], res[11], "TRUSEQ\nSBS V3", res[12], res[13]};

        for (String cellVal:cellStrings){
            PdfPCell c1 = new PdfPCell(new Phrase(cellVal));
            if (cellVal.equals("MiniSeq\nSystem") || cellVal.equals("HiSeq 2500\nSystem")){
                c1.setRowspan(2);
            }else if (cellVal.equals("HiSeq\nSeries")){
                c1.setRowspan(4);
            }
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
        }

        try {
            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
