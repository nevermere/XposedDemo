package com.android.xposed.net.adapter;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * 描述：
 *
 * @author fzJiang
 * @date 2019-08-21 15:40.
 */
public abstract class BaseDefault0Adapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    protected static final String DEFAULT_EMPTY = "";
    protected static final String DEFAULT_NULL = "null";
    protected static final String DEFAULT_NULL_UP = "NULL";

}