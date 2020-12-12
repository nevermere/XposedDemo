package com.android.xposed.net.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * 描述：
 *
 * @author fzJiang
 * @date 2019-08-21 15:43.
 */
public class FloatDefault0Adapter extends BaseDefault0Adapter<Float> {

    @Override
    public Float deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            final String jsonStr = json.getAsString();
            if (DEFAULT_EMPTY.equals(jsonStr)
                    || DEFAULT_NULL.equals(jsonStr) || DEFAULT_NULL_UP.equals(jsonStr)) {
                // 定义为long类型,如果后台返回""或者null,则返回0
                return 0F;
            }
        } catch (Exception ignore) {

        }
        try {
            return json.getAsFloat();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }
}
