package com.bugtracker.repository;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.Comment;
import com.bugtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBug(Bug bug);

    List<Comment> findByAuthor(User author);

    List<Comment> findByBugOrderByCreatedAtDesc(Bug bug);

    long countByBug(Bug bug);
}