package com.metnote.controller.content.api;

import com.metnote.cache.lock.CacheLock;
import com.metnote.model.dto.BaseCommentDTO;
import com.metnote.model.entity.Sheet;
import com.metnote.model.entity.SheetComment;
import com.metnote.model.enums.CommentStatus;
import com.metnote.model.enums.PostStatus;
import com.metnote.model.params.SheetCommentParam;
import com.metnote.model.vo.BaseCommentVO;
import com.metnote.model.vo.BaseCommentWithParentVO;
import com.metnote.model.vo.CommentWithHasChildrenVO;
import com.metnote.model.vo.SheetDetailVO;
import com.metnote.model.vo.SheetListVO;
import com.metnote.service.OptionService;
import com.metnote.service.SheetCommentService;
import com.metnote.service.SheetService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Content sheet controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-26
 */
@RestController("ApiContentSheetController")
@RequestMapping("/api/content/sheets")
public class SheetController {

    private final SheetService sheetService;

    private final SheetCommentService sheetCommentService;

    private final OptionService optionService;

    public SheetController(SheetService sheetService, SheetCommentService sheetCommentService, OptionService optionService) {
        this.sheetService = sheetService;
        this.sheetCommentService = sheetCommentService;
        this.optionService = optionService;
    }

    @GetMapping
    @ApiOperation("Lists sheets")
    public Page<SheetListVO> pageBy(@PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        Page<Sheet> sheetPage = sheetService.pageBy(PostStatus.PUBLISHED, pageable);
        return sheetService.convertToListVo(sheetPage);
    }

    @GetMapping("{sheetId:\\d+}")
    @ApiOperation("Gets a sheet")
    public SheetDetailVO getBy(@PathVariable("sheetId") Integer sheetId,
                               @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                               @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        SheetDetailVO sheetDetailVO = sheetService.convertToDetailVo(sheetService.getById(sheetId));

        if (formatDisabled) {
            // Clear the format content
            sheetDetailVO.setFormatContent(null);
        }

        if (sourceDisabled) {
            // Clear the original content
            sheetDetailVO.setOriginalContent(null);
        }

        sheetService.publishVisitEvent(sheetDetailVO.getId());

        return sheetDetailVO;
    }

    @GetMapping("{sheetId:\\d+}/comments/top_view")
    public Page<CommentWithHasChildrenVO> listTopComments(@PathVariable("sheetId") Integer sheetId,
                                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                          @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        return sheetCommentService.pageTopCommentsBy(sheetId, CommentStatus.PUBLISHED, PageRequest.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{sheetId:\\d+}/comments/{commentParentId:\\d+}/children")
    public List<BaseCommentDTO> listChildrenBy(@PathVariable("sheetId") Integer sheetId,
                                               @PathVariable("commentParentId") Long commentParentId,
                                               @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        // Find all children comments
        List<SheetComment> sheetComments = sheetCommentService.listChildrenBy(sheetId, commentParentId, CommentStatus.PUBLISHED, sort);
        // Convert to base comment dto
        return sheetCommentService.convertTo(sheetComments);
    }


    @GetMapping("{sheetId:\\d+}/comments/tree_view")
    @ApiOperation("Lists comments with tree view")
    public Page<BaseCommentVO> listCommentsTree(@PathVariable("sheetId") Integer sheetId,
                                                @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        return sheetCommentService.pageVosBy(sheetId, PageRequest.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{sheetId:\\d+}/comments/list_view")
    @ApiOperation("Lists comment with list view")
    public Page<BaseCommentWithParentVO> listComments(@PathVariable("sheetId") Integer sheetId,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        return sheetCommentService.pageWithParentVoBy(sheetId, PageRequest.of(page, optionService.getCommentPageSize(), sort));
    }

    @PostMapping("comments")
    @ApiOperation("Comments a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody SheetCommentParam sheetCommentParam) {

        // Escape content
        sheetCommentParam.setContent(HtmlUtils.htmlEscape(sheetCommentParam.getContent(), StandardCharsets.UTF_8.displayName()));
        return sheetCommentService.convertTo(sheetCommentService.createBy(sheetCommentParam));
    }
}
