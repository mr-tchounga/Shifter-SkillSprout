package com.shifter.shifter_back.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
//    @Column
//    private String firstname;
//    @Column
//    private String lastname;
    @Column
    private String name;
    @Column(nullable = false)
    private String email;
    @Column
    private String phone;
    @Column(nullable = false)
    private String password;
    @Column
    private String picture;
//    @Column
//    private String gamification;
    @Column
    private String score;
    @Column
    private String scored;

    @Column(name = "is_expired")
    private boolean isExpired = false;
    @Column(name = "is_locked")
    private boolean isLocked = false;
    @Column(name = "is_credential_expired")
    private boolean isCredentialExpired = false;
    @Column(name = "is_enabled")
    private boolean isEnabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.isCredentialExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
