package com.bugtracker.service;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.Comment;
import com.bugtracker.entity.User;
import com.bugtracker.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    // Oluşturma
    public Comment createComment(Comment comment) {
        // DOğrulama
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Yorum içeriği boş olamaz!");
        }

        if (comment.getBug() == null) {
            throw new IllegalArgumentException("Yorum bir bug'a ait olmalıdır!");
        }

        if (comment.getAuthor() == null) {
            throw new IllegalArgumentException("Yorum yazarı belirtilmelidir!");
        }

        return commentRepository.save(comment);
    }

    // Okuma
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByBug(Bug bug) {
        return commentRepository.findByBugOrderByCreatedAtDesc(bug);
    }

    public List<Comment> getCommentsByAuthor(User author) {
        return commentRepository.findByAuthor(author);
    }

    // Güncelleme
    public Comment updateComment(Long id, String newContent, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Yorum bulunamadı!"));

        // Sadece yorum sahibi veya admin güncelleyebilir
        if (!comment.getAuthor().equals(user) && !user.canDeleteBug()) {
            throw new IllegalArgumentException("Bu yorumu güncelleme yetkiniz yok!");
        }

        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Yorum içeriği boş olamaz!");
        }

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    // Silme
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Yorum bulunamadı!"));

        // Sadece yorum sahibi veya admin silebilir
        if (!comment.getAuthor().equals(user) && !user.canDeleteBug()) {
            throw new IllegalArgumentException("Bu yorumu silme yetkiniz yok!");
        }

        commentRepository.deleteById(id);
    }

    public long countCommentsByBug(Bug bug) {
        return commentRepository.countByBug(bug);
    }
}