package io.walkme.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.Query;
import io.walkme.storage.entities.Session;
import io.walkme.utils.HibernateUtil;

import java.util.concurrent.ConcurrentHashMap;

public class SessionService {
    private static final String SESSION_TABLE_NAME = "wm_session";
    private static SessionService instance;
    private static final Logger logger = LogManager.getLogger(SessionService.class);

    /**
     * key - user id
     * value - session token
     */
    private final ConcurrentHashMap<Long, String> sessions = new ConcurrentHashMap<>();

    private SessionService() {

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
                query.getResultStream().forEach(s -> sessions.put(s.getUser().getId(), s.getSessionToken()));
            } else {
                throw new IllegalStateException("The sessions map must be empty for performing this operation.");
            }

            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public boolean isSessionExist(String token) {
        return sessions.containsValue(token);
    }

    public void saveSession(Session session) {
        org.hibernate.Session hibSession = null;
        try {
            hibSession = HibernateUtil.getSession();
            hibSession.beginTransaction();

            hibSession.saveOrUpdate(session);
            hibSession.getTransaction().commit();

            sessions.put(session.getUser().getId(), session.getSessionToken());

        } finally {
            if (hibSession != null) {
                hibSession.close();
            }
        }
    }

    public void deleteSession(Long userId) {
        sessions.remove(userId);
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
