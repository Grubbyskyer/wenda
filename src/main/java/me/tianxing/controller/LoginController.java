package me.tianxing.controller;

import me.tianxing.constants.StringConstants;
import me.tianxing.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation.ANONYMOUS.required;
import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

/**
 * Created by TX on 2016/7/22.
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/register/"}, method = {RequestMethod.POST})
    public String register(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                           @RequestParam(value = "callback", required = false) String callback,
                           HttpServletResponse response) {
        try {
            Map<String, String> map = userService.register(username, password);
            // map包含ticket或者msg
            // 注册成功情况下包含"ticket"子段
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                if (rememberme) { // 如果点击了<记住我>，那么为其设置cookie的存活时间
                    cookie.setMaxAge(3600 * 24 * 5); // 设置存活5天 默认值为-1表示关闭浏览器就消失
                }
                // 为浏览器添加cookie
                response.addCookie(cookie);
                // 如果有回调URL，判断该URL是不是一个站内地址，如果是则返回该URL
                if (StringUtils.isNotBlank(callback) && callback.startsWith(StringConstants.DOMAIN_NAME)) {
                    return "redirect:" + callback;
                }
                // 若没有callback，或callback非法，则直接返回主页
                return "redirect:/";
            } else { // 注册失败，返回到登录注册界面，此时包含"msg"子段
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("注册异常！" + e.getMessage());
            model.addAttribute("msg", StringConstants.SERVER_ERROR);
            return "login";
        }
    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        @RequestParam(value = "callback", required = false) String callback,
                        HttpServletResponse response) {
        try {
            Map<String, String> map = userService.login(username, password);
            // map包含ticket或者msg
            // 登录成功情况下包含"ticket"子段
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                if (rememberme) { // 如果点击了<记住我>，那么为其设置cookie的存活时间
                    cookie.setMaxAge(3600 * 24 * 5); // 设置存活5天 默认值为-1表示关闭浏览器就消失
                }
                // 为浏览器添加cookie
                response.addCookie(cookie);
                // 如果有回调URL，判断该URL是不是一个站内地址，如果是则返回该URL
                if (StringUtils.isNotBlank(callback) && callback.startsWith(StringConstants.DOMAIN_NAME)) {
                    return "redirect:" + callback;
                }
                // 若没有callback，或callback非法，则直接返回主页
                return "redirect:/";
            } else { // 登录失败，返回到登录注册界面，此时包含"msg"子段
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("登录异常！" + e.getMessage());
            model.addAttribute("msg", StringConstants.SERVER_ERROR);
            return "login";
        }
    }

    // 登出功能，直接从cookie中找到ticket，然后将其“删除”（将其status置为1）
    @RequestMapping(path = {"/logout"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

    // 注册和登录的界面，方法用GET，因为是读取界面而不是提交数据
    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String reglogin(Model model, @RequestParam(value = "callback", defaultValue = "", required = false) String callback) {
        model.addAttribute("callback", callback);
        return "login";
    }

}
