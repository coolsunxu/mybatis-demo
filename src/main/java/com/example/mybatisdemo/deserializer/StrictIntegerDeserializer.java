package com.example.mybatisdemo.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class StrictIntegerDeserializer extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        if (p.getCurrentToken() != JsonToken.VALUE_NUMBER_INT) {
            ctx.reportInputMismatch(this, "需要整数类型，但收到的是%s", p.getCurrentToken());
            // 或者抛出异常
            throw ctx.wrongTokenException(p, Integer.class, JsonToken.VALUE_NUMBER_INT, "需要整数类型，但收到的是" + p.getCurrentToken());
        }
        return p.getIntValue();
    }
}
