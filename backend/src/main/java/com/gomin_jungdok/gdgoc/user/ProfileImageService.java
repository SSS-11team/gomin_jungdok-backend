package com.gomin_jungdok.gdgoc.user;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class ProfileImageService {
    private final Storage storage;
    private final UserRepository userRepository;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public ProfileImageService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public void uploadProfileImage(MultipartFile file, User user) throws IOException {
        if (file != null && !file.isEmpty()) {

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            storage.create(blobInfo, file.getBytes());

            String imageUrl = "https://storage.googleapis.com/" + bucketName + "/" + fileName;

            user.setProfileImage(imageUrl);

        }
    }
}
