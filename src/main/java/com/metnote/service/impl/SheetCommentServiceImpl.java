package com.metnote.service.impl;

import com.metnote.exception.BadRequestException;
import com.metnote.exception.NotFoundException;
import com.metnote.model.dto.post.BasePostMinimalDTO;
import com.metnote.model.entity.Sheet;
import com.metnote.model.entity.SheetComment;
import com.metnote.model.support.HaloConst;
import com.metnote.model.vo.SheetCommentWithSheetVO;
import com.metnote.repository.SheetCommentRepository;
import com.metnote.repository.SheetRepository;
import com.metnote.service.OptionService;
import com.metnote.service.SheetCommentService;
import com.metnote.service.UserService;
import com.metnote.utils.ServiceUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sheet comment service implementation.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
@Service
public class SheetCommentServiceImpl extends BaseCommentServiceImpl<SheetComment> implements SheetCommentService {

    private final SheetRepository sheetRepository;

    public SheetCommentServiceImpl(SheetCommentRepository sheetCommentRepository,
                                   OptionService optionService,
                                   UserService userService,
                                   ApplicationEventPublisher eventPublisher,
                                   SheetRepository sheetRepository) {
        super(sheetCommentRepository, optionService, userService, eventPublisher);
        this.sheetRepository = sheetRepository;
    }

    @Override
    public void validateTarget(Integer sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new NotFoundException("查询不到该页面的信息").setErrorData(sheetId));

        if (sheet.getDisallowComment()) {
            throw new BadRequestException("该页面已被禁止评论").setErrorData(sheetId);
        }
    }

    @Override
    public SheetCommentWithSheetVO convertToWithSheetVo(SheetComment comment) {
        Assert.notNull(comment, "SheetComment must not be null");
        SheetCommentWithSheetVO sheetCommentWithSheetVO = new SheetCommentWithSheetVO().convertFrom(comment);

        BasePostMinimalDTO basePostMinimalDTO = new BasePostMinimalDTO().convertFrom(sheetRepository.getOne(comment.getPostId()));

        sheetCommentWithSheetVO.setSheet(buildSheetFullPath(basePostMinimalDTO));
        return sheetCommentWithSheetVO;
    }

    @Override
    public List<SheetCommentWithSheetVO> convertToWithSheetVo(List<SheetComment> sheetComments) {
        if (CollectionUtils.isEmpty(sheetComments)) {
            return Collections.emptyList();
        }

        Set<Integer> sheetIds = ServiceUtils.fetchProperty(sheetComments, SheetComment::getPostId);

        Map<Integer, Sheet> sheetMap = ServiceUtils.convertToMap(sheetRepository.findAllById(sheetIds), Sheet::getId);

        return sheetComments.stream()
                .filter(comment -> sheetMap.containsKey(comment.getPostId()))
                .map(comment -> {
                    SheetCommentWithSheetVO sheetCmtWithPostVO = new SheetCommentWithSheetVO().convertFrom(comment);

                    BasePostMinimalDTO postMinimalDTO = new BasePostMinimalDTO().convertFrom(sheetMap.get(comment.getPostId()));

                    sheetCmtWithPostVO.setSheet(buildSheetFullPath(postMinimalDTO));
                    return sheetCmtWithPostVO;
                })
                .collect(Collectors.toList());
    }

    private BasePostMinimalDTO buildSheetFullPath(BasePostMinimalDTO basePostMinimalDTO) {
        StringBuilder fullPath = new StringBuilder();

        if (optionService.isEnabledAbsolutePath()) {
            fullPath.append(optionService.getBlogBaseUrl());
        }

        fullPath.append(HaloConst.URL_SEPARATOR)
                .append(optionService.getSheetPrefix())
                .append(HaloConst.URL_SEPARATOR)
                .append(basePostMinimalDTO.getSlug())
                .append(optionService.getPathSuffix());

        basePostMinimalDTO.setFullPath(fullPath.toString());
        return basePostMinimalDTO;
    }

    @Override
    public Page<SheetCommentWithSheetVO> convertToWithSheetVo(Page<SheetComment> sheetCommentPage) {
        Assert.notNull(sheetCommentPage, "Sheet comment page must not be null");

        return new PageImpl<>(convertToWithSheetVo(sheetCommentPage.getContent()), sheetCommentPage.getPageable(), sheetCommentPage.getTotalElements());

    }
}
