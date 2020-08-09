package com.metnote.model.vo;

import com.metnote.model.dto.BaseMetaDTO;
import com.metnote.model.dto.CategoryDTO;
import com.metnote.model.dto.TagDTO;
import com.metnote.model.dto.post.BasePostDetailDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Set;

/**
 * Post vo.
 *
 * @author johnniang
 * @author guqing
 * @date 2019-03-21
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostDetailVO extends BasePostDetailDTO {

    private Set<Integer> tagIds;

    private List<TagDTO> tags;

    private Set<Integer> categoryIds;

    private List<CategoryDTO> categories;

    private Set<Long> metaIds;

    private List<BaseMetaDTO> metas;
}

