package com.shop.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.shop.constant.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
@Log
@RequiredArgsConstructor
public class FileService {

    final private AmazonS3 s3Client;

    public FileService() {
        AWSCredentials credentials = new BasicAWSCredentials(Config.accessKey, Config.secretKey);

        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }

//    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
//        UUID uuid = UUID.randomUUID();
//        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
//        String savedFileName = uuid.toString() + extension;
//        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
//        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
//        fos.write(fileData);
//        fos.close();
//        return savedFileName;
//    }
//
//    public void deleteFile(String filePath) throws Exception {
//        File deleteFile = new File(filePath);
//
//        if(deleteFile.exists()) {
//            deleteFile.delete();
//            log.info("파일을 삭제하였습니다.");
//        } else {
//            log.info("파일이 존재하지 않습니다.");
//        }
//    }

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileData.length);
        objectMetadata.setContentType("image/" + extension.substring(1));

        // save in S3
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData);
        this.s3Client.putObject(new PutObjectRequest(Config.bucketName, savedFileName, byteArrayInputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));

        byteArrayInputStream.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {
        s3Client.deleteObject(Config.bucketName, filePath);

    }
}
