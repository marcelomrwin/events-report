package com.masales;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@ApplicationScoped
public class EventService {

    @Inject
    MongodbServices mongodbServices;

    public DiffEvent createDiffEvent(Event event) {
        DiffEvent diffEvent = new DiffEvent();
        LocalDateTime created = LocalDateTime.ofInstant(new Date(event.getCreated()).toInstant(), ZoneId.systemDefault());
        diffEvent.setCreated(created);
        LocalDateTime createdPOD = LocalDateTime.ofInstant(new Date(event.getCreatedpod()).toInstant(), ZoneId.systemDefault());
        diffEvent.setPodCreated(createdPOD);
        LocalDateTime createdEMS = LocalDateTime.ofInstant(new Date(event.getCreatedems()).toInstant(), ZoneOffset.UTC);
        diffEvent.setEmsCreated(createdEMS);


        LocalDateTime dlvPOD = LocalDateTime.ofInstant(new Date(event.getDlvpod()).toInstant(), ZoneOffset.ofHours(+2));
        diffEvent.setPodDelivery(dlvPOD);
        LocalDateTime dlvEMS = LocalDateTime.ofInstant(new Date(event.getDlvems()).toInstant(), ZoneOffset.systemDefault());
        diffEvent.setEmsDelivery(dlvEMS);

        Duration diffPOD = Duration.between(created, createdPOD);
        diffEvent.setDiffBetweenCreatedAndPod(diffPOD.getSeconds());

        Duration diffPODEMS = Duration.between(createdPOD, dlvEMS);
        diffEvent.setDiffBetweenPodAndEms(diffPODEMS.getSeconds());

        Duration diffCreatedEMS = Duration.between(created, dlvEMS);
        diffEvent.setDiffBetweenCreatedAndEms(diffCreatedEMS.getSeconds());

        return diffEvent;
    }

    public List<DiffEvent> flop() {
        List<Event> events = mongodbServices.listEventsFoundOnPODAndEMS();
        List<DiffEvent> diffs = new ArrayList<>();
        events.forEach(e -> diffs.add(createDiffEvent(e)));
        return diffs;
    }

    public List<DiffEvent> flopWithGroupIntervalSeconds(int interval) {
        List<DiffEvent> diffs = flop();
        List<DiffEvent> groupedDiffs = new ArrayList<>();
        Iterator<DiffEvent> it = diffs.iterator();
        if (it.hasNext()){
            DiffEvent firstEvent = it.next();
            LocalDateTime actualTime = firstEvent.emsDelivery.plusSeconds(interval);

            groupedDiffs.add(firstEvent);
            while(it.hasNext()){
                DiffEvent actual = it.next();
                if (actual.getEmsDelivery().isAfter(actualTime)){
                    groupedDiffs.add(actual);
                    actualTime = actual.emsDelivery.plusSeconds(interval);
                }
            }
            //ensure the last event is on the final result
            groupedDiffs.add(diffs.get(diffs.size()-1));
        }
        return groupedDiffs;
    }
}
