package com.datvexemphim;

import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Fix enabled=null for existing users (caused by new enabled column)
        long fixed = userRepository.fixEnabledForNullUsers();
        if (fixed > 0) {
            log.info("Fixed {} users with enabled=null", fixed);
        }
    }
}
