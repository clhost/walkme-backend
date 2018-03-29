package io.walkme.storage.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

@Entity
@Table(name = "wm_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "salt", unique = true, nullable = false, length = 20)
    private String salt;

    @Column(name = "f_name", nullable = false, length = 50)
    @Expose
    private String firstName;

    @Column(name = "l_name", nullable = false, length = 50)
    @Expose
    private String lastName;

    @Column(name = "social_id", nullable = false, unique = true)
    @Expose
    private long socialId;

    @Column(name = "avatar", nullable = false)
    @Expose
    private String avatar;

    public User(long id,
                String salt,
                String firstName,
                String lastName,
                long socialId,
                String avatar) {
        this.id = id;
        this.salt = salt;
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialId = socialId;
        this.avatar = avatar;
    }

    public User() {

    }

    public long getId() {
        return id;
    }

    public String getSalt() {
        return salt;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof User)) {
            return false;
        }

        User user = (User) o;

        if (id != user.id) {
            return false;
        }

        if (socialId != user.socialId) {
            return false;
        }

        if (salt != null ? !salt.equals(user.salt) : user.salt != null) {
            return false;
        }

        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) {
            return false;
        }

        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) {
            return false;
        }

        return avatar != null ? avatar.equals(user.avatar) : user.avatar == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));

        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (int) (socialId ^ (socialId >>> 32));
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", salt='" + salt + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", socialId=" + socialId +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}