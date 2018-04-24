package io.walkme.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import io.walkme.services.fields.PlaceFields;
import io.walkme.storage.entities.Place;
import io.walkme.storage.entities.Schedule;
import io.walkme.mappers.JsonToScheduleMapper;
import io.walkme.mappers.Mapper;
import io.walkme.utils.HibernateUtil;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Используется нативный SQL для большей производительности
 */
public class PlaceService implements EntityService<Place, String, PlaceFields> {
    public static final String TABLE_NAME = "wm_place";
    private static final Mapper<Schedule, JsonObject> mapper = new JsonToScheduleMapper();

    @Nullable
    @Override
    public Place get(String byParameter, PlaceFields columnType) throws Exception {
        Session session = null;
        Place place = null;
        NativeQuery<Place> nativeQuery;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            switch (columnType) {
                case ID:
                    nativeQuery = session.createNativeQuery(
                            "select * from " + TABLE_NAME + " " +
                                    "where " + columnType.getName() + " = :pid", Place.class);
                    nativeQuery.setParameter("pid", byParameter);
                    place = nativeQuery.getSingleResult();
                    break;
                case CATEGORY_ID:
                    throw new UnsupportedOperationException("Get for column category_id is unsupported. Instead of " +
                            "this use the getAll method.");
            }
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        if (place != null) {
            JsonObject object = new JsonParser().parse(place.getScheduleAsJsonString()).getAsJsonObject();
            place.setSchedule(mapper.map(object));
        }
        return place;
    }

    @Nullable
    @Override
    public List<Place> getAll(List<String> byParametersList, PlaceFields columnType) throws Exception {
        Session session = null;
        List<Place> places = null;
        NativeQuery<Place> nativeQuery;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            switch (columnType) {
                case ID:
                    throw new UnsupportedOperationException("Get all for column id is unsupported. Instead of " +
                            "this use the get method.");
                case CATEGORY_ID:
                    if (byParametersList.size() == 0) {
                        throw new IllegalStateException("The criteria list parameter is empty.");
                    }

                    StringBuilder builder = new StringBuilder();
                    builder.append("(");

                    for (String s : byParametersList) {
                        builder.append(Long.parseLong(s)).append(", ");
                    }

                    String b = builder.toString();
                    String set = b.trim().substring(0, b.length() - 2) + ")";

                    nativeQuery = session.createNativeQuery(
                            "select * from " + TABLE_NAME + " " +
                                    "where " + columnType.getName() + " in " + set, Place.class);
                    places = nativeQuery.getResultList();
                    break;
            }
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        if (places != null) {
            for (Place place : places) {
                if (place != null) {
                    JsonObject object = new JsonParser().parse(place.getScheduleAsJsonString()).getAsJsonObject();
                    place.setSchedule(mapper.map(object));
                }
            }
        }
        return places;
    }

    @Override
    public void save(Place place) throws Exception {
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            session.save(place);
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(String byParameter, PlaceFields columnType) throws Exception {
        throw new UnsupportedOperationException("Delete is still unsupported.");
    }

    @Override
    public void update(Place place) throws Exception {
        throw new UnsupportedOperationException("Update is still unsupported.");
    }
}
