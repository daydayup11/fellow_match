package org.mumu.user_centor.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {
    //上传图片到OSS
    String uploadFileAvatar(MultipartFile file);

}
