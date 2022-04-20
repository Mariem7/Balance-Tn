package com.backendspringboot.employee;

import com.backendspringboot.constant.Authority;

public enum Role {
    ROLE_USER(Authority.USER_AUTHORITIES),
    ROLE_ADMIN(Authority.ADMIN_AUTHORITIES),
    ROLE_MANAGER(Authority.MANAGER_AUTHORITIES);

    private String[] authorities;
    Role(String... authorities){
        this.authorities = authorities;
    }

    public String[] getAuthorities(){
        return authorities;
    }
}
