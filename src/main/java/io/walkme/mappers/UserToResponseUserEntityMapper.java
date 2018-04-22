package io.walkme.mappers;

import io.walkme.response.user.ResponseUserEntity;
import io.walkme.storage.entities.User;

public class UserToResponseUserEntityMapper implements Mapper<ResponseUserEntity, User> {
    @Override
    public ResponseUserEntity map(User user) {
        return new ResponseUserEntity(
                user.getFirstName() + " " + user.getLastName(),
                user.getAvatar()
        );
    }
}
