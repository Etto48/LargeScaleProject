package it.unipi.gamecritic.controllers.api;

import java.util.Arrays;
import java.util.Vector;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class StatsAPI {
    public class Stat {
        public String name;
        public String type;    
    }

    public class TagStat extends Stat {
        public String img_url;
        public String value;

        public TagStat(String name, String img_url, String value) {
            this.name = name;
            this.type = "tag";
            this.img_url = img_url;
            this.value = value;
        }
    }

    public class GraphStat extends Stat {
        public Vector<Float> y;
        public Vector<String> x;

        public GraphStat(String name, Vector<Float> y, Vector<String> x) {
            this.name = name;
            this.type = "graph";
            this.y = y;
            this.x = x;
        }
    }

    @RequestMapping(value = "/api/stats", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String stats(
        HttpServletRequest request,
        HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Admin"))
            {
                // TODO: get stats
                Vector<Stat> stats = new Vector<Stat>();
                stats.add(new GraphStat("Reviews", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Likes", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new TagStat("Most popular platform", "https://www.bhphotovideo.com/images/images1500x1500/dell_00x6h_5480_aio_i7_10700t_8gb_1599860.jpg", "PC"));
                stats.add(new GraphStat("Comments", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Views", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new TagStat("Most popular developer", "https://i.imgur.com/RBmLFbS.png", "Valve"));
                stats.add(new GraphStat("Sales", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Revenue", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Refunds", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new TagStat("Most popular genre", null, "Action"));
                stats.add(new TagStat("Most popular publisher", "https://i.imgur.com/RBmLFbS.png", "Valve"));
                stats.add(new GraphStat("Refund Revenue", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Refund Rate", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));

                Gson gson = new Gson();
                return gson.toJson(stats);
            }
            else if (user.getAccountType().equals("Company"))
            {
                // TODO: get stats
                Vector<Stat> stats = new Vector<Stat>();
                stats.add(new GraphStat("Reviews", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Likes", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new TagStat("Most popular platform", "https://www.bhphotovideo.com/images/images1500x1500/dell_00x6h_5480_aio_i7_10700t_8gb_1599860.jpg", "PC"));
                stats.add(new GraphStat("Comments", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Views", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new TagStat("Most popular developer", "https://i.imgur.com/RBmLFbS.png", "Valve"));
                stats.add(new GraphStat("Sales", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Revenue", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Refunds", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new TagStat("Most popular genre", null, "Action"));
                stats.add(new TagStat("Most popular publisher", "https://i.imgur.com/RBmLFbS.png", "Valve"));
                stats.add(new GraphStat("Refund Revenue", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));
                stats.add(new GraphStat("Refund Rate", new Vector<Float>(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)), new Vector<String>(Arrays.asList("1", "2", "3", "4", "5"))));

                Gson gson = new Gson();
                return gson.toJson(stats);
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an admin nor a company");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in");
        }
    }
}
