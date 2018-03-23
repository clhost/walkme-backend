package storage.entities;

import javax.persistence.*;

@Entity
@Table(name = "e_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "salt", unique = true, nullable = false)
    private String salt;

    @Column(name = "auth_token", nullable = false)
    private String authToken;

    @Column(name = "ok_refresh_token")
    private String okRefreshToken;

    @Column(name = "f_name", nullable = false)
    private String firstName;

    @Column(name = "l_name", nullable = false)
    private String lastName;

    @Column(name = "social_id", nullable = false)
    private long socialId;

    @Column(name = "avatar", nullable = false)
    private String avatar;

    public User(long id,
                String salt,
                String authToken,
                String okRefreshToken,
                String firstName,
                String lastName,
                long socialId,
                String avatar) {
        this.id = id;
        this.salt = salt;
        this.authToken = authToken;
        this.okRefreshToken = okRefreshToken;
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialId = socialId;
        this.avatar = avatar;
    }

    public long getId() {
        return id;
    }

    public String getSalt() {
        return salt;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getOkRefreshToken() {
        return okRefreshToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getSocialId() {
        return socialId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setOkRefreshToken(String okRefreshToken) {
        this.okRefreshToken = okRefreshToken;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSocialId(long socialId) {
        this.socialId = socialId;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "(" +
                id + ", " +
                authToken + ", " +
                okRefreshToken + ", " +
                firstName + ", " +
                lastName + ", " +
                avatar +
                ")";
    }
}
