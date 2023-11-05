package com.fm.api.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.fm.api.error.ErrorMessage;
import com.fm.api.utils.FileUtil;
import com.fm.base.models.dto.ReturnFileUrl;
import com.fm.base.models.sql.FileAttachment;
import com.fm.base.repository.sql.FileRepository;
import com.fm.base.repository.sql.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.fm.api.utils.Constants.FILE_EXCEPT;

@Service
@Slf4j
public class FileService {
    @Value("${s3.bucket.name}")
    private String bucketName;
    @Value("${s3.endpointUrl}")
    private String endpointUrl;
    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;

    public List<FileAttachment> uploadFilePdf(List<MultipartFile> multipartFiles, Integer id, List<ReturnFileUrl.saveFileOrder> list) {
        List<FileAttachment> fileAttachments = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = getFileName(multipartFile);
            String checkFileName = FileUtil.getSafeFileName(fileName);
            String extFile = FilenameUtils.getExtension(checkFileName);
            if (!FILE_EXCEPT.contains(extFile)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.FILE_OPTION_MUST_BE_PDF);
            }
            try {
                InputStream inputStream = multipartFile.getInputStream();
                PDDocument document = PDDocument.load(inputStream);
                FileAttachment file = uploadFile(multipartFile, document.getNumberOfPages(), id, list);
                fileAttachments.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileAttachments;
    }


    public void deleteFile(List<Integer> ids) {
        List<FileAttachment> files = fileRepository.findAllById(ids);
        files.forEach(fileAttachment -> {
            String originalName = fileAttachment.getFileName();
            s3Client.deleteObject(bucketName, originalName);
            fileRepository.deleteFile(ids);
        });
    }

    String getFileName(MultipartFile multiPart) {
        return Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

    /*private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        return convertedFile;
    }*/


    public FileAttachment uploadFile(MultipartFile multipartFile, int numberOfPages, Integer id, List<ReturnFileUrl.saveFileOrder> list) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String fileName = getFileName(multipartFile);
        String contentType = URLConnection.guessContentTypeFromName(multipartFile.getOriginalFilename());
        if (!StringUtils.hasText(extension)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.FILE_DO_NOT_EXIST);
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(multipartFile.getSize());

        String filePath = dtf.format(now) + "/" + UUID.randomUUID() + "." + extension;
        s3Client.putObject(bucketName, filePath, multipartFile.getInputStream(), metadata);
        String fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
        String preSign = generateFilePreSignedUrl(filePath);
        FileAttachment fileAttachment = new FileAttachment(fileName, fileUrl, multipartFile.getContentType(), numberOfPages, id);
        FileAttachment fileAttachment1 = fileRepository.save(fileAttachment);
        list.add(new ReturnFileUrl.saveFileOrder(fileAttachment1.getId(), fileName, fileUrl, preSign, numberOfPages));
        return fileAttachment1;
    }

    private String generateFilePreSignedUrl(String fileName) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1); // Generated URL will be valid for 24 hours
        return s3Client.generatePresignedUrl(bucketName, fileName, calendar.getTime(), HttpMethod.GET).toString();
    }

    public ByteArrayOutputStream downloadFile(String fileName) {
        try {
            fileName = fileName.trim().replace(endpointUrl + "/" + bucketName + "/", "");
            S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream;

        } catch (IOException | AmazonClientException serviceException) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.THE_SPECIFIED_KEY_DOES_NOT_EXIST);
        }

    }


}



