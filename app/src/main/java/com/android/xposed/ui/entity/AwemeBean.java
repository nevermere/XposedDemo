package com.android.xposed.ui.entity;

import java.util.List;
import java.util.Map;

/**
 * 描述：
 *
 * @author fzJiang
 * @date 2020-12-11 9:48.
 */
public class AwemeBean {

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private String url;
        private Map<String, List<String>> map;
        private String xKhronos;
        private String xGorgon;
        private String xPods;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, List<String>> getMap() {
            return map;
        }

        public void setMap(Map<String, List<String>> map) {
            this.map = map;
        }

        public String getxKhronos() {
            return xKhronos;
        }

        public void setxKhronos(String xKhronos) {
            this.xKhronos = xKhronos;
        }

        public String getxGorgon() {
            return xGorgon;
        }

        public void setxGorgon(String xGorgon) {
            this.xGorgon = xGorgon;
        }

        public String getxPods() {
            return xPods;
        }

        public void setxPods(String xPods) {
            this.xPods = xPods;
        }
    }
}
