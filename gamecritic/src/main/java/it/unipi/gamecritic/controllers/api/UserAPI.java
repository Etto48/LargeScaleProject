package it.unipi.gamecritic.controllers.api;

import java.security.MessageDigest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserAPI {
    @RequestMapping(value = "/api/user/follow", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String user_follow(
        @RequestParam(value = "username", required = true) String username,
        @RequestParam(value = "follow", required = true) boolean follow,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            // TODO: follow user
            System.out.println("Follow user \"" + username + "\" ("+follow+") by " + user.username);
            return "{}";
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }

    @RequestMapping(value = "/api/user/edit", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String user_edit(
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "password", required = false) String password,
        @RequestParam(value = "image", required = false) MultipartFile image,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if (email != null)
            {
                // TODO: save email in the database
                System.out.println("Email changed: " + email);
            }
            if (password != null)
            {
                try {
                    byte[] password_hash = MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-8"));
                    String password_hash_b64 = java.util.Base64.getEncoder().encodeToString(password_hash);
                    // TODO: save password in the database
                    System.out.println("Password changed: " + password_hash_b64);
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing password");
                }
            }
            if (image != null)
            {
                String image_type = image.getContentType();
                if(image_type == null || !image_type.equals("image/png"))
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PNG images are supported");
                }
                if(image.getSize() > 1024*1024)
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image too large");
                }
                try {
                    byte[] image_bytes = image.getBytes();
                    String image_b64 = java.util.Base64.getEncoder().encodeToString(image_bytes);
                    // TODO: save image in the database    
                    System.out.println("Image uploaded: " + image.getBytes().length/1024f + "KB");
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing image");
                }
            }
            return "{}";
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }
}
