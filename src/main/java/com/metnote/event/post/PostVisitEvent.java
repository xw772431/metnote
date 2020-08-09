package com.metnote.event.post;

import com.metnote.utils.ServiceUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * Post visit event.
 *
 * @author johnniang
 * @date 19-4-22
 */
public class PostVisitEvent extends AbstractVisitEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param postId post id must not be null
     */
    public PostVisitEvent(Object source, @NonNull Integer postId) {
        super(source, postId);
        Assert.isTrue(!ServiceUtils.isEmptyId(postId), "Post id must not be empty");
    }
}
