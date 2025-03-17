package com.prolink.user.repository;

import com.prolink.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	//  Find user by username
    Optional<User> findByUsername(String username);

    //  Search users by full name (ignoring case)
    List<User> findByFullNameContainingIgnoreCase(String query);

    @Query("SELECT u FROM User u JOIN u.pendingRequests p WHERE u.username = :username")
    List<User> findPendingRequests(@Param("username") String username);


    //  Fetch connected users using username
    @Query("SELECT u FROM User u JOIN u.connections c WHERE c.username = :username")
    List<User> findConnections(@Param("username") String username);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
    	       "AND u.username <> :username " +
    	       "AND u NOT IN (SELECT c FROM User usr JOIN usr.connections c WHERE usr.username = :username)")
    List<User> searchUsersExcludingConnections(@Param("query") String query, @Param("username") String username);

}
