package com.crc.farming.service;


import com.crc.farming.pojo.User;

import java.util.List;

public interface UserService {
    boolean insertUser(User user) ;

    User logincheck(String name, String password);

    boolean updateUserById(String name,String tel,String email,String address,Integer id);

    User findUserById(Integer id);

    boolean updateUserPassWordById(String password, Integer id);

    List<User> getAllUser();


    boolean deleteUser(Integer userid);
}
