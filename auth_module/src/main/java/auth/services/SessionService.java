package auth.services;

import auth.entities.Session;
import auth.utils.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.Query;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SessionService {
    private static final String SESSION_TABLE_NAME = "wm_session";
    private static SessionService instance;
    private static final Logger logger = LogManager.getLogger(SessionService.class);

    /**
     * key - user id
     * value - session token
     */
    private final ConcurrentHashMap<Long, Set<String>> sessions = new ConcurrentHashMap<>();

    private SessionService() {
        // for singleton
    }

    public static SessionService getInstance() {
        SessionService result = instance;
        if (result == null) {
            synchronized (SessionService.class) {
                result = instance;
                if (result == null) {
                    result = new SessionService();
                    instance = result;
                }
            }
        }
        return result;
    }

    public void loadFromDatabase() {
        org.hibernate.Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Query<Session> query = session.createNativeQuery(
                    "select * from " + SESSION_TABLE_NAME + ";",
                    Session.class);

            if (sessions.size() == 0) {
                Map<Long, List<Session>> map =
                        query.getResultStream().collect(Collectors.groupingBy(s -> s.getUser().getId()));
                map.forEach((i, t) ->
                        sessions.put(i, t.stream().map(Session::getSessionToken).collect(Collectors.toSet()))
                );
            } else {
                throw new IllegalStateException("The sessions map must be empty for performing this operation.");
            }

            session.getTransaction().commit();
        } finally {
            logger.info("All sessions has been loaded in RAM.");
            if (session != null) {
                session.close();
            }
        }
    }

    public boolean isSessionExist(String token) {
        for (Map.Entry<Long, Set<String>> entry : sessions.entrySet()) {
            if (entry.getValue().contains(token)) {
                return true;
            }
        }
        return false;
    }

    public void saveSession(Session session) {
        org.hibernate.Session hibSession = null;
        try {
            hibSession = HibernateUtil.getSession();
            hibSession.beginTransaction();

            hibSession.saveOrUpdate(session);
            hibSession.getTransaction().commit();

            Set<String> e = sessions.get(session.getUser().getId());
            if (e != null) {
                e.add(session.getSessionToken());
            } else {
                e = new HashSet<>();
                e.add(session.getSessionToken());
                sessions.put(session.getUser().getId(), e);
            }
        } finally {
            if (hibSession != null) {
                hibSession.close();
            }
        }
    }

    public void deleteSession(String token) {
        Iterator<Map.Entry<Long, Set<String>>> iterator = sessions.entrySet().iterator();

        while (iterator.hasNext()) {
            Set<String> e = iterator.next().getValue();
            if (e.contains(token)) {
                e.remove(token);
                break;
            }
        }

        org.hibernate.Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Query<Session> query = session.createNativeQuery(
                    "delete from " + SESSION_TABLE_NAME + " where session_token = :st",
                    Session.class);
            query.setParameter("st", token);

            query.executeUpdate();
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void clear() {
        org.hibernate.Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Query<Session> query = session.createNativeQuery(
                    "delete from " + SESSION_TABLE_NAME + ";",
                    Session.class);

            query.executeUpdate();
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
