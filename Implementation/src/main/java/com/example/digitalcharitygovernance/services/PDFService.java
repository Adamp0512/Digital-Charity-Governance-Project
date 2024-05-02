package com.example.digitalcharitygovernance.services;

import com.example.digitalcharitygovernance.models.Meeting;
import com.example.digitalcharitygovernance.models.MeetingAction;
import com.example.digitalcharitygovernance.models.Role;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Service
public class PDFService {
    public byte[] CreatePDF(Meeting meetingToDownload) throws IOException { //method creates meeting summary returning PDF
        ByteArrayOutputStream byteArrayPDF; //return type
        try (PDDocument document = new PDDocument()) {
            PDPage outputPage = new PDPage();
            document.addPage(outputPage);
            PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            try (PDPageContentStream outputPageContents = new PDPageContentStream(document, outputPage)) {
                outputPageContents.beginText();
                outputPageContents.setFont(font, 12);
                outputPageContents.newLineAtOffset(100, 700);
                outputPageContents.showText("Meeting Overview");
                outputPageContents.newLineAtOffset(0, -20);
                outputPageContents.showText("Title - " + meetingToDownload.getTitle());
                outputPageContents.newLineAtOffset(0, -20);
                outputPageContents.showText("Date - " + meetingToDownload.getDateTime());
                outputPageContents.newLineAtOffset(0, -20);
                outputPageContents.showText("Location - " + meetingToDownload.getLocation());
                outputPageContents.newLineAtOffset(0, -50);
                outputPageContents.showText("Roles at meeting - ");
                Iterator<Role> roleIterator = meetingToDownload.getMeetingRoles().iterator();
                while(roleIterator.hasNext()){
                    outputPageContents.newLineAtOffset(0, -20);
                    outputPageContents.showText(roleIterator.next().getName());}
                outputPageContents.newLineAtOffset(0, -50);
                outputPageContents.showText("Actions -");
                for(MeetingAction meetingAction : meetingToDownload.getMeetingActions()){
                    outputPageContents.newLineAtOffset(0, -20);
                    outputPageContents.showText(meetingAction.getActionInfo());}
                outputPageContents.endText();
            }
            byteArrayPDF = new ByteArrayOutputStream();
            document.save(byteArrayPDF); //saves the document as byteArrayPDF
        }
        return byteArrayPDF.toByteArray(); //PDF can only be sent to user as a byte array in a custom http packet
    }
}
