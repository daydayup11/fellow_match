package org.mumu.user_centor.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class PostAddRequest implements Serializable {
    private static final long serialVersionUID = 1337555112052210463L;

    private String avatarUrl;

    private String content;

    private String title;
}
