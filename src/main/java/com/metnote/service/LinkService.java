package com.metnote.service;

import com.metnote.model.dto.LinkDTO;
import com.metnote.model.entity.Link;
import com.metnote.model.params.LinkParam;
import com.metnote.model.vo.LinkTeamVO;
import com.metnote.service.base.CrudService;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Link service interface.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-14
 */
public interface LinkService extends CrudService<Link, Integer> {

    /**
     * List link dtos.
     *
     * @param sort sort
     * @return all links
     */
    @NonNull
    List<LinkDTO> listDtos(@NonNull Sort sort);

    /**
     * Lists link team vos.
     *
     * @param sort must not be null
     * @return a list of link team vo
     */
    @NonNull
    List<LinkTeamVO> listTeamVos(@NonNull Sort sort);

    /**
     * Lists link team vos by random
     *
     * @param sort
     * @return a list of link team vo by random
     */
    @NonNull
    List<LinkTeamVO> listTeamVosByRandom(@NonNull Sort sort);

    /**
     * Creates link by link param.
     *
     * @param linkParam must not be null
     * @return create link
     */
    @NonNull
    Link createBy(@NonNull LinkParam linkParam);

    /**
     * Exists by link name.
     *
     * @param name must not be blank
     * @return true if exists; false otherwise
     */
    boolean existByName(String name);

    /**
     * List all link teams.
     *
     * @return a list of teams.
     */
    List<String> listAllTeams();

    /**
     * List all link teams by random
     *
     * @return a list of teams by random
     */
    @NonNull
    List<Link> listAllByRandom();
}
