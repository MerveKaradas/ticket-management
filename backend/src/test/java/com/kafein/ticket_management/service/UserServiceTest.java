// package com.kafein.ticket_management.service;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.mockStatic;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.verifyNoInteractions;

// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// import org.junit.jupiter.api.Disabled;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockedStatic;
// import org.mockito.MockedStatic.Verification;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
// import com.kafein.ticket_management.dto.request.RequestLoginDto;
// import com.kafein.ticket_management.exception.ResourceNotFoundException;
// import com.kafein.ticket_management.exception.UnauthorizedException;
// import com.kafein.ticket_management.exception.UserAlreadyExistsException;
// import com.kafein.ticket_management.mapper.UserMapper;
// import com.kafein.ticket_management.model.User;
// import com.kafein.ticket_management.model.enums.Role;
// import com.kafein.ticket_management.repository.UserRepository;
// import com.kafein.ticket_management.security.JwtUtil;

// @ExtendWith(MockitoExtension.class)
// public class UserServiceTest {

//     @Mock
//     private UserRepository userRepository;
//     @Mock
//     private TokenService refreshTokenService;
//     @Mock
//     private UserMapper userMapper;
//     @Mock
//     private PasswordEncoder passwordEncoder;
//     @Mock
//     private JwtUtil jwtUtil;

//     @InjectMocks
//     private UserService userService;

//     @Test
//     @DisplayName("Geçerli email girildiğinde başarılı bir şekilde kullanıcı yüklenmelidir")
//     void loadUserByUsername_WhenUserExist_ShouldReturnUser() {
//         String email = "m@karadas.com";
//         User user = User.builder().email(email).build();

//         given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

//         userService.loadUserByUsername(email);

//         verify(userRepository, times(1)).findByEmail(email);
//     }

//     @Test
//     @DisplayName("Geçersiz email girildiyse ResourceNotFoundException fırlatılmalıdır")
//     void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
//         String email = "m@karadas.com";

//         given(userRepository.findByEmail(email)).willReturn(Optional.empty());

//         assertThrows(ResourceNotFoundException.class, () -> {
//             userService.loadUserByUsername(email);
//         });

//     }

//     @Test
//     @DisplayName("Zaten geçerli bir kullanıcı varsa UserAlreadyExistsException fırlatılmalıdır")
//     void createUser_WhenEmailAlreadyExists_ShouldThrowException() {
//         RequestCreateUserDto requestCreateUserDto = new RequestCreateUserDto("kafein", "solutions", "kafein@gmail.com",
//                 "Password123!");

//         given(userRepository.existsByEmail(requestCreateUserDto.email())).willReturn(true);

//         assertThrows(UserAlreadyExistsException.class, () -> {
//             userService.createUser(requestCreateUserDto);
//         });

//         verify(userRepository, never()).save(any(User.class));
//         verifyNoInteractions(userMapper);

//     }

//     @Test
//     @DisplayName("Geçerli bilgiler girildiğinde başarılı bir şekilde kullanıcı oluşturulmalıdır")
//     void createUser_WithValidDetails_ShouldSucceed() {
//         RequestCreateUserDto requestCreateUserDto = new RequestCreateUserDto("kafein", "solutions", "kafein@gmail.com",
//                 "Password123!");

//         given(userRepository.existsByEmail(requestCreateUserDto.email())).willReturn(false);

//         userService.createUser(requestCreateUserDto);

//         verify(userRepository, times(1)).save(any(User.class));
//         verify(userMapper, times(1)).toDto(any(User.class));

//     }

//     @Test
//     @DisplayName("Geçerli bilgiler girildiğinde başarılı bir şekilde giriş yapılmaldır")
//     void login_WithValidDetails_ShouldSucceed() {
//         String email = "m@karadas.com";
//         String password = "Password123!";
//         UUID userId = UUID.randomUUID();
//         User user = User.builder().id(userId).email(email).password(password).build();
//         RequestLoginDto requestLoginDto = new RequestLoginDto(email, password);

//         given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
//         given(passwordEncoder.matches(requestLoginDto.password(), user.getPassword())).willReturn(true);

//         userService.login(requestLoginDto);
//     }

//     @Test
//     @DisplayName("Hatali şifre girildiğinde BadCredentialsException fırlatılmalıdır")
//     void login_WithWrongPassword_ShouldThrowException() {
//         String email = "m@karadas.com";
//         String password = "Password123!";
//         UUID userId = UUID.randomUUID();
//         User user = User.builder().id(userId).email(email).password(password).build();
//         RequestLoginDto requestLoginDto = new RequestLoginDto(email, "HataliSifre!1");

//         given(userRepository.findByEmail(requestLoginDto.email())).willReturn(Optional.of(user));
//         given(passwordEncoder.matches(requestLoginDto.password(), user.getPassword())).willReturn(false);

