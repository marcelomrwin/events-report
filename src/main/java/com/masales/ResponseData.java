package com.masales;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class ResponseData {
    @JsonProperty("data")
    private final List<DiffEvent> list;

    public ResponseData(List<DiffEvent> list) {
        this.list = list;
    }

    @JsonProperty("size")
    public Integer getSize(){
        return list.size();
    }
}
