package com.metnote.security.authentication;

import com.metnote.security.support.UserDetail;
import org.springframework.lang.NonNull;

/**
 * Authentication.
 *
 * @author johnniang
 */
public interface Authentication {

    /**
     * Get user detail.
     *
     * @return user detail
     */
    @NonNull
    UserDetail getDetail();
}
