package com.wys.pojo;


import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author susq
 * @since 2017-12-25-10:57
 */
@Data
public class ParamValidateVo {

    @NotNull(message = "id不能为空")
    private Long no;

    @NotBlank(message = "name不能为空")
    private String name;

//    @NotEmpty(message = "nums不能为空")
//    private List<Integer> nums;
}