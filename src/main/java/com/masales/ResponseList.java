package com.masales;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class ResponseList {
    @JsonProperty("data")
    private final List<Event> list;

    public ResponseList(List<Event> list) {
        this.list = list;
    }

    @JsonProperty("size")
    public Integer getSize(){
        return list.size();
    }
}
