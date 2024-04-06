package com.xflprflx.paycheck.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ApachePdfService {

    public String extractDate(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 PDDocument document = PDDocument.load(inputStream)) {
                if (document.getNumberOfPages() == 0) {
                    return null;
                }
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                Pattern pattern = Pattern.compile("\\b(\\d{2})/(\\d{2})/(\\d{4})\\b");
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    return buildDateString(matcher.group(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buildDateString(String extractedDate) {
        var dateSplit = extractedDate.split("/");
        var day = dateSplit[0];
        var month = dateSplit[1];
        var year = dateSplit[2];
        var date = year + "-" + month + "-" + day + "T00:00:00.000";
        return date;
    }
}
