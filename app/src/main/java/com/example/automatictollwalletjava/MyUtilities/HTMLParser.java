package com.example.automatictollwalletjava.MyUtilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class HTMLParser {

    final String link_VDRC = "http://app.vr.org.vn/ptpublic/ThongtinptPublic.aspx";

    public String parse(String loc_Str)
    {
        Document doc;
        try {
            doc = Jsoup.connect(link_VDRC).get();
            loc_Str = doc.title();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loc_Str;
    }
}
