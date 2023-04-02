package org.mumu.user_centor.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 退出队伍请求体
 */
@Data
public class TeamQuitRequest implements Serializable {


    private static final long serialVersionUID = -7131304617776640131L;
    /**
     * id
     */
    private Long teamId;

}
