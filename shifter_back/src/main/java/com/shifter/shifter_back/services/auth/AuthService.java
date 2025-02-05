package com.shifter.shifter_back.services.auth;

import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.models.auth.PasswordResetToken;
import com.shifter.shifter_back.payloads.requests.*;
import com.shifter.shifter_back.payloads.responses.JwtAuthenticationResponse;
import com.shifter.shifter_back.payloads.responses.UserMachineDetails;

public interface AuthService {
    public JwtAuthenticationResponse signUp(SignupRequest request, UserMachineDetails userMachineDetails);
//    public JwtAuthenticationResponse createUser(SignupRequest request, UserMachineDetails userMachineDetails);
    public JwtAuthenticationResponse signIn(SigninRequest request, UserMachineDetails userMachineDetails);
    public JwtAuthenticationResponse refreshToken(TokenRefreshRequest request);
    public void logout(User user);
    public PasswordResetToken forgotPassword(String email);
    public boolean isResetTokenValid(String token);
    public User resetPassword(ForgotPassword forgotPassword);
    public User updatePassword(PasswordReset passwordReset, User user);
    public User getCurrentUserFromToken(String token);
}
