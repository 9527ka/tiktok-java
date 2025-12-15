package project.mall.activity.service;


import project.mall.activity.model.ActivityTemplate;

import java.util.List;

public interface ActivityTemplateService {

    List<ActivityTemplate> listAllValidActivityType();

    ActivityTemplate getById(String id);


}
