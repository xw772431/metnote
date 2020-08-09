package com.metnote.model.vo;

import com.metnote.model.dto.BaseCommentDTO;
import com.metnote.model.dto.post.BasePostMinimalDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * PostComment list with post vo.
 *
 * @author johnniang
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SheetCommentWithSheetVO extends BaseCommentDTO {

    private BasePostMinimalDTO sheet;
}
