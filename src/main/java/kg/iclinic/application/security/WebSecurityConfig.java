package kg.iclinic.application.security;

import kg.iclinic.application.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        User.UserBuilder users = User.withDefaultPasswordEncoder();

        auth.inMemoryAuthentication()
                .withUser(users.username("eldar").password("test123").roles("MANAGER"));
        // Setting Service to find User in the database.
        // And Setting PassswordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // Requires login with role ROLE_EMPLOYEE or ROLE_MANAGER.
        // If not, it will redirect to /admin/login.
        http.authorizeRequests().antMatchers("/uzi/listTodayOrders", "/uzi/savePatient", "/uzi/deleteOrder", "/uzi/showFormForEditPatient",
                "/uzi/showFormForAddProduct", "/uzi/addProduct", "/uzi/listProducts", "/uzi/deleteProduct", "/uzi/showFormForEditProduct",
                "/cons/listDoctors", "/cons/showFormForEditDoctor", "/cons/saveDoctor", "/cons/deleteDoctor", "/cons/listTodayOrders",
                "/cons/showFormForEditConsultation", "/cons/saveConsultation", "/cons/deleteConsultation", "/trt/*")//
                .access("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER')");

        // Pages only for MANAGER
        http.authorizeRequests().antMatchers("/uzi/listOrderBetweenDates", "/uzi/showSortByDateForm",
                "/uzi/showDetailsOfOrderList", "/uzi/showDetailsOfOrderListMonthly", "/uzi/showSalariesBetweenDatesForm", "/uzi/listSalariesBetweenDates",
                "/uzi/listCurrentWeekSalary", "/cons/showPeriodStats")
                .access("hasRole('ROLE_MANAGER')");

        // When user login, role XX.
        // But access to the page requires the YY role,
        // An AccessDeniedException will be thrown.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

        // Configuration for Login Form.
        http.authorizeRequests().and()
                .formLogin()
                .loginPage("/showMyLoginPage")
                .defaultSuccessUrl("/uzi/listTodayOrders")
                .loginProcessingUrl("/authenticateTheUser")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/showMyLoginPage")
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied");

    }
}
