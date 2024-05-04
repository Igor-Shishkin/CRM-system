package com.crm.system.services.utils.userServiceUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class PhotoDetailsManager {

    public HttpHeaders getHeaders(byte[] photoOfUser) {
        HttpHeaders headers = new HttpHeaders();
        String imageType = getImageType(photoOfUser);

        headers.setContentType((imageType == null) ? MediaType.IMAGE_JPEG : MediaType.parseMediaType(imageType));
        headers.setContentLength(photoOfUser.length);

        return headers;
    }
    private String getImageType(byte[] imageData) {
        if (imageData.length >= 2) {
            if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
                return "image/jpeg";
            } else if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50) {
                return "image/png";
            }
        }
        return null;
    }
}