//         assertThrows(BadCredentialsException.class, () -> {
//             userService.login(requestLoginDto);
//         });

//     }

//     @Test
//     @DisplayName("Hatali mail adresi girildiğinde BadCredentialsException fırlatılmalıdır")
//     void login_WithWrongEmail_ShouldThrowException() {
//         String password = "Password123!";
//         RequestLoginDto requestLoginDto = new RequestLoginDto("HataliMail@gmail.com", password);

//         given(userRepository.findByEmail(requestLoginDto.email())).willReturn(Optional.empty());

//         assertThrows(BadCredentialsException.class, () -> {
//             userService.login(requestLoginDto);
//         });

//     }

//     @Test
//     void getUserById() {
//         UUID userId = UUID.randomUUID();
//         User user = User.builder().id(userId).build();
//         given(userRepository.findById(userId)).willReturn(Optional.of(user));

//         userService.getUserById(userId);
//     }

//     @Test
//     @DisplayName("Admin daha önce oluşturulmamışsa admin kullanıcısı oluşturulmalıdır")
//     void createAdminUser_WhenAdminDoesNotExists_ShouldSucceed() {
//         String email = "admin@kafein.com";

//         given(userRepository.existsByEmail(email)).willReturn(false);

//         userService.createAdminUser();

//         verify(userRepository, times(1)).save(any(User.class));

//     }

//     @Test
//     @DisplayName("Admin zaten oluşturulmuşsa bir şey yapılmamalıdır")
//     void createAdminUser_WhenAdminAlreadyExists_ShouldDoNothing() {
//         String email = "admin@kafein.com";

//         given(userRepository.existsByEmail(email)).willReturn(true);

//         userService.createAdminUser();

//         verify(userRepository, never()).save(any(User.class));

//     }

//     @Test
//     @DisplayName("Kullanıcı oturumu açıkken logoutAll yapıldığında tüm refresh tokenleri iptal edilmelidir")
//     void logoutAll_WhenUserAuthenticated_ShouldRevokeToken() {
//         // ARRANGE
//         User user = User.builder().email("merve@kafein.com").build();
//         Authentication auth = mock(Authentication.class);
//         SecurityContext securityContext = mock(SecurityContext.class);

//         // SecurityContextHolder'ı statik olarak mock'luyoruz
//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.getPrincipal()).willReturn(user);

//             // ACT
//             userService.logoutAll();

//             // ASSERT
//             verify(refreshTokenService, times(1)).revokeAllRefreshToken(user);
//         }
//     }

//     @Test
//     @DisplayName("Oturum açılmamışken logoutAll yapılmaya çalışıldığında UnauthorizedException fırlatılmalıdır")
//     void logoutAll_WhenNotAuthenticated_ShouldThrowUnauthorizedException() {
//         // ARRANGE
//         SecurityContext securityContext = mock(SecurityContext.class);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(null); // Auth yok

//             // ACT ve ASSERT
//             assertThrows(UnauthorizedException.class, () -> userService.logoutAll());
//             verifyNoInteractions(refreshTokenService);
//         }
//     }

//     @Test
//     @DisplayName("Kullanıcı oturumu açıkken logout yapıldığında mevcut refresh token iptal edilmelidir")
//     void logout_WhenUserAuthenticated_ShouldRevokeToken() {
//         // ARRANGE
//         String currentRefreshToken = "Myrefreshtoken";
//         User user = User.builder().email("merve@kafein.com").build();
//         Authentication auth = mock(Authentication.class);
//         SecurityContext securityContext = mock(SecurityContext.class);

//         // SecurityContextHolder'ı statik olarak mock'luyoruz
//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.getPrincipal()).willReturn(user);

//             // ACT
//             userService.logout(currentRefreshToken);

//             // ASSERT
//             verify(refreshTokenService, times(1)).revokeRefreshToken(currentRefreshToken);
//         }
//     }

//     @Test
//     @DisplayName("Oturum açılmamışken logout yapılmaya çalışıldığında UnauthorizedException fırlatılmalıdır")
//     void logout_WhenNotAuthenticated_ShouldThrowUnauthorizedException() {
//         // ARRANGE
//         String currentRefreshToken = "Myrefreshtoken";
//         SecurityContext securityContext = mock(SecurityContext.class);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(null); // Auth yok

//             // ACT ve ASSERT
//             assertThrows(UnauthorizedException.class, () -> userService.logout(currentRefreshToken));
//             verifyNoInteractions(refreshTokenService);
//         }
//     }

//     @Test
//     @DisplayName("Oturumu açık olan kullanıcıyı başarıyla döndürmelidir")
//     void getCurrentUser_WhenAuthenticated_ShouldReturnUser() {

