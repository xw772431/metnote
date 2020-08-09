package com.metnote.controller.content.api;

import com.metnote.model.dto.CategoryDTO;
import com.metnote.model.dto.post.BasePostSimpleDTO;
import com.metnote.model.entity.Category;
import com.metnote.model.entity.Post;
import com.metnote.model.enums.PostStatus;
import com.metnote.service.CategoryService;
import com.metnote.service.PostCategoryService;
import com.metnote.service.PostService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Content category controller.
 *
 * @author ryanwang
 * @date 2019-06-09
 */
@RestController("ApiContentCategoryController")
@RequestMapping("/api/content/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final PostCategoryService postCategoryService;

    private final PostService postService;

    public CategoryController(CategoryService categoryService,
                              PostCategoryService postCategoryService,
                              PostService postService) {
        this.categoryService = categoryService;
        this.postCategoryService = postCategoryService;
        this.postService = postService;
    }

    @GetMapping
    @ApiOperation("Lists categories")
    public List<? extends CategoryDTO> listCategories(@SortDefault(sort = "updateTime", direction = DESC) Sort sort,
                                                      @RequestParam(name = "more", required = false, defaultValue = "false") Boolean more) {
        if (more) {
            return postCategoryService.listCategoryWithPostCountDto(sort);
        }
        return categoryService.convertTo(categoryService.listAll(sort));
    }

    @GetMapping("{slug}/posts")
    @ApiOperation("Lists posts by category slug")
    public Page<BasePostSimpleDTO> listPostsBy(@PathVariable("slug") String slug,
                                               @PageableDefault(sort = {"topPriority", "updateTime"}, direction = DESC) Pageable pageable) {
        // Get category by slug
        Category category = categoryService.getBySlugOfNonNull(slug);

        Page<Post> postPage = postCategoryService.pagePostBy(category.getId(), PostStatus.PUBLISHED, pageable);
        return postService.convertToSimple(postPage);
    }
}
