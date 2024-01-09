package it.unipi.gamecritic.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommentAPI {
    static Integer id = 69; // TODO: get the id of the new comment from the database
    public class NewCommentInfo {
        public Integer id;
        public String author;
        public NewCommentInfo(Integer id, String author) {
            this.id = id;
            this.author = author;
        }
    }

    @RequestMapping(value = "/api/comment/new", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String new_comment(
        @RequestParam(value = "review_id", required = true) Integer review_id,
        @RequestParam(value = "parent_id", required = false) Integer parent_id,
        @RequestParam(value = "quote", required = true) String quote,
        HttpServletRequest request,
        HttpSession session) {
            
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to comment");
        }
        else
        {
            // TODO: insert the comment in the database
            System.out.println("New comment for review " + review_id + " (in response to: "+parent_id+") from \"" + user.username + "\":\nquote: \"" + quote + "\"");

            NewCommentInfo info = new NewCommentInfo(id++, user.username);
            Gson gson = new Gson();
            return gson.toJson(info);
        }
    }
}
