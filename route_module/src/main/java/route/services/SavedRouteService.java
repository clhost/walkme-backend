package route.services;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import route.services.fields.SavedRouteFields;
import route.storage.entities.SavedRoute;
import route.utils.HibernateUtil;

import java.util.List;

public class SavedRouteService implements EntityService<SavedRoute, String, SavedRouteFields> {
    private static final String TABLE_NAME = "wm_fav_route";

    @Override
    public SavedRoute get(String byParameter, SavedRouteFields columnType) {
        Session session = null;
        SavedRoute favoriteRoute = null;
        NativeQuery<SavedRoute> nativeQuery;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            switch (columnType) {
                case ID:
                case USER_ID:
                    nativeQuery = session.createNativeQuery(
                            "select * from " + TABLE_NAME + " " +
                                    "where " + columnType.getName() + " = :pid", SavedRoute.class);
                    nativeQuery.setParameter("pid", byParameter);
                    favoriteRoute = nativeQuery.getSingleResult();
                    break;
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return favoriteRoute;
    }

    @Override
    public List<SavedRoute> getAll(List<String> e, SavedRouteFields savedRouteFields) {
        return null;
    }

    @Override
    public void save(SavedRoute savedRoute) {
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            session.save(savedRoute);
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(String e, SavedRouteFields savedRouteFields) {

    }

    @Override
    public void update(SavedRoute savedRoute) {
        throw new UnsupportedOperationException("Update is still unsupported.");
    }
}