//         // ARRANGE
//         User user = User.builder().email("merve@kafein.com").build();
//         SecurityContext securityContext = mock(SecurityContext.class);
//         Authentication auth = mock(Authentication.class);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when((Verification) SecurityContextHolder.getContext()).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.isAuthenticated()).willReturn(true);
//             given(auth.getPrincipal()).willReturn(user);
//             // ACT
//             User result = userService.getCurrentUser();

//             // ASSERT
//             assertEquals(user.getEmail(), result.getEmail());

//         }
//     }

//     @Test
//     @DisplayName("Oturum doğrulanmamışsa UnauthorizedException fırlatılmalıdır")
//     void getCurrentUser_WhenNotAuthenticated_ShouldThrowUnauthorizedException() {

//         // ARRANGE
//         SecurityContext securityContext = mock(SecurityContext.class);
//         Authentication auth = mock(Authentication.class);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when((Verification) SecurityContextHolder.getContext()).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.isAuthenticated()).willReturn(false);

//             // ACT ve ASSERT
//             assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());

//         }

//     }

//     @Test
//     @DisplayName("Kullanıcı anonimse UnauthorizedException fırlatılmalıdır")
//     void getCurrentUser_WhenPrincipalIsAnonymous_ShouldThrowUnauthorizedException() {

//         // ARRANGE
//         SecurityContext securityContext = mock(SecurityContext.class);
//         Authentication auth = mock(Authentication.class);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when((Verification) SecurityContextHolder.getContext()).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.isAuthenticated()).willReturn(true);
//             given(auth.getPrincipal()).willReturn("anonymousUser");

//             // ACT ve ASSERT
//             assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());
//         }
//     }

//     @Test
//     @DisplayName("USER rolüne sahip kullanıcı için false dönmelidir")
//     void isAdmin_WhenUserHasRoleUser_ShouldReturnFalse() {

//         // ARRANGE
//         User user = User.builder().role(Role.USER).email("merve@kafein.com").build();
//         SecurityContext securityContext = mock(SecurityContext.class);
//         Authentication auth = mock(Authentication.class);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when((Verification) SecurityContextHolder.getContext()).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.isAuthenticated()).willReturn(true);
//             given(auth.getPrincipal()).willReturn(user);

//             // ACT
//             boolean result = userService.isAdmin();

//             // ASSERT
//             assertEquals(false, result);

//         }

//     }

//     @Test
//     @DisplayName("ADMIN rolüne sahip kullanıcı için true dönmelidir")
//     void isAdmin_WhenUserHasRoleAdmin_ShouldReturnTrue() {

//         // ARRANGE
//         User user = User.builder().role(Role.ADMIN).email("merve@kafein.com").build();
//         SecurityContext securityContext = mock(SecurityContext.class);
//         Authentication auth = mock(Authentication.class);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when((Verification) SecurityContextHolder.getContext()).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.isAuthenticated()).willReturn(true);
//             given(auth.getPrincipal()).willReturn(user);

//             // ACT
//             boolean result = userService.isAdmin();

//             // ASSERT
//             assertEquals(true, result);

//         }

//     }

//     @Test
//     @DisplayName("Admin yetkisiyle tüm kullanıcıları listeleyebilmelidir")
//     void getAllUsers_WhenUserIsAdmin_ShouldReturnUserList() {

//         // ARRANGE
//         User user = User.builder().role(Role.ADMIN).email("merve@kafein.com").build();
//         SecurityContext securityContext = mock(SecurityContext.class);
//         Authentication auth = mock(Authentication.class);
//         List<User> list = List.of(user);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when((Verification) SecurityContextHolder.getContext()).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.isAuthenticated()).willReturn(true);
//             given(auth.getPrincipal()).willReturn(user);
//             given(userRepository.findAll()).willReturn(list);

//             // ACT
//             userService.getAllUsers();

//             // ASSERT
//             verify(userRepository, times(1)).findAll();

//         }

//     }

//     @Test
//     @DisplayName("Admin olmayan kullanıcı listeleme yapmak istediğinde AccessDeniedException fırlatılmalıdır")
//     void getAllUsers_WhenUserIsNotAdmin_ShouldThrowAccessDeniedException() {

//         // ARRANGE
//         User user = User.builder().role(Role.USER).email("merve@kafein.com").build();
//         SecurityContext securityContext = mock(SecurityContext.class);
//         Authentication auth = mock(Authentication.class);

//         try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
//             mockedSecurity.when((Verification) SecurityContextHolder.getContext()).thenReturn(securityContext);
//             given(securityContext.getAuthentication()).willReturn(auth);
//             given(auth.isAuthenticated()).willReturn(true);
//             given(auth.getPrincipal()).willReturn(user);

//             assertThrows(AccessDeniedException.class, () -> {
//                 userService.getAllUsers();
//             });

//             verify(userRepository, never()).findAll();
//         }

//     }

// }
