package com.unicorn.api;

import com.unicorn.common.service.TransactionIdService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.unicorn.common.service.TransactionIdService.TRANSACTION_ID;

/**
 * Initialize client provided bankAccountTransaction identifier or a unique bankAccountTransaction identifier for every
 * request to help on support/debugging/logging.
 */

@Configuration
public class TransactionIdConfigurer extends WebMvcConfigurerAdapter {


    @Autowired
    private TransactionIdService transactionIdService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TransactionIdInterceptor());
    }

    private class TransactionIdInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

            transactionIdService.setTransactionId(httpServletRequest.getHeader(TRANSACTION_ID));
            return true;
        }


        @Override
        public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        }

        @Override
        public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
            MDC.clear();
        }
    }
}