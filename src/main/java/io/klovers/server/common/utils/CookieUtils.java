package io.klovers.server.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class CookieUtils {

    public static void create(HttpServletResponse response, String name, String value, Boolean httpOnly, Boolean secure, Integer maxAge, String domain) throws UnsupportedEncodingException {
        //Cookie cookie = new Cookie(name, URLEncoder.encode(value, "UTF-8"));
        ResponseCookie cookie = ResponseCookie.from(name, URLEncoder.encode(value, "UTF-8"))
                .domain(getDomain(domain))
                .path("/")
                .httpOnly(httpOnly)
                .secure(secure)
                .maxAge(maxAge)
                .sameSite(secure ? "None" : null)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void clear(HttpServletRequest request, HttpServletResponse response, String name) {
        String domain = request.getServerName();
        ResponseCookie cookie = ResponseCookie.from(name, null)
                .domain(getDomain(domain))
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private static String getDomain(String domain) {
        if (domain != null) {
            String[] domains = StringUtils.split(domain, ".");
            if (domains != null && domains.length == 3) {
                domain = domains[1] + "." + domains[2];
            }
            return domain;
        }
        return null;
    }

    public static String getValue(HttpServletRequest request, String name) throws UnsupportedEncodingException {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie != null ? URLDecoder.decode(cookie.getValue(), "UTF-8") : null;
    }

}
