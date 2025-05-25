package dev.alejandro.mcps3.service.impl;

import dev.alejandro.mcps3.service.IS3Service;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public byte[] getResource(String key) throws IOException {
        S3Resource s3Resource = s3Template.download(bucketName, key);
        if (s3Resource.exists()) {
            return s3Resource.getInputStream().readAllBytes();
        }
        return null;
    }

    @Tool(name = "Upload S3 Resource", description = "Uploads a resource to S3")
    @Override
    public Optional<S3Resource> uploadResource(String key, byte[] data) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        if (s3Template.download(bucketName, key).exists()) {
            throw new IllegalArgumentException("File already exists with key: " + key);
        }
        InputStream inputStream = new ByteArrayInputStream(data);

        S3Resource s3Resource = s3Template.upload(bucketName, key, inputStream);
        if (s3Resource.exists()) {
            return Optional.of(s3Resource);
        }
        return Optional.empty();
    }

    @Tool(name = "List S3 Buckets", description = "Lists all S3 buckets in the bucket")
    @Override
    public List<String> listFile() {
        ListObjectsResponse response = s3Client.listObjects(b -> b.bucket(bucketName));
        return response.contents().stream().map(S3Object::key).toList();
    }
}
