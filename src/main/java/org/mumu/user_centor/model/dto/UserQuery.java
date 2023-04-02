package org.mumu.user_centor.model.dto;

import lombok.Data;
import org.mumu.user_centor.common.PageRequest;

import java.util.List;

@Data
public class UserQuery extends PageRequest {
    String searchText;
    List<Long> ids;
}