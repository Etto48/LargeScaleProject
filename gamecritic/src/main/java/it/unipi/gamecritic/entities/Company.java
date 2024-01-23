package it.unipi.gamecritic.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class Company {
    @Id
    public String _id;
    @Field("Name")
    public String name;
    @Field("imglink")
    public String imglink;
    @Field("Overview")
    public String Overview;
    public void set_id(String _id) {
        this._id = _id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setImglink(String imglink) {
        this.imglink = imglink;
    }
    public void setOverview(String overview) {
        Overview = overview;
    }
    public String get_id() {
        return _id;
    }
    public String getName() {
        return name;
    }
    public String getImglink() {
        return imglink;
    }
    public String getOverview() {
        return Overview;
    }
}
