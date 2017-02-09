package com.sun.rooster.web.support.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
* Authentication拦截器
* @author Felix Lee
* @date 2015/1/12
*/
public class AuthInterceptor extends HandlerInterceptorAdapter {

//    @Autowired
//    private SystemService systemService ;

//    // 不需要过滤的URL
//    public static final Set<String> unCheckList = Sets.newHashSet("/login?action=login") ;
//
//    /**
//     * 在业务处理器处理请求之前被调用
//     * 如果返回false
//     *     从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
//     * 如果返回true
//     *    执行下一个拦截器,直到所有的拦截器都执行完毕
//     *    再执行被拦截的Controller
//     *    然后进入拦截器链,
//     *    从最后一个拦截器往回执行所有的postHandle()
//     *    接着再从最后一个拦截器往回执行所有的afterCompletion()
//     */
//    @Override
//    public boolean preHandle(HttpServletRequest request,
//                             HttpServletResponse response, Object handler) throws Exception {
//        String invokeMethod = this.getInvokeMethod(request) ;
//        String requestUrl = this.getUrl(request) ;
//        System.out.println("url:" + requestUrl);
//        System.out.println("invokeMethod:" + invokeMethod);
//
//        //不过滤名单
//        if (unCheckList.contains(invokeMethod)) {
//            return true;
//        }
//
//        //检查登录用户是否为空
//        MemberDo loginUser = (MemberDo)request.getSession().getAttribute(LoginConstant.LOGIN_USER_INFO);
//        //获取登录用户的角色对象，里面有所属的权限集合
//        RoleDo loginUserRole = (RoleDo)request.getSession().getAttribute(LoginConstant.LOGIN_ROLE_INFO);
//        if(loginUser == null || loginUserRole == null ) {
//            System.out.println("登录用户为空！>>>>>>>>>>>>");
//            request.getRequestDispatcher("login.jsp").forward(request, response);
//            return false;
//        }
//        //获取所有权限
//        Map<String , Object> permissionMap = (Map<String , Object>)
//                RequestContextUtils.getWebApplicationContext(request)
//                        .getServletContext().getAttribute(SysConstants.ALL_PERMISSION);
//        if (permissionMap == null ) {
//            //如果丢失缓存就重新加载
//            permissionMap = systemService.uploadPermissionMapToContext() ;
//        }
//        //查找是否是需要过滤的URL
//        if (permissionMap.get(invokeMethod) != null ) {
//            RoleDo role  =  (RoleDo) request.getSession()
//                    .getAttribute(LoginConstant.LOGIN_ROLE_INFO);
//            for (PermissionDo per : role.getPermissionList()) {
//                if (!StringUtils.isEmpty(per.getUrl()) && per.getUrl().equals(invokeMethod)) {
//                    return true ;
//                }
//            }
//            System.out.println("无权限访问,url:" + invokeMethod + ",user:" + loginUser.getName());
//            request.getRequestDispatcher("system?action=prompt&title=Access Denied&" +
//                    "content=You don't have permission to access this URL, please contact with your system administrator.")
//                    .forward(request, response);
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作
//     * 可在modelAndView中加入数据，比如当前时间
//     */
//    @Override
//    public void postHandle(HttpServletRequest request,
//                           HttpServletResponse response, Object handler,
//                           ModelAndView modelAndView) throws Exception {
////        System.out.println("==============执行顺序: 2、postHandle================");
////        if(modelAndView != null){  //加入当前时间
////            modelAndView.addObject("var", "测试postHandle");
////        }
//    }
//
//    /**
//     * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
//     *
//     * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
//     */
//    @Override
//    public void afterCompletion(HttpServletRequest request,
//                                HttpServletResponse response, Object handler, Exception ex)
//            throws Exception {
////        System.out.println("==============执行顺序: 3、afterCompletion================");
//    }
//
//    /**
//     * 获取请求的URL
//     * @param request
//     * @return
//     */
//    public String getUrl (HttpServletRequest request) {
//        StringBuffer url = new StringBuffer(request.getRequestURL()) ;
//        Iterator parameters = request.getParameterMap().entrySet().iterator() ;
//
//        synchronized (this) {
//            boolean firstFlag = true ;
//            while (parameters.hasNext()) {
//                Map.Entry<String , String[]> e = (Map.Entry<String , String[]>) parameters.next();
//                if (firstFlag) {
//                    url.append("?" + e.getKey()+"="+e.getValue()[0]) ;
//                    firstFlag =  false ;
//                }else {
//                    url.append("&" + e.getKey()+"="+e.getValue()[0]) ;
//                }
//            }
//        }
//
//        return  url.toString() ;
//    }
//
//    /**
//     * 获取调用的方法
//     * @param request
//     * @return
//     */
//    public String getInvokeMethod (HttpServletRequest request) {
//        StringBuffer url = new StringBuffer(request.getRequestURI()) ;
//        if (request.getParameterMap().get("action") != null ) {
//            String[] method = (String[] )request.getParameterMap().get("action") ;
//            if (!StringUtils.isEmpty(method[0])) {
//                url.append("?action=" + method[0]) ;
//            }
//        }
//        return url.toString() ;
//    }


}
