package ru.kostapo.utils;

import ru.kostapo.exceptions.BadParameterException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

public class StringUtils {

    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public  String getValueByKey(HttpServletRequest request, String key) throws IOException {
        String data = getRequestBody(request);
        String[] pairs = data.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if(keyValue[0].equals(key) && !keyValue[1].isEmpty()) {
                return keyValue[1];
            }
        }
        throw new BadParameterException("ОТСУТСТВУЕТ ЗНАЧЕНИЕ ПАРАМЕТРА " +  key);
    }

    public boolean isNameValid(String name) {
        for(char ch : name.toCharArray()) {
            if (Character.isLetter(ch)) {
                if (Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.CYRILLIC
                        && Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.BASIC_LATIN) {
                    throw new BadParameterException("НЕ КОРРЕКТНОЕ НАЗВАНИЕ ВАЛЮТЫ");
                }
            } else {
                throw new BadParameterException("НЕ КОРРЕКТНОЕ НАЗВАНИЕ ВАЛЮТЫ");
            }
        }
        return true;
    }

    public boolean isCodeValid(String code) {
        if(code.length() != 3) {
            throw new BadParameterException("НЕ КОРРЕКТНЫЙ КОД ВАЛЮТЫ "+code);
        }
        for(char ch : code.toCharArray()) {
            if (Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.BASIC_LATIN) {
                throw new BadParameterException("НЕ КОРРЕКТНЫЙ КОД ВАЛЮТЫ "+code);
            }
        }
        return true;
    }

    public String getPathInfo(HttpServletRequest request) {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            throw new BadParameterException("НЕ ВЕРНЫЙ ПАРАМЕТР ПУТИ");
        }
        return request.getPathInfo();
    }

    public boolean isCodePairValid(String codePair) {
        if(codePair.length() != 6) {
            throw new BadParameterException("НЕ КОРРЕКТНАЯ ВАЛЮТНАЯ ПАРА");
        }
        return (isCodeValid(codePair.substring(0, 3)) && isCodeValid(codePair.substring(3, 6)));
    }

    public BigDecimal getBigDecimalByString(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            throw new BadParameterException("ПАРАМЕТР "+str+" НЕ ЯВЛЯЕТСЯ ЧИСЛОМ");
        }
    }
}
