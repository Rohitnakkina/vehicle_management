package com.techm.service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.techm.entity.ServiceRecord;
import com.techm.entity.WorkItem;
import com.techm.repository.ServiceRecordRepository;

import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class InvoiceService {

    private final ServiceRecordRepository serviceRecordRepository;

    public InvoiceService(ServiceRecordRepository serviceRecordRepository) {
        this.serviceRecordRepository = serviceRecordRepository;
    }

    public byte[] generateInvoice(Long serviceRecordId) throws Exception {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(serviceRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Service Record ID: " + serviceRecordId));

        // Create PDF in Memory
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("SERVICE INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("\nInvoice ID: " + serviceRecord.getId()));
        document.add(new Paragraph("Date: " + serviceRecord.getServiceDate()));
        document.add(new Paragraph("Customer: " + serviceRecord.getVehicle().getCustomer().getName()));
        document.add(new Paragraph("Vehicle: " + serviceRecord.getVehicle().getRegistrationNumber()));
        document.add(new Paragraph("Service Advisor: " + serviceRecord.getServiceAdvisor().getName()));
        document.add(new Paragraph("\n----------------------------------------------------"));

        // Table for Services
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{1, 3, 2, 2});

       
        table.addCell(getHeaderCell("No."));
        table.addCell(getHeaderCell("Item Description"));
        table.addCell(getHeaderCell("Quantity"));
        table.addCell(getHeaderCell("Cost"));

        // Service Items
        List<WorkItem> workItems = serviceRecord.getWorkItems();
        double totalCost = 0;
        int index = 1;
        for (WorkItem item : workItems) {
            table.addCell(String.valueOf(index++));
            table.addCell(item.getDescription());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell("₹" + item.getCost());
            totalCost = item.getQuantity()*item.getCost();
        }

        document.add(table);

        // Total Cost
        document.add(new Paragraph("\nTotal Cost: $" + totalCost, new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private PdfPCell getHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}

