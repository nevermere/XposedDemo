package com.android.xposed.controller;

import android.text.TextUtils;

import com.android.xposed.hook.XposedBinderManager;
import com.android.xposed.model.AwemeModel;
import com.android.xposed.model.BaseModel;
import com.android.xposed.utils.GsonUtil;
import com.android.xposed.utils.LogUtil;
import com.android.xposed.utils.StringUtil;
import com.yanzhenjie.andserver.annotation.CrossOrigin;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;

import java.util.List;
import java.util.Map;

import static com.android.xposed.model.BaseModel.FAILED;
import static com.android.xposed.model.BaseModel.SUCCESS;

/**
 * 描述：CoreController
 *
 * @author fzJiang
 * @date 2020-12-08 16:51.
 */
@RestController
@RequestMapping(path = "/com/android/xposed")
public class CoreController {

    @CrossOrigin(
            methods = {RequestMethod.POST, RequestMethod.GET}
    )
    @PostMapping(path = "/aweme", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String aweme(@RequestParam(name = "url") String url,
                 @RequestParam(name = "map") String map) {

        LogUtil.i("-----url-----" + url);
        LogUtil.i("-----map-----" + map);

        BaseModel<AwemeModel> returnModel = new BaseModel<>();
        if (TextUtils.isEmpty(url) || map == null) {
            returnModel.setCode(FAILED);
            returnModel.setMessage("参数不可为空");
            return GsonUtil.gsonString(returnModel);
        }

        Map<String, List<String>> mapArgs = StringUtil.mapStringToMap(map);
        LogUtil.i("-----mapArgs-----" + mapArgs);

//        Map<String, List<String>> mapArgs = new HashMap<>(4);
//        // 1.accept-encoding
//        List<String> stringList = new ArrayList<>();
//        stringList.add("gzip");
//        mapArgs.put("accept-encoding", stringList);
//
//        // 2.sdk-version
//        stringList = new ArrayList<>();
//        stringList.add("1");
//        mapArgs.put("sdk-version", stringList);
//
//        // 3.x-ss-req-ticket
//        stringList = new ArrayList<>();
//        stringList.add("1607485100099");
//        mapArgs.put("x-ss-req-ticket", stringList);
//
//        // 4. cookie map<String,String>
//        stringList = new ArrayList<>();
//
//        Map<String, String> cookieMap = new HashMap<>(3);
//        cookieMap.put("odin_tt", "85203ff96ec2873105e1e715dfe563a884f8f0acc027a90bf3bfbde74d71926ab5c41ca533129c1b89f4d164f02939c89cc8e6237a1803be32a51be74bb7491f");
//        cookieMap.put("install_id", "3623632126613896");
//        cookieMap.put("ttreq", "1$652a62c3560a471978e36e5a14383dabac4f1f1f");
//
//        stringList.add(StringUtil.mapToString(cookieMap));
//        mapArgs.put("cookie", stringList);
//
//        LogUtil.i("cookie:" + StringUtil.mapToString(cookieMap));


        // 发起请求
        AwemeModel awemeModel = new AwemeModel();
        awemeModel.setUrl(url);
        awemeModel.setMap(mapArgs);
        final Map<String, String> resultMap
                = XposedBinderManager.getInstance().sendData(url, mapArgs, false);
        if (resultMap != null) {
            String key;
            for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                key = entry.getKey();

                if ("X-Gorgon".equals(key)) {
                    awemeModel.setxGorgon(entry.getValue());
                } else if ("X-Khronos".equals(key)) {
                    awemeModel.setxKhronos(entry.getValue());
                } else if ("X-Pods".equals(key)) {
                    awemeModel.setxPods(entry.getValue());
                }
            }

            returnModel.setCode(SUCCESS);
            returnModel.setData(awemeModel);
        } else {
            returnModel.setCode(FAILED);
            returnModel.setMessage("参数有误");
        }
        return GsonUtil.gsonString(returnModel);
    }
}