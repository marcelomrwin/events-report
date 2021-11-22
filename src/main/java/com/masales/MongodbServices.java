package com.masales;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import io.quarkus.runtime.StartupEvent;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@ApplicationScoped
public class MongodbServices {

    @ConfigProperty(name = "quarkus.mongodb.connection-string")
    String mongoConnectionString;

    MongoClient mongoClient;
    MongoCollection<Event> events;

    @Inject
    ObjectMapper mapper;

    @ConfigProperty(name = "app.report.path")
    String reportPath;

    @PostConstruct
    public void config() {
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoConnectionString))
                .codecRegistry(codecRegistry)
                .build();
        this.mongoClient = MongoClients.create(clientSettings);
        events = mongoClient.getDatabase("eventsdb").getCollection("events", Event.class);
    }

    public void add(Event event) {
        events.insertOne(event);
    }

    public void update(Event event) {
        events.findOneAndReplace(eq("id", event.getId()), event);
    }

    public void delete(Event event) {
        events.deleteOne(eq("id", event.getId()));
    }

    public List<Event> list() {
        List<Event> list = new ArrayList<>();
        try (MongoCursor<Event> iterator = events.find().iterator()) {
            while(iterator.hasNext())
                list.add(iterator.next());
        }
        return list;
    }

    public long count() {
        return events.countDocuments();
    }

    public long countEventsFoundOnPODAndEMS() {
        return events.countDocuments(and(eq("inems", Boolean.TRUE), eq("inpod",Boolean.TRUE)));
    }

    public List<Event> listByType(String type) {
        List<Event> list = new ArrayList<>();
        try (MongoCursor<Event> iterator = events.find(eq("type", type)).iterator()) {
            while (iterator.hasNext())
                list.add(iterator.next());
        }
        return list;
    }

    public List<Event> listEventsOfTypeOntRangingEvent() {
        return listByType("ontRangingEvent");
    }

    public List<Event> listEventsOfTypeObjectDuplicated() {
        return listByType("ObjectDuplicated");
    }

    public List<Event> listEventsOfTypeObjectCreated() {
        return listByType("objectCreated");
    }

    public Event findByNotificationId(String notificationId){
        return events.find(eq("id",notificationId)).first();
    }

    public List<Event> listEventsNotFoundInPOD() {
        List<Event> list = new ArrayList<>();
        try (MongoCursor<Event> iterator = events.find(eq("inpod", Boolean.FALSE)).iterator()) {
            while (iterator.hasNext())
                list.add(iterator.next());
        }
        return list;
    }

    public List<Event> listEventsNotFoundInEMS() {
        List<Event> list = new ArrayList<>();
        try (MongoCursor<Event> iterator = events.find(eq("inems", Boolean.FALSE)).iterator()) {
            while (iterator.hasNext())
                list.add(iterator.next());
        }
        return list;
    }

    public List<Event> listEventsFoundOnPODAndEMS() {
        List<Event> list = new ArrayList<>();
        try (MongoCursor<Event> iterator = events.find(and(eq("inems", Boolean.TRUE), eq("inpod",Boolean.TRUE))  ).iterator()) {
            while (iterator.hasNext())
                list.add(iterator.next());
        }
        return list;
    }

    public Map<String, Event> findFirstAndLastEventGenerated(){
        Event first = events.find().sort(eq("created", +1)).limit(1).first();
        Event last = events.find().sort(eq("created", -1)).limit(1).first();
        Map<String,Event> retMap = new WeakHashMap<>();
        retMap.put("first",first);
        retMap.put("last",last);
        return retMap;
    }

    public Map<String, Event> findFirstAndLastEventCompleted() {
        Event first = events.find( and(exists("dlvems"),ne("dlvems",null),gt("dlvems",0))).sort(eq("dlvems", +1)).limit(1).first();
        Event last = events.find().sort(eq("dlvems", -1)).limit(1).first();
        Map<String,Event> retMap = new WeakHashMap<>();
        retMap.put("first",first);
        retMap.put("last",last);
        return retMap;
    }

    void onStart(@Observes StartupEvent ev) {
        if (events.countDocuments() <= 0) {
            File reportFolder = new File(reportPath);
            if (!reportFolder.exists()) {
                throw new RuntimeException("Report Folder does not exists! [" + reportPath + "]\nVerify Environment Variable REPORT_PATH");
            }
            File eventsFolder = new File(reportFolder, "events");
            if (!eventsFolder.exists()) {
                throw new RuntimeException("Events folder does not exists! Did you run tools/events_summary after test execution?\nThis step is necessary to generate events.js");
            }
            File eventjs = new File(eventsFolder, "events.js");

            try (Scanner sc = new Scanner(eventjs)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.contains("{")) {
                        String json = line.substring(line.indexOf("{")).trim();
                        Event event = mapper.readValue(json, Event.class);
                        add(event);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Javascript file events.js not found! Did you run tools/events_summary after test execution?\nThis step is necessary to generate events.js");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            LoggerFactory.getLogger(getClass()).info("Database imported");
        }
    }

}
