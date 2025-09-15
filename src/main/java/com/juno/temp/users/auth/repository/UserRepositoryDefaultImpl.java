package com.juno.temp.users.auth.repository;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryDefaultImpl implements UserRepository{
    @Override
    public String find(String id) {
        return "%s@mail.com".formatted(id);
    }
}
