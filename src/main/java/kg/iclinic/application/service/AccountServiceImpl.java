package kg.iclinic.application.service;

import kg.iclinic.application.dao.AccountRepository;
import kg.iclinic.application.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService{

    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account findAccount(String userName) {
        return accountRepository.getById(userName);
    }

}
