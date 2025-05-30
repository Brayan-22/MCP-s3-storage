package dev.alejandro.mcps3.service;

import io.awspring.cloud.s3.S3Resource;
import org.springframework.ai.tool.annotation.Tool;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IS3Service {
    @Tool(name = "Get S3 Resource", description = "Gets a resource from S3")
    String getResource(String path, String key) throws IOException;
    @Tool(name = "Upload S3 Resource", description = "Uploads a resource to S3")
    boolean uploadResource(String key, String path);
    @Tool(name = "List S3 Buckets", description = "Lists all S3 buckets in the bucket")
    List<String> listFile();
}
