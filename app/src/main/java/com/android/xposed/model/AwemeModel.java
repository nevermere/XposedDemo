package com.android.xposed.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 描述：AwemeModel
 *
 * @author fzJiang
 * @date 2020-12-08 17:28.
 */
public class AwemeModel implements Serializable {

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

    @NonNull
    @Override
    public String toString() {
        return "AwemeModel{" +
                "url='" + url + '\'' +
                ", map=" + map +
                ", xKhronos='" + xKhronos + '\'' +
                ", xGorgon='" + xGorgon + '\'' +
                ", xPods='" + xPods + '\'' +
                '}';
    }
}
