package com.fm.base.exceptionMappers;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;

import java.util.List;

public class Utils {
    public static String getFieldPath(List<Reference> refs) {
        String path = "";
        for (Reference r: refs) {
            path += "." + r.getFieldName();
        }
        return path.substring(1);
    }
}