package com.example.CloudAPI.Block;

import com.example.CloudAPI.Users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, BlockId> {
    @Query("SELECT b.blocked FROM Block b WHERE b.blocker.id = :blockerId")
    List<User> findBlockedUsersByBlockerId(Long blockerId);
}
