package com.swd.gym_face_id_access.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.*;
@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_SOUTHEAST_1) // Set your AWS region
                .credentialsProvider(DefaultCredentialsProvider.create()) // Auto-detects env vars
                .build();
    }

    @Bean
    public RekognitionClient rekognitionClient(){
       return RekognitionClient.builder()
               .region(Region.AP_SOUTHEAST_1)
               .build();
    }
}
