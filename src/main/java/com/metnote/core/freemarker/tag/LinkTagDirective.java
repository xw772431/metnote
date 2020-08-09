package com.metnote.core.freemarker.tag;

import com.metnote.model.support.MetnoteConst;
import com.metnote.service.LinkService;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Freemarker custom tag of link.
 *
 * @author ryanwang
 * @date 2019-03-22
 */
@Component
public class LinkTagDirective implements TemplateDirectiveModel {

    private final LinkService linkService;

    public LinkTagDirective(Configuration configuration, LinkService linkService) {
        this.linkService = linkService;
        configuration.setSharedVariable("linkTag", this);
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        final DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);

        if (params.containsKey(MetnoteConst.METHOD_KEY)) {
            String method = params.get(MetnoteConst.METHOD_KEY).toString();
            switch (method) {
                case "list":
                    env.setVariable("links", builder.build().wrap(linkService.listAll()));
                    break;
                case "listByRandom":
                    env.setVariable("links", builder.build().wrap(linkService.listAllByRandom()));
                    break;
                case "listTeams":
                    env.setVariable("teams", builder.build().wrap(linkService.listTeamVos(Sort.by(DESC, "createTime"))));
                    break;
                case "listTeamsByRandom":
                    env.setVariable("teams", builder.build().wrap(linkService.listTeamVosByRandom(Sort.by(DESC, "createTime"))));
                    break;
                case "count":
                    env.setVariable("count", builder.build().wrap(linkService.count()));
                    break;
                default:
                    break;
            }
        }
        body.render(env.getOut());
    }
}
