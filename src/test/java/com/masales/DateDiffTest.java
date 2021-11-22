package com.masales;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class DateDiffTest {

    @Inject
    EventService service;

    @Test
    public void verifyDiff(){
        Event evt = new Event();
        evt.setCreated(1637267318593L);
        evt.setCreatedems(1637270918492L);
        evt.setCreatedpod(1637190000000L);
        evt.setDlvpod(1637190000000L);
        evt.setDlvems(1637267325623L);
        evt.setId("9e6f38fd-1dd3-459b-9f84-72805c028a8a");
        evt.setInems(true);
        evt.setInpod(true);
        evt.setType("ontRangingEvent");

        DiffEvent diffEvent = service.createDiffEvent(evt);
        System.out.println(diffEvent.getCreated());

    }
}
