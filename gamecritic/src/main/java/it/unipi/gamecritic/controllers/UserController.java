//UserController

package it.unipi.gamecritic.controllers;

import java.util.List;
import java.util.Vector;

import it.unipi.gamecritic.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    private final UserRepository userRepository;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
    public String user(
            @PathVariable(value="username") String username,
            Model model,
            HttpServletRequest request,
            HttpSession session)
    {
        User u = (User) session.getAttribute("user");
        model.addAttribute("request", request);
        model.addAttribute("user", u);

        // get user from database
        List<User> users = userRepository.findByDynamicAttribute("username", username);
        for (User user : users) {
            if (user.username.equals(username)) {
                model.addAttribute("viewed_user", user);

                Float avg_top_score = 0f;
                if (user.top_reviews != null)
                {
                    for (Review review : user.top_reviews) {
                        avg_top_score += review.score;
                    }
                    avg_top_score /= user.top_reviews.size();
                    model.addAttribute("avg_top_score", avg_top_score);
                }

                model.addAttribute("user", user);
                return "user";
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @RequestMapping(value = "/user/{username}/reviews", method = RequestMethod.GET)
    public String user_reviews(
            @PathVariable(value="username") String username,
            Model model,
            HttpServletRequest request,
            HttpSession session)
    {
        model.addAttribute("request", request);
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("viewed_user_username", username);

        // TODO: get reviews from database
        Vector<Review> reviews = new Vector<Review>()
        {
            {
                add(new Review()
                {
                    {
                        id = 1;
                        game_id = 5;
                        game = "Super Mario Bros";
                        date = "2021-01-01";
                        score = 7;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 2;
                        game_id = 6;
                        game = "Super Mario Bros 2";
                        date = "2021-01-01";
                        score = 9;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 3;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 8;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 4;
                        game_id = 5;
                        game = "Super Mario Bros";
                        date = "2021-01-01";
                        score = 5;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 5;
                        game_id = 6;
                        game = "Super Mario Bros 2";
                        date = "2021-01-01";
                        score = 2;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 6;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 10;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 7;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 10;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 8;
                        game_id = 5;
                        game = "Super Mario Bros";
                        date = "2021-01-01";
                        score = 1;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 9;
                        game_id = 6;
                        game = "Super Mario Bros 2";
                        date = "2021-01-01";
                        score = 6;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 10;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 2;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 11;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 2;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
            }
        };
        model.addAttribute("reviews", reviews);
        Float avg_score = 0f;
        Vector<Float> distribution = new Vector<Float>();
        for (int i = 0; i < 10; i++) {
            distribution.add(0f);
        }
        for (Review review : reviews) {
            avg_score += review.score;
            distribution.set(review.score - 1, distribution.get(review.score - 1) + 1);
        }
        avg_score /= reviews.size();
        for (int i = 0; i < 10; i++) {
            distribution.set(i, distribution.get(i) / reviews.size() * 100);
        }
        model.addAttribute("avg_score", avg_score);
        model.addAttribute("score_distribution", distribution);
        return "user_reviews";
    }

    @RequestMapping(value = "/user/{username}/image.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] user_image(@PathVariable(value="username") String username) {
        // TODO: get user image from database
        Vector<String> example = new Vector<String>()
        {
            {
                add("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAAElUlEQVR4nO3dMY5TSRhGUTyyIAGxAFgJe4EYqbNeCBGIiVnMrIQNICIizw7QfNLUyD33nPi1XW37qpK/3rvcbrdniy9vP0zXP/54N13/6+fH6frVuv6H798OreS/cW//74tXf07Xf3r913T9uv4/pqvhf0YApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEi7fH7zfvqDe5uPX+fLV6fn0Venz2OsTp/fWK2fjx2ANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIO26zos/jG9w+n7wv35u8/frvPhTd/o8w709L2J9fTsAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQNr8fIDVU3+ewDpPf/p+/KfX89Tv97+yA5AmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkzecB1vnye7s//b2dT7g3pz/P088TWH9vdgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmAtMvtdjv6Buv9+Nf706+vv7Ke3zu9ntPPK7ADkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKRdT89nr/drf/FquvzunJ5fvzfr72f9PTx7dva8gR2ANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIO3y/OXXo29wej7+y9sP0/WPP95N16/z6w/fv03Xr2r/7+nnG9gBSBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANKu67z+6fns0/Pu+/mE7fp1/at1/v5hfP35+Qzj/3v6/MD6/doBSBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANKu+/z6Nn9/er5/df68wX3dL3+93//6/c7f15393uwApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGmX5y+/Tn+wzpefnu/f59036/3s7+35AKfXc2/f77oeOwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRA2nWdt17n0R/H+9mfdnqefn391en1nD4/sJo/T88HgH9OAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIu673U38Y3+De7t9/2ovD5x8+vT768sfn73cfp6s9HwAGAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRA2uV2u01/sM67n37+wPn5+7PnGU5b5+NXv35u8/rr8wfW9a/rsQOQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApF0+v3l/9A3W+f7T8+Knzyes61/5PP9ddgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmAtOs6n33aup7Hw88H4PdO/35Ov74dgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiDtbybO81YSs0muAAAAAElFTkSuQmCC");
                add("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAAEiUlEQVR4nO3dsXHbWBRAUXKHmQP24kiJGpFL8KgB5VYBtlyCGrETJdpeHDjWBlvBC94MOPec+BMEQN75ycfH+dv3X6eJp8f70fipn1/+HY3/+vp5NP75x+/R+O3r3bZ9vdu/19T0ev9ZOg+4CQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQNrl+vZp9IHn02y99fT426bn8/PtWOvdp+vvr6fh/X+cDZ+anv+fu7+j8dPf1wxAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkHb++PhY/YLpfu1Tt75//6279d/XDECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQdn55eF/9gun+7tP139P95qf790+PfzTb1zs9/vT5ge33S5gBSBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIu0w9sr/8+3Hr002w9+vT5h6np+vjp9T697j6PcbqbDd9+nsEMQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZB2Wd8vf3n997ajvU9g+/mHqVt/PsQMQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZA2fj/A9v734/3sH2f72U/31z89zoYfzfb1bj9vsP1/MwOQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApJ1fHt5HH9jeL3/7/QDb+/dvr18fr+8fOtr93/6/mQFIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0i5HW99/tOcHpsff3i9/+35O3fr/wQxAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkHbZXi9+NNP9+6f35+n1fjR+ano+2+8rOBrvB4ABAZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIg7fzt+6/VL7i+fRqNv/X169Prnardn+3rNQOQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApF22v+Dr6+fR+O33FWyfz/b69el6+lu//88/fi+dyf/MAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBp4+cBnh7vR+On68u3169P15dfT7v7/W/bvt7t3+vpdfZ/m16vGYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIg7fzy8L76BdP14tu297+/dbXfywxAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkDZ+HuDP3d/R+OvbbL/56fGnts9n+v6EqfF+/7H7Pz2+GYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgbf39ANvrubcdbb/8qaO9D+Fo/wczAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBEDaf8bg64Acu6yTAAAAAElFTkSuQmCC");
                add("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAAEfUlEQVR4nO3dIW4dVxiGYbu6m+gGGmbQTYQ0xCiFXVlgUExCmk1UillWkGW4oKik6if1qGO9z4PH47lz76sh/zlz/+nr+7vF48PH6fjT3n15Mx3/+e23Q1fyl6fnX4+e//T9v9r9XK33/4dD1wGvggBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkHb/8vIy/cE6b/3h+x/T8VebL+e/ta43+O3Hn6fj1/USngCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAafe//P7T/30Nf7POf6/rDVan59FXp9djrK72fa08AUgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSbusfrPPfq32efpuPr6l9X+t6A08A0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgLR5PcBqnc9+fNjOv86jn95f/8P3N9Pxp13t/Qbr/Ty9nsETgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBtXg9wtf3gTzs9j15zen3Cuh7DE4A0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIg7f7T1/dH/8G6H/xqnS8/fT2v3dXup/cDwEECIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBEDabZ3n/vz223T8ul/76vT7CvbzX8vT89nPe7Xv990X7weAf00ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEi7rX+wzluv6wfW+fWrOX39V1ufsO7ff3q+f+UJQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZA2rwdY579X6/sK1v3+1/Ov+9+v6x9Wp+fjT9/Px4fp8Pn3tl6PJwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRA2rwe4Gr29QP8k9r99AQgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASLut+9k/PW/7x592+vrX+fjz+/dv1v31Hx8+Tsdf7fOuvwdPANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYC02zrPvc5br+df59dX67z73d211j+s9s+7Wb+vq/3ePAFIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0u5fXl6O/oPT+8dfbX3C+j6B1enrOX0/V+v1rDwBSBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANJuT8/b/vfrfvOn9/s/vd/87uz7BNb7//iwnf9q73NYrb9nTwDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmAtPn9AKfn768337957fvlr17778ETgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiDtTx+qysPl/2BEAAAAAElFTkSuQmCC");
                add("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAAEgklEQVR4nO3dvW1cRxSAUa6xgUsQm1AHKoNsgM4EqRhDyuwG3IYroJtwCXS2DgylBm5w5Vl/58Rvwdm3/DDJ/Fzenl4fJn787f3o+b+e/xg9P/Xr77+Mnn/58NPSSP4xfT9T3ue/m76fH0ZPw/+MAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQdpnuB5g6bX389nju3Wnvc3v/gxmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIO06/cC93w/w8Lgzjm+2z8sff99lL8/uB4C7JQDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApF1ut9voA9P11tP16x///Hn0/NT2+ffb+x+2x7N93v/Xx8+j56f7K6bjNwOQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApF2n67NPc9r6/u319LXvu33/gxmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIO06PY9/e//A9vn6U9P16C/Ps/Psp8b3LTycdd/Ctun/sxmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIO24+wG2z7OfOm2/xHQ828b3AwzP75+avn8zAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBEDa5e3pdfUPTNeLn7Y/4eXD7Lz/8fr4oen+h+l9Atum+xm293uYAUgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSLl/efVr9A9P19FPT9e7b5+tvr1/f3m8w3Y9x2u87ZQYgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBvvB9g+L/+09ein3W9w2vn62/sxtu9DMAOQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApF2nH9g+n/609eIfH2bj2d6fMDX9vtPxb9+3MP5/e5w9bgYgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASLu8Pb2OPrC9vnxqezzb+x+2be+XuPff1wxAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkHadrreemq6n//r4efT8aefx37vp+xzftzC9T2B43v90PGYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgLTLl3efRh+Yrueenk8/tb4efWi6n2HqtPFv78fY3k9iBiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIu9xut/96DN/VdL349vr7bbXvO2UGIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEi7nnae/fQ+gen9AFOnjWf7PP7t+wG2z/ufMgOQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApP0NDfnseAEEE5EAAAAASUVORK5CYII=");
                add("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAAEZ0lEQVR4nO3dwW0USRSA4Z3VSASwIeATG8xKDoCNApMCOAsHgCUHw80pEMCees9ckB7o4bb+7zv3tGum/asuVdWX4zj+2PTv579X7z/1z7f/Rtff3j+Prn+8uxldP7U9nqe/3oyu3/bw8evq/f9cvTucnABIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkHbdXq+/vZ57Ov7pevrp/R+G95/aHs/T9P4ne75TZgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmAtMuXD29X/8D0vPnp+f3b95+a7jeY2n7/QO15mQFIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0i7vP70bfWC6Pnv9PP7h+fTT9fTb6/u3bX/fsz2v6X4DMwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRA2uU4jtEHpuu/t53tvP/t8/vPNp7p+vtt0/0GZgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmAtPH7Aaam67O3be9n2N6fsL3+vva8zACkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaeP3A0xN13OfbT399ni2ne37TsezvT/BDECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQdn28uxl94Pb+eWkoP3f/7fPjt8/jP5vt9fpPy89r+v9sBiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIG78fYPu8/+39BtP14q99P0Dt95/uTzADkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKRdt8/XP5vp+vLpevqzmX7f26Vx/C7T/2czAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBEDadbrefby+fPm8+bM52/n6ZzP9fZ6W30dhBiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIuxzH8dJj+M70fPft/QxTDx+/rt5/+30O27/n9u8zZQYgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBvvB9her79tez/A9vd97eOf2t5vYAYgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASLtOP7B9fvzZ7n97/zy6fvv8/ul698e7m9H1r/15TZkBSBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIu7z+9e+kxfGd7vfvZ1vdv83v+mBmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIO3y5cPblx7DL5meN79t+zz72vfdZgYgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASPsfotvkiaHVB18AAAAASUVORK5CYII=");
                add("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAAEkElEQVR4nO3dsZEbRxBA0YMKjIIW5UkxSCkgBzhIgQwBKUgOc7gUmMOZ9JgEaUCGZF+pVdU6oP579u5yuIdf48zMHr5//PD0yD4/vx9df3n5Mrr+z19/X33+1PZ4ps8/n76Nrr83P731AOAtCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaYfb7Ta64dHXx09N17u/u35dGsnffnz6eXT9dL/E1L39fafjMQOQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApB2319NvP5+3dW9/3+l4zACkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAacfpDdPz8rfPp5+OZ2o6/vPT7Pz+qfF47uz9TG3/3swApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGnj/QDvrl9H11+us+dvnze/vZ5++n6mpvsN7m3/wOXly+rzn559HwD+NQGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIO3w/eOH0Q3T9eXb679/fJqtj99er7+9n8H7fN10P4MZgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiDtcLvdRjdsr3ffPp9+up9hyvhftz3+6X4JMwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRA2nH7H5iuz56eTz+1PZ57W68/Pb//3t7/9v4TMwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRA2vp+gO315VPT9eWXl9l6+st1dPm66f/3fFoayD/mv4fd/RVmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYC0wx+//Da6Yft8+nuzfT79tul5/Pdm+/sMZgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmAtMPtdhvdsH3e//b+ge3xT9ejT033Y0w9+vufjt8MQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZD28N8HmK4vn45n+n2A7fX60/0G0+8DbL/PKd8HgEUCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBEDacbp+fXz+/fNsPf3U+TS7fn4+/e55/9u2z+Of7peYm73/6e/ZDECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQdpye735+2l1fPh3P9np3Xrf9/YGp6e/HDECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQdpye7355eez1+tP14pfr0kD+o+3xPPrfa/p7NgOQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApB3Pp29vPYb/1XS9+NT2+/z8/H71+efT6uPXTd+/GYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIg7S8smc7CcZUQwQAAAABJRU5ErkJggg==");
                add("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAAEcElEQVR4nO3dPW4UWRSA0W5kWAoRuQNI2YlnHWjWgVcCKQHkTmApTnp2gHSDK72e75y43F3l6k8veT/Xp7cfLxNPL19G1z9/+Hd0/dT0frY9vv+8+vm//nxf/fyp097v9H7ejK6G/xkBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiDt+vP3t9UvmM6PP22++2n3f9r9TJ12/0YA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgLSH6X7qX19/jK7/592n0fWnnT9wedn9+Knp/PjT9u+f/h62f29GANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYC09fMBpqbz16fz0bedNv9+272/LyMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQNrD9A+2zxN4fP08uv608wemzzvlef9u+rxGANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYC08XqAbdvzvx/fz9YbPF929/vfNn3ey8vs8un72l4/MGUEIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEi73m631S/Yni/+68/30fVT0/n00+edqv1/ttcPGAFIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0u7+fICp6XkCp81f3z4/4fnD7DyEez8/wQhAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkDZeD7A9H31quj/99n752+sZtp12/sD2egwjAGkCIE0ApAmANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBEDa9entx9EfbM8X357fP93/fmp7/cNp97/9vqafP10/YAQgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASLvebrfRH2zv137v6wemzzu1fT7Dvc/vdz4ADAiANAGQJgDSBECaAEgTAGkCIE0ApAmANAGQJgDSBECaAEgTAGkP0/nc26bz7x9fZ/PLp7b3+5867X5Oe1/T37MRgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBtfD7AaesHtvebn9qerz+dfz+1fZ7DtunvwQhAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkDY+H+C0+ehT0/n64/3vh/PRp54vs/vZXp9w2nqMy8vsciMAaQIgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQNr4fIDpfO7T9sufrjfwvH93789rBCBNAKQJgDQBkCYA0gRAmgBIEwBpAiBNAKQJgDQBkCYA0gRAmgBIu/78/W31C6b7x0/PK5iazi/fPk9gavt+TjtPYPv3YAQgTQCkCYA0AZAmANIEQJoASBMAaQIgTQCkCYA0AZAmANIEQJoASPsPOJb7l/UfDmcAAAAASUVORK5CYII=");
            }
        };
        Integer index = Math.floorMod(username.hashCode(), example.size());
        return java.util.Base64.getDecoder().decode(example.get(index));
    }

    @RequestMapping(value = "/user/{username}/followers", method = RequestMethod.GET)
    public String user_followers(
            @PathVariable(value = "username") String username,
            Model model,
            HttpServletRequest request,
            HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("request", request);
        model.addAttribute("viewed_user_username", username);

        // TODO: get followers from database
        Vector<User> followers = new Vector<User>();
        followers.add(new User("Pippo", null, null, null));
        followers.add(new User("Pluto", null, null, null));
        followers.add(new User("Paperino", null, null, null));

        model.addAttribute("follows", followers);
        model.addAttribute("mode", "followers");

        return "user_follow_list";
    }

    @RequestMapping(value = "/user/{username}/followed", method = RequestMethod.GET)
    public String user_followed(
            @PathVariable(value = "username") String username,
            Model model,
            HttpServletRequest request,
            HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("request", request);
        model.addAttribute("viewed_user_username", username);

        // TODO: get followed from database
        Vector<User> followed = new Vector<User>();
        followed.add(new User("Pippo", null, null, null));
        followed.add(new User("Pluto", null, null, null));
        followed.add(new User("Paperino", null, null, null));

        model.addAttribute("follows", followed);
        model.addAttribute("mode", "followed");

        return "user_follow_list";
    }
}
