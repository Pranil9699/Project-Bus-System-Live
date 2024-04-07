package com.bus.utils;

import com.bus.entities.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Component
public class PDFGenerator {

    public static ByteArrayInputStream bookingDetailsPDFReport(BookingDetails booking, MyOrder findByBookingDetails_Id) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
          
            // Add Lal Pari Bus header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
            Paragraph header = new Paragraph("Lal Pari Bus", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            
            document.add(Chunk.NEWLINE);

            // Add a border
            PdfPTable borderTable = new PdfPTable(1);
            borderTable.setWidthPercentage(100);
            PdfPCell borderCell = new PdfPCell();
            borderCell.setBorder(Rectangle.BOTTOM);
            borderTable.addCell(borderCell);
            document.add(borderTable);

            document.add(Chunk.NEWLINE);

            // Add Booking Details table
            PdfPTable bookingTable = createTable();
            addTableCell(bookingTable, "ID", booking.getId().toString());
            addTableCell(bookingTable, "Origin", booking.getOrigin());
            addTableCell(bookingTable, "Destination", booking.getDestination());
            addTableCell(bookingTable, "Date", booking.getDate().toString());
            addTableCell(bookingTable, "Tickets", String.valueOf(booking.getTickets()));
            addSection(document, bookingTable, "Booking Details");

            // Add Order Details table
            PdfPTable orderTable = createTable();
            
            addTableCell(orderTable, "Order ID", findByBookingDetails_Id.getOrderId());
            addTableCell(orderTable, "Amount", "Rs. " + findByBookingDetails_Id.getAmount());
            addTableCell(orderTable, "Receipt", findByBookingDetails_Id.getReceipt());
            addTableCell(orderTable, "Status", findByBookingDetails_Id.getStatus());
            addSection(document, orderTable, "Payment Details\n");

            // Add Greeting
            Font greetingFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
            Paragraph greeting = new Paragraph("Thank you for booking with Lal Pari Bus. We wish you a pleasant journey!", greetingFont);
            greeting.setAlignment(Element.ALIGN_CENTER);
            document.add(greeting);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static PdfPTable createTable() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        return table;
    }

    private static void addTableCell(PdfPTable table, String header, String value) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header));
        headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPaddingBottom(10);
        table.addCell(headerCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value));
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    private static void addSection(Document document, PdfPTable table, String sectionTitle) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Paragraph section = new Paragraph(sectionTitle, sectionFont);
        section.setAlignment(Element.ALIGN_CENTER);
        document.add(section);

        // Add a border
        PdfPTable borderTable = new PdfPTable(1);
        borderTable.setWidthPercentage(100);
        PdfPCell borderCell = new PdfPCell();
        borderCell.setBorder(Rectangle.BOTTOM);
        borderTable.addCell(borderCell);
        document.add(borderTable);

        document.add(table);

        // Add a border
        PdfPTable borderTable2 = new PdfPTable(1);
        borderTable2.setWidthPercentage(100);
        PdfPCell borderCell2 = new PdfPCell();
        borderCell2.setBorder(Rectangle.BOTTOM);
        borderTable2.addCell(borderCell2);
        document.add(borderTable2);

        document.add(Chunk.NEWLINE);
    }
}
