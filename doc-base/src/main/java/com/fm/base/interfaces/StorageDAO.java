package com.fm.base.interfaces;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface StorageDAO {
    Optional<String> generateFilePresignedUrl(String url, Integer expTimeInMilliseconds);
    Optional<String> generateFilePresignedUrl(String url);
    Optional<String> uploadFile(String fileName, InputStream inputStream, String contentType, Map<String, String> userMetadata);
    Optional<String> uploadFile(String fileName, InputStream inputStream, String contentType);
}
