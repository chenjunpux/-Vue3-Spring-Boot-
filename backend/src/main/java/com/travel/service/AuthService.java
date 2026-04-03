package com.travel.service;

import com.travel.dto.LoginDTO;
import com.travel.dto.RegisterDTO;
import com.travel.dto.AuthResponse;
import com.travel.dto.UserVO;
import com.travel.entity.User;

public interface AuthService {
    AuthResponse login(LoginDTO dto);
    AuthResponse register(RegisterDTO dto);
    AuthResponse refreshToken(String refreshToken);
    UserVO getCurrentUser();
}
