package com.shifter.shifter_back.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "is_expired")
    private boolean isExpired = false;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "is_locked")
    private boolean isLocked = false;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "is_credential_expired")
    private boolean isCredentialExpired = false;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
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



