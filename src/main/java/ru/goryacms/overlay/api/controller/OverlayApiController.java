package ru.goryacms.overlay.api.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.goryacms.overlay.api.service.OverlayApiService;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class OverlayApiController {
    @Autowired
    private Environment env;

    @Autowired
    private OverlayApiService overlayApiService;

    @RequestMapping(value = "/api/v1/overlay/file", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<byte[]> getOverlayPdf(@RequestParam("pdf") MultipartFile pdf,
                                            @RequestParam("overlay") MultipartFile overlay,
                                            @RequestParam(name = "opacity", required = false) Float opacity,
                                            @RequestParam(name = "horizontal", required = false) Position horizontal,
                                            @RequestParam(name = "vertical", required = false) Position vertical,
                                            @RequestParam(name = "scale", required = false) Float scale) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        try {
            byte[] pdfBytes = pdf.getBytes();
            byte[] overlayBytes = overlay.getBytes();
            String extensionOverlay = FilenameUtils.getExtension(overlay.getOriginalFilename());
            byte[] resultArray = overlayApiService.getPdfWithOverlay(pdfBytes, overlayBytes, extensionOverlay, opacity, horizontal, vertical, scale);
            return new ResponseEntity<>(resultArray, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity("Some error description", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/v1/overlay/html", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<byte[]> getOverlayPdfFromHtml(@RequestParam("pdf") MultipartFile pdf,
                                                    @RequestParam("html") String overlay,
                                                    @RequestParam(name = "opacity", required = false) Float opacity,
                                                    @RequestParam(name = "horizontal", required = false) Position horizontal,
                                                    @RequestParam(name = "vertical", required = false) Position vertical,
                                                    @RequestParam(name = "scale", required = false) Float scale) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        try {
            byte[] pdfBytes = pdf.getBytes();
            byte[] overlayBytes = overlay.getBytes();
            byte[] resultArray = overlayApiService.getPdfWithOverlay(pdfBytes, overlayBytes, "html", opacity, horizontal, vertical, scale);
            return new ResponseEntity<>(resultArray, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity("Some error description", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Position.class, new PositionEnumConverter());
    }
}
