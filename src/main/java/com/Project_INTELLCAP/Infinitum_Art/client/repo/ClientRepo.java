package com.Project_INTELLCAP.Infinitum_Art.client.repo;

import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepo extends JpaRepository<Client,Long> {
}
