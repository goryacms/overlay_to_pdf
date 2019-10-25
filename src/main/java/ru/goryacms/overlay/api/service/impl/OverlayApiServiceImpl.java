package ru.goryacms.overlay.api.service.impl;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.stereotype.Service;
import ru.goryacms.overlay.api.controller.Position;
import ru.goryacms.overlay.api.exceptions.BadRequestException;
import ru.goryacms.overlay.api.service.OverlayApiService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Service
public class OverlayApiServiceImpl implements OverlayApiService {
    private Float opacity = 0.5f;
    private Float scale = 1.0f;
    private String horizontal = Position.CENTER.toString();
    private String vertical = Position.MIDDLE.toString();

    @Override
    public byte[] getPdfWithOverlay(byte[] pdfBytes, byte[] overlayBytes, String extension, Float opacity, Position horizontal, Position vertical, Float scale) throws IOException {
        if (nonNull(opacity)) {
            this.opacity = opacity;
        }
        if (nonNull(scale)) {
            this.scale = scale;
        }
        if (nonNull(horizontal)) {
            this.horizontal = horizontal.toString();
        }
        if (nonNull(vertical)) {
            this.vertical = vertical.toString();
        }
        switch (extension) {
            case "jpeg":
            case "jpg":
            case "png":
            case "bmp":
                return jpegOnPdf(pdfBytes, overlayBytes);
            case "pdf":
                return pdfOnPdf(pdfBytes, overlayBytes);
            case "html":
                return htmlOnPdf(pdfBytes, overlayBytes);
            default:
                throw new BadRequestException("Undefined extension overlay file");
        }
    }

    @Override
    public byte[] jpegOnPdf(byte[] pdf, byte[] img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(getPdfFileFromByteArray(pdf)), new PdfWriter(baos));
        Document doc = new Document(pdfDoc);

        ImageData imgData = ImageDataFactory.create(img);
        //  Определение размеров изображения с учётом коэффициента масштабирования
        float w = imgData.getWidth() * scale;
        float h = imgData.getHeight() * scale;
        // Определение прозрачности
        PdfExtGState gs1 = new PdfExtGState();
        gs1.setFillOpacity(opacity);

        PdfPage pdfPage = pdfDoc.getPage(1);

        Rectangle pagesize = pdfPage.getPageSizeWithRotation();

        pdfPage.setIgnorePageRotationForContent(true);

        PdfCanvas over = new PdfCanvas(pdfPage, true);

        over.saveState();
        over.setExtGState(gs1);

        Map<String, Float> coordinates = formCoordinates(pagesize);
        float x = coordinates.get("x"), y = coordinates.get("y");

        over.addImage(imgData, w, 0, 0, h, x - (w / 2), y - (h / 2), false);
        over.restoreState();
        doc.close();

        return baos.toByteArray();
    }

    @Override
    public byte[] pdfOnPdf(byte[] pdf, byte[] overlayPdf) throws IOException {
        byte[] overlayJpeg = generateImageFromPdf(overlayPdf);
        return jpegOnPdf(pdf, overlayJpeg);
    }

    @Override
    public byte[] htmlOnPdf(byte[] pdf, byte[] overlayHtml) throws IOException {
        byte[] overlayJpeg = htmlToImage(Base64.getDecoder().decode(overlayHtml));
        return jpegOnPdf(pdf, overlayJpeg);
    }


    private byte[] generateImageFromPdf(byte[] filename) throws IOException {
        PDDocument document;
        File pdfFile = getPdfFileFromByteArray(filename);

        document = PDDocument.load(pdfFile);

        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
        File file = new File("file.jpeg");
        FileOutputStream fos = new FileOutputStream(file);
        ImageIOUtil.writeImage(bim, "jpeg", fos);
        fos.flush();
        fos.close();
        document.close();
        return FileUtils.readFileToByteArray(file);
    }

    private byte[] htmlToImage(byte[] htmlByte) throws IOException {
        String html = new String(htmlByte);
        int width = 200, height = 100;
        File file;

        BufferedImage bim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2D = bim.createGraphics();
        g2D.setColor(null);

        JEditorPane jep = new JEditorPane("text/html", html);
        jep.setSize(width, height);
        jep.print(g2D);

        file = new File("file.jpeg");
        FileOutputStream fos = new FileOutputStream(file);
        ImageIOUtil.writeImage(bim, "jpeg", fos);
        fos.flush();
        fos.close();

        return FileUtils.readFileToByteArray(file);
    }

    private File getPdfFileFromByteArray(byte[] decodedBytes) throws IOException {
        File file = new File("file.pdf");
        FileUtils.writeByteArrayToFile(file, decodedBytes);
        return file;
    }

    private Map<String, Float> formCoordinates(Rectangle pagesize) {
        Map<String, Float> result = new HashMap<>();
        float x = 0, y = 0;

        switch (horizontal) {
            case "LEFT":
                x = (pagesize.getLeft() + pagesize.getRight() / 2) / 2;
                break;
            case "CENTER":
                x = (pagesize.getLeft() + pagesize.getRight()) / 2;
                break;
            case "RIGHT":
                x = pagesize.getLeft() + (pagesize.getRight() - pagesize.getRight() / 4);
                break;
        }

        switch (vertical) {
            case "TOP":
                y = (pagesize.getTop() - pagesize.getTop() / 4) + pagesize.getBottom();
                break;
            case "MIDDLE":
                y = (pagesize.getTop() + pagesize.getBottom()) / 2;
                break;
            case "BOTTOM":
                y = (pagesize.getTop() / 2 + pagesize.getBottom()) / 2;
                break;
        }
        result.put("x", x);
        result.put("y", y);
        return result;
    }
}