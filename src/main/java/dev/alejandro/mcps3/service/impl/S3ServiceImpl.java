package dev.alejandro.mcps3.service.impl;

import dev.alejandro.mcps3.service.IS3Service;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.compiler.CodeGenerator.region_return;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Service
public class S3ServiceImpl implements IS3Service {

    private final S3Template s3Template;
    private final S3Client s3Client;
    public S3ServiceImpl(S3Template client, S3Client s3Client) {
        this.s3Template = client;
        this.s3Client = s3Client;
    }
    @Value("${spring.cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Tool(name = "Get S3 Resource", description = "Gets a resource from S3")
    @Override
    public String getResource(String localSavePath, String key) throws IOException {
        S3Resource s3Resource = s3Template.download(bucketName, key);
        if (s3Resource.exists()) {
            try (InputStream inputStream = s3Resource.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(localSavePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return localSavePath;
            } catch (IOException e) {
                throw new IOException("Error saving S3 resource to local path: " + localSavePath, e);
            }
        }
        return null;
    }

    @Tool(name = "Upload S3 Resource", description = "Uploads a resource to S3")
    @Override
    public boolean uploadResource(String key, String path) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }
        if (s3Template.objectExists(bucketName, key)) {
            throw new IllegalArgumentException("File already exists with key: " + key);
        }

        try (InputStream inputStream = new FileInputStream(path)) {
            S3Resource s3Resource = s3Template.upload(bucketName, key, inputStream);
            if (s3Resource.exists()) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    @Tool(name = "List S3 Buckets", description = "Lists all S3 buckets in the bucket")
    @Override
    public List<String> listFile() {
        ListObjectsResponse response = s3Client.listObjects(b -> b.bucket(bucketName));
        return response.contents().stream().map(S3Object::key).toList();
    }
}
