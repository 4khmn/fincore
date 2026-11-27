package com.fincore.identityservice.service;


import com.fincore.identityservice.dto.auth.LoginRequestDto;
import com.fincore.identityservice.dto.auth.LoginResponseDto;
import com.fincore.identityservice.dto.auth.RegisterRequestDto;
import com.fincore.identityservice.dto.auth.RegisterResponseDto;
import com.fincore.identityservice.entity.Profile;
import com.fincore.identityservice.entity.User;
import com.fincore.identityservice.entity.enums.Role;
import com.fincore.identityservice.exception.AlreadyExistsException;
import com.fincore.identityservice.mapper.AuthenticationMapper;
import com.fincore.identityservice.mapper.UserProfileMapper;
import com.fincore.identityservice.repository.UserRepository;
import com.fincore.identityservice.security.JwtService;
import com.fincore.identityservice.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticationMapper authenticationMapper;


    @InjectMocks
    private AuthenticationServiceImpl authService;


    @Test
    void register_Success() {
        RegisterRequestDto requestDto = new RegisterRequestDto(
                "username",
                "password",
                "firstName",
                "lastName"
        );
        User user = new User();
        user.setRole(Role.USER);
        user.setUsername("username");

        Profile profile = new Profile();
        profile.setFirstName("firstName");
        profile.setLastName("lastName");

        RegisterResponseDto expected = new RegisterResponseDto(
                UUID.randomUUID(),
                "username"
        );

        given(userRepository.existsByUsername("username")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(authenticationMapper.toUser(requestDto)).willReturn(user);

        given(passwordEncoder.encode(requestDto.password())).willReturn("hashHashHash");
        given(authenticationMapper.toProfile(requestDto)).willReturn(profile);
        given(authenticationMapper.toRegisterResponseDto(any(User.class))).willReturn(expected);

        RegisterResponseDto actual = authService.register(requestDto);

        assertNotNull(expected);
        assertEquals(expected, actual);

        verify(userRepository, times(1)).existsByUsername("username");
        verify(authenticationMapper, times(1)).toUser(requestDto);
        verify(authenticationMapper, times(1)).toProfile(requestDto);
    }

    @Test
    void register_whenUsernameAlreadyExists_shouldReturnAlreadyExistsException() {
        RegisterRequestDto requestDto = new RegisterRequestDto(
                "username",
                "password",
                "firstName",
                "lastName"
        );
        given(userRepository.existsByUsername(requestDto.username())).willReturn(true);

        assertThatThrownBy(() -> authService.register(requestDto))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("Username is already in use");
        verify(userRepository, times(1)).existsByUsername(requestDto.username());
        verifyNoInteractions(authenticationMapper);
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void login_success() {
        String username = "alice";
        String password = "secret-password";
        String expectedToken = "jwt.token.value";

        LoginRequestDto request = new LoginRequestDto(username, password);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);

        UserPrincipal principal = mock(UserPrincipal.class);

        try (MockedStatic<UserPrincipal> principalMock = mockStatic(UserPrincipal.class)) {
            principalMock.when(() -> UserPrincipal.create(user)).thenReturn(principal);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(jwtService.generateToken(principal)).thenReturn(expectedToken);

            LoginResponseDto response = authService.login(request);

            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo(expectedToken);

            InOrder inOrder = inOrder(authenticationManager, userRepository, jwtService);
            inOrder.verify(authenticationManager).authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            inOrder.verify(userRepository).findByUsername(username);
            inOrder.verify(jwtService).generateToken(principal);
            inOrder.verifyNoMoreInteractions();
        }
    }

    @Test
    void login_whenAuthenticationFails_shouldThrowAndNotQueryUserOrGenerateToken() {
        LoginRequestDto request = new LoginRequestDto("alice", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("alice", "wrong-password")
        );
        verifyNoInteractions(userRepository, jwtService);
    }

    @Test
    void login_whenUserNotFoundAfterAuthentication_shouldThrowIllegalArgumentException() {
        String username = "ghost-user";
        LoginRequestDto request = new LoginRequestDto(username, "secret-password");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, "secret-password")
        );
        verify(userRepository).findByUsername(username);
        verify(jwtService, never()).generateToken(any());
    }
}



