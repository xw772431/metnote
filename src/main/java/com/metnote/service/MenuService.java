package com.metnote.service;

import com.metnote.model.dto.MenuDTO;
import com.metnote.model.entity.Menu;
import com.metnote.model.params.MenuParam;
import com.metnote.model.vo.MenuTeamVO;
import com.metnote.model.vo.MenuVO;
import com.metnote.service.base.CrudService;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Menu service interface.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-14
 */
public interface MenuService extends CrudService<Menu, Integer> {

    /**
     * Lists all menu dtos.
     *
     * @param sort must not be null
     * @return a list of menu output dto
     */
    @NonNull
    List<MenuDTO> listDtos(@NonNull Sort sort);

    /**
     * Lists menu team vos.
     *
     * @param sort must not be null
     * @return a list of menu team vo
     */
    @NonNull
    List<MenuTeamVO> listTeamVos(@NonNull Sort sort);

    /**
     * List menus by team.
     *
     * @param team team
     * @param sort sort
     * @return list of menus
     */
    List<MenuDTO> listByTeam(@NonNull String team, Sort sort);

    /**
     * List menus by team as tree.
     *
     * @param team team
     * @param sort sort
     * @return list of tree menus
     */
    List<MenuVO> listByTeamAsTree(@NonNull String team, Sort sort);

    /**
     * Creates a menu.
     *
     * @param menuParam must not be null
     * @return created menu
     */
    @NonNull
    Menu createBy(@NonNull MenuParam menuParam);

    /**
     * Lists as menu tree.
     *
     * @param sort sort info must not be null
     * @return a menu tree
     */
    List<MenuVO> listAsTree(@NonNull Sort sort);

    /**
     * Lists menu by parent id.
     *
     * @param id id
     * @return a list of menu
     */
    List<Menu> listByParentId(@NonNull Integer id);

    /**
     * List all menu teams.
     *
     * @return a list of teams.
     */
    List<String> listAllTeams();
}
