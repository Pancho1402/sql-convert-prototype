package org.sql.convert;

import com.google.gson.Gson;

public class JsonFormat {
    private JsonFormat(){}
    public static String jsonConvert(Object map){
        Gson gson = new Gson();
        return gson.toJson(map);
    }

}
