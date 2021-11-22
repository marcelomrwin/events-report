package com.masales;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;

@RegisterForReflection
public class DiffEvent {
    LocalDateTime created;
    LocalDateTime podCreated;
    LocalDateTime podDelivery;
    LocalDateTime emsCreated;
    LocalDateTime emsDelivery;
    long diffBetweenCreatedAndPod;
    long diffBetweenPodAndEms;
    long diffBetweenCreatedAndEms;

    public LocalDateTime getPodDelivery() {
        return podDelivery;
    }

    public void setPodDelivery(LocalDateTime podDelivery) {
        this.podDelivery = podDelivery;
    }

    public LocalDateTime getEmsDelivery() {
        return emsDelivery;
    }

    public void setEmsDelivery(LocalDateTime emsDelivery) {
        this.emsDelivery = emsDelivery;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getPodCreated() {
        return podCreated;
    }

    public void setPodCreated(LocalDateTime podCreated) {
        this.podCreated = podCreated;
    }

    public LocalDateTime getEmsCreated() {
        return emsCreated;
    }

    public void setEmsCreated(LocalDateTime emsCreated) {
        this.emsCreated = emsCreated;
    }

    public long getDiffBetweenCreatedAndPod() {
        return diffBetweenCreatedAndPod;
    }

    public void setDiffBetweenCreatedAndPod(long diffBetweenCreatedAndPod) {
        this.diffBetweenCreatedAndPod = diffBetweenCreatedAndPod;
    }

    public long getDiffBetweenPodAndEms() {
        return diffBetweenPodAndEms;
    }

    public void setDiffBetweenPodAndEms(long diffBetweenPodAndEms) {
        this.diffBetweenPodAndEms = diffBetweenPodAndEms;
    }

    public long getDiffBetweenCreatedAndEms() {
        return diffBetweenCreatedAndEms;
    }

    public void setDiffBetweenCreatedAndEms(long diffBetweenCreatedAndEms) {
        this.diffBetweenCreatedAndEms = diffBetweenCreatedAndEms;
    }

}
