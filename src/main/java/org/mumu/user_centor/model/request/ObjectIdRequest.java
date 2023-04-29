package org.mumu.user_centor.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求体
 */
@Data
public class ObjectIdRequest implements Serializable {
    private static final long serialVersionUID = 333027911058356190L;
    /**
     * 删除对象 id
     */
    private Long id;
}