package com.busoft.cognitoService.service;

import com.busoft.cognitoService.hash.SecretHash;
import com.busoft.cognitoService.vo.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.busoft.cognitoService.hash.SecretHash.calculateSecretHash;

@Service
public class CognitoService {
    @Autowired
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;
    @Value("${aws.userPoolId}")
    private String userPoolId;
    @Value("${aws.clientId}")
    private String clientId;
    @Value("${aws.clientSecret}")
    private String clientSecret;
    @Value("${aws.region}")
    private String region;

    public ResponseVO createUser(UserManagementVO userManagementVO) {
        try {
            List<AttributeType> attributeTypes = new ArrayList<>();
            userManagementVO.getAttributes().forEach(attribute-> attributeTypes.add(AttributeType.builder().name(attribute.getKey()).value(attribute.getValue()).build()));
            AdminCreateUserRequest.Builder adminCreateUserRequest = AdminCreateUserRequest
                    .builder()
                    .userPoolId(userPoolId)
                    .username(userManagementVO.getUserName())
                    .userAttributes(attributeTypes);
            if(!userManagementVO.isSendEmail())
                adminCreateUserRequest.messageAction("SUPPRESS");
            cognitoIdentityProviderClient.adminCreateUser(adminCreateUserRequest.build());
            ResponseVO responseVO = new ResponseVO();
            responseVO.setStatus("SUCCESS");
            return responseVO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ResponseVO GetUser(UserManagementVO userManagementVO) {
        try {
            AdminGetUserRequest adminGetUserRequest = AdminGetUserRequest
                    .builder()
                    .userPoolId(userPoolId)
                    .username(userManagementVO.getUserEmail())
                    .build();
            AdminGetUserResponse adminGetUserResponse = cognitoIdentityProviderClient.adminGetUser(adminGetUserRequest);
            ResponseVO responseVO = new ResponseVO();
            responseVO.setStatus("SUCCESS");
            return responseVO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseVO deleteUser(UserManagementVO userManagementVO) {
        try {
            AdminDeleteUserRequest deleteUserRequest = AdminDeleteUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(userManagementVO.getUserEmail())
                    .build();
            cognitoIdentityProviderClient.adminDeleteUser(deleteUserRequest);
            ResponseVO responseVO = new ResponseVO();
            responseVO.setStatus("SUCCESS");
            return responseVO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LoginResponseVO login(LoginRequestVO loginRequestVO) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", loginRequestVO.getUserEmail());
        authParams.put("PASSWORD", loginRequestVO.getPassword());
        authParams.put("SECRET_HASH", calculateSecretHash(loginRequestVO.getUserEmail(), clientId, clientSecret));

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .build();

        AdminInitiateAuthResponse authResponse = cognitoIdentityProviderClient.adminInitiateAuth(authRequest);
        LoginResponseVO loginResponseVO = new LoginResponseVO();
        CognitoAuthResponseVO responseVO = new CognitoAuthResponseVO();
        if (authResponse.challengeName() != null) {
            responseVO.setSuccess(false);
            responseVO.setChallengeName(authResponse.challengeNameAsString());
            responseVO.setSession(authResponse.session());
            responseVO.setMessage("Challenge required: " + authResponse.challengeNameAsString());
        } else if (authResponse.authenticationResult() != null) {
            AuthenticationResultType result = authResponse.authenticationResult();
            responseVO.setSuccess(true);
            responseVO.setAccessToken(result.accessToken());
            responseVO.setIdToken(result.idToken());
            responseVO.setRefreshToken(result.refreshToken());
            responseVO.setMessage("Login successful");
        } else {
            responseVO.setSuccess(false);
            responseVO.setMessage("Unexpected response from Cognito");
        }
        loginResponseVO.setCognitoAuthResponseVO(responseVO);
        return loginResponseVO;
    }

    public CognitoAuthResponseVO challenge(CognitoAuthRequestVO cognitoAuthRequestVO) throws Exception {
        Map<String, String> challengeResponses = new HashMap<>();
        String username = cognitoAuthRequestVO.getUserEmail();

        challengeResponses.put("USERNAME", username);
        challengeResponses.put("SECRET_HASH", calculateSecretHash(username, clientId, clientSecret));

        ChallengeNameType challengeType = ChallengeNameType.fromValue(cognitoAuthRequestVO.getChallengeName());

        switch (challengeType) {
            case NEW_PASSWORD_REQUIRED:
                challengeResponses.put("NEW_PASSWORD", cognitoAuthRequestVO.getAnswer());
                break;
            case SMS_MFA:
                challengeResponses.put("SMS_MFA_CODE", cognitoAuthRequestVO.getAnswer());
                break;
            case SOFTWARE_TOKEN_MFA:
                challengeResponses.put("SOFTWARE_TOKEN_MFA_CODE", cognitoAuthRequestVO.getAnswer());
                break;
            case SELECT_MFA_TYPE:
                challengeResponses.put("ANSWER", cognitoAuthRequestVO.getAnswer());
                break;
        }

        AdminRespondToAuthChallengeRequest challengeRequest = AdminRespondToAuthChallengeRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .challengeName(challengeType)
                .challengeResponses(challengeResponses)
                .session(cognitoAuthRequestVO.getSession())
                .build();

        try {
            AdminRespondToAuthChallengeResponse challengeResponse =
                    cognitoIdentityProviderClient.adminRespondToAuthChallenge(challengeRequest);

            if (challengeResponse.authenticationResult() != null) {
                AuthenticationResultType result = challengeResponse.authenticationResult();
                return new CognitoAuthResponseVO(true, "Login successful", result.accessToken(), result.idToken(), result.refreshToken());
            } else {
                return new CognitoAuthResponseVO(false, "Next challenge required: " + challengeResponse.challengeNameAsString(), null, null, null,
                        challengeResponse.challengeNameAsString(), challengeResponse.session());
            }

        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResponseVO forgetPassword(UserManagementVO userManagementVO) {
        try {
            ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                    .clientId(clientId)
                    .username(userManagementVO.getUserEmail())
                    .secretHash(calculateSecretHash(userManagementVO.getUserEmail(), clientId, clientSecret))
                    .build();
            ForgotPasswordResponse response = cognitoIdentityProviderClient.forgotPassword(request);
            ResponseVO responseVO = new ResponseVO();
            responseVO.setStatus("SUCCESS");
            return responseVO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseVO confirmCodeForForgetPassword(UserManagementVO userManagementVO) {
        try {
            ConfirmForgotPasswordRequest confirmRequest = ConfirmForgotPasswordRequest.builder()
                    .clientId(clientId)
                    .username(userManagementVO.getUserEmail())
                    .confirmationCode(userManagementVO.getCode())
                    .password(userManagementVO.getNewPassword())
                    .secretHash(calculateSecretHash(userManagementVO.getUserEmail(), clientId, clientSecret))
                    .build();
            ConfirmForgotPasswordResponse confirmResponse = cognitoIdentityProviderClient.confirmForgotPassword(confirmRequest);
            ResponseVO responseVO = new ResponseVO();
            responseVO.setStatus("SUCCESS");
            return responseVO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseVO getSecretCodeForTOPT(UserManagementVO userManagementVO, String accessToken) {
        try {
            AssociateSoftwareTokenRequest request = AssociateSoftwareTokenRequest.builder()
                    .accessToken(accessToken)
                    .build();
            AssociateSoftwareTokenResponse response = cognitoIdentityProviderClient.associateSoftwareToken(request);
            String secret = response.secretCode();
            ResponseVO responseVO = new ResponseVO();
            responseVO.setStatus("SUCCESS");
            responseVO.setSecretCode(secret);
            return responseVO;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseVO confirmTOPT(UserManagementVO userManagementVO, String accessToken) {
        try {
            ResponseVO responseVO = new ResponseVO();
            ;
            VerifySoftwareTokenRequest verifyRequest = VerifySoftwareTokenRequest.builder()
                    .accessToken(accessToken)
                    .userCode(userManagementVO.getCode())
                    .build();

            VerifySoftwareTokenResponse verifyResponse = cognitoIdentityProviderClient.verifySoftwareToken(verifyRequest);
            if (verifyResponse.statusAsString().equalsIgnoreCase("SUCCESS")) {
                SetUserMfaPreferenceRequest mfaRequest = SetUserMfaPreferenceRequest.builder()
                        .softwareTokenMfaSettings(
                                SoftwareTokenMfaSettingsType.builder()
                                        .enabled(true)
                                        .preferredMfa(true)
                                        .build()
                        )
                        .accessToken(accessToken)
                        .build();
                cognitoIdentityProviderClient.setUserMFAPreference(mfaRequest);
                responseVO.setStatus("SUCCESS");
            }
            return responseVO;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public CognitoAuthResponseVO getAccessToken(UserManagementVO userManagementVO,String refreshToken) {
        try {
            Map<String, String> authParams = new HashMap<>();
            authParams.put("REFRESH_TOKEN", refreshToken);
            authParams.put("SECRET_HASH", SecretHash.calculateSecretHash(userManagementVO.getUserEmail(), clientId, clientSecret));

            InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .clientId(clientId)
                    .authParameters(authParams)
                    .build();

            InitiateAuthResponse response = cognitoIdentityProviderClient.initiateAuth(initiateAuthRequest);
            String newAccessToken = response.authenticationResult().accessToken();
            String newRefreshToken = response.authenticationResult().refreshToken();
            String newTokenId = response.authenticationResult().idToken();
            return new CognitoAuthResponseVO(true, "Refresh Token", newAccessToken, newTokenId, newRefreshToken);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public CognitoAuthResponseVO validateToken(String accessToken) throws MalformedURLException, BadJOSEException, ParseException, JOSEException {
        try {
            CognitoAuthResponseVO cognitoAuthResponseVO = new CognitoAuthResponseVO();
            URL jwkUrl = new URL("https://cognito-idp." + region + ".amazonaws.com/" + userPoolId + "/.well-known/jwks.json");
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(jwkUrl);
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
            jwtProcessor.setJWSKeySelector(keySelector);

            SecurityContext ctx = null;
            JWTClaimsSet claimsSet = jwtProcessor.process(accessToken, ctx);
            if (!"access".equals(claimsSet.getStringClaim("token_use"))) {
                cognitoAuthResponseVO.setMessage("Give AccessToken");
                return cognitoAuthResponseVO;
            }
            if (!clientId.equals(claimsSet.getStringClaim("client_id"))) {
                cognitoAuthResponseVO.setMessage("Wrong ClientId");
                return cognitoAuthResponseVO;
            }
            cognitoAuthResponseVO.setMessage("VALID");
            return cognitoAuthResponseVO;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public AllUserVO getAllUser(FilterVO filterVO) {
        try {
            AllUserVO allUserVO = new AllUserVO();
            ListUsersResponse response = listUsers(filterVO.getLimit(), filterVO.getPaginationToken());
            allUserVO.setUserVOS(response.users().stream().filter(UserType::enabled).map(this::convertUserVO).toList());
            allUserVO.setPaginationToken(response.paginationToken());
            return allUserVO;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    public ListUsersResponse listUsers(int limit, String paginationToken) {
        ListUsersRequest.Builder requestBuilder = ListUsersRequest.builder()
                .userPoolId(userPoolId)
                .limit(limit);

        if (paginationToken != null && !paginationToken.isEmpty()) {
            requestBuilder.paginationToken(paginationToken);
        }

        ListUsersRequest request = requestBuilder.build();
        return cognitoIdentityProviderClient.listUsers(request);
    }

    private UserVO convertUserVO(UserType userType){
        UserVO userVO = new UserVO();
        userVO.setUserId(getUserAttribute(userType.attributes(),"sub"));
        userVO.setUserName(userType.username());
        userVO.setUserStatus(userType.userStatusAsString());
        userVO.setUserCreatedDate(userType.userCreateDate().toString());
        userVO.setUserUpdatedDate(userType.userLastModifiedDate().toString());
        userVO.setUserEmail(getUserAttribute(userType.attributes(),"email"));
        AdminGetUserResponse userResponse = cognitoIdentityProviderClient.adminGetUser(AdminGetUserRequest.builder()
                .userPoolId(userPoolId)
                .username(userType.username())
                .build());
        if (userResponse.userMFASettingList() != null && !userResponse.userMFASettingList().isEmpty()) {
            userVO.setUserMFAStatus("ENABLED");
            userVO.setUserMFAOptions(userResponse.userMFASettingList());
        }else
            userVO.setUserMFAStatus("DISABLED");

        return userVO;
    }

    private String getUserAttribute(List<AttributeType> attributeTypes,String attribute){
        return attributeTypes.stream().filter(attributeType -> attributeType.name().equals(attribute)).map(AttributeType::value).findFirst().orElse(null);
    }

    public ResponseVO updateUser(UserManagementVO userManagementVO) {
        try {
            List<AttributeType> attributeTypes = new ArrayList<>();
            userManagementVO.getAttributes().forEach(attribute -> attributeTypes.add(AttributeType.builder().name(attribute.getKey()).value(attribute.getValue()).build()));
            AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest = AdminUpdateUserAttributesRequest.builder()
                    .userPoolId(userPoolId)
                    .username(userManagementVO.getUserName())
                    .userAttributes(attributeTypes)
                    .build();
            cognitoIdentityProviderClient.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);
            ResponseVO responseVO = new ResponseVO();
            responseVO.setStatus("SUCCESS");
            return responseVO;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseVO mfaToggle(UserManagementVO userManagementVO) {
        try{
            AdminSetUserMfaPreferenceRequest.Builder builder = AdminSetUserMfaPreferenceRequest.builder()
                    .userPoolId(userPoolId)
                    .username(userManagementVO.getUserEmail());
            if(userManagementVO.getMfaType().equalsIgnoreCase("email"))
                builder.emailMfaSettings(EmailMfaSettingsType.builder().enabled(userManagementVO.isMfaToggle()).preferredMfa(userManagementVO.isMfaToggle()).build());
            if(userManagementVO.getMfaType().equalsIgnoreCase("sms"))
                builder.smsMfaSettings(SMSMfaSettingsType.builder().enabled(userManagementVO.isMfaToggle()).preferredMfa(userManagementVO.isMfaToggle()).build());
            if(userManagementVO.getMfaType().equalsIgnoreCase("totp"))
                builder.softwareTokenMfaSettings(SoftwareTokenMfaSettingsType.builder().enabled(userManagementVO.isMfaToggle()).preferredMfa(userManagementVO.isMfaToggle()).build());
            cognitoIdentityProviderClient.adminSetUserMFAPreference(builder.build());
            ResponseVO responseVO = new ResponseVO();
            responseVO.setStatus("SUCCESS");
            return responseVO;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
