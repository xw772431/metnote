package com.metnote.service.impl;

import cn.hutool.core.date.DateUtil;
import com.metnote.exception.BadRequestException;
import com.metnote.exception.ForbiddenException;
import com.metnote.exception.NotFoundException;
import com.metnote.model.dto.post.BasePostMinimalDTO;
import com.metnote.model.entity.Post;
import com.metnote.model.entity.PostComment;
import com.metnote.model.enums.CommentViolationTypeEnum;
import com.metnote.model.enums.PostPermalinkType;
import com.metnote.model.properties.CommentProperties;
import com.metnote.model.support.HaloConst;
import com.metnote.model.vo.PostCommentWithPostVO;
import com.metnote.repository.PostCommentRepository;
import com.metnote.repository.PostRepository;
import com.metnote.service.CommentBlackListService;
import com.metnote.service.OptionService;
import com.metnote.service.PostCommentService;
import com.metnote.service.UserService;
import com.metnote.utils.ServiceUtils;
import com.metnote.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
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
 * PostCommentService implementation class
 *
 * @author ryanwang
 * @author johnniang
 * @date 2019-03-14
 */
@Slf4j
@Service
public class PostCommentServiceImpl extends BaseCommentServiceImpl<PostComment> implements PostCommentService {

    private final PostRepository postRepository;

    private final CommentBlackListService commentBlackListService;

    public PostCommentServiceImpl(PostCommentRepository postCommentRepository,
                                  PostRepository postRepository,
                                  UserService userService,
                                  OptionService optionService,
                                  CommentBlackListService commentBlackListService,
                                  ApplicationEventPublisher eventPublisher) {
        super(postCommentRepository, optionService, userService, eventPublisher);
        this.postRepository = postRepository;
        this.commentBlackListService = commentBlackListService;
    }

    @Override
    public Page<PostCommentWithPostVO> convertToWithPostVo(Page<PostComment> commentPage) {
        Assert.notNull(commentPage, "PostComment page must not be null");

        return new PageImpl<>(convertToWithPostVo(commentPage.getContent()), commentPage.getPageable(), commentPage.getTotalElements());

    }

    @Override
    public PostCommentWithPostVO convertToWithPostVo(PostComment comment) {
        Assert.notNull(comment, "PostComment must not be null");
        PostCommentWithPostVO postCommentWithPostVO = new PostCommentWithPostVO().convertFrom(comment);

        BasePostMinimalDTO basePostMinimalDTO = new BasePostMinimalDTO().convertFrom(postRepository.getOne(comment.getPostId()));

        postCommentWithPostVO.setPost(buildPostFullPath(basePostMinimalDTO));
        return postCommentWithPostVO;
    }

    @Override
    public List<PostCommentWithPostVO> convertToWithPostVo(List<PostComment> postComments) {
        if (CollectionUtils.isEmpty(postComments)) {
            return Collections.emptyList();
        }

        // Fetch goods ids
        Set<Integer> postIds = ServiceUtils.fetchProperty(postComments, PostComment::getPostId);

        // Get all posts
        Map<Integer, Post> postMap = ServiceUtils.convertToMap(postRepository.findAllById(postIds), Post::getId);

        return postComments.stream()
                .filter(comment -> postMap.containsKey(comment.getPostId()))
                .map(comment -> {
                    // Convert to vo
                    PostCommentWithPostVO postCommentWithPostVO = new PostCommentWithPostVO().convertFrom(comment);

                    BasePostMinimalDTO basePostMinimalDTO = new BasePostMinimalDTO().convertFrom(postMap.get(comment.getPostId()));

                    postCommentWithPostVO.setPost(buildPostFullPath(basePostMinimalDTO));

                    return postCommentWithPostVO;
                }).collect(Collectors.toList());
    }

    private BasePostMinimalDTO buildPostFullPath(BasePostMinimalDTO post) {
        PostPermalinkType permalinkType = optionService.getPostPermalinkType();

        String pathSuffix = optionService.getPathSuffix();

        String archivesPrefix = optionService.getArchivesPrefix();

        int month = DateUtil.month(post.getCreateTime()) + 1;

        String monthString = month < 10 ? "0" + month : String.valueOf(month);

        int day = DateUtil.dayOfMonth(post.getCreateTime());

        String dayString = day < 10 ? "0" + day : String.valueOf(day);

        StringBuilder fullPath = new StringBuilder();

        if (optionService.isEnabledAbsolutePath()) {
            fullPath.append(optionService.getBlogBaseUrl());
        }

        fullPath.append(HaloConst.URL_SEPARATOR);

        if (permalinkType.equals(PostPermalinkType.DEFAULT)) {
            fullPath.append(archivesPrefix)
                    .append(HaloConst.URL_SEPARATOR)
                    .append(post.getSlug())
                    .append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.ID)) {
            fullPath.append("?p=")
                    .append(post.getId());
        } else if (permalinkType.equals(PostPermalinkType.DATE)) {
            fullPath.append(DateUtil.year(post.getCreateTime()))
                    .append(HaloConst.URL_SEPARATOR)
                    .append(monthString)
                    .append(HaloConst.URL_SEPARATOR)
                    .append(post.getSlug())
                    .append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.DAY)) {
            fullPath.append(DateUtil.year(post.getCreateTime()))
                    .append(HaloConst.URL_SEPARATOR)
                    .append(monthString)
                    .append(HaloConst.URL_SEPARATOR)
                    .append(dayString)
                    .append(HaloConst.URL_SEPARATOR)
                    .append(post.getSlug())
                    .append(pathSuffix);
        }

        post.setFullPath(fullPath.toString());

        return post;
    }

    @Override
    public void validateTarget(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("查询不到该文章的信息").setErrorData(postId));

        if (post.getDisallowComment()) {
            throw new BadRequestException("该文章已经被禁止评论").setErrorData(postId);
        }
    }

    @Override
    public void validateCommentBlackListStatus() {
        CommentViolationTypeEnum banStatus = commentBlackListService.commentsBanStatus(ServletUtils.getRequestIp());
        Integer banTime = optionService.getByPropertyOrDefault(CommentProperties.COMMENT_BAN_TIME, Integer.class, 10);
        if (banStatus == CommentViolationTypeEnum.FREQUENTLY) {
            throw new ForbiddenException(String.format("您的评论过于频繁，请%s分钟之后再试。", banTime));
        }
    }

}
