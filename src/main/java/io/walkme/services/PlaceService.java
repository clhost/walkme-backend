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
import io.walkme.storage.entities.WalkMeCategory;
import io.walkme.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Native sql is used for better performance
 */
public class PlaceService implements GenericEntityService<Place, String> {
    private static final Mapper<Schedule, JsonObject> mapper = new JsonToScheduleMapper();
    private static final String PLACE_TABLE_NAME = "wm_place";

    @Override
    public Place get(String val, String column) throws Exception {
        Session session = null;
        Place place = null;
        NativeQuery<Place> nativeQuery;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            switch (column) {
                case PlaceFields.ID:
                    nativeQuery = session.createNativeQuery(
                            "select * from " +
                                    PLACE_TABLE_NAME + " where " + PlaceFields.ID + " = :pid", Place.class);
                    nativeQuery.setParameter("pid", val);
                    place = nativeQuery.getSingleResult();
                    break;
                case PlaceFields.CATEGORY_ID:
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

    @Override
    public List<Place> getAll(List<String> criteria, String column) throws Exception {
        Session session = null;
        List<Place> places = null;
        NativeQuery<Place> nativeQuery;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            switch (column) {
                case PlaceFields.ID:
                    throw new UnsupportedOperationException("Get all for column id is unsupported. Instead of " +
                            "this use the get method.");
                case PlaceFields.CATEGORY_ID:
                    if (criteria.size() == 0) {
                        throw new IllegalStateException("The criteria list parameter is empty");
                    }

                    StringBuilder builder = new StringBuilder();
                    builder.append("(");

                    for (String s : criteria) {
                        builder.append(Long.parseLong(s)).append(", ");
                    }

                    String b = builder.toString();
                    String set = b.trim().substring(0, b.length() - 2) + ")";

                    nativeQuery = session.createNativeQuery(
                            "select * from " +
                                    PLACE_TABLE_NAME + " where " + column + " in " + set, Place.class);
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
        Session session = HibernateUtil.getSession();
        session.beginTransaction();

        session.save(place);
        session.getTransaction().commit();

        session.close();
    }

    @Override
    public void delete(String val, String key) throws Exception {
        throw new UnsupportedOperationException("Delete is still unsupported.");
    }

    @Override
    public void update(Place place) throws Exception {
        throw new UnsupportedOperationException("Update is still unsupported.");
    }
}
