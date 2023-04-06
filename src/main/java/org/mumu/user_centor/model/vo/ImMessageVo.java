package org.mumu.user_centor.model.vo;

import lombok.Data;
import org.mumu.user_centor.model.domain.Im;

@Data
public class ImMessageVo {

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 消息
     */
    private Im im;

}
