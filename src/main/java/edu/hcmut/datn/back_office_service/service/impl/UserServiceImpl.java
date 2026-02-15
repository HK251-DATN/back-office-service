package edu.hcmut.datn.back_office_service.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.hcmut.datn.back_office_service.dao.User;
import edu.hcmut.datn.back_office_service.exception.user.DuplicateUserException;
import edu.hcmut.datn.back_office_service.exception.user.UserNotFound;
import edu.hcmut.datn.back_office_service.repository.UserRepository;
import edu.hcmut.datn.back_office_service.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        if (userRepository.existsById(user.getUserId())) {
            throw new DuplicateUserException("Duplicate user");
        }

        return userRepository.save(user);
    }

    @Override
    public User read(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFound("User not found"));
    }

    @Override
    public List<User> readAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
		Page<User> userPage = userRepository.findAll(pageable);

        return userPage.toList();
    }

    @Override
    public User update(Long userId, User user) {
        User curUser = read(userId);

        if (!user.getFName().isEmpty()) {
            curUser.setFName(user.getFName());
        }

        if (!user.getLName().isEmpty()) {
            curUser.setLName(user.getLName());
        }

        if (!user.getAvtUrl().isEmpty()) {
            curUser.setAvtUrl(user.getAvtUrl());
        }

        if (user.getDob() != null) {
            curUser.setDob(user.getDob());
        }

        if (user.getPNum().isBlank()) {
            curUser.setPNum(user.getPNum());
        }

        if (user.getAccStatus() != null) {
            curUser.setAccStatus(user.getAccStatus());
        }

        return userRepository.save(curUser);
    }

    @Override
    public void delete(Long userId) {
        User curUser = read(userId);

        userRepository.delete(curUser);
    }
}
