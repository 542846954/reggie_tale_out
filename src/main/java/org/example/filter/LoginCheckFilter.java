package org.example.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseContext;
import org.example.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*检查用户是否登录完成*/
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    /*路径匹配器，支持通配符*/
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        /*需要放行的资源路径*/
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次请求{}，无需拦截",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id={}",request.getSession().getAttribute("employee"));
            BaseContext.setCurrentId(((Long) request.getSession().getAttribute("employee")));
            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id={}",request.getSession().getAttribute("user"));
            BaseContext.setCurrentId(((Long) request.getSession().getAttribute("user")));
            filterChain.doFilter(request,response);
            return;
        }


        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean check(String[] urls,String requestURI){
        for (String url:urls){
            if (PATH_MATCHER.match(url,requestURI)){
                return true;
            }
        }
        return false;
    }
}
