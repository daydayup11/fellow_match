package org.mumu.user_centor.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求
 *
 * @author HP
 *
 */
@Data
public class DeleteRequest implements Serializable {


    private static final long serialVersionUID = 1256848584634814815L;
    private long id;
}