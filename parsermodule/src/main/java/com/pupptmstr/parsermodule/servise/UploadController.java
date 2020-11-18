package com.pupptmstr.parsermodule.servise;

import java.io.*;
import java.util.*;

import com.pupptmstr.parsermodule.parser.ItemGroup;
import com.pupptmstr.parsermodule.parser.PdfParser;
import com.pupptmstr.parsermodule.servise.models.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    @GetMapping("/parse")
    public String parse() throws IOException {

        return "Use POST requests with @RequestParam('files')";
    }

    @PostMapping("/parse/document")
    public ResponseEntity<ResponseModel> parseDocument(
        @RequestParam("files") MultipartFile[] files) throws IOException {
        Map<String, List<ItemGroup>> parsedFiles = new HashMap<>();
        List<String> errors = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            if (filename != null) {
                File fileToParse = new File(filename);
                if (!file.isEmpty()) {
                    try {
                        byte[] bytes = file.getBytes();
                        BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(new File(filename)));
                        stream.write(bytes);
                        stream.close();
                        parsedFiles.put(filename, PdfParser.parse(fileToParse));
                        if (fileToParse.delete()) {
                            System.out.println("INFO  : " + filename + " deleted");
                        } else {
                            System.out.println("ERROR : " + filename + " not deleted");
                        }
                    } catch (IOException e) {
                        errors.add(filename);
                        if (fileToParse.exists()) {
                            if (fileToParse.delete()) {
                                System.out.println("INFO  : " + filename + " deleted");
                            } else {
                                System.out.println("ERROR : " + filename + " not deleted");
                            }
                        }
                    }
                } else {
                    errors.add(filename);
                }
            }
        }
        ResponseModel res = new ResponseModel(parsedFiles, errors);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/parse/studentbook")
    public ResponseEntity<String> parseStudentBook(
        @RequestParam("file") MultipartFile file
    ) {
        //todo("сдлеать парс учебника здесь")
        return new ResponseEntity<>("не вышло", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
