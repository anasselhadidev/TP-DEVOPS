package com.Project_INTELLCAP.Infinitum_Art.auth.repo;

import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.Token;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface TokenRepo extends CrudRepository<Token, Long> {

    public Token findByToken(String token);

    @Transactional
    @Modifying
    public void deleteByUser(Users user);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresAt < :now")
    int deleteByExpiryDateBefore(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.createdAt < :threshold AND t.user.active = false")
    int deleteByCreatedAtBeforeAndUser_ActiveFalse(@Param("threshold") LocalDateTime threshold);



}
