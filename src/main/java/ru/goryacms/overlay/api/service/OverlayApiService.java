package ru.goryacms.overlay.api.service;

import ru.goryacms.overlay.api.controller.Position;

import java.io.IOException;

public interface OverlayApiService {
    /**
     * Наложение изображения на первую страницу исходного pdf-файла
     *
     * @param pdf - исходный pdf-файл в виде байт-массива
     * @param img - изображение произвольного формата в виде байт-массива
     * @return результирующий pdf в виде байт-массива
     * @throws IOException
     */
    byte[] jpegOnPdf(byte[] pdf, byte[] img) throws IOException;

    /**
     * Наложение pdf на первую страницу исходного pdf-файла
     *
     * @param pdf        - исходный pdf-файл в виде байт-массива
     * @param overlayPdf - pdf-файл в виде байт-массива, который необходимо наложить на исходный
     * @return результирующий pdf в виде байт-массива
     * @throws IOException
     */
    byte[] pdfOnPdf(byte[] pdf, byte[] overlayPdf) throws IOException;

    /**
     * Наложение html-текста на первую страницу исходного pdf-файла
     *
     * @param pdf         - исходный pdf-файл в виде байт-массива
     * @param overlayHtml - html-текст в виде байт-массива, кодированный в base64
     * @return результирующий pdf в виде байт-массива
     * @throws IOException
     */
    byte[] htmlOnPdf(byte[] pdf, byte[] overlayHtml) throws IOException;

    /**
     * Метод-диспетчер, оперирующими параметрами результирующего файла и направляющий вызывающий требуемый метод формирования
     *
     * @param pdfBytes     - исходный pdf-файл в виде байт-массива
     * @param overlayBytes - файл наложения в виде байт-массива
     * @param extension    - расширение файла наложения
     * @param opacity      - коэффициент прозрачности файла наложения
     * @param horizontal   - позиционирование файла наложения по горизонтали (LEFT, CENTER, RIGHT)
     * @param vertical     - позиционирование файла наложения по вертикали (TOP, MIDDLE, BOTTOM)
     * @param scale        - коэффициент масштабирования файла наложения
     * @return результирующий pdf в виде байт-массива
     * @throws IOException
     */
    byte[] getPdfWithOverlay(byte[] pdfBytes, byte[] overlayBytes, String extension, Float opacity, Position horizontal, Position vertical, Float scale) throws IOException;
}
