package it.unipi.gamecritic.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommentAPI {
    @RequestMapping(value = "/api/comment/new", method = RequestMethod.POST)
    @ResponseBody
    public String new_comment(
        @RequestParam(value = "review_id", required = true) Integer review_id,
        @RequestParam(value = "parent_id", required = false) Integer parent_id,
        @RequestParam(value = "quote", required = true) String quote,
        HttpServletRequest request,
        HttpSession session) {
            
        it.unipi.gamecritic.entities.User user = (it.unipi.gamecritic.entities.User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to comment");
        }
        else
        {
            // TODO: insert the comment in the database
            System.out.println("New comment for review " + review_id + " (in response to: "+parent_id+") from \"" + user.username + "\":\nquote: \"" + quote + "\"");

            return "success";
        }
    }
}
