package com.juno.temp.users.auth.repository;

import org.springframework.stereotype.Repository;

//@Repository
public class UserRepositoryImpl implements UserRepository{
    @Override
    public String find(final String id) {
        return "1234_juno_uuid_%s".formatted(id);
    }
}
