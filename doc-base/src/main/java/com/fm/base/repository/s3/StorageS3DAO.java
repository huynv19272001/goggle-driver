package com.fm.base.repository.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class StorageS3DAO implements StorageDAO {

    private static final Logger LOG = LoggerFactory.getLogger(StorageDAO.class);
    private static final Integer DEFAULT_EXP_TIME_IN_MILLISECONDS = 60*60*1000;

    // According to https://docs.aws.amazon.com/AmazonS3/latest/userguide/UsingMetadata.html
    //When uploading an object, you can also assign metadata to the object.
    // You provide this optional information as a name-value (key-value) pair when you send a PUT or POST request to create the object.
    // When you upload objects using the REST API, the optional user-defined metadata names must begin
    // with "x-amz-meta-" to distinguish them from other HTTP headers.
    // When you retrieve the object using the REST API, this prefix is returned.
    private static final String USER_METADATA_KEY_PREFIX = "x-amz-meta-";
    private static final Map<String, String> DEFAULT_USER_METADATA = ImmutableMap.of(
        "owner", "BoxPark"
    );

    private final AWSStaticCredentialsProvider awsStaticCredentialsProvider;
    private final String region;
    private final String bucket;

    public StorageS3DAO(final String accessId, final String secretKey, final String region, final String bucket) {
        final AWSCredentials awsCredentials = new BasicAWSCredentials(accessId, secretKey);
        this.awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        this.region = region;
        this.bucket = bucket;
    }

    @Override
    public Optional<String> generateFilePresignedUrl(String url, Integer expTimeInMilliseconds) {
        if (url == null) {
            return Optional.empty();
        }
        if (!url.contains("amazonaws") && !url.contains("s3")) {
            return Optional.of(url);
        }
        try {
            final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(awsStaticCredentialsProvider)
                .build();

            String objectKey = url;
            if (url.contains("amazonaws.com/")) {
                String[] parts = url.split("amazonaws\\.com/");
                if (parts.length > 1) {
                    objectKey = parts[1];
                }
            }
            if (objectKey.startsWith(bucket + "/")) {
                objectKey = objectKey.replaceFirst("^" + bucket + "/", "");
            }

            // Generate the presigned URL.
            final GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(DateTime.now().plusMillis(expTimeInMilliseconds).toDate());
            final URL presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return Optional.of(presignedUrl.toString());
        } catch (AmazonServiceException e) {
            // it, so it returned an error response.
            log.error("The call was transmitted successfully, but Amazon S3 couldn't process for url {} ", url);
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            log.error("Amazon S3 couldn't be contacted for a response, or the client, thus presigning for {} has failed", url);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> generateFilePresignedUrl(String url) {
        return generateFilePresignedUrl(url, DEFAULT_EXP_TIME_IN_MILLISECONDS);
    }

    @Override
    public Optional<String> uploadFile(String fileName, InputStream inputStream, String contentType, Map<String, String> userMetadata) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(awsStaticCredentialsProvider)
                .build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            userMetadata.forEach((k, v) -> {
                metadata.addUserMetadata(USER_METADATA_KEY_PREFIX + k, v);
            });
            try {
                // Try to set content length to avoid memory error
                final byte[] bytes = IOUtils.toByteArray(inputStream);
                metadata.setContentLength(bytes.length);
                s3Client.putObject(bucket, fileName, new ByteArrayInputStream(bytes), metadata);
            } catch (IOException e) {
                s3Client.putObject(bucket, fileName, inputStream, metadata);
            }
            final URL url = s3Client.getUrl(bucket, fileName);
            return Optional.of(url.toString());
        }
        catch(AmazonServiceException e) {
            log.error("The call was transmitted successfully, but Amazon S3 couldn't process");
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            log.error("Amazon S3 couldn't be contacted for a response, or the client");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> uploadFile(String fileName, InputStream inputStream, String contentType) {
        return uploadFile(fileName, inputStream, contentType, DEFAULT_USER_METADATA);
    }

    public void getTemporaryCredential(){

    }
}
