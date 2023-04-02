package org.mumu.user_centor.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamJoinRequest implements Serializable {


    private static final long serialVersionUID = -8976003636212488240L;
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}