package com.tgshelterbot.service;

import com.tgshelterbot.model.dto.UserDtoCrud;
import com.tgshelterbot.model.dto.UserDtoCrudSerialized;

import java.util.Collection;

public interface UserCRUDService {

    UserDtoCrudSerialized getUser(Long id);

    Collection<UserDtoCrudSerialized> getAllUsers();

    UserDtoCrudSerialized updateUser(UserDtoCrud userDtoCrud);

    UserDtoCrudSerialized deleteUser(Long id);
}
