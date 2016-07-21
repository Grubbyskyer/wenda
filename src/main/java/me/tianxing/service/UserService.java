package me.tianxing.service;

import me.tianxing.dao.UserDAO;
import me.tianxing.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by TX on 2016/7/21.
 */
@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

}
