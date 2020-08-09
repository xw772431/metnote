package com.metnote.core.freemarker.tag;

import com.metnote.model.entity.PostComment;
import com.metnote.model.enums.CommentStatus;
import com.metnote.model.support.MetnoteConst;
import com.metnote.service.PostCommentService;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Freemarker custom tag of comment.
 *
 * @author ryanwang
 * @date 2019-03-22
 */
@Component
public class CommentTagDirective implements TemplateDirectiveModel {

    private final PostCommentService postCommentService;

    public CommentTagDirective(Configuration configuration, PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
        configuration.setSharedVariable("commentTag", this);
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        final DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);

        if (params.containsKey(MetnoteConst.METHOD_KEY)) {
            String method = params.get(MetnoteConst.METHOD_KEY).toString();
            switch (method) {
                case "latest":
                    int top = Integer.parseInt(params.get("top").toString());
                    Page<PostComment> postComments = postCommentService.pageLatest(top, CommentStatus.PUBLISHED);
                    env.setVariable("comments", builder.build().wrap(postCommentService.convertToWithPostVo(postComments)));
                    break;
                case "count":
                    env.setVariable("count", builder.build().wrap(postCommentService.count()));
                    break;
                default:
                    break;
            }
        }
        body.render(env.getOut());
    }
}
