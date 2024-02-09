package it.unipi.gamecritic.controllers.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.Comment;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.Comment.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommentAPI {
    private final CommentRepository commentRepository;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CommentAPI.class);

    public CommentAPI(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    public class NewCommentInfo {
        public String id;
        public String author;
        public NewCommentInfo(String id, String author) {
            this.id = id;
            this.author = author;
        }
    }

    @RequestMapping(value = "/api/comment/new", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String new_comment(
        @RequestParam(value = "review_id", required = true) String review_id,
        @RequestParam(value = "quote", required = true) String quote,
        HttpServletRequest request,
        HttpSession session) {
            
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to comment");
        }
        else
        {
            Comment comment = new Comment(review_id, user.username, quote, null);
            String comment_id = commentRepository.insertComment(comment);

            NewCommentInfo info = new NewCommentInfo(comment_id, user.username);
            Gson gson = new Gson();
            return gson.toJson(info);
        }
    }
}
