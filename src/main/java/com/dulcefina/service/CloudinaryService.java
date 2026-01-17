package com.dulcefina.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final String folder;

    public CloudinaryService(Cloudinary cloudinary,
                             @Value("${cloudinary.folder:products}") String folder) {
        this.cloudinary = cloudinary;
        this.folder = folder;
    }

    public Map upload(MultipartFile file, String publicIdPrefix) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", folder,
                "public_id", publicIdPrefix,
                "resource_type", "image",
                "overwrite", true
        ));
        return uploadResult;
    }

    public Map destroy(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
