package com.masales;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.codecs.pojo.annotations.BsonProperty;

@RegisterForReflection
public class Event {
    @BsonProperty
    private String id;
    @BsonProperty
    private String type;
    @BsonProperty
    private long created;
    @BsonProperty
    private String createdstring;
    @BsonProperty
    private boolean inpod;
    @BsonProperty
    private long createdpod;
    @BsonProperty
    private String createdpodstring;
    @BsonProperty
    private long dlvpod;
    @BsonProperty
    private String dlvpodstring;
    @BsonProperty
    private boolean inems;
    @BsonProperty
    private long createdems;
    @BsonProperty
    private String createdemsstring;
    @BsonProperty
    private long dlvems;
    @BsonProperty
    private String dlvemsstring;

    public String getCreatedstring() {
        return createdstring;
    }

    public void setCreatedstring(String createdstring) {
        this.createdstring = createdstring;
    }

    public String getCreatedpodstring() {
        return createdpodstring;
    }

    public void setCreatedpodstring(String createdpodstring) {
        this.createdpodstring = createdpodstring;
    }

    public String getDlvpodstring() {
        return dlvpodstring;
    }

    public void setDlvpodstring(String dlvpodstring) {
        this.dlvpodstring = dlvpodstring;
    }

    public String getCreatedemsstring() {
        return createdemsstring;
    }

    public void setCreatedemsstring(String createdemsstring) {
        this.createdemsstring = createdemsstring;
    }

    public String getDlvemsstring() {
        return dlvemsstring;
    }

    public void setDlvemsstring(String dlvemsstring) {
        this.dlvemsstring = dlvemsstring;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public boolean isInpod() {
        return inpod;
    }

    public void setInpod(boolean inpod) {
        this.inpod = inpod;
    }

    public long getCreatedpod() {
        return createdpod;
    }

    public void setCreatedpod(long createdpod) {
        this.createdpod = createdpod;
    }

    public long getDlvpod() {
        return dlvpod;
    }

    public void setDlvpod(long dlvpod) {
        this.dlvpod = dlvpod;
    }

    public boolean isInems() {
        return inems;
    }

    public void setInems(boolean inems) {
        this.inems = inems;
    }

    public long getCreatedems() {
        return createdems;
    }

    public void setCreatedems(long createdems) {
        this.createdems = createdems;
    }

    public long getDlvems() {
        return dlvems;
    }

    public void setDlvems(long dlvems) {
        this.dlvems = dlvems;
    }
}
