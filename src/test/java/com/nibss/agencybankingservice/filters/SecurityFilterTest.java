/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.filters;

import javax.servlet.FilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author cmegafu
 */
public class SecurityFilterTest {
    MockHttpServletRequest request;
    MockHttpServletResponse response;
    FilterChain filterChain;
}
