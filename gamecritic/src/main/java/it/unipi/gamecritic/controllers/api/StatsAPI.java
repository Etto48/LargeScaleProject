package it.unipi.gamecritic.controllers.api;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.user.CompanyManager;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.Game.GameRepository;
import it.unipi.gamecritic.repositories.Game.GameRepositoryNeo4J;
import it.unipi.gamecritic.repositories.Game.DTO.TopGameDTO;
import it.unipi.gamecritic.repositories.User.UserRepository;
import it.unipi.gamecritic.repositories.User.UserRepositoryNeo4J;
import it.unipi.gamecritic.repositories.User.DTO.TopUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class StatsAPI {
    private final GameRepository gameRepository;
    @SuppressWarnings("unused")
    private final GameRepositoryNeo4J gameRepositoryNeo4J;
    private final UserRepository userRepository;
    private final UserRepositoryNeo4J userRepositoryNeo4J;

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(StatsAPI.class);

    @Autowired
    public StatsAPI(
        GameRepository gameRepository, 
        GameRepositoryNeo4J gameRepositoryNeo4J, 
        UserRepository userRepository, 
        UserRepositoryNeo4J userRepositoryNeo4J) 
    {
        this.gameRepository = gameRepository;
        this.gameRepositoryNeo4J = gameRepositoryNeo4J;
        this.userRepository = userRepository;
        this.userRepositoryNeo4J = userRepositoryNeo4J;
    }

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
        public List<Float> y;
        public List<String> x;

        public GraphStat(String name, List<Float> y, List<String> x) {
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
                List<Stat> stats = new Vector<Stat>();
                
                List<Float> globalScoreDistribution = gameRepository.globalScoreDistribution();
                stats.add(new GraphStat("Global score distribution", globalScoreDistribution, Arrays.asList("1", "2", "3", "4","5", "6", "7", "8", "9", "10")));
                
                List<TopUserDTO> topUsersByLikes = userRepositoryNeo4J.topUsersByLikes();
                if(!topUsersByLikes.isEmpty())
                {
                    stats.add(new GraphStat("Top users by likes", topUsersByLikes.stream().map(x -> Float.valueOf(x.score)).toList(), topUsersByLikes.stream().map(x -> x.username).toList()));
                    UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
                    builder.pathSegment("user", topUsersByLikes.get(0).username, "image.png");
                    stats.add(new TagStat("Most liked user", builder.build().toUri().toString(), topUsersByLikes.get(0).username));
                }

                List<TopUserDTO> topUsersByReviews = userRepository.topUsersByReviews(6);
                if(!topUsersByReviews.isEmpty())
                {
                    stats.add(new GraphStat("Top users by reviews", topUsersByReviews.stream().map(x -> Float.valueOf(x.score)).toList(), topUsersByReviews.stream().map(x -> x.username).toList()));
                    UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
                    builder.pathSegment("user", topUsersByReviews.get(0).username, "image.png");
                    stats.add(new TagStat("Most active user", builder.build().toUri().toString(), topUsersByReviews.get(0).username));
                }

                Gson gson = new Gson();
                return gson.toJson(stats);
            }
            else if (user.getAccountType().equals("Company"))
            {
                String companyName = ((CompanyManager) user).company_name;

                List<Stat> stats = new Vector<Stat>();

                List<Float> companyScoreDistribution = gameRepository.companyScoreDistribution(companyName);
                stats.add(new GraphStat("Company score distribution", companyScoreDistribution, Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")));

                List<TopGameDTO> topGamesByAverageScore = gameRepository.topGamesByAverageScore(6, companyName, 10);
                if(!topGamesByAverageScore.isEmpty())
                {
                    stats.add(new GraphStat("Top games by average score", topGamesByAverageScore.stream().map(x -> x.avg_score).toList(), topGamesByAverageScore.stream().map(x -> x.name).toList()));
                    stats.add(new TagStat("Best game", topGamesByAverageScore.get(0).image, topGamesByAverageScore.get(0).name));
                }

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
