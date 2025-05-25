package dev.alejandro.mcps3;

import dev.alejandro.mcps3.service.IS3Service;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class McpS3Application {

    public static void main(String[] args) {
        SpringApplication.run(McpS3Application.class, args);
    }


    @Bean
    List<ToolCallback> s3mcptools(IS3Service service) {
        return List.of(ToolCallbacks.from(service));
    }
}
