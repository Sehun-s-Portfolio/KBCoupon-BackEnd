package kbcp.svc.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Slf4j
@RequestMapping(value="/download")
@Controller
public class FileController {

    @GetMapping(value="/file.do")
    public ResponseEntity<InputStreamResource> downloadFile(
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        String filePath = "C:/test/images";  //파일 경로
        String fileNm = "450542.jpg";  //파일 이름

        String fileType = fileNm.substring(fileNm.lastIndexOf(".")).trim();  //파일 형식
        String mineType = "";

        //.equalsIgnoreCase() 를 사용하여 대소문자 구분 없이 파일 형식을 확인한다
        if (fileType.equalsIgnoreCase(".hwp")) {
            mineType = "application/x-hwp";
        } else if (fileType.equalsIgnoreCase(".pdf")) {
            mineType = "application/pdf";
        } else if (fileType.equalsIgnoreCase(".doc") || fileType.equalsIgnoreCase(".docx")) {
            mineType = "application/msword";
        } else if (fileType.equalsIgnoreCase(".xls") || fileType.equalsIgnoreCase(".xlsx")) {
            mineType = "application/vnd.ms-excel";
        } else if (fileType.equalsIgnoreCase(".ppt") || fileType.equalsIgnoreCase(".pptx")) {
            mineType = "application/vnd.ms-powerpoint";
        } else if (fileType.equalsIgnoreCase(".zip")) {
            mineType = "application/zip";
        } else if (fileType.equalsIgnoreCase(".jpeg") || fileType.equalsIgnoreCase(".jpg") || fileType.equalsIgnoreCase(".png")) {
            mineType = "image/jpeg";
        } else if (fileType.equalsIgnoreCase(".txt")) {
            mineType = "textplain";
        }

        System.out.println("[downloadFile] fileNm : "+fileNm);

        File file = new File(filePath, fileNm);
        InputStreamResource isr = new InputStreamResource(FileUtils.openInputStream(file));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.parseMediaType(mineType));
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
        headers.add("Access-Control-Allow-Headers", "Content-Type");

        headers.add("Content-Disposition", "attachment; filename=" + fileNm + ";");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Set-Cookie", "fileDownload=true; path='/");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        headers.setContentLength(file.length());

        ResponseEntity<InputStreamResource> responseEntity = new ResponseEntity<InputStreamResource>(isr, headers, HttpStatus.OK);

        return responseEntity;
    }

}
