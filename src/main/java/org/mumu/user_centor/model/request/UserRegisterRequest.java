package org.mumu.user_centor.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -1905189245624363904L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
