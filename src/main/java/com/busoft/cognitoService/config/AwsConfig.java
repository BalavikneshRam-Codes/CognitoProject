package com.busoft.cognitoService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class AwsConfig {
    @Autowired
    private Environment env;
    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient(){
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(env.getProperty("aws.access.key"), env.getProperty("aws.secret.key"));
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(env.getProperty("aws.region")))
                .build();
    }
}
