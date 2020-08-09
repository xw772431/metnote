package com.metnote.model.params;

import com.metnote.model.dto.base.InputConverter;
import com.metnote.utils.ReflectionUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.lang.reflect.ParameterizedType;

/**
 * Base meta param.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Data
public abstract class BaseMetaParam<META> implements InputConverter<META> {

    @NotBlank(message = "文章 id 不能为空")
    private Integer postId;

    @NotBlank(message = "Meta key 不能为空")
    @Size(max = 1023, message = "Meta key 的字符长度不能超过 {max}")
    private String key;

    @NotBlank(message = "Meta value 不能为空")
    @Size(max = 1023, message = "Meta value 的字符长度不能超过 {max}")
    private String value;

    @Override
    public ParameterizedType parameterizedType() {
        return ReflectionUtils.getParameterizedTypeBySuperClass(BaseMetaParam.class, this.getClass());
    }
}
