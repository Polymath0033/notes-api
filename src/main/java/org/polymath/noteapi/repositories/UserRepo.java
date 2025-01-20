package org.polymath.noteapi.repositories;

import org.polymath.noteapi.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<Users, UUID>  {
  Users findUserByEmail(String email);
  boolean existsByEmail(String email);
  boolean existsByUsername(String username);
  Users findUserById(UUID id);
}
