package ru.vladshi.springlearning.config;

//import jakarta.servlet.ServletContext;
//import jakarta.servlet.ServletException;
//import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }


    @Override
    @NonNull
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

//    @Override
//    public void onStartup(ServletContext aServletContext) throws ServletException {
//        super.onStartup(aServletContext);
//        registerHiddenFieldFilter(aServletContext);
//    }
//
//    private void registerHiddenFieldFilter(ServletContext aContext) {  // для чтения скрытых PATCH, DELETE http-методов,
//        aContext.addFilter("hiddenHttpMethodFilter",                // передаваемых через форму в html
//                new HiddenHttpMethodFilter()).addMappingForUrlPatterns(null ,true, "/*");
//    }
}
