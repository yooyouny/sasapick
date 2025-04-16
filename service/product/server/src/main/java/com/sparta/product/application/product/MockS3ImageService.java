package com.sparta.product.application.product;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "false", matchIfMissing = true)
public class MockS3ImageService implements ImageService {
  @Override
  public String uploadImage(String type, MultipartFile image) {
    return "http://example.com/dummy-image.jpg";
  }

  @Override
  public String generateFileName(String originName) {
    return "";
  }

  @Override
  public void deleteImage(String imgUrl) {
  }
}
