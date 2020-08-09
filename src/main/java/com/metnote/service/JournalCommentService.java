package com.metnote.service;

import com.metnote.model.entity.JournalComment;
import com.metnote.model.vo.JournalCommentWithJournalVO;
import com.metnote.service.base.BaseCommentService;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Journal comment service interface.
 *
 * @author johnniang
 * @date 2019-04-25
 */
public interface JournalCommentService extends BaseCommentService<JournalComment> {

    @NonNull
    List<JournalCommentWithJournalVO> convertToWithJournalVo(@Nullable List<JournalComment> journalComments);

    @NonNull
    Page<JournalCommentWithJournalVO> convertToWithJournalVo(@NonNull Page<JournalComment> journalCommentPage);
}
