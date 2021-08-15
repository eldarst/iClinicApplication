package kg.iclinic.application.service;

import kg.iclinic.application.entity.Account;

public interface AccountService {
    Account findAccount(String userName);
}
