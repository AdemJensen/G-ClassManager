package top.grapedge.gclass.base;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @program: G-ClassManager
 * @description: 进行各种字符串转换、加密
 * @author: Grapes
 * @create: 2019-03-14 11:45
 **/
public class Parser {
    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return Primitives.wrap(classOfT).cast(new Gson().fromJson(json, classOfT));
    }

    public static <T> T fromJson(String json, Type typeOf) {
        return new Gson().fromJson(json, typeOf);
    }

    public static String toJson(Object src) {
        return new Gson().toJson(src);
    }

    public static String md5(String string) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(string));
            return String.format("%032x", new BigInteger(1, md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String md5(File file) {
        // TODO
        return "";
    }
}
