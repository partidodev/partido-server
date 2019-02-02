package net.fosforito.partido.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component(value = "permissionEvaluator")
public class PermissionEvaluatorImpl implements PermissionEvaluator {
  public PermissionEvaluatorImpl() {
  }

  /**
   * Determine if a user can view a given image.
   */
  public boolean userCanUpdateGroup(UserDetails user, Long groupId) {
    return false;
  }

  @Override
  public boolean hasPermission(
      Authentication auth, Object targetDomainObject, Object permission) {
    if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
      return false;
    }
    String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

    return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
  }

  @Override
  public boolean hasPermission(
      Authentication auth, Serializable targetId, String targetType, Object permission) {
    if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
      return false;
    }
    return hasPrivilege(auth, targetType.toUpperCase(),
        permission.toString().toUpperCase());
  }

  private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
    for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
      if (grantedAuth.getAuthority().startsWith(targetType)) {
        if (grantedAuth.getAuthority().contains(permission)) {
          return true;
        }
      }
    }
    return false;
  }
}

