package com.metnote.controller.admin.api;

import com.metnote.model.dto.MenuDTO;
import com.metnote.model.entity.Menu;
import com.metnote.model.params.MenuParam;
import com.metnote.model.vo.MenuVO;
import com.metnote.service.MenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Menu controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-03
 */
@RestController
@RequestMapping("/api/admin/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @ApiOperation("Lists all menus")
    public List<MenuDTO> listAll(@SortDefault(sort = "team", direction = DESC) Sort sort) {
        return menuService.listDtos(sort.and(Sort.by(ASC, "priority")));
    }

    @GetMapping("tree_view")
    @ApiOperation("Lists categories as tree")
    public List<MenuVO> listAsTree(@SortDefault(sort = "team", direction = DESC) Sort sort) {
        return menuService.listAsTree(sort.and(Sort.by(ASC, "priority")));
    }

    @GetMapping("{menuId:\\d+}")
    @ApiOperation("Gets menu detail by id")
    public MenuDTO getBy(@PathVariable("menuId") Integer menuId) {
        return new MenuDTO().convertFrom(menuService.getById(menuId));
    }

    @PostMapping
    @ApiOperation("Creates a menu")
    public MenuDTO createBy(@RequestBody @Valid MenuParam menuParam) {
        return new MenuDTO().convertFrom(menuService.createBy(menuParam));
    }

    @PutMapping("{menuId:\\d+}")
    @ApiOperation("Updates a menu")
    public MenuDTO updateBy(@PathVariable("menuId") Integer menuId,
                            @RequestBody @Valid MenuParam menuParam) {
        // Get the menu
        Menu menu = menuService.getById(menuId);

        // Update changed properties of the menu
        menuParam.update(menu);

        // Update menu in database
        return new MenuDTO().convertFrom(menuService.update(menu));
    }

    @DeleteMapping("{menuId:\\d+}")
    @ApiOperation("Deletes a menu")
    public MenuDTO deleteBy(@PathVariable("menuId") Integer menuId) {
        List<Menu> menus = menuService.listByParentId(menuId);
        if (null != menus && menus.size() > 0) {
            menus.forEach(menu -> {
                menu.setParentId(0);
                menuService.update(menu);
            });
        }
        return new MenuDTO().convertFrom(menuService.removeById(menuId));
    }

    @GetMapping("teams")
    @ApiOperation("Lists all menu teams")
    public List<String> teams() {
        return menuService.listAllTeams();
    }
}
