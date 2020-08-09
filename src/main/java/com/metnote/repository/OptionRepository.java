package com.metnote.repository;

import com.metnote.model.entity.Option;
import com.metnote.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Option repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-20
 */
public interface OptionRepository extends BaseRepository<Option, Integer>, JpaSpecificationExecutor<Option> {

    /**
     * Query option by key
     *
     * @param key key
     * @return Option
     */
    Optional<Option> findByKey(String key);

    /**
     * Delete option by key
     *
     * @param key key
     */
    void deleteByKey(String key);
}
