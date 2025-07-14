package com.example.biblio.repository;

import com.example.biblio.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUserNameAndMotDePasse(String userName, String motDePasse);
    Users findByUserName(String userName);
}
