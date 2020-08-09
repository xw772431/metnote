package com.metnote.aspect;

import com.metnote.model.entity.PostComment;
import com.metnote.model.entity.User;
import com.metnote.security.authentication.AuthenticationImpl;
import com.metnote.security.context.SecurityContextHolder;
import com.metnote.security.context.SecurityContextImpl;
import com.metnote.security.support.UserDetail;
import com.metnote.service.PostCommentService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author giveup
 * @description SensitiveConcealAspectTest
 * @date 1:14 AM 27/5/2020
 */
@SpringBootTest
@Disabled("Due to ip address: [0:0:0:0:0:0:0:1]")
class SensitiveConcealAspectTest {

    @Autowired
    PostCommentService postCommentService;

    @Test
    void testGuest() {
        List<PostComment> postComments = postCommentService.listBy(1);
        for (PostComment postComment : postComments) {
            assertEquals("", postComment.getIpAddress());
            assertEquals("", postComment.getEmail());
        }
    }

    @Test
    void testAdmin() {
        SecurityContextHolder.setContext(new SecurityContextImpl(new AuthenticationImpl(new UserDetail(new User()))));

        List<PostComment> postComments = postCommentService.listBy(1);
        for (PostComment postComment : postComments) {
            assertEquals("127.0.0.1", postComment.getIpAddress());
            assertEquals("hi@halo.run", postComment.getEmail());
        }
    }

}
