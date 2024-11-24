package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserDao userDao;
    @Resource
    private UserMapper usermapper;
    public List<User> userinfor(){
        return usermapper.userinfor();
    }

    public void save(User user) {
        String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        user.setCreateTime(now);
        userDao.save(user);
    }

    public void delete(Long id) {
        userDao.deleteById(id);
    }

    public User findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public Page<User> findPage(Integer pageNum, Integer pageSize, String name) {
        // 构建分页查询条件
        Sort sort = new Sort(Sort.Direction.DESC, "create_time");
        Pageable pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        return userDao.findByNameLike(name, pageRequest);
    }
    public void bookmarkContact(Long contactId, boolean bookmark) {
        User user = userDao.findById(contactId).orElse(null);
        if (user != null) {
            user.setBookmarked(bookmark);
            userDao.save(user);
        }
    }
}
