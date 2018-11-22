package com.jeancoder.core.rendering;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * @author wow zhang_gh@cpis.cn
 * @date 2018年5月18日
 */
public class ThymeleafAppUtil {

    private static TemplateEngine templateEngine;

    /**
     * static代码块，加载初始模板设置：/WEB-INF/templates/**.html文件
     */
    static {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(null);
        templateResolver.setTemplateMode("XHTML");
        templateResolver.setPrefix("/template/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3600000L);
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    public static TemplateEngine getTemplateEngine() {
     return templateEngine;
    }

}