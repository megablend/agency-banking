package com.nibss.agencybankingservice.filters;


import com.nibss.agencybankingservice.exceptions.UnableToDecryptException;
import com.nibss.agencybankingservice.util.FilterUtils;
import static com.nibss.agencybankingservice.util.FilterUtils.doErrorResponse;
import static com.nibss.agencybankingservice.util.FilterUtils.isAllowedSignatureMethod;
import com.nibss.agencybankingservice.wrappers.CustomRequestWrapper;
import com.nibss.cryptography.Hasher;
import com.nibss.cryptography.IVAesHasher;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.upl.nibss.bvn.model.AgentManager;
import com.upl.nibss.bvn.model.security.InstitutionCredentials;
import com.upl.nibss.bvn.model.security.Institutions;
import com.upl.nibss.bvn.repo.InstitutionService;
import com.upl.nibss.bvn.service.AgentMgrService;
import com.upl.nibss.bvn.service.UserService;
import com.upl.nibss.bvn.util.PasswordUtil;
import java.util.StringTokenizer;

@Slf4j
public class SecurityFilter implements Filter {

    private final AgentMgrService agentManagerService;
    private final InstitutionService institutionService;
    public  final String salt;
    private static final String SIGNATURE = "SIGNATURE";
    private static final String SIGNATURE_METH = "SIGNATURE_METH";
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    

    public SecurityFilter(final AgentMgrService agentManagerService, final InstitutionService institutionService, final UserService userService, final String salt) {
        this.agentManagerService = agentManagerService;
        this.salt = salt;
        this.institutionService = institutionService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        
        // check for authorization property
        if( null == httpServletRequest.getHeader(AUTHORIZATION_PROPERTY)) {
            doErrorResponse(httpServletResponse, "Authorization  not found in request header", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // check for signature
        if (null == httpServletRequest.getHeader(SIGNATURE)) {
            doErrorResponse(httpServletResponse, "No Signature found in the header", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // check for signature method
        if (null == httpServletRequest.getHeader(SIGNATURE_METH)) {
            doErrorResponse(httpServletResponse, "No Signature method found in the header", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // check if the provided method is allowed
        if (!isAllowedSignatureMethod(httpServletRequest.getHeader(SIGNATURE_METH))) {
            doErrorResponse(httpServletResponse, "The signature method provided is not allowed", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // get usernam and password
        StringTokenizer tokenizer = FilterUtils.getUsernameAndPassword(httpServletRequest.getHeader(AUTHORIZATION_PROPERTY), AUTHENTICATION_SCHEME);
        if (null == tokenizer || tokenizer.countTokens() != 2) {
            doErrorResponse(httpServletResponse, "Invalid authentication token provided", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        String agentManagerCode = tokenizer.nextToken();
        String password = tokenizer.nextToken();
        
        if (null == agentManagerCode) {
            doErrorResponse(httpServletResponse, "No Agent Manager Code provided", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        if (null == password) {
            doErrorResponse(httpServletResponse, "No password provided", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // validate signature
        if (!FilterUtils.isSignatureValid(httpServletRequest.getHeader(SIGNATURE), agentManagerCode, password, httpServletRequest.getHeader(SIGNATURE_METH))) {
            doErrorResponse(httpServletResponse, "Invalid Signature", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // check if this agent manager exists
        AgentManager agentManager = agentManagerService.getByCode(agentManagerCode);
        if (null == agentManager) {
            doErrorResponse(httpServletResponse, "Invalid Agent Manager", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // validate institution
        Institutions institution = institutionService.getInstitutionByCode(agentManager.getAgentManagerInstitution().getCode());
        if (null == institution) {
            doErrorResponse(httpServletResponse, "This agent manager does not belong to a valid Institution", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        InstitutionCredentials credentials = institutionService.getCredentialsByInstitution(institution);
        if (null == credentials) {
            doErrorResponse(httpServletResponse, new StringBuilder("No credentials profiled for the institution ").append(institution.getCode()).toString(), "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // check if the password provided is correct
        log.trace("The institution code being used is {}", institution.getCode());
        log.trace("The institution IV being used is {}", credentials.getIvSpec());
        if (!PasswordUtil.hashPassword(password, institution.getCode(),credentials.getIvSpec()).equals(credentials.getPassword())){
            doErrorResponse(httpServletResponse, "Invalid institution password provided.", "401",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        servletRequest.setAttribute("agentManager", agentManager);
        servletRequest.setAttribute("institution", institution);
        Hasher hasher = new IVAesHasher(credentials.getAesKey(), credentials.getIvSpec());
        try {
            CustomRequestWrapper requestWrapper = new CustomRequestWrapper(httpServletRequest,hasher);
            filterChain.doFilter(requestWrapper, servletResponse);
        } catch (UnableToDecryptException  e) {
            log.error("could not decrypt agent manager request",e);
            doErrorResponse(httpServletResponse, "Your request could not be decrypted", "400", HttpServletResponse.SC_BAD_REQUEST);
        } catch(Exception e) {
            log.error("could not decrypt agent manager request",e);
            doErrorResponse(httpServletResponse, "Your request could not be decrypted", "400", HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    public void destroy() {

    }


}
