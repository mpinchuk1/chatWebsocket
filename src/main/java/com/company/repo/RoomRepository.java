package com.company.repo;

import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT r FROM ChatRoom r where r.name = :name")
    ChatRoom findByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false " +
            "END FROM ChatRoom r WHERE r.name = :name")
    boolean existsByName(@Param("name") String name);


}
