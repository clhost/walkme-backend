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
    public SavedRoute get(String byParameter, SavedRouteFields columnType) throws Exception {
        Session session = null;
        SavedRoute favoriteRoute = null;
        NativeQuery<SavedRoute> nativeQuery;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            switch (columnType) {
                case ID:
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
    public List<SavedRoute> getAll(List<String> e, SavedRouteFields savedRouteFields) throws Exception {
        return null;
    }

    @Override
    public void save(SavedRoute savedRoute) throws Exception {

    }

    @Override
    public void delete(String e, SavedRouteFields savedRouteFields) throws Exception {

    }

    @Override
    public void update(SavedRoute savedRoute) throws Exception {

    }
}
